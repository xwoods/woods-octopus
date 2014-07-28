package org.octopus.module;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.octopus.Keys;
import org.octopus.OctopusConfig;
import org.octopus.bean.core.Domain;
import org.octopus.bean.core.User;

@IocBean
public abstract class AbstractBaseModule {

    @Inject("refer:dao")
    protected Dao dao;

    @Inject("refer:conf")
    protected OctopusConfig conf;

    protected Domain DMN(HttpSession session) {
        return (Domain) session.getAttribute(Keys.SESSION_DOMAIN);
    }

    protected User ME(HttpSession session) {
        return (User) session.getAttribute(Keys.SESSION_USER);
    }

}
