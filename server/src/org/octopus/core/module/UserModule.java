package org.octopus.core.module;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.web.ajax.Ajax;
import org.nutz.web.ajax.AjaxReturn;
import org.nutz.web.query.CndMaker;
import org.nutz.web.query.Query;
import org.nutz.web.query.QueryStr;
import org.octopus.Octopus;
import org.octopus.core.Keys;
import org.octopus.core.Msg;
import org.octopus.core.bean.Domain;
import org.octopus.core.bean.DomainUser;
import org.octopus.core.bean.InviteRegister;
import org.octopus.core.bean.User;
import org.octopus.core.chat.ChatCache;
import org.octopus.core.chat.UserCache;
import org.octopus.core.fs.FsPath;

@At("/user")
@Ok("ajax")
public class UserModule extends AbstractBaseModule {

    private Log log = Logs.get();

    private String password(String password) {
        return Octopus.encrypt(password);
    }

    /**
     * @param field
     * @param value
     * @return 存在, 返回true
     */
    @At("/checkExist")
    public boolean checkExist(@Param("field") String field, @Param("value") String value) {
        int en = dao.count(User.class, Cnd.where(field, "=", value));
        return en > 0;
    }

    /**
     * @param inviteCode
     * @return 没有使用,返回true
     */
    @At("/invite/ok")
    public boolean checkInviteCode(@Param("ic") String inviteCode) {
        return dao.count(InviteRegister.class,
                         Cnd.where("id", "=", inviteCode).and("hasReg", "=", false)) == 1;
    }

    @At("/invite/get")
    public InviteRegister getInviteInfo(@Param("ic") String inviteCode) {
        InviteRegister ir = dao.fetch(InviteRegister.class,
                                      Cnd.where("id", "=", inviteCode).and("hasReg", "=", false));
        return ir;
    }

    @At("/invite/add")
    public void addInviteInfo(@Param("..") InviteRegister ir,
                              @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        ir.setCreateUser(me.getName());
        dao.insert(ir);
    }

    @At("/invite/list")
    public AjaxReturn listInviteInfo(@Param("..") Query q) {
        q.dao = dao;
        q.clz = InviteRegister.class;
        QueryResult qr = CndMaker.queryResult(new QueryStr() {
            public void analysisQueryStr(SimpleCriteria sc, String kwd, String... otherQCnd) {
                if (!Strings.isBlank(kwd)) {
                    sc.where().and("userName", "like", "%" + kwd + "%");
                    sc.where().or("domainList", "like", "%" + kwd + "%");
                    sc.where().or("domainNameList", "like", "%" + kwd + "%");
                }
            }
        }, q);
        return Ajax.ok().setData(qr);
    }

    @At("/register")
    public AjaxReturn register(@Param("..") User user, @Param("ic") String inviteCode) {
        if (!checkInviteCode(inviteCode)) {
            log.warnf("UserIC[%s] Has Used Or Not Right", inviteCode);
            return Ajax.fail().setMsg(Msg.USER_INVITE_CODE_EXPIRED);
        }
        if (checkExist("email", user.getEmail())) {
            return Ajax.fail().setMsg(Msg.USER_EXIST_EMAIL);
        }
        if (checkExist("name", user.getName())) {
            return Ajax.fail().setMsg(Msg.USER_EXIST_NAME);
        }

        // 注册到域里
        InviteRegister ir = dao.fetch(InviteRegister.class,
                                      Cnd.where("id", "=", inviteCode).and("hasReg", "=", false));
        if (ir == null) {
            log.errorf("Same Time, SomeOne Use InviteCode [%s]", inviteCode);
            return Ajax.fail().setMsg(Msg.USER_INVITE_CODE_EXPIRED);
        }
        // 注册用户信息
        user.setPassword(password(user.getPassword()));
        user.setCreateTime(new Date());
        user.setEnable(true);

        Octopus.createUser(dao, user);
        UserCache.addUser(user);
        ChatCache.setUnread(user.getName(), 0);

        // 根据邀请信息, 注册到域
        String[] dlist = Strings.splitIgnoreBlank(ir.getDomainList(), ",");
        for (String dnm : dlist) {
            Octopus.addUser2Domain(dao, dnm, user.getName(), null, ir.getCreateUser());
        }
        // 邀请信息更新
        ir.setHasReg(true);
        ir.setUseTime(new Date());
        ir.setRegName(user.getName());
        dao.update(ir, "hasReg|useTime|regName");

        return Ajax.ok().setMsg(Msg.USER_REGISTER_OK);
    }

    @At("/query")
    public AjaxReturn queryUser(@Param("..") Query q) {
        q.tableSet(dao, User.class, null);
        QueryResult qr = CndMaker.queryResult(new QueryStr() {
            public void analysisQueryStr(SimpleCriteria sc, String kwd, String... otherQCnd) {
                if (!Strings.isBlank(kwd)) {
                    sc.where().and("name", "like", "%" + kwd + "%");
                    sc.where().or("alias", "like", "%" + kwd + "%");
                }
            }
        }, q);
        return Ajax.ok().setData(qr);
    }

    @At("/login")
    public AjaxReturn login(@Param("domain") String dmnName,
                            @Param("email") String email,
                            @Param("password") String password,
                            HttpServletRequest req,
                            HttpSession sess) {
        AjaxReturn ar = null;
        Domain dmn = dao.fetch(Domain.class, Cnd.where("name", "=", dmnName));
        if (null == dmn) {
            ar = Ajax.fail().setMsg(Msg.DOMAIN_NOT_EXIST);
        } else {
            User u = dao.fetch(User.class,
                               Cnd.where("password", "=", password(password)).and("email",
                                                                                  "=",
                                                                                  email));
            if (null != u) {
                if (u.isEnable()) {
                    DomainUser du = dao.fetch(DomainUser.class,
                                              Cnd.where("domain", "=", dmn.getName())
                                                 .and("user", "=", u.getName()));
                    if (null != du) {
                        // 用户其他信息
                        NutMap userInfo = new NutMap();
                        userInfo.put("lastLogin", u.getLastLogin());
                        userInfo.put("lastIP", u.getLastIP());
                        // 更新后设置当前的
                        u.setLastDomain(dmnName);
                        u.setLastLogin(new Date());
                        u.setLastIP(Lang.getIP(req));
                        dao.update(u, "lastLogin|lastIP|lastDomain");
                        //
                        userInfo.put("curtLogin", u.getLastLogin());
                        userInfo.put("curtIP", u.getLastIP());
                        userInfo.put("userType", du.getUserType());
                        userInfo.put("userFace", u.getName());
                        // 放入session中
                        sess.setAttribute(Keys.SESSION_DOMAIN, dmn);
                        sess.setAttribute(Keys.SESSION_USER, u);
                        sess.setAttribute(Keys.SESSION_USER_INFO, userInfo);
                        ar = Ajax.ok().setMsg(Msg.USER_LOGIN_OK);
                    } else {
                        ar = Ajax.fail().setMsg(Msg.USER_NOTIN_DOMAIN);
                    }
                } else {
                    ar = Ajax.fail().setMsg(Msg.USER_ENABLE_FALSE);
                }
            } else {
                ar = Ajax.fail().setMsg(Msg.USER_LOGIN_FAIL);
            }
        }
        return ar;
    }

    @At("/logout")
    public void logout(HttpSession sess) {
        sess.removeAttribute(Keys.SESSION_USER);
        sess.removeAttribute(Keys.SESSION_DOMAIN);
        sess.removeAttribute(Keys.SESSION_USER_INFO);
    }

    @At("/ping")
    public void ping(@Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        if (me != null) {
            UserCache.ping(me);
        }
    }

    @At("/friends")
    public Object myFriends(@Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        if (me == null) {
            return null;
        }
        return UserCache.getMyFriends(me);
    }

    @At("/friends/online")
    public Object myFriendsOnline(@Param("friends") String fsNames) {
        String[] nms = fsNames.split(",");
        return UserCache.getMyFriendsOnline(Arrays.asList(nms));
    }

    @At("/face/?")
    @Ok("raw")
    public Object userFace(String uname, HttpServletResponse resp) {
        try {
            String encode = new String(uname.getBytes("UTF-8"), "ISO8859-1");
            resp.setHeader("Content-Disposition", "attachment; filename=" + encode);
            resp.setHeader("Content-Type", "image/jpg");
        }
        catch (UnsupportedEncodingException e) {
            throw Lang.wrapThrow(e);
        }
        File uface = new File(FsPath.userFace(uname));
        if (uface.exists()) {
            return uface;
        } else {
            log.warnf("User[%s] Face Not Found!", uname);
            return this.getClass().getResourceAsStream("/fs/user/face.png");
        }
    }

    @At("/profile/?")
    @Ok("raw")
    public Object userProfile(String uname, HttpServletResponse resp) {
        try {
            String encode = new String(uname.getBytes("UTF-8"), "ISO8859-1");
            resp.setHeader("Content-Disposition", "attachment; filename=" + encode);
            resp.setHeader("Content-Type", "image/jpg");
        }
        catch (UnsupportedEncodingException e) {
            throw Lang.wrapThrow(e);
        }
        File uprofile = new File(FsPath.userProfile(uname));
        if (uprofile.exists()) {
            return uprofile;
        } else {
            log.warnf("User[%s] Face Not Found!", uname);
            return this.getClass().getResourceAsStream("/fs/user/profile.json");
        }
    }

    @At("/na")
    public AjaxReturn userNameAlias() {
        final NutMap naMap = new NutMap();
        Sql sql = Sqls.create("select name, alias from t_user");
        sql.setCallback(new SqlCallback() {
            @Override
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                while (rs.next()) {
                    naMap.put(rs.getString(1), rs.getString(2));
                }
                return null;
            }
        });
        dao.execute(sql);
        return Ajax.ok().setData(naMap);
    }
}
