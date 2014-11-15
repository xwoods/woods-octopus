package org.octopus;

import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.annotation.Views;
import org.nutz.mvc.ioc.provider.ComboIocProvider;
import org.nutz.web.ajax.AjaxViewMaker;
import org.nutz.web.error.ErrPageViewMaker;

@Modules(scanPackage = true)
@Localization(value = "msg", defaultLocalizationKey = "zh-CN")
@IocBy(type = ComboIocProvider.class, args = {"*js",
                                              "ioc/",
                                              "*annotation",
                                              "org.octopus"})
@SetupBy(OctopusSetup.class)
@Views(value = {AjaxViewMaker.class, ErrPageViewMaker.class})
@Fail("errpage")
public class OctopusMainModule {}
