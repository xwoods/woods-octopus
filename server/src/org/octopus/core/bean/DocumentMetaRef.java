package org.octopus.core.bean;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_document_meta_ref")
public class DocumentMetaRef {

    @Id
    private long id;

    private String typeName;

    private String metaName;

    // 可以设置自己的特殊默认值
    private String spDfValue;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getMetaName() {
        return metaName;
    }

    public void setMetaName(String metaName) {
        this.metaName = metaName;
    }

    public String getSpDfValue() {
        return spDfValue;
    }

    public void setSpDfValue(String spDfValue) {
        this.spDfValue = spDfValue;
    }

}
