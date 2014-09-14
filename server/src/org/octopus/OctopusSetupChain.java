package org.octopus;

import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

public class OctopusSetupChain {

    private Log log = Logs.get();

    private List<Setup> setupList;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public OctopusSetupChain(List<String> setupClzList) {
        setupList = new ArrayList<Setup>();
        // 生成所有的setup对象
        for (String clzNm : setupClzList) {
            try {
                Mirror mirror = Mirror.me(Class.forName(clzNm));
                if (mirror.hasInterface(Setup.class)) {
                    Object setupObj = mirror.born();
                    log.infof("add setup [%s] to setup-chain", clzNm);
                    setupList.add((Setup) setupObj);
                }
            }
            catch (Exception e) {
                log.error(e);
            }
        }
    }

    public void initEach(NutConfig nc) {
        for (Setup st : setupList) {
            st.init(nc);
        }
    }

    public void destroyEach(NutConfig nc) {
        for (Setup st : setupList) {
            st.destroy(nc);
        }
    }

}
