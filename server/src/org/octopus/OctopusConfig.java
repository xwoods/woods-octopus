package org.octopus;

import java.util.List;

import org.nutz.web.WebConfig;

public class OctopusConfig extends WebConfig {

    public OctopusConfig(String path) {
        super(path);
    }

    public String getFSHome() {
        return get("fs-home");
    }

    public List<String> getDBTablePkg() {
        return getList("$db-table-pkg");
    }

    public List<String> getSetupChain() {
        return getList("$setup-chain");
    }

}
