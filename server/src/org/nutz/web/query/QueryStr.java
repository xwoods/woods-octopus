package org.nutz.web.query;

import org.nutz.dao.util.cri.SimpleCriteria;

public interface QueryStr {

    void analysisQueryStr(SimpleCriteria sc, String kwd, String... otherQCnd);
}
