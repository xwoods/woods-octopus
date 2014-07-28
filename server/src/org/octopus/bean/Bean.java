package org.octopus.bean;

import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.woods.json4excel.annotation.J4EIgnore;

public class Bean {

    public String genID() {
        return org.nutz.lang.random.R.UU16();
    }

    @Name
    @J4EIgnore
    @Prev(els = @EL("$me.genID()"))
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
