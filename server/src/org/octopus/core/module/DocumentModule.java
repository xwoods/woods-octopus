package org.octopus.core.module;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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
import org.nutz.web.fliter.CheckNotLogin;
import org.octopus.OctopusErr;
import org.octopus.core.Keys;
import org.octopus.core.bean.Document;
import org.octopus.core.bean.User;
import org.octopus.core.fs.FsModule;
import org.octopus.core.fs.FsPath;
import org.octopus.core.fs.ReadType;

@Filters({@By(type = CheckNotLogin.class, args = {Keys.SESSION_USER, "/login"})})
@At("/doc")
@Ok("ajax")
public class DocumentModule extends AbstractBaseModule {

    private Log log = Logs.get();

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
    public HttpStatusView checkDocumentPvg(User user,
                                           Document doc,
                                           boolean checkRead,
                                           boolean checkWrite,
                                           boolean checkRemove) {

        // 不登陆不能访问 || 文件为空
        if (user == null || doc == null) {
            return new HttpStatusView(403);
        }
        boolean isOwner = doc.getCreateUser().equals(user.getName());
        // 不是文件创建者 && 文件私有
        if ((doc.isPrivate() && !isOwner)) {
            log.warnf("File[%s](Create by %s) Can't Access by %s",
                      doc.getName(),
                      doc.getCreateUser(),
                      user.getName());
            return new HttpStatusView(403);
        }
        // 检查是不是文件夹 FIXME 默认是检查是不是文件类型的
        boolean checkIsFile = true;
        if (checkIsFile && doc.getReadAs() == ReadType.DIR) {
            log.warnf("Dir[%s](Create by %s) Can't As File by %s",
                      doc.getName(),
                      doc.getCreateUser(),
                      user.getName());
            return new HttpStatusView(403);
        }
        // READ
        if (checkRead && (!doc.isCanRead() && !isOwner)) {
            log.warnf("File[%s](Create by %s) Can't Read by %s",
                      doc.getName(),
                      doc.getCreateUser(),
                      user.getName());
            return new HttpStatusView(403);
        }
        // WRITE
        if (checkWrite && (!doc.isCanWrite() && !isOwner)) {
            log.warnf("File[%s](Create by %s) Can't Write by %s",
                      doc.getName(),
                      doc.getCreateUser(),
                      user.getName());
            return new HttpStatusView(403);
        }
        // REMOVE
        if (checkRemove && (!doc.isCanRemove() && !isOwner)) {
            log.warnf("File[%s](Create by %s) Can't Remove by %s",
                      doc.getName(),
                      doc.getCreateUser(),
                      user.getName());
            return new HttpStatusView(403);
        }
        return null;
    }

    @At("/bin/read")
    @Ok("raw")
    public Object readBinary(@Param("docId") String docId,
                             HttpServletResponse resp,
                             @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        Document doc = dao.fetch(Document.class, Cnd.where("id", "=", docId));
        HttpStatusView errStatusView = checkDocumentPvg(me, doc, true, false, false);
        if (errStatusView != null) {
            return errStatusView;
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
        Document doc = dao.fetch(Document.class, Cnd.where("id", "=", docId));
        HttpStatusView errStatusView = checkDocumentPvg(me, doc, true, false, false);
        if (errStatusView != null) {
            return errStatusView;
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
        fsIO.writeText(doc, new StringInputStream(content), me);
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
        String docId = req.getHeader("DOC_ID");
        Document doc = dao.fetch(Document.class, docId);
        if (doc == null) {
            throw OctopusErr.DOCUMENT_NOT_EXIST(docId);
        }
        // 写入文件
        try {
            BufferedInputStream ins = Streams.buff(req.getInputStream());
            if (doc.isBinary()) {
                fsIO.writeBinary(doc, ins, me);
            } else {
                fsIO.writeText(doc, ins, me);
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
        String module = req.getHeader("DOC_M");
        String moduleKey = req.getHeader("DOC_MKEY");
        String pid = req.getHeader("DOC_PID");
        String fnm = urlDecode(req.getHeader("DOC_FNM"));
        boolean isPrivate = req.getHeader("DOC_RPIVATE") == null ? true
                                                                : Boolean.valueOf(req.getHeader("DOC_RPIVATE"));
        // 如果有父节点的话
        Document parent = null;
        if (!Strings.isBlank(pid)) {
            parent = dao.fetch(Document.class, pid);
            if (parent == null) {
                throw OctopusErr.DOCUMENT_PARENT_NOT_EXIST(pid);
            }
        } else {
            // 做个假的parent, 用define作为parent
            parent = new Document();
            parent.setModule(module);
            parent.setDefine(FsModule.definePath(module, moduleKey));
            parent.setId(parent.getDefine());
        }
        // 生成Doc对象
        Document doc = fsIO.make(parent, fnm, isPrivate, me);
        // 写入文件
        try {
            BufferedInputStream ins = Streams.buff(req.getInputStream());
            if (doc.isBinary()) {
                fsIO.writeBinary(doc, ins, me);
            } else {
                fsIO.writeText(doc, ins, me);
            }
            // FIXME 移动到别的地方去, 不要放在这里
            fsExtraMaker.makePreview(doc);
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
        HttpStatusView errStatusView = checkDocumentPvg(me, doc, true, false, false);
        if (errStatusView != null) {
            return errStatusView;
        }
        try {
            String encode = new String(doc.getName().getBytes("UTF-8"), "ISO8859-1");
            resp.setHeader("Content-Disposition", "attachment; filename=" + encode);
            resp.setHeader("Content-Type", "image/jpeg");
        }
        catch (UnsupportedEncodingException e) {
            throw Lang.wrapThrow(e);
        }
        return new File(FsPath.fileExtra(doc, FsPath.EXTRA_PREVIEW), "preview.jpg");
    }
}