package org.nutz.web.nav;

import java.util.List;

public class NavItem {
    private String url;
    private String name;
    private String icon;
    private List<NavItem> subNav;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<NavItem> getSubNav() {
        return subNav;
    }

    public void setSubNav(List<NavItem> subNav) {
        this.subNav = subNav;
    }

}
