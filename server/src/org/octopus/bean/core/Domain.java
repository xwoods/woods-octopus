package org.octopus.bean.core;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Table;
import org.octopus.bean.BeanWithTrace;

@Table("t_domain")
public class Domain extends BeanWithTrace {

    private String name;

    private String alias;

    @ColDefine(width = 1024)
    private String about;

    @ColDefine(width = 1024)
    private String home;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

}
