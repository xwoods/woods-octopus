package org.octopus.core.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_document_meta")
public class DocumentMeta {

    @Id
    private long id;

    private String name;

    private String valueType;

    private String defaultValue;

    @ColDefine(type = ColType.TEXT)
    private String typeList;

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

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getTypeList() {
        return typeList;
    }

    public void setTypeList(String typeList) {
        this.typeList = typeList;
    }

}
