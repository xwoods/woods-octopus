package org.octopus.core.module;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.octopus.OctopusConfig;
import org.octopus.core.Keys;
import org.octopus.core.bean.Domain;
import org.octopus.core.bean.User;
import org.octopus.core.fs.FsIO;

@IocBean
public abstract class AbstractBaseModule {

    @Inject("refer:dao")
    protected Dao dao;

    @Inject("refer:conf")
    protected OctopusConfig conf;

    @Inject("refer:fsIO")
    protected FsIO fsIO;

    protected Domain DMN(HttpSession session) {
        return (Domain) session.getAttribute(Keys.SESSION_DOMAIN);
    }

    protected User ME(HttpSession session) {
        return (User) session.getAttribute(Keys.SESSION_USER);
    }

}
