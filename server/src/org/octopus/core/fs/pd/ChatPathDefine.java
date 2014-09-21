package org.octopus.core.fs.pd;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.octopus.core.bean.Chat;
import org.octopus.core.fs.PathDefine;

public class ChatPathDefine implements PathDefine {

    public Dao dao;

    public ChatPathDefine(Dao dao) {
        this.dao = dao;
    }

    @Override
    public String define(String mkey) {
        Chat c = dao.fetch(Chat.class, Cnd.where("id", "=", mkey));
        if (c != null) {
            return "" + c.getId();
        }
        return null;
    }

}
