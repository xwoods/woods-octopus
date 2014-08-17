package org.octopus.module.core;

import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
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
import org.octopus.Keys;
import org.octopus.bean.core.MyConf;
import org.octopus.bean.core.User;
import org.octopus.module.AbstractBaseModule;

@Filters({@By(type = CheckNotLogin.class, args = {Keys.SESSION_USER, "/login"})})
@At("/myconf")
@Ok("ajax")
public class MyConfModule extends AbstractBaseModule {

    @At("/r")
    public AjaxReturn readConf(@Param("user") String user,
                               @Param("type") String type,
                               @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        if (Strings.isBlank(user)) {
            user = me.getName();
        }
        String confStr = null;
        MyConf mc = dao.fetch(MyConf.class, Cnd.where("owner", "=", user).and("type", "=", type));
        if (mc != null) {
            confStr = mc.getContent();
        }
        return Ajax.ok().setData(confStr);
    }

    @At("/w")
    public void readConf(@Param("user") String user,
                         @Param("type") String type,
                         @Param("content") String content,
                         @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        if (Strings.isBlank(user)) {
            user = me.getName();
        }
        MyConf mc = dao.fetch(MyConf.class, Cnd.where("owner", "=", user).and("type", "=", type));
        if (mc == null) {
            mc = new MyConf();
            mc.setOwner(user);
            mc.setType(type);
            mc.setContent(content);
            dao.insert(mc);
        } else {
            mc.setContent(content);
            dao.update(mc, "content");
        }
    }
}
