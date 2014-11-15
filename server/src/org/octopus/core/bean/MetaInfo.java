package org.octopus.core.bean;

import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

public class MetaInfo {

    private Map<String, Object> data;

    private boolean changed;

    public MetaInfo(String mstr) {
        data = Json.fromJsonAsMap(Object.class, mstr);
    }

    public boolean isChanged() {
        return changed;
    }

    public boolean exist(String name) {
        return data.containsKey(name);
    }

    public void set(String name, Object val) {
        data.put(name, val);
        changed = true;
    }

    private Object _get(String name) {
        return data.get(name);
    }

    public int getInt(String name) {
        if (_get(name) != null) {
            try {
                return Castors.me().castTo(_get(name), Integer.class);
            }
            catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    public String getString(String name) {
        if (_get(name) != null) {
            return String.valueOf(_get(name));
        }
        return "";
    }

    @Override
    public String toString() {
        return Json.toJson(data, JsonFormat.compact());
    }
}
