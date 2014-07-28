package org.octopus.module.core;

import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.web.fliter.CheckLogin;
import org.nutz.web.fliter.CheckNotLogin;
import org.octopus.Keys;
import org.octopus.module.AbstractBaseModule;

public class PageModule extends AbstractBaseModule {

    @Filters(@By(type = CheckLogin.class, args = {Keys.SESSION_USER, "/browser"}))
    @At({"/", "/index", "/home", "/login"})
    @Ok("jsp:jsp.login")
    public void login() {}

    @Filters(@By(type = CheckNotLogin.class, args = {Keys.SESSION_USER, "/login"}))
    @At({"/browser"})
    @Ok("jsp:jsp.main")
    public void main() {}

}
