package org.octopus;

import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

public class OctopusSetup implements Setup {

    private final static Log log = Logs.get();

    @Override
    public void init(NutConfig nc) {
        log.infof("Octopus Version : %s", Octopus.VERSION);

        Ioc ioc = nc.getIoc();
        Dao dao = ioc.get(Dao.class, "dao");
        OctopusConfig conf = ioc.get(OctopusConfig.class, "conf");

        // 设置secretKey
        Octopus.setSecretKey(conf.get("password-key"));

        // 设置RS, 可以使用其他服务器来提供静态文件
        nc.setAttribute(Keys.RS, Strings.sBlank(conf.getAppRs(), "/rs"));

        // 初始化数据库
        Octopus.initDatabase(dao, conf);

        // 全局对象初始化
        Global.init(dao);

        // 开启msg文件监控
        // MsgWatcher.startWatch(OctopusMainModule.class, ioc);

    }

    @Override
    public void destroy(NutConfig nc) {

    }

}
