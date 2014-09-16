package org.octopus.core.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import org.nutz.dao.Cnd;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.view.HttpStatusView;
import org.nutz.web.fliter.CheckNotLogin;
import org.octopus.core.Keys;
import org.octopus.core.bean.Document;
import org.octopus.core.bean.User;
import org.octopus.core.fs.FsAs;

@Filters({@By(type = CheckNotLogin.class, args = {Keys.SESSION_USER, "/login"})})
@At("/ex")
@Ok("ajax")
public class ExchangeModule extends AbstractBaseModule {

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
        if (checkIsFile && doc.getFileAs() == FsAs.DIR) {
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

    @At("/r/bin")
    @Ok("raw")
    public Object readBinary(@Param("fid") String fid,
                             @Param("useTrans") boolean useTrans,
                             HttpServletResponse resp,
                             @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        Document doc = dao.fetch(Document.class, Cnd.where("id", "=", fid));
        HttpStatusView errStatusView = checkDocumentPvg(me, doc, true, false, false);
        if (errStatusView != null) {
            return errStatusView;
        }
        try {
            String encode = new String(doc.getName().getBytes("UTF-8"), "ISO8859-1");
            resp.setHeader("Content-Disposition", "attachment; filename=" + encode);
            if (!Strings.isBlank(doc.getMime()))
                resp.setHeader("Content-Type", doc.getMime());
        }
        catch (UnsupportedEncodingException e) {
            throw Lang.wrapThrow(e);
        }
        // 用户目录 + 文件目录
        // FIXME 这里要重新实现一下了
        return null;
    }

    @At("/r/txt")
    @Ok("raw")
    public Object readTxt(@Param("fid") String fid,
                          HttpServletResponse resp,
                          @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        Object tf = readBinary(fid, false, resp, me);
        if (tf instanceof HttpStatusView) {
            return tf;
        }
        try {
            return Streams.readAndClose(new FileReader((File) tf));
        }
        catch (FileNotFoundException e) {
            log.error(e);
        }
        return new HttpStatusView(500);
    }
}
