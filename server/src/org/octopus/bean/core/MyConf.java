package org.octopus.bean.core;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;
import org.octopus.bean.Bean;

@Table("t_myconf")
@TableIndexes({@Index(name = "t_myconf_owner", fields = {"owner"}, unique = false),
               @Index(name = "t_myconf_type", fields = {"type"}, unique = false)})
public class MyConf extends Bean {

    // 所有者
    private String owner;

    // 配置类型
    private String type;

    // 配置内容
    @ColDefine(type = ColType.TEXT)
    private String content;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
