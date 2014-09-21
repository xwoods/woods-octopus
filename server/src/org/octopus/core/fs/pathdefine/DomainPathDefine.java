package org.octopus.core.fs.pathdefine;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.octopus.core.bean.Domain;
import org.octopus.core.fs.PathDefine;

public class DomainPathDefine implements PathDefine {

    public Dao dao;

    public DomainPathDefine(Dao dao) {
        this.dao = dao;
    }

    @Override
    public String define(String mkey) {
        Domain d = dao.fetch(Domain.class, Cnd.where("name", "=", mkey));
        if (d != null) {
            return d.getName();
        }
        return null;
    }

}
