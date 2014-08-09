package org.octopus.module.core;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.web.ajax.Ajax;
import org.nutz.web.ajax.AjaxReturn;
import org.nutz.web.fliter.CheckNotLogin;
import org.octopus.Global;
import org.octopus.Keys;
import org.octopus.Msg;
import org.octopus.Octopus;
import org.octopus.bean.DomainConf;
import org.octopus.bean.core.Domain;
import org.octopus.bean.core.DomainUser;
import org.octopus.bean.core.User;
import org.octopus.module.AbstractBaseModule;

@Filters({@By(type = CheckNotLogin.class, args = {Keys.SESSION_USER, "/login"})})
@At("/domain")
@Ok("ajax")
public class DomainModule extends AbstractBaseModule {

    private Log log = Logs.get();

    @At("/checkExist/?/?")
    public boolean checkExist(String field, String value) {
        return dao.count(Domain.class, Cnd.where(field, "=", value)) >= 1;
    }

    @At("/register")
    public AjaxReturn register(@Param("..") Domain domain, HttpSession session) {
        if (checkExist("name", domain.getName())) {
            return Ajax.fail().setMsg(Msg.DOMAIN_EXIST_NAME);
        }
        // 尝试加载对应的配置
        DomainConf dmnConf = null;
        try {
            dmnConf = Json.fromJson(DomainConf.class,
                                    new InputStreamReader(Octopus.class.getResourceAsStream("/dmn/"
                                                                                            + domain.getName()
                                                                                            + ".js")));
        }
        catch (Exception e) {
            log.warnf("not find dmnConf by [%s], use default.js", domain.getName());
            // 加载默认
            dmnConf = Json.fromJson(DomainConf.class,
                                    new InputStreamReader(Octopus.class.getResourceAsStream("/dmn/default.js")));
            dmnConf.getDomain().setName(domain.getName());
            dmnConf.getDomain().setAlias(Strings.isBlank(domain.getAlias()) ? domain.getName()
                                                                           : domain.getAlias());
        }
        dmnConf.getDomain().setAbout(Strings.isBlank(domain.getAbout()) ? "" : domain.getAbout());
        dmnConf.getManager().setName(domain.getName());
        // 初始化域
        Octopus.initDomain(dao, dmnConf);
        // 更新nav
        Global.me().updateDomainNav();
        return Ajax.ok().setMsg(Msg.USER_REGISTER_OK);
    }

    @At("/user/list")
    public AjaxReturn listUser(@Param("domain") String domain, HttpSession session) {
        Domain dmn = Strings.isBlank(domain) ? DMN(session) : dao.fetch(Domain.class, domain);
        List<NutMap> re = new ArrayList<NutMap>();
        List<DomainUser> dulist = dao.query(DomainUser.class, Cnd.where("domain", "=", domain));
        for (DomainUser du : dulist) {
            User u = dao.fetch(User.class, du.getUser());
            u.setPassword(null);
            NutMap uobj = new NutMap();
            uobj.put("user", u);
            uobj.put("type", du.getUserType());
            re.add(uobj);
        }
        return Ajax.ok().setData(re);
    }

    @At("/user/add")
    public AjaxReturn addUser(@Param("users") String users,
                              @Param("domain") String domain,
                              @Param("userType") String userType,
                              HttpSession session) {
        User me = ME(session);
        if (Strings.isBlank(userType)) {
            userType = Keys.DMN_USER_TYPE_NORMAL;
        }
        Domain dmn = dao.fetch(Domain.class, domain);
        if (dmn == null) {
            log.errorf("Domain[%s] Not Exist, Can't Add Users", domain);
            return Ajax.fail();
        }
        String[] userNames = Strings.splitIgnoreBlank(users, ",");
        for (String user : userNames) {
            if (dao.fetch(User.class, user) == null) {
                log.warnf("User[%s] Not Exist, Can't Add to Domain[%s]", user, domain);
                continue;
            }
            DomainUser du = dao.fetch(DomainUser.class,
                                      Cnd.where("domain", "=", domain).and("user", "=", user));
            if (null == du) {
                du = new DomainUser();
                du.setDomain(domain);
                du.setUser(user);
                du.setUserType(userType);
                du.setCreateTime(new Date());
                du.setCreateUser(me.getName());
                dao.insert(du);
            }
        }
        return Ajax.ok();
    }

    @At("/user/setType")
    public AjaxReturn setUserType(@Param("userId") String uId,
                                  @Param("userType") String userType,
                                  HttpSession session) {
        Domain dmn = DMN(session);
        DomainUser du = dao.fetch(DomainUser.class,
                                  Cnd.where("domainId", "=", dmn.getId()).and("userId", "=", uId));
        if (null != du && !du.getUserType().equals(userType)) {
            du.setUserType(userType);
            du.setModifyTime(new Date());
            du.setModifyUser(ME(session).getId());
            dao.update(du, "userType|modifyTime|modifyUser");
            return Ajax.ok();
        }
        return Ajax.fail();
    }
}
