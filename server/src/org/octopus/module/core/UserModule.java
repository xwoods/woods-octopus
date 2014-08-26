package org.octopus.module.core;

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
import org.nutz.ioc.loader.annotation.Inject;
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
import org.octopus.Keys;
import org.octopus.Msg;
import org.octopus.Octopus;
import org.octopus.bean.core.Domain;
import org.octopus.bean.core.DomainUser;
import org.octopus.bean.core.User;
import org.octopus.cache.ChatCache;
import org.octopus.cache.UserCache;
import org.octopus.module.AbstractBaseModule;
import org.octopus.module.core.query.DomainCndMaker;

@At("/user")
@Ok("ajax")
public class UserModule extends AbstractBaseModule {

    private Log log = Logs.get();

    private String password(String password) {
        return Octopus.encrypt(password);
    }

    @At("/checkExist/?/?")
    public boolean checkExist(String field, String value) {
        if (Strings.isBlank(value)) {
            return false;
        }
        return dao.count(User.class, Cnd.where(field, "=", value)) >= 1;
    }

    @Inject("java:$conf.get('invite-code', '123456')")
    protected String inviteCode;

    @At("/register")
    public AjaxReturn register(@Param("..") User user, @Param("ic") String inviteCode) {
        if (!this.inviteCode.equals(inviteCode)) {
            log.warnf("UserIC[%s] Not Match ServerIC[%s]", inviteCode, this.inviteCode);
            return Ajax.fail().setMsg(Msg.USER_INVITE_CODE_EXPIRED);
        }
        if (checkExist("name", user.getName())) {
            return Ajax.fail().setMsg(Msg.USER_EXIST_NAME);
        }
        if (checkExist("alias", user.getAlias())) {
            return Ajax.fail().setMsg(Msg.USER_EXIST_ALIAS);
        }
        // if (checkExist("email", user.getEmail())) {
        // return Ajax.fail().setMsg(Msg.USER_EXIST_EMAIL);
        // }
        // if (checkExist("phone", user.getPhone())) {
        // return Ajax.fail().setMsg(Msg.USER_EXIST_PHONE);
        // }
        if (Strings.isBlank(user.getAlias())) {
            user.setAlias(user.getName());
        }
        user.setPassword(password(user.getPassword()));
        user.setCreateTime(new Date());
        user.setEnable(true);

        Octopus.createUser(dao, user);
        UserCache.addUser(user);
        ChatCache.setUnread(user.getName(), 0);

        return Ajax.ok().setMsg(Msg.USER_REGISTER_OK);
    }

    @At("/query")
    public AjaxReturn queryUser(@Param("kwd") String kwd,
                                @Param("pgnm") int pgnm,
                                @Param("pgsz") int pgsz,
                                @Param("orderby") String orderby,
                                @Param("asc") boolean asc) {
        QueryResult qr = new DomainCndMaker().queryResult(dao,
                                                          User.class,
                                                          null,
                                                          pgnm,
                                                          pgsz,
                                                          orderby,
                                                          asc,
                                                          kwd);
        return Ajax.ok().setData(qr);
    }

    @At("/login")
    public AjaxReturn login(@Param("domain") String dmnName,
                            @Param("name") String lname,
                            @Param("password") String password,
                            HttpServletRequest req,
                            HttpSession sess) {
        AjaxReturn ar = null;
        Domain dmn = dao.fetch(Domain.class, Cnd.where("name", "=", dmnName));
        if (null == dmn) {
            ar = Ajax.fail().setMsg(Msg.DOMAIN_NOT_EXIST);
        } else {
            User u = null;
            if (dmnName.equals(lname)) {
                // 说明是域管理员
                u = dao.fetch(User.class,
                              Cnd.where("password", "=", password(password))
                                 .and("name", "=", lname));
            } else {
                // lname 可以是 name, phone, email
                // TODO 暂时仅仅检查name吧
                u = dao.fetch(User.class,
                              Cnd.where("password", "=", password(password))
                                 .and("name", "=", lname));
            }
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
    }

    @At("/ping")
    public void ping(@Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        if (me != null) {
            UserCache.ping(me);
        }
    }

    @At("/friends")
    public Object myFriends(@Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
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
        File uface = new File(Octopus.getFsHome() + "/" + uname + "/face");
        if (uface.exists()) {
            return uface;
        } else {
            log.warnf("User[%s] Face Not Found!", uname);
            return this.getClass().getResourceAsStream("/face.png");
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
