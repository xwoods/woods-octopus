/**
 * Created by pw on 14-7-16.
 */

var isDebug = false;

function afterLoadReady() {
    var blr = $("#before-load-ready");
    if (isDebug) {
        blr.remove();
    } else {
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
tmpl_li += '        <span>{{name}}</span>';
tmpl_li += '    </a>';
tmpl_li += '</li>';

var tmpl_li_sub = '';
tmpl_li_sub += '<li class="has-sub">';
tmpl_li_sub += '    <a href="javascrpt:void(0)">';
tmpl_li_sub += '        <i class="fa {{icon}} fa-lg"></i>';
tmpl_li_sub += '        <span>{{name}}</span>';
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

    $z.initMsg();

    // 调整.container的min-height
    var containerJq = $('.container');
    var winsz = $z.browser.winsz();
    containerJq.css({
        'min-height': winsz.height - 90
    });

    // 基本信息
    var dmnNm = $(document.body).attr('dmnNm');

    // 侧边导航栏
    var navListJq = $('.nav-list');
    var crumbJq = $('.crumb');
    navListJq.delegate('a', 'click', function () {
        var clkA = $(this);
        var clkLi = clkA.parent();
        var clkUl = clkLi.parent();
        var hasSub = clkLi.hasClass('has-sub');
        var isSub = clkUl.hasClass('subnav-list');
        // 一级餐单, 那就把其他的关闭
        if (!isSub) {
            clkLi.siblings('.has-sub').removeClass('open');
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
        if (hasSub) {
            clkLi.toggleClass('open');
        }

        // 切换页面信息
        var route = clkA.attr('href').substr(1);
        if (!hasSub) {
            var ch = crumbHtml(route);
            crumbJq.find('li:not(.crumb-first)').remove();
            crumbJq.append(ch);
        }
    });

    // 顶部导航栏
    var headMenu = $('.header-menu');
    headMenu.delegate('li.header-module', 'click', function (e) {
        var cliJq = $(this);
        if (cliJq.hasClass('open')) {
            cliJq.removeClass('open');
        } else {
            cliJq.siblings().removeClass('open');
            cliJq.addClass('open');
        }
        e.stopPropagation();
        $(document.body).one('click', function () {
            headMenu.find('li.header-module.open').removeClass('open');
        });
    });

    headMenu.delegate('ul.sub-menu', 'click', function (e) {
        // 防止上层被触发click事件
        e.stopPropagation();
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

});

// 控制module跳转

var routeApp = angular.module('routeApp', ['ngRoute']);

routeApp.controller('CurUserCtl', function ($scope) {
    $scope.quit = function () {
        $z.http.get('/user/logout', function () {
            window.location.href = "/login";
        });
    }
});

routeApp.controller('NoImplCtl', function ($scope) {

});

routeApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider
        .when('/me', {
            templateUrl: 'views/me.jsp',
            controller: 'MeCtl'
        })
        .when('/users', {
            templateUrl: 'views/users.jsp',
            controller: 'UsersCtl'
        })
        .when('/setting', {
            templateUrl: 'views/setting.jsp',
            controller: 'SettingCtl'
        })
        .when('/storage/dataimport', {
            templateUrl: 'views/storage/import.jsp',
            controller: 'ImportCtl'
        })
        .when('/storage/storage', {
            templateUrl: 'views/storage/storage.jsp',
            controller: 'StorageCtl'
        })
        .when('/storage/storageTotal', {
            templateUrl: 'views/storage/storageTotal.jsp',
            controller: 'StorageTotalCtl'
        })
        .when('/storage/material', {
            templateUrl: 'views/storage/material.jsp',
            controller: 'MaterialCtl'
        })
        .when('/storage/storageInOut', {
            templateUrl: 'views/storage/storageInOut.jsp',
            controller: 'StorageInOutCtl'
        })
        .when('/noimpl', {
            templateUrl: 'views/noimpl.jsp',
            controller: 'NoImplCtl'
        })
        .otherwise({redirectTo: '/noimpl'})
}]);
