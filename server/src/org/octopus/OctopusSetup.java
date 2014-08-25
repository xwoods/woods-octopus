package org.octopus;

import java.io.File;

import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.octopus.bean.core.User;
import org.octopus.cache.ChatCache;
import org.octopus.cache.UserCache;
import org.octopus.fs.FS;

public class OctopusSetup implements Setup {

    private Log log = Logs.get();

    private OctopusSetupChain setupChain;

    @Override
    public void init(NutConfig nc) {
        log.infof("Octopus Version : %s", Octopus.VERSION);

        Ioc ioc = nc.getIoc();
        Dao dao = ioc.get(Dao.class, "dao");
        OctopusConfig conf = ioc.get(OctopusConfig.class, "conf");

        // 设置secretKey与GodPassword
        Octopus.setSecretKey(conf.get("password-key"));
        Octopus.setGodPassword(conf.get("password-superuser"));

        // 初始化fs
        FS.loadConf();
        String fsHomePath = conf.get("fs-home");
        if (Strings.isBlank(fsHomePath)) {
            fsHomePath = Lang.runRootPath() + "/fs";
        }
        Files.createDirIfNoExists(fsHomePath);
        Octopus.setFsHome(fsHomePath);

        // 设置RS, 可以使用其他服务器来提供静态文件
        nc.setAttribute(Keys.RS, Strings.sBlank(conf.getAppRs(), "/rs"));

        // 初始化数据库与数据
        Octopus.initDatabase(dao, conf);
        Octopus.initUserAndDomain(dao);

        // 全局对象初始化
        Global.init(dao);

        // 开启msg文件监控
        // MsgWatcher.startWatch(OctopusMainModule.class, ioc);

        // 用户目录初始化
        dao.each(User.class, null, new Each<User>() {
            @Override
            public void invoke(int index, User ele, int length) throws ExitLoop, ContinueLoop,
                    LoopException {
                if (log.isDebugEnabled()) {
                    log.debugf("Create User-Home : %s", ele.getName());
                }
                Files.createDirIfNoExists(new File(Octopus.getFsHome(), ele.getName()));
            }
        });

        // 用户缓存
        UserCache.init(dao);
        UserCache.startRunner();

        // 聊天缓存
        ChatCache.init(dao);
        ChatCache.startRunner();

        // setupChain
        if (ioc.has("setupChain")) {
            setupChain = ioc.get(OctopusSetupChain.class, "setupChain");
            setupChain.initEach(nc);
        }
    }

    @Override
    public void destroy(NutConfig nc) {

        UserCache.stopRunner();
        ChatCache.stopRunner();

        if (setupChain != null) {
            setupChain.destroyEach(nc);
        }
    }

}
