package org.octopus;

import java.io.InputStreamReader;
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
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;
import org.octopus.bean.DomainConf;
import org.octopus.bean.core.Domain;
import org.octopus.bean.core.DomainUser;
import org.octopus.bean.core.User;

public class Octopus {

    private Octopus() {}

    private final static Log log = Logs.get();

    public final static String VERSION = "0.0.1";

    private static String secretKey = "1234567890";

    protected static void setSecretKey(String sKey) {
        secretKey = sKey;
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
    public static void initDatabase(Dao dao, OctopusConfig conf) {
        String[] tables = Strings.splitIgnoreBlank(conf.get("$db-table-pkg"), "\n");
        log.infof("Database : %s", conf.get("db-dbname"));
        log.infof("Tables   : %s", Strings.join(",", tables));

        // 建数据库
        // FIXME 这里暂时仅仅测试了mysql
        // Sql createDatabase = Sqls.create("create database " +
        // conf.get("db-dbname"));
        // dao.execute(createDatabase);

        // 建核心表
        createTableByPKG(dao, "org.octopus.bean.core");
        // 建项目表
        for (String tbPkg : tables) {
            createTableByPKG(dao, tbPkg);
        }

        // 默认域
        DomainConf adminDmnConf = Json.fromJson(DomainConf.class,
                                                new InputStreamReader(Octopus.class.getResourceAsStream("/dmn/admin.js")));
        initDomain(dao, adminDmnConf);
    }

    // 建表
    public static void createTableByPKG(Dao dao, String pkg) {
        List<Class<?>> clzs = Scans.me().scanPackage(pkg);
        for (Class<?> clz : clzs) {
            Table anTable = clz.getAnnotation(Table.class);
            if (anTable != null) {
                String tbName = anTable.value();
                if (tbName.indexOf("${") != -1) {
                    log.warnf("Find Class(%s) has Dynamic-Table-Name(%s), Can't Create Table",
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
        Domain dmn = dao.fetch(Domain.class, Cnd.where("name", "=", dmnConf.getDomain().getName()));
        if (null == dmn) {
            dmn = new Domain();
            dmn.setName(dmnConf.getDomain().getName());
            dmn.setAlias(dmnConf.getDomain().getAlias());
            dmn.setAbout(dmnConf.getDomain().getAbout());
            dmn.setCreateTime(new Date());
            dmn.setCreateUser("God");
            dao.insert(dmn);
        }
        String superUserPassword = encrypt(dmnConf.getManager().getPassword());
        User admin = dao.fetch(User.class, Cnd.where("name", "=", dmnConf.getDomain().getName()));
        if (null == admin) {
            admin = new User();
            admin.setName(dmnConf.getDomain().getName());
            admin.setAlias(dmnConf.getManager().getAlias());
            admin.setPassword(superUserPassword);
            admin.setCreateTime(new Date());
            admin.setCreateUser("God");
            admin.setEnable(true);
            dao.insert(admin);
        }
        DomainUser du = dao.fetch(DomainUser.class,
                                  Cnd.where("domainId", "=", dmn.getId()).and("userId",
                                                                              "=",
                                                                              admin.getId()));
        if (null == du) {
            du = new DomainUser();
            du.setDomainId(dmn.getId());
            du.setUserId(admin.getId());
            du.setUserType(Keys.DMN_USER_TYPE_ADMIN);
            du.setCreateTime(new Date());
            du.setCreateUser("God");
            dao.insert(du);
        }

        // 普通用户
        List<User> ulist = dmnConf.getUsers();
        if (null != ulist && ulist.size() > 0) {
            for (User user : ulist) {
                User existUser = dao.fetch(User.class, Cnd.where("name", "=", user.getName()));
                if (null == existUser) {
                    user.setPassword(encrypt("123456"));
                    user.setCreateTime(new Date());
                    user.setCreateUser(admin.getId());
                    user.setEnable(true);
                    dao.insert(user);

                    DomainUser duser = dao.fetch(DomainUser.class,
                                                 Cnd.where("domainId", "=", dmn.getId())
                                                    .and("userId", "=", user.getId()));
                    if (null == duser) {
                        duser = new DomainUser();
                        duser.setDomainId(dmn.getId());
                        duser.setUserId(user.getId());
                        duser.setUserType(Keys.DMN_USER_TYPE_NORMAL);
                        duser.setCreateTime(new Date());
                        duser.setCreateUser(admin.getId());
                        dao.insert(duser);
                    }
                } else {
                    log.warnf("User [%s] Existed!", user.getName());
                    if (dmnConf.isCrossDomain()) {
                        DomainUser duser = dao.fetch(DomainUser.class,
                                                     Cnd.where("domainId", "=", dmn.getId())
                                                        .and("userId", "=", existUser.getId()));
                        if (null == duser) {
                            duser = new DomainUser();
                            duser.setDomainId(dmn.getId());
                            duser.setUserId(existUser.getId());
                            duser.setUserType(Keys.DMN_USER_TYPE_NORMAL);
                            duser.setCreateTime(new Date());
                            duser.setCreateUser(admin.getId());
                            dao.insert(duser);
                        }
                    }
                }
            }
        }
    }
}
