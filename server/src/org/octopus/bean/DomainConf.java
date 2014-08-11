package org.octopus.bean;

import java.util.List;

import org.octopus.bean.core.Domain;
import org.octopus.bean.core.User;

/**
 * 根据该配置可以初始化一个域的信息
 * 
 * @author pw
 * 
 */
public class DomainConf {

    private Domain domain;

    private User manager;

    private List<String> users;

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

}
