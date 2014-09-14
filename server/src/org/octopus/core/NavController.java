package org.octopus.core;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Dao;
import org.nutz.json.Json;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Disks;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.web.nav.NavItem;
import org.octopus.core.bean.Domain;

public class NavController {

    private NavController() {}

    private static NavController me;

    private Log log = Logs.get();

    private Dao dao;

    // domaim 名称使用小写
    private Map<String, List<NavItem>> domainNavMap = new HashMap<String, List<NavItem>>();

    public static void init(Dao dao) {
        if (me == null) {
            me = new NavController();
            me.dao = dao;
            // 执行初始化操作
            me.updateDomainNav();
        }
    }

    public static NavController me() {
        return me;
    }

    public List<NavItem> getDomainNav(String dmnNm) {
        return domainNavMap.get(dmnNm.toLowerCase());
    }

    public void updateDomainNav() {
        domainNavMap.clear();
        List<Domain> domains = dao.query(Domain.class, null);
        domainNavMap.put("default", Json.fromJsonAsList(NavItem.class, loadNavJS("default")));
        for (Domain domain : domains) {
            String dmnNm = domain.getName().toLowerCase();
            log.infof("Load Domain [%s] Navigation", dmnNm);
            List<NavItem> nList = domainNavMap.get(dmnNm);
            if (nList == null) {
                String navFile = Disks.absolute("nav/" + dmnNm + ".js");
                if (!Strings.isBlank(navFile)) {
                    domainNavMap.put(dmnNm, Json.fromJsonAsList(NavItem.class, loadNavJS(dmnNm)));
                } else {
                    log.infof("Not Find %s.js, So Use default.js", dmnNm);
                    domainNavMap.put(dmnNm, domainNavMap.get("default"));
                }
            }
        }
    }

    private String loadNavJS(String navFileName) {
        try {
            File defaultNavFile = new File(Disks.absolute("nav/" + navFileName + ".js"));
            String defaultNavJS = Streams.readAndClose(new FileReader(defaultNavFile));
            return defaultNavJS;
        }
        catch (Exception e) {
            log.error(e);
            return "[]";
        }
    }
}
