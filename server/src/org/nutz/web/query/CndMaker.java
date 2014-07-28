package org.nutz.web.query;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.lang.Strings;

public abstract class CndMaker {

    public abstract void analysisQueryStr(SimpleCriteria sc, String kwd, String... otherQCnd);

    public Cnd makeQCnd(String orderby, boolean asc, String kwd, String... otherQCnd) {
        SimpleCriteria sc = new SimpleCriteria();
        if (!Strings.isBlank(kwd) || otherQCnd != null) {
            analysisQueryStr(sc, kwd, otherQCnd);
        }
        if (!Strings.isBlank(orderby)) {
            if (asc) {
                sc.asc(orderby);
            } else {
                sc.desc(orderby);
            }
        }
        return Cnd.byCri(sc);
    }

    public QueryResult queryResult(Dao dao,
                                   Class<?> clz,
                                   int pgnm,
                                   int pgsz,
                                   String orderby,
                                   boolean asc,
                                   String kwd,
                                   String... otherQCnd) {
        Cnd qcnd = makeQCnd(orderby, asc, kwd, otherQCnd);
        Pager pager = null;
        if (pgsz > 0) {
            pager = new Pager().setPageNumber(pgnm).setPageSize(pgsz);
            pager.setRecordCount(dao.count(clz, qcnd));
        }
        List<?> qlist = dao.query(clz, qcnd, pager);
        return new QueryResult(qlist, pager);
    }
}
