<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="_setup.jsp" %>
<!DOCTYPE html>
<head lang="en">
    <meta charset="UTF-8">
    <title>${msg['page.main.title']}</title>
    <link rel="shortcut icon" href="${rs}/core/img/favicon.ico"/>
    <link rel="stylesheet" href="${rs}/core/css/font-awesome.css">
    <link rel="stylesheet" href="${rs}/core/css/reset.css"/>
    <link rel="stylesheet" href="${rs}/core/css/octopus.css">

    <link rel="stylesheet" href="${rs}/core/css/zui/zui.grid.css">

    <link rel="stylesheet" href="${rs}/core/css/module/main.css">
    <link rel="stylesheet" href="${rs}/core/css/module/me.css">
    <link rel="stylesheet" href="${rs}/core/css/module/users.css">
    <link rel="stylesheet" href="${rs}/core/css/module/setting.css">
</head>
<body dmnNm="${domain.name}" ng-app="mainApp">
<%@include file="_msg.jsp" %>
<div class="header">
    <span>Oct</span><img class="logo" src="${rs}/core/img/octopus-logo.png"><span>pus</span>
    <ol class="header-menu">
        <li class="header-module">
            <i class="fa fa-tasks fa-2x header-icon"><span class="check-list-tip">20</span></i>
            <ul class="sub-menu">
                <li class="title">${msg['page.header.task']}</li>
                <li class="bottom-bar">
                    <span>${msg['page.header.checkall']}</span><i class="fa fa-chevron-circle-right fa-1x"></i>
                </li>
            </ul>
        </li>
        <li class="header-module">
            <i class="fa fa-exclamation-circle fa-2x header-icon"><span class="check-list-tip">20</span></i>
            <ul class="sub-menu">
                <li class="title">${msg['page.header.notice']}</li>
                <li class="bottom-bar">
                    <span>${msg['page.header.checkall']}</span><i class="fa fa-chevron-circle-right fa-1x"></i>
                </li>
            </ul>
        </li>
        <li class="header-module">
            <i class="fa fa-comments fa-2x header-icon"><span class="check-list-tip">20</span></i>
            <ul class="sub-menu">
                <li class="title">${msg['page.header.message']}</li>
                <li class="bottom-bar">
                    <span>${msg['page.header.checkall']}</span><i class="fa fa-chevron-circle-right fa-1x"></i>
                </li>
            </ul>
        </li>
        <li class="header-module hm-user">
            <img src="${rs}/core/img/face/${userInfo.face}" class="user-face">
            <p class="user-name">${msg[user.alias]}</p>
            <i class="fa fa-lg fa-angle-down"></i>
            <ul class="sub-menu">
                <li>
                    <i class="fa fa-lg fa-user"></i>
                    <span>${msg['page.header.user.profile']}</span>
                </li>
                <li>
                    <i class="fa fa-lg fa-eye"></i>
                    <span>${msg['page.header.user.permission']}</span>
                </li>
                <li class="divider"></li>
                <li>
                    <i class="fa fa-lg fa-unlock-alt"></i>
                    <span>${msg['page.header.user.lockscreen']}</span>
                </li>
                <li id="logout">
                    <i class="fa fa-lg fa-sign-out"></i>
                    <span>${msg['page.header.user.logout']}</span>
                </li>
            </ul>
        </li>
    </ol>
</div>
<div class="header-back"></div>
<div class="main-container">
    <div class="main-nav">
        <div class="nav-menu">
        </div>
        <ul class="nav-list">
        </ul>
    </div>
    <div class="main-content">
        <div class="container">
            <div class="row">
                <ol class="crumb">
                    <li class="crumb-first">
                        <i class="fa fa-home fa-lg"></i>
                        <span>${msg[domain.alias]}</span>
                    </li>
                    <li>
                        <span>${msg['page.main.nav.me']}</span>
                    </li>
                </ol>
            </div>
            <div class="row">
                <div ng-view></div>
            </div>
        </div>
    </div>
</div>
<div class="footer">
    <span>© 2012-2014 XWoods. All rights reserved Powered by <a href="http://nutzam.com">Nutz-Project</a></span>
</div>
<div id="before-load-ready">
    <div class="loading-tip">
        <i class="fa fa-refresh fa-spin-1s fa-3x"></i>
        <span>${msg['page.loading.tip']}</span>
    </div>
</div>
<!-- 库 -->
<script language="JavaScript" src="${rs}/core/js/jquery.js"></script>
<script language="JavaScript" src="${rs}/core/js/jquery-ui.js"></script>
<script language="JavaScript" src="${rs}/core/js/handlebars.js"></script>
<script language="JavaScript" src="${rs}/core/js/zlib/core.js"></script>
<script language="JavaScript" src="${rs}/core/js/zlib/log.js"></script>
<script language="JavaScript" src="${rs}/core/js/zlib/err.js"></script>
<script language="JavaScript" src="${rs}/core/js/zlib/browser.js"></script>
<script language="JavaScript" src="${rs}/core/js/zlib/http.js"></script>
<script language="JavaScript" src="${rs}/core/js/underscore.js"></script>
<script language="JavaScript" src="${rs}/core/js/angular.js"></script>
<script language="JavaScript" src="${rs}/core/js/angular-route.js"></script>
<!-- 控件 -->
<script language="JavaScript" src="${rs}/core/js/zui/zui.grid.js"></script>
<!-- module -->
<script language="JavaScript" src="${rs}/module/route.js"></script>
<script language="JavaScript" src="${rs}/core/js/module/main.js"></script>
<script language="JavaScript" src="${rs}/core/js/module/me.js"></script>
<script language="JavaScript" src="${rs}/core/js/module/users.js"></script>
<script language="JavaScript" src="${rs}/core/js/module/setting.js"></script>
</body>
</html>