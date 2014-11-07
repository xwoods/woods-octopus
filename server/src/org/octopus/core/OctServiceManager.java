package org.octopus.core;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.octopus.core.service.OctService;

/**
 * 设置/获取Service, 查看Service状态
 * 
 * OctServiceManager本身通过 OctServiceManager.ME() 方法获得, 方便在任何类中获得Service
 * 
 * @author pw
 * 
 */
public class OctServiceManager {

    private Log log = Logs.get();

    private OctServiceManager() {}

    private static OctServiceManager me = new OctServiceManager();

    public static OctServiceManager ME() {
        return me;
    }

    public Map<Class<?>, OctService> clz2Service = new ConcurrentHashMap<Class<?>, OctService>();

    /**
     * 注册服务
     * 
     * @param service
     * 
     * @return 是否注册成功
     */
    public boolean registerService(OctService service) {
        Class<?> clz = service.getClass();
        if (clz2Service.containsKey(clz)) {
            log.warnf("Oct-Service %s Has Registered", clz.getName());
            return false;
        } else {
            clz2Service.put(clz, service);
            log.infof("Oct-Service %s Register Success", clz.getName());
            return true;
        }
    }

    /**
     * 获得Clz对应的服务
     * 
     * @param clz
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> clz) {
        OctService service = clz2Service.get(clz);
        if (service != null) {
            return (T) service;
        }
        throw new RuntimeException(String.format("Oct-Service %s Not Found, Please Check registe-Log",
                                                 clz.getName()));
    }

    /**
     * @return 返回所有注册的Service
     */
    public Collection<OctService> allServices() {
        return clz2Service.values();
    }
}
