package org.octopus.module.core.query;

import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.lang.Strings;
import org.nutz.web.query.CndMaker;

public class DomainCndMaker extends CndMaker {

    @Override
    public void analysisQueryStr(SimpleCriteria sc, String kwd, String... otherQCnd) {
        if (!Strings.isBlank(kwd)) {
            sc.where().and("name", "like", "%" + kwd + "%");
            sc.where().or("alias", "like", "%" + kwd + "%");
        }
    }

}
