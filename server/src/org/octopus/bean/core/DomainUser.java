package org.octopus.bean.core;

import org.nutz.dao.entity.annotation.Table;
import org.octopus.bean.BeanWithTrace;

@Table("t_domain_user")
public class DomainUser extends BeanWithTrace {

    private String domainId;

    private String userId;

    private String userType;

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}
