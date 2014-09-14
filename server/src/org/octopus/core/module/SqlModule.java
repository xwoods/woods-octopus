package org.octopus.core.module;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.web.ajax.Ajax;
import org.nutz.web.ajax.AjaxReturn;

@At("/sql")
@Ok("ajax")
public class SqlModule extends AbstractBaseModule {

    private Log log = Logs.get();

    @At("/select")
    public AjaxReturn execSql(@Param("sql") String sqlstr) {
        final Map<String, Integer> colNmTp = new HashMap<String, Integer>();
        final List<String> colList = new ArrayList<String>();
        log.debugf("Exec Sql : %s", sqlstr);
        final List<Object> resultList = new ArrayList<Object>();
        Sql sql = Sqls.create(sqlstr);
        sql.setCallback(new SqlCallback() {
            @Override
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                // 输出列名
                for (int i = 1; i <= columnCount; i++) {
                    String colnm = metaData.getColumnName(i);
                    int coltp = metaData.getColumnType(i);
                    String coltpStr = metaData.getColumnTypeName(i);
                    log.debugf("Column : %s (%s-%d)", colnm, coltpStr, coltp);
                    colNmTp.put(colnm, coltp);
                    colList.add(colnm);
                }
                while (rs.next()) {
                    NutMap rowData = new NutMap();
                    for (String colnm : colList) {
                        Object colData = null;
                        switch (colNmTp.get(colnm)) {
                        case Types.VARCHAR:
                            colData = rs.getString(colnm);
                            break;
                        case Types.INTEGER:
                            colData = rs.getInt(colnm);
                            break;
                        case Types.BIGINT:
                            colData = rs.getLong(colnm);
                            break;
                        case Types.DATE:
                            colData = rs.getDate(colnm);
                        case Types.FLOAT:
                            colData = rs.getFloat(colnm);
                        case Types.DOUBLE:
                            colData = rs.getDouble(colnm);
                            break;
                        default:
                            break;
                        }
                        rowData.put(colnm, colData);
                    }
                    resultList.add(rowData);
                }
                return null;
            }
        });
        dao.execute(sql);
        return Ajax.ok().setData(resultList);
    }
}
