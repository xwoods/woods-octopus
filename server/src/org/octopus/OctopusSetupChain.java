package org.octopus;

import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.resource.Scans;

public class OctopusSetupChain {

    private Log log = Logs.get();

    private List<Setup> setupList;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public OctopusSetupChain(List<String> setupClzList) {
        setupList = new ArrayList<Setup>();
        // 生成所有的setup对象
        List<Class<?>> kls = Scans.me().scanPackage("org.octopus", "^.+Setup.class$");
        for (Class<?> klass : kls) {
        	try {
        		if (klass.getName().endsWith("OctopusSetup"))
        			continue;
                Mirror mirror = Mirror.me(klass);
                if (mirror.hasInterface(Setup.class)) {
                    Object setupObj = null;
                    if (klass.getAnnotation(IocBean.class) != null)
                    	setupObj = Mvcs.getIoc().get(klass);
                    else
                    	setupObj = mirror.born();
                    log.infof("add setup [%s] to setup-chain", klass);
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
