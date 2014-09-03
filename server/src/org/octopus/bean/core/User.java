package org.octopus.bean.core;

import java.util.Date;

import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;
import org.octopus.bean.CreateModify;

@Table("t_user")
@TableIndexes({@Index(name = "t_user_phone", fields = {"phone"}, unique = true),
               @Index(name = "t_user_email", fields = {"email"}, unique = true)})
public class User extends CreateModify {

    // emial作为登陆用的主键
    private String email;

    // name跟alias合并, 方便在整个系统中不用同步修改显示问题
    @Name
    private String name;

    // private String alias;

    private String password;

    private String phone;

    // 逻辑上的删除, 默认是true, 一旦设置为false则不用使用了
    private boolean enable;

    private Date lastLogin;

    private String lastIP;

    private String lastDomain;

    public String getLastDomain() {
        return lastDomain;
    }

    public void setLastDomain(String lastDomain) {
        this.lastDomain = lastDomain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastIP() {
        return lastIP;
    }

    public void setLastIP(String lastIP) {
        this.lastIP = lastIP;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}
