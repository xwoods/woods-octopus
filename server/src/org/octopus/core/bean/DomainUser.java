package org.octopus.core.bean;

import org.nutz.dao.entity.annotation.Table;

@Table("t_domain_user")
public class DomainUser extends BeanCreateModify {

    private String domain;

    private String user;

    private String userType;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}
