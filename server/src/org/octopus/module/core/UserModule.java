package org.octopus.module.core;

import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
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
import org.octopus.module.AbstractBaseModule;

@At("/user")
@Ok("ajax")
public class UserModule extends AbstractBaseModule {

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

    @At("/register")
    public AjaxReturn register(@Param("..") User user) {
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
        dao.insert(user);
        return Ajax.ok().setMsg(Msg.USER_REGISTER_OK);
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
                                              Cnd.where("domainId", "=", dmn.getId())
                                                 .and("userId", "=", u.getId()));
                    if (null != du) {
                        // 用户其他信息
                        NutMap userInfo = new NutMap();
                        userInfo.put("lastLogin", u.getLastLogin());
                        userInfo.put("lastIP", u.getLastIP());
                        // 更新后设置当前的
                        updateLoginInfo(u, req);
                        userInfo.put("curtLogin", u.getLastLogin());
                        userInfo.put("curtIP", u.getLastIP());
                        userInfo.put("userType", du.getUserType());
                        // FIXME 获得用户的照片
                        userInfo.put("userFace", "face_0" + new Random().nextInt(10) + ".jpg");
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

    private void updateLoginInfo(User u, HttpServletRequest req) {
        u.setLastLogin(new Date());
        u.setLastIP(Lang.getIP(req));
        dao.update(u, "lastLogin|lastIP");
    }

    @At("/logout")
    public void logout(HttpSession sess) {
        sess.removeAttribute(Keys.SESSION_USER);
    }

}
