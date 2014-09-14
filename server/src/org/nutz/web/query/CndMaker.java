package org.nutz.web.query;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.QueryResult;
import org.nutz.dao.TableName;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

public abstract class CndMaker {

    private static Cnd makeQCnd(QueryStr qs2c,
                                String orderby,
                                boolean asc,
                                String kwd,
                                String[] otherQCnd) {
        if (otherQCnd != null) {
            List<String> realotherQCnd = new ArrayList<String>();
            for (String otcnd : otherQCnd) {
                if (!Strings.isBlank(otcnd)) {
                    realotherQCnd.add(otcnd);
                }
            }
            if (otherQCnd.length != realotherQCnd.size()) {
                otherQCnd = realotherQCnd.toArray(new String[0]);
            }
        }
        SimpleCriteria sc = new SimpleCriteria();
        if (!Strings.isBlank(kwd) || otherQCnd != null) {
            qs2c.analysisQueryStr(sc, kwd, otherQCnd);
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

    public static QueryResult queryResult(QueryStr qs2c, Query q) {
        return queryResult(qs2c,
                           q.dao,
                           q.clz,
                           q.refer,
                           q.pgnm,
                           q.pgsz,
                           q.orderby,
                           q.asc,
                           q.kwd,
                           q.otherQCnd);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static QueryResult queryResult(QueryStr qs2c,
                                          final Dao dao,
                                          final Class<?> clz,
                                          NutMap refer,
                                          int pgnm,
                                          int pgsz,
                                          String orderby,
                                          boolean asc,
                                          String kwd,
                                          String... otherQCnd) {
        final Cnd qcnd = makeQCnd(qs2c, orderby, asc, kwd, otherQCnd);
        final Pager pager = new Pager().setPageNumber(pgnm).setPageSize(pgsz);
        final List qlist = new ArrayList();
        if (pgsz > 0) {
            if (refer != null) {
                TableName.run(refer, new Runnable() {
                    @Override
                    public void run() {
                        pager.setRecordCount(dao.count(clz, qcnd));
                    }
                });
            } else {
                pager.setRecordCount(dao.count(clz, qcnd));
            }
        }
        if (refer != null) {
            TableName.run(refer, new Runnable() {
                public void run() {
                    List qreList = dao.query(clz, qcnd, pager);
                    qlist.addAll(qreList);
                }
            });
        } else {
            List<?> qreList = dao.query(clz, qcnd, pager);
            qlist.addAll(qreList);
        }
        return new QueryResult(qlist, pager);
    }
}
