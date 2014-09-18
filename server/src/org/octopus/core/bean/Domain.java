package org.octopus.core.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;
import org.octopus.core.fs.PathDefine;

@Table("t_domain")
@TableIndexes({@Index(name = "t_domain_alias", fields = {"alias"}, unique = true)})
public class Domain extends CreateModify implements PathDefine {

    @Name
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

    @Override
    public String define() {
        return "/" + name;
    }

}
