package org.octopus.core.fs;

import java.util.HashMap;
import java.util.Map;

import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 管理模块的注册与获取PathDefine
 * 
 * @author pw
 * 
 */
public class FsModule {

    private static Log log = Logs.get();

    /**
     * 内置模块 - 用户个人网盘
     */
    public final static String M_USERS = "users";

    /**
     * 内置模块 - 聊天附件
     */
    public final static String M_CHAT = "chat";

    /**
     * 内置模块 - 域共享文件
     */
    public final static String M_DOMAINS = "domains";

    private static Map<String, PathDefine> m2definMap = new HashMap<String, PathDefine>();

    public static void regModule(String moduleName) {
        regModule(moduleName, null);
    }

    /**
     * 注册模块, 并给出目录定义
     * 
     * @param moduleName
     * @param pd
     */
    public static void regModule(String moduleName, PathDefine pd) {
        log.infof("FsModule Add [%s]", moduleName);
        FsPath.addModulePath(moduleName);
        m2definMap.put(moduleName, pd);
    }

    /**
     * 根据模块名称跟关键key, 获得对应的目录
     * 
     * @param moduleName
     * @param mkey
     * @return
     */
    public static String definePath(String moduleName, String mkey) {
        PathDefine pd = m2definMap.get(moduleName);
        if (pd == null) {
            return mkey;
        }
        return pd.define(mkey);
    }
}
