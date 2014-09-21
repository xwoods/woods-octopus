package org.octopus.core.fs;

import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Strings;
import org.nutz.lang.util.Disks;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.octopus.core.bean.Document;

/**
 * 文件系统路径管理
 * 
 * @author pw
 * 
 */
public class FsPath {

    private static Log log = Logs.get();

    private static String root = Disks.normalize("~/fs");

    /**
     * 设置文件系统的根目录
     * 
     * @param rpath
     */
    public static void setRoot(String rpath) {
        root = Disks.normalize(rpath);
        log.infof("FsPath Set Root [%s]", root);
    }

    private static Map<String, String> modulePathMap = new HashMap<String, String>();

    /**
     * 设置模块目录
     * 
     * @param moduleName
     */
    public static void addModulePath(String moduleName) {
        addModulePath(moduleName, moduleName);
    }

    /**
     * 设置模块目录
     * 
     * @param moduleName
     * @param modulePath
     */
    public static void addModulePath(String moduleName, String modulePath) {
        if (!modulePath.startsWith("/")) {
            modulePath = "/" + modulePath;
        }
        log.infof("ModulePath Add [%s - %s]", moduleName, modulePath);
        modulePathMap.put(moduleName, modulePath);
    }

    /**
     * @param moduleName
     * @return 返回对应的目录
     */
    public static String getModulePath(String moduleName) {
        String modulePath = modulePathMap.get(moduleName);
        if (!Strings.isBlank(modulePath)) {
            return root + modulePath;
        }
        throw new RuntimeException(String.format("Module[%s] Not Reg, Not Found Path", moduleName));
    }

    /**
     * @param uu16
     * @return 根据id进行转换后得到目录
     */
    private static String evalPath(String uu16) {
        // 默认针对R.UU16(), 共32位, 2位一分割
        // d10b7a95db034927887c6b78ff3a7de8
        // /d1/0b/7a/95/db/03/49/27/88/7c/6b/78/ff/3a/7d/e8
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < uu16.length(); i++) {
            if (i % 2 == 0) {
                sb.append("/");
            }
            sb.append(uu16.charAt(i));
        }
        return sb.toString();
    }

    // -------------------------------------------- 文件相关

    public static final String EXTRA_PREVIEW = "preview";
    public static final String EXTRA_INFO = "info";
    public static final String EXTRA_TRANS = "trans";

    public static String file(Document doc) {
        String pd = doc.getDefine();
        if (!pd.startsWith("/")) {
            pd += "/" + pd;
        }
        if (pd.endsWith("/")) {
            pd = pd.substring(pd.length() - 1);
        }
        return getModulePath(doc.getModule()) + pd + evalPath(doc.getId());
    }

    /**
     * 文件额外附属文件
     * 
     * @param doc
     * @param extra
     * @return
     */
    public static String fileExtra(Document doc, String extra) {
        return file(doc) + "." + extra;
    }

    // ------------------------------------ 特殊文件目录, 不在document结构中的

    public static String usersPath() {
        return getModulePath(FsModule.M_USERS);
    }

    /**
     * @param uname
     * @return 用户头像
     */
    public static String userFace(String uname) {
        return getModulePath(FsModule.M_USERS) + "/" + uname + "/face";
    }

    /**
     * @param uname
     * @return 用户的profile
     */
    public static String userProfile(String uname) {
        return getModulePath(FsModule.M_USERS) + "/" + uname + "/profile";
    }

}
