package org.octopus.core.bean;

import org.nutz.castor.Castors;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.octopus.core.fs.MetaType;

@Table("t_document_meta")
public class DocumentMeta {

    @Name
    private String name;

    private MetaType type;

    private String dfValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MetaType getType() {
        return type;
    }

    public void setType(MetaType type) {
        this.type = type;
    }

    public String getDfValue() {
        return dfValue;
    }

    public void setDfValue(String dfValue) {
        this.dfValue = dfValue;
    }

    public Object value(String val) {
        return transValue(val == null ? dfValue : val);
    }

    private Object transValue(String val) {
        Object rval = null;
        switch (type) {
        case INT:
            rval = Castors.me().cast(val, String.class, Integer.class);
            break;
        case BOOL:
            rval = Castors.me().cast(val, String.class, Boolean.class);
            break;
        case FLOAT:
            rval = Castors.me().cast(val, String.class, Float.class);
            break;
        case DOUBLE:
            rval = Castors.me().cast(val, String.class, Double.class);
            break;
        default:
            // string
            rval = val;
            break;
        }
        return rval;
    }
}
