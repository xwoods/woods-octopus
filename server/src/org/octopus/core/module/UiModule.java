package org.octopus.core.module;

import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.web.nav.NavItem;
import org.octopus.core.NavController;

@At("/ui")
public class UiModule extends AbstractBaseModule {

    private Log log = Logs.get();

    @GET
    @At("/lang/?")
    @Ok(">>:${obj}")
    public String setLanguage(String lang, HttpServletRequest req) {
        String refer = req.getHeader("Referer");
        if (!Strings.isBlank(lang)) {
            log.infof("UI Lang Change To : %s", lang);
            Mvcs.setLocalizationKey(lang);
            Mvcs.updateRequestAttributes(req);
        }
        return refer;
    }

    @GET
    @At("/nav/reload")
    @Ok(">>:${obj}")
    public String resetNav(HttpServletRequest req) {
        String refer = req.getHeader("Referer");
        NavController.me().updateDomainNav();
        return refer;
    }

    @At("/nav/get/?")
    @Ok("Ajax")
    public List<NavItem> getNav(String dmnName) {
        if (Strings.isBlank(dmnName)) {
            return null;
        }
        return NavController.me().getDomainNav(dmnName);
    }

    private String routeContent;

    @At("/route")
    @Ok("raw")
    public String webRoute() {
        if (Strings.isBlank(routeContent)) {
            routeContent = Streams.readAndClose(new InputStreamReader(this.getClass()
                                                                          .getResourceAsStream("/route.js")));
        }
        return routeContent;
    }

}
