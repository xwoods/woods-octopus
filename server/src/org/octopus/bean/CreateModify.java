package org.octopus.bean;

import java.util.Date;

import org.woods.json4excel.annotation.J4EIgnore;

public class CreateModify {

    @J4EIgnore
    protected Date createTime;

    @J4EIgnore
    protected String createUser;

    @J4EIgnore
    protected Date modifyTime;

    @J4EIgnore
    protected String modifyUser;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }
}
