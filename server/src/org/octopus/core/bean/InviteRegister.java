package org.octopus.core.bean;

import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Table;

@Table("t_invite_register")
public class InviteRegister extends Bean {

    // 用户名
    private String userName;

    // 最终使用注册名称
    private String regName;

    // 先生 or 女士
    private boolean isMale;

    // xxx,xxxx,xxxxx
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    private String domainList;

    // 管理域, 德纳, 凝圣
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    private String domainNameList;

    // 是否已经注册过了
    private boolean hasReg;

    // 邀请注册的人
    private String createUser;

    // 使用时间
    private Date useTime;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean isMale) {
        this.isMale = isMale;
    }

    public String getDomainList() {
        return domainList;
    }

    public void setDomainList(String domainList) {
        this.domainList = domainList;
    }

    public String getDomainNameList() {
        return domainNameList;
    }

    public void setDomainNameList(String domainNameList) {
        this.domainNameList = domainNameList;
    }

    public boolean isHasReg() {
        return hasReg;
    }

    public void setHasReg(boolean hasReg) {
        this.hasReg = hasReg;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getUseTime() {
        return useTime;
    }

    public void setUseTime(Date useTime) {
        this.useTime = useTime;
    }

    public String getRegName() {
        return regName;
    }

    public void setRegName(String regName) {
        this.regName = regName;
    }

}
