package org.octopus.module.core.query;

import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.lang.Strings;
import org.nutz.web.query.CndMaker;

public class IssueCndMaker extends CndMaker {

    @Override
    public void analysisQueryStr(SimpleCriteria sc, String kwd, String... otherQCnd) {
        if (otherQCnd != null && otherQCnd.length >= 1) {
            sc.where().andEquals("domain", otherQCnd[0]);
        }
        if (otherQCnd != null && otherQCnd.length >= 2) {
            sc.where().andEquals("createUser", otherQCnd[1]);
        }
        if (!Strings.isBlank(kwd)) {
            sc.where().orEquals("content", kwd);
        }
    }

}
