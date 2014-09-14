package org.octopus;

import java.io.File;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;
import org.octopus.core.Keys;
import org.octopus.core.bean.Domain;
import org.octopus.core.bean.DomainConf;
import org.octopus.core.bean.DomainUser;
import org.octopus.core.bean.User;

public class Octopus {

    private Octopus() {}

    private final static Log log = Logs.get();

    public final static String VERSION = "0.2";

    private static String secretKey = "1234567890";

    private static String godPassword = "123456";

    protected static void setSecretKey(String sKey) {
        secretKey = sKey;
    }

    protected static void setGodPassword(String gpd) {
        godPassword = gpd;
    }

    /**
     * 使用设定的密匙加密内容
     */
    public static String encrypt(String content) {
        return Lang.fixedHexString(reciprocalCipher(secretKey,
                                                    content.getBytes(),
                                                    Cipher.ENCRYPT_MODE));
    }

    private static byte[] reciprocalCipher(String secretKey, byte[] content, int mode) {
        try {
            // DES 需要的密匙的长度为8的倍数，不足的的话左填充「0」来补全，超过的8位将只用前8位
            secretKey = Strings.cutRight(secretKey, 8, '0');
            // 密匙用 sha1 来加密后使用，这样即使被拿到原始的密匙也让人不能一下破解
            secretKey = Lang.sha1(secretKey);
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(secretKey.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(mode, securekey, random);
            return cipher.doFinal(content);
        }
        catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    private static byte[] toHexBytes(String str) {
        byte[] strBytes = str.getBytes();
        int length = strBytes.length;

        byte[] result = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            String temp = new String(strBytes, i, 2);
            result[i / 2] = (byte) Integer.parseInt(temp, 16);
        }
        return result;
    }

    /**
     * 初始化数据库, 生成最基本的域跟域用户
     * 
     * @param dao
     * @param conf
     */
    @SuppressWarnings("unchecked")
    public static void initDatabase(Dao dao, OctopusConfig conf) {
        List<String> tables = conf.getDBTablePkg();
        log.infof("Database : %s", conf.get("db-dbname"));
        log.infof("Tables   : %s", Strings.join(",", tables));
        // 建核心表
        createTableByPKG(dao, "org.octopus.core.bean");
        // 建项目表
        for (String tbPkg : tables) {
            createTableByPKG(dao, tbPkg);
        }
    }

    // 初始化户用户与域
    public static void initUserAndDomain(Dao dao) {
        String godName = "God";
        User god = dao.fetch(User.class, godName);
        if (god == null) {
            god = new User();
            god.setName(godName);
            god.setEmail("god@octopus.com");
            god.setPassword(encrypt(godPassword));
            god.setCreateTime(new Date());
            god.setCreateUser(godName); // 自己创造自己
            god.setEnable(true);
            createUser(dao, god);
        }

        // 初始化admin域
        String adminStr = "admin";
        User admin = dao.fetch(User.class, adminStr);
        if (admin == null) {
            admin = new User();
            admin.setName(adminStr);
            admin.setEmail("admin@octopus.com");
            admin.setPassword(encrypt(godPassword));
            admin.setCreateTime(new Date());
            admin.setCreateUser(god.getName());
            admin.setEnable(true);
            createUser(dao, admin);
        }
        Domain adminDomain = dao.fetch(Domain.class, adminStr);
        if (adminDomain == null) {
            adminDomain = new Domain();
            adminDomain.setName(adminStr);
            adminDomain.setAlias(adminStr);
            adminDomain.setAbout("");

            DomainConf dmnConf = new DomainConf();
            dmnConf.setDomain(adminDomain);
            dmnConf.setManager(admin);
            initDomain(dao, dmnConf);
        }
    }

    public static void createUser(Dao dao, User nUser) {
        dao.insert(nUser);
        Files.createDirIfNoExists(new File(fsHomePath, nUser.getName()));
    }

    // 建表
    public static void createTableByPKG(Dao dao, String pkg) {
        List<Class<?>> clzs = Scans.me().scanPackage(pkg);
        for (Class<?> clz : clzs) {
            Table anTable = clz.getAnnotation(Table.class);
            if (anTable != null) {
                String tbName = anTable.value();
                if (tbName.indexOf("${") != -1) {
                    log.warnf("Class(%s) Has Dynamic-Table-Name(%s), Can't Create Table",
                              clz.getName(),
                              tbName);
                } else {
                    dao.create(clz, false);
                }
            }
        }
    }

    /**
     * 初始化一个Domain, 建立对应的域用户, 普通用户
     * 
     * @param dao
     * @param dmnConf
     */
    public static void initDomain(Dao dao, DomainConf dmnConf) {
        String domainName = dmnConf.getDomain().getName();
        String adminName = dmnConf.getManager().getName();
        // 建立域
        Domain dmn = dao.fetch(Domain.class, Cnd.where("name", "=", domainName));
        if (null == dmn) {
            log.infof("Create Domain [%s] Success", domainName);
            dmn = new Domain();
            dmn.setName(domainName);
            dmn.setAlias(dmnConf.getDomain().getAlias());
            dmn.setAbout(dmnConf.getDomain().getAbout());
            dmn.setCreateTime(new Date());
            dmn.setCreateUser(adminName);
            dao.insert(dmn);
        } else {
            log.warnf("Create Domain [%s] Fail, Domain Existed!", domainName);
        }
        // 管理员加入域中
        DomainUser du = dao.fetch(DomainUser.class,
                                  Cnd.where("domain", "=", domainName).and("user", "=", adminName));
        if (null == du) {
            log.infof("Add Admin[%s] to Domain [%s] Success", adminName, domainName);
            du = new DomainUser();
            du.setDomain(domainName);
            du.setUser(adminName);
            du.setUserType(Keys.DMN_USER_TYPE_ADMIN);
            du.setCreateTime(new Date());
            du.setCreateUser(adminName);
            dao.insert(du);
        } else {
            log.warnf("Add Admin[%s] to Domain [%s] Fail, Domain Existed", adminName, domainName);
        }

        // 普通用户
        List<String> ulist = dmnConf.getUsers();
        if (null != ulist && ulist.size() > 0) {
            for (String user : ulist) {
                User existUser = dao.fetch(User.class, Cnd.where("name", "=", user));
                if (null == existUser) {
                    log.warnf("Add User[%s] to Domain [%s] Fail, User Not Existed",
                              user,
                              domainName);
                } else {
                    DomainUser duser = dao.fetch(DomainUser.class,
                                                 Cnd.where("domain", "=", domainName).and("user",
                                                                                          "=",
                                                                                          user));
                    if (null == duser) {
                        log.infof("Add User[%s] to Domain [%s] Success", user, domainName);
                        duser = new DomainUser();
                        duser.setDomain(domainName);
                        duser.setUser(user);
                        duser.setUserType(Keys.DMN_USER_TYPE_NORMAL);
                        duser.setCreateTime(new Date());
                        duser.setCreateUser(adminName);
                        dao.insert(duser);
                    }
                }
            }
        }
    }

    // 根据id进行转换后得到目录
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

    // 根据用户信息, 返回对应的目录
    public static String userDir(String uname) {
        return "/" + uname;
    }

    // 文件全路径 (某用户名下的某个文件)
    public static String fullPath(String uname, String fid) {
        String fullPath = fsHomePath + userDir(uname) + evalPath(fid);
        if (log.isDebugEnabled()) {
            log.debugf("fullPath : %s", fullPath);
        }
        return fullPath;
    }

    private static String fsHomePath;

    public static void setFsHome(String path) {
        fsHomePath = path;
    }

    public static String getFsHome() {
        return fsHomePath;
    }
}
