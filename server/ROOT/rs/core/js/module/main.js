/**
 * Created by pw on 14-7-16.
 */

var isDebug = false;

function afterLoadReady() {
    var blr = $("#before-load-ready");
    if (!isDebug && blr.length > 0) {
        setTimeout(function () {
            // blr.animate({top: -1 * blr.height()}, 300, function(){
            blr.animate({opacity: 0}, 300, function () {
                blr.remove();
            });
        }, 1200);
    }
}

var tmpl_li = '';
tmpl_li += '<li>';
tmpl_li += '    <a href="#{{url}}">';
tmpl_li += '        <i class="fa {{icon}} fa-lg"></i>';
tmpl_li += '        <span class="module-title">{{name}}</span>';
tmpl_li += '    </a>';
tmpl_li += '</li>';

var tmpl_li_sub = '';
tmpl_li_sub += '<li class="has-sub">';
tmpl_li_sub += '    <a href="javascript:void(0)">';
tmpl_li_sub += '        <i class="fa {{icon}} fa-lg"></i>';
tmpl_li_sub += '        <span class="module-title">{{name}}</span>';
tmpl_li_sub += '        <i class="fa fa-lg subnav-switch"></i>';
tmpl_li_sub += '    </a>';
tmpl_li_sub += '    <ul class="subnav-list">';
tmpl_li_sub += '        {{#each subNav}}';
tmpl_li_sub += '        <li>';
tmpl_li_sub += '            <a href="#{{url}}">';
tmpl_li_sub += '                <span>{{name}}</span>';
tmpl_li_sub += '            </a>';
tmpl_li_sub += '        </li>';
tmpl_li_sub += '        {{/each}}';
tmpl_li_sub += '    </ul>';
tmpl_li_sub += '</li>';

function handlebarsHtml(tmpl, content) {
    var template = Handlebars.compile(tmpl);
    return template(content);
}

function navHtml(nc) {
    var nhtml = '';
    for (var i = 0; i < nc.length; i++) {
        nhtml += handlebarsHtml(nc[i].subNav ? tmpl_li_sub : tmpl_li, nc[i]);
    }
    return nhtml;
}

function navConf(nc) {
    for (var i = 0; i < nc.length; i++) {
        var ni = nc[i];
        ni.name = $z.msg(ni.name);
        navUrlNameMap["" + ni.url] = ni.name;
        if (ni.subNav) {
            $.each(ni.subNav, function (i, ele) {
                ele.name = $z.msg(ele.name);
                navUrlNameMap["" + ele.url] = ele.name;
            });
        }
    }
}

window.namap = {};

window.getAlias = function (name) {
    var alias = window.namap[name];
    if ($z.util.isBlank(alias)) {
        return name;
    }
    return alias;
}

var navUrlNameMap = {};

var tmpl_crumb_li = '<li><span>{{value}}</span></li>';

function crumbHtml(route) {
    var rlist = [];
    var rs = route.substr(1).split('/');
    var lastPath = "/";
    for (var i = 0; i < rs.length; i++) {
        var r = {key: lastPath + rs[i]};
        r.value = navUrlNameMap[r.key];
        rlist.push(r);
        lastPath += rs[i] + "/";
    }
    var chtml = '';
    for (var i = 0; i < rlist.length; i++) {
        chtml += handlebarsHtml(tmpl_crumb_li, rlist[i]);
    }
    return chtml;
}

$(document).ready(function () {

    if (isDebug) {
        var blr = $("#before-load-ready");
        blr.remove();
    }

    $z.initMsg();

    $z.http.get('/user/na', function (re) {
        window.namap = re.data;
        console.log("Map Name-Alias Ready");
    });

    // 调整.container的min-height
    var containerJq = $('.container');
    var winHeight = $z.browser.winsz().height;
    var headerHeight = $('.header').outerHeight();
    var footerHeight = $('.footer').outerHeight();
    console.log("win : " + winHeight + ", header : " + headerHeight + ", footer : " + footerHeight);
    containerJq.css({
        'min-height': winHeight - headerHeight - footerHeight
    });

    // 基本信息
    var dmnNm = $(document.body).attr('dmnNm');

    var mainContainer = $('.main-container');

    // 侧边导航栏
    var navListJq = $('.nav-list');
    var crumbJq = $('.crumb');

    mainContainer.delegate('.main-nav .nav-list a', 'click', function () {
        var clkA = $(this);
        var clkLi = clkA.parent();
        var clkUl = clkLi.parent();
        var hasSub = clkLi.hasClass('has-sub');
        var isSub = clkUl.hasClass('subnav-list');

        // 非mini模式
        if (!mainContainer.hasClass('mini')) {
            // 一级餐单, 那就把其他的关闭
            if (!isSub) {
                clkLi.siblings('.has-sub').removeClass('open');
            }
            if (hasSub) {
                clkLi.toggleClass('open');
            }
        }

        if (!clkLi.hasClass('active')) {
            if (!isSub) {
                if (!hasSub) {
                    navListJq.find('li.active').removeClass('active');
                    clkLi.addClass('active');
                }
            } else {
                navListJq.find('li.active').removeClass('active');
                clkUl.parent().addClass('active');
                clkLi.addClass('active');
            }
        }

        // 切换页面信息
        var route = clkA.attr('href').substr(1);
        if (!hasSub) {
            var ch = crumbHtml(route);
            crumbJq.find('li:not(.crumb-first)').remove();
            crumbJq.append(ch);
        }
    });

    // 切换mini
    var navMenu = $('.nav-menu');
    navMenu.delegate(".nav-mini-toogle", 'click', function () {
        navListJq.find('.has-sub.open').removeClass('open');
        var tbtn = $(this);
        if (tbtn.hasClass("mode-mini")) {
            tbtn.removeClass('mode-mini');
            tbtn.removeClass('fa-angle-double-right');
            tbtn.addClass('fa-angle-double-left');
            mainContainer.removeClass("mini");
        } else {
            tbtn.addClass('mode-mini');
            tbtn.removeClass('fa-angle-double-left');
            tbtn.addClass('fa-angle-double-right');
            mainContainer.addClass("mini");
        }
    });

    // 加载后台nav配置
    $z.http.get("/ui/nav/get/" + dmnNm, function (re) {
        // 加载nav
        var nc = re.data;
        navConf(nc);
        navListJq.append(navHtml(nc));
        // 判断url中的#后面的参数
        var route = $z.http.urlAfter();
        if (route) {
            var clkA = navListJq.find('a[href="#' + route + '"]');
            console.log("find route : " + route + ", has " + clkA.length);
            if (clkA.length > 0 && !clkA.parent().hasClass('has-sub')) {
                clkA.find('span').click();
            } else {
                navListJq.find('a').first().find('span').click();
            }
        } else {
            navListJq.find('a').first().find('span').click();
        }
        // 去掉loading
        afterLoadReady();
    });

    $(document.body).find('#logout').on('click', function () {
        $z.http.get('/user/logout', function () {
            window.location.href = "/login";
        });
    });

});

// 控制module跳转
var coreRoute = [
    {
        url: '/me',
        page: '/core/me.jsp',
        ctrl: 'MeCtl'
    },
    {
        url: '/users',
        page: '/core/users.jsp',
        ctrl: 'UsersCtl'
    },
    {
        url: '/setting',
        page: '/core/setting.jsp',
        ctrl: 'SettingCtl'
    },
    {
        url: '/domains',
        page: '/core/domains.jsp',
        ctrl: 'DomainsCtl'
    },
    {
        url: '/issue',
        page: '/core/issue.jsp',
        ctrl: 'IssueCtl'
    },
    {
        url: '/releaseNote',
        page: '/core/release.jsp',
        ctrl: 'ReleaseCtl'
    },
    {
        url: '/notImpl',
        page: '/core/notImpl.jsp',
        ctrl: 'NoImplCtl'
    }
];

var mainApp = angular.module('mainApp', ['ngRoute']);

mainApp.controller('NoImplCtl', function ($scope) {
});

mainApp.config(['$routeProvider', function ($routeProvider) {
    var routeList = [].concat(coreRoute).concat(appRoute);
    for (var i = 0; i < routeList.length; i++) {
        var route = routeList[i];
        console.log("reg route : " + route.url);
        $routeProvider.when(route.url, {
            templateUrl: "views" + route.page,
            controller: route.ctrl
        });
    }
    $routeProvider.otherwise({redirectTo: '/notImpl'});
}]);


