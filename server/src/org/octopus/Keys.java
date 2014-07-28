package org.octopus;

public class Keys {

    private Keys() {}

    public static final String SESSION_DOMAIN = "domain";
    public static final String SESSION_USER = "user";
    public static final String SESSION_USER_INFO = "userInfo";
    public static final String RS = "rs";

    // 可以看到管理界面
    public static final String DMN_USER_TYPE_ADMIN = "admin";
    // 普通用户
    public static final String DMN_USER_TYPE_NORMAL = "normal";
    // 客人, 基本来说只有少量的读权限
    public static final String DMN_USER_TYPE_GUEST = "guest";
}
