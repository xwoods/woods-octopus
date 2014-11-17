package org.octopus.core.module;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.dao.Cnd;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.stream.StringInputStream;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.view.HttpStatusView;
import org.nutz.web.ajax.Ajax;
import org.nutz.web.ajax.AjaxReturn;
import org.nutz.web.fliter.CheckNotLogin;
import org.octopus.OctopusErr;
import org.octopus.core.Keys;
import org.octopus.core.bean.Document;
import org.octopus.core.bean.MetaInfo;
import org.octopus.core.bean.User;
import org.octopus.core.fs.FsModule;
import org.octopus.core.fs.FsPath;
import org.octopus.core.fs.TransInfo;

@Filters({@By(type = CheckNotLogin.class, args = {Keys.SESSION_USER, "/login"})})
@At("/doc")
@Ok("ajax")
public class DocumentModule extends AbstractBaseModule {

    private Log log = Logs.get();

    @At("/list")
    public List<Document> listDocument(@Param("pid") String parentId,
                                       @Param("module") String module,
                                       @Param("moduleKey") String moduleKey,
                                       @Param("cate") String cate,
                                       HttpServletResponse resp,
                                       @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        Cnd lcdn = null;
        if (!Strings.isBlank(parentId)) {
            lcdn = Cnd.where("parentId", "=", parentId);
        } else {
            lcdn = Cnd.where("module", "=", module).and("parentId",
                                                        "=",
                                                        FsModule.definePath(module, moduleKey));
        }
        if (!Strings.isBlank(cate)) {
            lcdn.and("cate", "=", cate);
        }
        lcdn.asc("name");
        return dao.query(Document.class, lcdn);
    }

    /**
     * 检查文件的访问权限, 做初步的判断
     * 
     * @param user
     *            请求访问用户
     * @param doc
     *            文档信息
     * @param checkRead
     *            是否可读
     * @param checkWrite
     *            是否可写
     * @param checkRemove
     *            是否可删除
     * @return 错误信息(如果为空, 则表示可以正常访问)
     */
    public int checkDocumentPvg(User user,
                                Document doc,
                                boolean checkRead,
                                boolean checkWrite,
                                boolean checkRemove) {

        // 不登陆不能访问 || 文件为空
        if (user == null || doc == null) {
            return 403;
        }
        boolean isOwner = doc.getCreateUser().equals(user.getName());
        // 不是文件创建者 && 文件私有
        if ((doc.isPrivate() && !isOwner)) {
            log.warnf("File[%s](Create by %s) Can't Access by %s",
                      doc.getName(),
                      doc.getCreateUser(),
                      user.getName());
            return 403;
        }
        // READ
        if (checkRead && (!doc.isCanRead() && !isOwner)) {
            log.warnf("File[%s](Create by %s) Can't Read by %s",
                      doc.getName(),
                      doc.getCreateUser(),
                      user.getName());
            return 403;
        }
        // WRITE
        if (checkWrite && (!doc.isCanWrite() && !isOwner)) {
            log.warnf("File[%s](Create by %s) Can't Write by %s",
                      doc.getName(),
                      doc.getCreateUser(),
                      user.getName());
            return 403;
        }
        // REMOVE
        if (checkRemove && (!doc.isCanRemove() && !isOwner)) {
            log.warnf("File[%s](Create by %s) Can't Remove by %s",
                      doc.getName(),
                      doc.getCreateUser(),
                      user.getName());
            return 403;
        }
        return 200;
    }

    @At("/bin/read")
    @Ok("raw")
    public Object readBinary(@Param("docId") String docId,
                             HttpServletResponse resp,
                             @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        Document doc = dao.fetch(Document.class, Cnd.where("id", "=", docId));
        int errCode = checkDocumentPvg(me, doc, true, false, false);
        if (errCode != 200) {
            return new HttpStatusView(errCode);
        }
        try {
            String encode = new String((doc.getName() + "." + doc.getType()).getBytes("UTF-8"),
                                       "ISO8859-1");
            resp.setHeader("Content-Disposition", "attachment; filename=" + encode);
            if (!Strings.isBlank(doc.getMime()))
                resp.setHeader("Content-Type", doc.getMime());
        }
        catch (UnsupportedEncodingException e) {
            throw Lang.wrapThrow(e);
        }
        return fsIO.readBinary(doc);
    }

    @At("/txt/read")
    @Ok("raw")
    public Object readTxt(@Param("docId") String docId,
                          @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        Document doc = fsIO.fetch(docId);
        int errCode = checkDocumentPvg(me, doc, true, false, false);
        if (errCode != 200) {
            return new HttpStatusView(errCode);
        }
        return fsIO.readText(doc);
    }

    private String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 
     * 
     * @param docId
     * @param content
     * @param me
     * @return
     */
    @At("/txt/write")
    @Ok("ajax")
    @Fail("ajax")
    public Document writeText(@Param("docId") String docId,
                              @Param("content") String content,
                              @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        Document doc = dao.fetch(Document.class, docId);
        if (doc == null) {
            throw OctopusErr.DOCUMENT_NOT_EXIST(docId);
        }
        fsIO.writeText(doc, new StringInputStream(content), me.getName());
        return doc;
    }

    /**
     * 上传文件, 一般是覆盖操作
     * 
     * @param req
     * @param module
     * @param moduleKey
     * @param me
     * @return
     */
    @At("/bin/write")
    @Ok("ajax")
    @Fail("ajax")
    public Document writeBinary(HttpServletRequest req,
                                @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        String docId = req.getHeader("docId");
        Document doc = dao.fetch(Document.class, docId);
        if (doc == null) {
            throw OctopusErr.DOCUMENT_NOT_EXIST(docId);
        }
        // 写入文件
        try {
            BufferedInputStream ins = Streams.buff(req.getInputStream());
            if (doc.isBinary()) {
                fsIO.writeBinary(doc, ins, me.getName());
            } else {
                fsIO.writeText(doc, ins, me.getName());
            }
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        return doc;
    }

    /**
     * 上传文件, 新建一个文件
     * 
     * @param req
     * @param module
     * @param moduleKey
     * @param me
     * @return
     */
    @At("/bin/add")
    @Ok("ajax")
    @Fail("ajax")
    public Document addBinary(HttpServletRequest req,
                              @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        String module = req.getHeader("module");
        String moduleKey = urlDecode(req.getHeader("moduleKey"));
        String pid = req.getHeader("pid");
        String fnm = urlDecode(req.getHeader("fnm"));
        boolean isPrivate = req.getHeader("isPrivate") == null ? true
                                                              : Boolean.valueOf(req.getHeader("isPrivate"));

        // 生成Doc对象
        Document doc = makeDocument(module, moduleKey, pid, fnm, null, isPrivate, me.getName());
        // 写入文件
        try {
            BufferedInputStream ins = Streams.buff(req.getInputStream());
            if (doc.isBinary()) {
                fsIO.writeBinary(doc, ins, me.getName());
            } else {
                fsIO.writeText(doc, ins, me.getName());
            }
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        return doc;
    }

    @At("/preview/?")
    @Ok("raw")
    public Object preview(String docId,
                          HttpServletResponse resp,
                          @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        Document doc = dao.fetch(Document.class, Cnd.where("id", "=", docId));
        int errCode = checkDocumentPvg(me, doc, true, false, false);
        if (errCode != 200) {
            return new HttpStatusView(errCode);
        }
        try {
            String encode = new String(doc.getName().getBytes("UTF-8"), "ISO8859-1");
            resp.setHeader("Content-Disposition", "attachment; filename=" + encode);
            resp.setHeader("Content-Type", "image/jpeg");
        }
        catch (UnsupportedEncodingException e) {
            throw Lang.wrapThrow(e);
        }
        // 按照文件类型, 返回对应的预览
        if ("video".equals(doc.getCate()) || "image".equals(doc.getCate())) {
            return new File(FsPath.fileExtra(doc, FsPath.EXTRA_DIR_PREVIEW), "preview.jpg");
        }
        // TODO 未实现
        else {
            return null;
        }
    }

    @At("/preview-video-poster/?")
    @Ok("raw")
    public Object previewVideoPost(String docId,
                                   HttpServletResponse resp,
                                   @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        Document doc = dao.fetch(Document.class, Cnd.where("id", "=", docId));
        int errCode = checkDocumentPvg(me, doc, true, false, false);
        if (errCode != 200) {
            return new HttpStatusView(errCode);
        }
        try {
            String encode = new String(doc.getName().getBytes("UTF-8"), "ISO8859-1");
            resp.setHeader("Content-Disposition", "attachment; filename=" + encode);
            resp.setHeader("Content-Type", "image/jpeg");
        }
        catch (UnsupportedEncodingException e) {
            throw Lang.wrapThrow(e);
        }
        return new File(FsPath.fileExtra(doc, FsPath.EXTRA_DIR_PREVIEW), "poster.jpg");
    }

    @At("/preview-video/?")
    @Ok("raw")
    public Object previewVideo(String docId,
                               HttpServletResponse resp,
                               @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        Document doc = dao.fetch(Document.class, Cnd.where("id", "=", docId));
        int errCode = checkDocumentPvg(me, doc, true, false, false);
        if (errCode != 200) {
            return new HttpStatusView(errCode);
        }
        try {
            String encode = new String(doc.getName().getBytes("UTF-8"), "ISO8859-1");
            resp.setHeader("Content-Disposition", "attachment; filename=" + encode);
            resp.setHeader("Content-Type", "video/mp4");
        }
        catch (UnsupportedEncodingException e) {
            throw Lang.wrapThrow(e);
        }
        return new File(FsPath.fileExtra(doc, FsPath.EXTRA_DIR_PREVIEW), "preview.mp4");
    }

    @At("/rename")
    @Ok("ajax")
    public AjaxReturn renameDocument(@Param("docId") String docId,
                                     @Param("docName") String docName,
                                     HttpServletResponse resp,
                                     @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        Document doc = dao.fetch(Document.class, Cnd.where("id", "=", docId));
        int errCode = checkDocumentPvg(me, doc, true, false, false);
        if (errCode != 200) {
            // TODO
        } else {
            doc.setName(docName);
            // 检查文件是否重名
            if (fsIO.existDocument(doc)) {
                fsIO.setNewName(doc);
            }
            dao.update(doc, "name");
        }
        return Ajax.ok().setData(doc.getName());
    }

    @At("/delete")
    @Ok("ajax")
    public AjaxReturn deleteDocument(@Param("docId") String docIds,
                                     HttpServletResponse resp,
                                     @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        Map<String, Boolean> delSuccessMap = new HashMap<String, Boolean>();
        String[] dlist = Strings.splitIgnoreBlank(docIds, ",");
        for (String d : dlist) {
            Document doc = dao.fetch(Document.class, Cnd.where("id", "=", d));
            int errCode = checkDocumentPvg(me, doc, true, false, true);
            if (errCode != 200) {
                delSuccessMap.put(d, false);
            } else {
                fsIO.delete(doc);
                delSuccessMap.put(d, true);
            }
        }
        return Ajax.ok().setData(delSuccessMap);
    }

    @At("/trans/video")
    @Ok("ajax")
    public AjaxReturn transDocument(@Param("docId") String docId,
                                    @Param("cutX") int cutX,
                                    @Param("cutY") int cutY,
                                    @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        if (cutX <= 1 && cutY <= 1) {
            return Ajax.fail();
        }
        Document oldDoc = dao.fetch(Document.class, Cnd.where("id", "=", docId));
        // FIXME
        oldDoc.setName(oldDoc.getName() + "_" + cutX + "x" + cutY);
        Document newDoc = fsIO.copy(oldDoc, me.getName(), true);
        // 先加上转换的后的分割信息
        MetaInfo mi = newDoc.metaInfo();
        mi.set("transCutX", cutX);
        mi.set("transCutY", cutY);
        newDoc.setMeta(mi.toString());
        dao.update(newDoc, "meta");
        // 转换
        TransInfo tinfo = new TransInfo();
        tinfo.setCutX(cutX);
        tinfo.setCutY(cutY);
        tinfo.setHasThumb(false);
        tinfo.setHasPreview(false);
        tinfo.setHasTrans(true);
        fsExtra.makeTrans(newDoc, tinfo);
        return Ajax.ok().setData(newDoc);
    }

    @At("/copy")
    @Ok("ajax")
    public AjaxReturn copyDocument(@Param("docId") String docId,
                                   HttpServletResponse resp,
                                   @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        Document oldDoc = dao.fetch(Document.class, Cnd.where("id", "=", docId));
        int errCode = checkDocumentPvg(me, oldDoc, true, false, false);
        if (errCode != 200) {
            return Ajax.fail().setData(200);
        } else {
            Document newDoc = fsIO.copy(oldDoc, me.getName(), false);
            return Ajax.ok().setData(newDoc);
        }
    }

    /**
     * 新建一个文件对象
     * 
     * @param module
     * @param moduleKey
     * @param pid
     * @param fnm
     * @param type
     * @param isPrivate
     * @param me
     * @return
     */
    @At("/create")
    @Ok("ajax")
    public Document createDocument(@Param("module") String module,
                                   @Param("moduleKey") String moduleKey,
                                   @Param("pid") String pid,
                                   @Param("fnm") String fnm,
                                   @Param("ftp") String type,
                                   @Param("isPrivate") Boolean isPrivate,
                                   @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        if (isPrivate == null) {
            isPrivate = true;
        }
        return makeDocument(module, moduleKey, pid, fnm, type, isPrivate, me.getName());
    }

    private Document makeDocument(String module,
                                  String moduleKey,
                                  String pid,
                                  String fnm,
                                  String type,
                                  boolean isPrivate,
                                  String ctUser) {
        Document doc = null;
        // 如果有父节点的话
        if (!Strings.isBlank(pid)) {
            Document parent = dao.fetch(Document.class, pid);
            if (parent == null) {
                throw OctopusErr.DOCUMENT_PARENT_NOT_EXIST(pid);
            }
            doc = fsIO.make(parent, fnm, type, isPrivate, ctUser);
        } else {
            // 做个假的parent, 用define作为parent
            doc = fsIO.make(module,
                            FsModule.definePath(module, moduleKey),
                            fnm,
                            type,
                            isPrivate,
                            ctUser);
        }
        return doc;
    }

}
