package org.nutz.web.query;

import org.nutz.dao.Dao;
import org.nutz.lang.util.NutMap;

public class Query {

    public Dao dao;
    public Class<?> clz;
    public NutMap refer;

    // 前台页面传过来的
    public int pgnm;
    public int pgsz;
    public String orderby;
    public boolean asc;
    public String kwd;

    // 其他查询参数
    public String[] otherQCnd;

    public void tableSet(Dao dao, Class<?> clz, NutMap refer) {
        this.dao = dao;
        this.clz = clz;
        this.refer = refer;
    }

    public void cndSet(String... oqc) {
        this.otherQCnd = oqc;
    }
}
