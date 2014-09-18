package org.octopus.core.bean;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.octopus.core.fs.PathDefine;

@Table("t_chat")
public class Chat implements PathDefine {

    @Id
    private long id;

    private String name;
    // 如果属于某个域, 那就只有该域的人可以加入该chat
    private String domain;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String define() {
        return "/" + id;
    }

}
