package org.octopus.core.fs;

import org.nutz.lang.util.Disks;
import org.octopus.core.bean.Document;
import org.octopus.core.bean.User;

/**
 * 文件系统路径管理
 * 
 * @author pw
 * 
 */
public class FsPath {

    private static String root = Disks.normalize("~/fs");

    private static final String users = "/users";

    private static final String chat = "/chat";

    /**
     * @return 用户文件存放目录
     */
    public static String usersPath() {
        return root + users;
    }

    /**
     * @return 聊天文件存放目录
     */
    public static String chatPath() {
        return root + chat;
    }

    /**
     * 设置文件系统的根目录
     * 
     * @param rpath
     */
    public static void setRoot(String rpath) {
        root = Disks.normalize(rpath);
    }

    /**
     * 用户目录, 使用用户名称作为目录名称
     * 
     * @param u
     * @return
     */
    public static String userHome(User u) {
        return userHome(u.getName());
    }

    /**
     * 用户目录, 使用用户名称作为目录名称
     * 
     * @param uname
     * @return
     */
    public static String userHome(String uname) {
        return usersPath() + "/" + uname;
    }

    /**
     * 用户目录, 使用用户名称作为目录名称
     * 
     * @param doc
     * @return
     */
    public static String filePath(Document doc) {
        String filePath = userHome(doc.getCreateUser()) + evalPath(doc.getId());
        return filePath;
    }

    /**
     * @param doc
     * @return 文件预览目录的路径
     */
    public static String filePreview(Document doc) {
        return filePath(doc) + ".preview";
    }

    /**
     * @param doc
     * @return 文件信息文件的路径
     */
    public static String fileInfo(Document doc) {
        return filePath(doc) + ".info";
    }

    /**
     * @param u
     * @return 用户头像
     */
    public static String userFace(User u) {
        return userHome(u) + "/face";
    }

    /**
     * @param uname
     * @return 用户头像
     */
    public static String userFace(String uname) {
        return userHome(uname) + "/face";
    }

    /**
     * @param u
     * @return 用户头像
     */
    public static String userProfile(User u) {
        return userHome(u) + "/profile";
    }

    /**
     * @param uname
     * @return 用户头像
     */
    public static String userProfile(String uname) {
        return userHome(uname) + "/profile";
    }

    /**
     * @param uu16
     * @return 根据id进行转换后得到目录
     */
    public static String evalPath(String uu16) {
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

}
