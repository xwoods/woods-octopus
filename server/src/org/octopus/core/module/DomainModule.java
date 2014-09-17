package org.octopus.core.module;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.util.cri.SimpleCriteria;
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
import org.nutz.web.ajax.Ajax;
import org.nutz.web.ajax.AjaxReturn;
import org.nutz.web.fliter.CheckNotLogin;
import org.nutz.web.query.CndMaker;
import org.nutz.web.query.Query;
import org.nutz.web.query.QueryStr;
import org.octopus.Octopus;
import org.octopus.core.Keys;
import org.octopus.core.Msg;
import org.octopus.core.NavController;
import org.octopus.core.bean.Domain;
import org.octopus.core.bean.DomainConf;
import org.octopus.core.bean.DomainUser;
import org.octopus.core.bean.User;

@Filters({@By(type = CheckNotLogin.class, args = {Keys.SESSION_USER, "/login"})})
@At("/domain")
@Ok("ajax")
public class DomainModule extends AbstractBaseModule {

    private Log log = Logs.get();

    @At("/checkExist")
    public boolean checkExist(@Param("field") String field, @Param("value") String value) {
        return dao.count(Domain.class, Cnd.where(field, "=", value)) >= 1;
    }

    @At("/register")
    public AjaxReturn register(@Param("..") Domain domain,
                               @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        if (checkExist("name", domain.getName())) {
            return Ajax.fail().setMsg(Msg.DOMAIN_EXIST_NAME);
        }
        domain.setAbout(Strings.isBlank(domain.getAbout()) ? "" : domain.getAbout());
        DomainConf dmnConf = new DomainConf();
        dmnConf.setDomain(domain);
        dmnConf.setManager(me);
        // 初始化域
        Octopus.initDomain(dao, dmnConf);
        // 更新nav
        NavController.me().updateDomainNav();
        return Ajax.ok().setMsg(Msg.USER_REGISTER_OK);
    }

    @At("/list")
    public Object listDomain() {
        return dao.query(Domain.class, null);
    }

    @At("/query")
    public AjaxReturn queryDomain(@Param("..") Query q) {
        q.dao = dao;
        q.clz = Domain.class;
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

    @At("/user/list")
    public AjaxReturn listUser(@Param("domain") String domain, HttpSession session) {
        List<User> uList = new ArrayList<User>();
        Domain dmn = Strings.isBlank(domain) ? DMN(session) : dao.fetch(Domain.class, domain);
        List<DomainUser> dulist = dao.query(DomainUser.class,
                                            Cnd.where("domain", "=", dmn.getName()));
        for (DomainUser du : dulist) {
            User u = dao.fetch(User.class, du.getUser());
            u.setPassword(null);
            uList.add(u);
        }
        return Ajax.ok().setData(uList);
    }

    @At("/user/add")
    public AjaxReturn addUser(@Param("users") String users,
                              @Param("domain") String domain,
                              @Param("userType") String userType,
                              HttpSession session) {
        User me = ME(session);
        return Octopus.addUser2Domain(dao, domain, users, userType, me.getName()) ? Ajax.ok()
                                                                                 : Ajax.fail();
    }

    @At("/user/remove")
    public AjaxReturn removeUser(@Param("users") String users,
                                 @Param("domain") String domain,
                                 @Param("userType") String userType,
                                 HttpSession session) {
        Domain dmn = dao.fetch(Domain.class, domain);
        if (dmn == null) {
            log.errorf("Domain[%s] Not Exist, Can't Remove Users", domain);
            return Ajax.fail();
        }
        String[] userNames = Strings.splitIgnoreBlank(users, ",");
        for (String user : userNames) {
            if (dao.fetch(User.class, user) == null) {
                log.warnf("User[%s] Not Exist, Can't Remove from Domain[%s]", user, domain);
                continue;
            }
            DomainUser du = dao.fetch(DomainUser.class,
                                      Cnd.where("domain", "=", domain).and("user", "=", user));
            if (null != du) {
                dao.delete(du);
            }
        }
        return Ajax.ok();
    }

    @At("/user/setType")
    public AjaxReturn setUserType(@Param("uname") String uname,
                                  @Param("userType") String userType,
                                  HttpSession session) {
        Domain dmn = DMN(session);
        DomainUser du = dao.fetch(DomainUser.class,
                                  Cnd.where("domain", "=", dmn.getName()).and("user", "=", uname));
        if (null != du && !du.getUserType().equals(userType)) {
            du.setUserType(userType);
            du.setModifyTime(new Date());
            du.setModifyUser(ME(session).getName());
            dao.update(du, "userType|modifyTime|modifyUser");
            return Ajax.ok();
        }
        return Ajax.fail();
    }
}
