package org.octopus.core.fs.pathdefine;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.octopus.core.bean.User;
import org.octopus.core.fs.PathDefine;

public class UserPathDefine implements PathDefine {

    public Dao dao;

    public UserPathDefine(Dao dao) {
        this.dao = dao;
    }

    @Override
    public String define(String mkey) {
        User u = dao.fetch(User.class, Cnd.where("name", "=", mkey));
        if (u != null) {
            return u.getName();
        }
        return null;
    }

}
