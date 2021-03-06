package org.octopus;

import java.io.File;
import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Files;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.octopus.core.Keys;
import org.octopus.core.NavController;
import org.octopus.core.bean.User;
import org.octopus.core.chat.ChatCache;
import org.octopus.core.chat.UserCache;
import org.octopus.core.fs.FsModule;
import org.octopus.core.fs.FsPath;
import org.octopus.core.fs.FsSetting;
import org.octopus.core.fs.pathdefine.ChatPathDefine;
import org.octopus.core.fs.pathdefine.DomainPathDefine;
import org.octopus.core.fs.pathdefine.UserPathDefine;
import org.octopus.core.service.OctVideoConvService;

public class OctopusSetup implements Setup {

    private Log log = Logs.get();

    private OctopusSetupChain setupChain;

    @Override
    public void init(NutConfig nc) {
        log.infof("Octopus Version : %s", Octopus.VERSION);

        Ioc ioc = nc.getIoc();
        Dao dao = ioc.get(Dao.class, "dao");
        OctopusConfig conf = ioc.get(OctopusConfig.class, "conf");

        // 设置RS, 可以使用其他服务器来提供静态文件
        nc.setAttribute(Keys.RS, Strings.sBlank(conf.getAppRs(), "/rs"));

        // 设置secretKey与GodPassword
        Octopus.setSecretKey(conf.get("password-key", Octopus.secretKey));
        Octopus.setGodPassword(conf.get("password-superuser", Octopus.godPassword));

        // 初始化数据库与数据
        Octopus.initDatabase(dao, conf);

        // 初始化FileSystem(fs)
        String fsHomePath = conf.getFSHome();
        if (!Strings.isBlank(fsHomePath)) {
            FsPath.setRoot(fsHomePath);
        }

        // FsModule注册
        FsModule.regModule(FsModule.M_USERS, new UserPathDefine(dao));
        FsModule.regModule(FsModule.M_CHAT, new ChatPathDefine(dao));
        FsModule.regModule(FsModule.M_DOMAINS, new DomainPathDefine(dao));

        // 加载文件配置
        FsSetting.loadSetting(dao, OctopusSetup.class.getResourceAsStream("/fs/file.properties"));

        // 用户与域信息初始化
        Octopus.initUserAndDomain(dao);

        // 侧边导航初始化
        NavController.init(dao);

        // 用户目录初始化
        dao.each(User.class, null, new Each<User>() {
            @Override
            public void invoke(int index, User ele, int length) throws ExitLoop, ContinueLoop,
                    LoopException {
                if (log.isDebugEnabled()) {
                    log.debugf("Create User-Home : %s", ele.getName());
                }
                Files.createDirIfNoExists(new File(FsPath.usersPath() + "/" + ele.getName()));
            }
        });

        // 用户缓存
        UserCache.init(dao);
        UserCache.startRunner();

        // 聊天缓存
        ChatCache.init(dao);
        ChatCache.startRunner();
        
        // videoConvert
        ioc.get(OctVideoConvService.class, "videoConvService").start();

        // setupChain初始化
        List<String> setupChainList = conf.getSetupChain();
        if (setupChainList != null && setupChainList.size() > 0) {
            setupChain = new OctopusSetupChain(setupChainList);
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
