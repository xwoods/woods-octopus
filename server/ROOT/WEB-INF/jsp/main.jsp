<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="_setup.jsp" %>
<!DOCTYPE html>
<head lang="en">
    <meta charset="UTF-8">
    <link rel="shortcut icon" href="${rs}/core/img/favicon.ico"/>
    <title>${msg['page.main.title']}</title>

    <!-- ############ 依赖 ############ -->
    <link rel="stylesheet" href="${rs}/deps/font-awesome/css/font-awesome.css">

    <script language="JavaScript" src="${rs}/deps/jquery.js"></script>
    <script language="JavaScript" src="${rs}/deps/jquery-ui.js"></script>

    <script language="JavaScript" src="${rs}/deps/handlebars.js"></script>

    <script language="JavaScript" src="${rs}/deps/zlib/core.js"></script>
    <script language="JavaScript" src="${rs}/deps/zlib/log.js"></script>
    <script language="JavaScript" src="${rs}/deps/zlib/err.js"></script>
    <script language="JavaScript" src="${rs}/deps/zlib/browser.js"></script>
    <script language="JavaScript" src="${rs}/deps/zlib/http.js"></script>

    <script language="JavaScript" src="${rs}/deps/zui/zui.grid.js"></script>
    <link rel="stylesheet" href="${rs}/deps/zui/zui.grid.css">

    <script language="JavaScript" src="${rs}/deps/underscore.js"></script>
    <script language="JavaScript" src="${rs}/deps/angular/angular.js"></script>
    <script language="JavaScript" src="${rs}/deps/angular/angular-route.js"></script>

    <!-- ############ 路由 ############ -->
    <script language="JavaScript" src="/ui/route"></script>

    <!-- ############ 模块 ############ -->
    <link rel="stylesheet" href="${rs}/core/css/reset.css"/>
    <link rel="stylesheet" href="${rs}/core/css/octopus.css">
    <link rel="stylesheet" href="${rs}/core/css/main.css">
    <link rel="stylesheet" href="${rs}/core/css/me.css">
    <link rel="stylesheet" href="${rs}/core/css/users.css">
    <link rel="stylesheet" href="${rs}/core/css/domains.css">
    <link rel="stylesheet" href="${rs}/core/css/setting.css">
    <link rel="stylesheet" href="${rs}/core/css/issue.css">
    <link rel="stylesheet" href="${rs}/core/css/release.css">

    <script language="JavaScript" src="${rs}/core/js/main.js"></script>
    <script language="JavaScript" src="${rs}/core/js/me.js"></script>
    <script language="JavaScript" src="${rs}/core/js/users.js"></script>
    <script language="JavaScript" src="${rs}/core/js/domains.js"></script>
    <script language="JavaScript" src="${rs}/core/js/setting.js"></script>
    <script language="JavaScript" src="${rs}/core/js/issue.js"></script>
    <script language="JavaScript" src="${rs}/core/js/release.js"></script>
</head>
<body dmnNm="${domain.name}" userNm="${user.name}"
      alertAudio="${rs}/core/audio/msg_iphone.mp3" ng-app="mainApp">
<%@include file="_msg.jsp" %>
<div class="header">
    <span>Oct</span><img class="logo" src="${rs}/core/img/octopus-logo.png"><span>pus</span>
    <ol class="header-menu">
        <li class="header-module">
            <i class="fa fa-comments fa-2x header-icon"><span class="check-list-tip">0</span></i>
            <ul class="sub-menu sub-menu-right">
                <li class="title">${msg['page.header.friends']}</li>
                <li class="myfriends" ng-controller="MyFriendsCtrl">
                    <input type="text" ng-model="friendFilter">
                    <ul>
                        <li ng-repeat="user in  users | filter:friendFilter | orderBy:'isOnline':true"
                            chatId="{{user.chatId}}"
                            class="friend-info online-{{user.isOnline}} friend-chat-{{user.chatId}}"
                            ng-click="chatWithFriend(user.name)">
                            <img src="/user/face/{{user.name}}" class="user-face"/>

                            <div class="friend-base">
                                <div class="friend-name">{{user.name}}</div>
                                <div class="friend-stat">
                                    <em></em>
                                    <span class="on">${msg['page.header.friends.online']}</span>
                                    <span class="off">${msg['page.header.friends.offline']}</span>
                                </div>
                            </div>
                            <div class="friend-unread hdn">0</div>
                        </li>
                    </ul>
                </li>
                <li class="bottom-bar">
                    <span>${msg['page.header.checkall']}</span><i class="fa fa-chevron-circle-right fa-1x"></i>
                </li>
            </ul>
        </li>
        <li class="header-module">
            <i class="fa fa-tasks fa-2x header-icon"><span class="check-list-tip">0</span></i>
            <ul class="sub-menu">
                <li class="title">${msg['page.header.task']}</li>
                <li class="bottom-bar">
                    <span>${msg['page.header.checkall']}</span><i class="fa fa-chevron-circle-right fa-1x"></i>
                </li>
            </ul>
        </li>
        <li class="header-module">
            <i class="fa fa-exclamation-circle fa-2x header-icon"><span class="check-list-tip">0</span></i>
            <ul class="sub-menu">
                <li class="title">${msg['page.header.notice']}</li>
                <li class="bottom-bar">
                    <span>${msg['page.header.checkall']}</span><i class="fa fa-chevron-circle-right fa-1x"></i>
                </li>
            </ul>
        </li>
        <li class="header-module hm-user">
            <img src="/user/face/${userInfo.userFace}" class="user-face">

            <p class="user-name">${user.name}</p>
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
            <ol>
                <li><i class="fa fa-angle-double-left fa-2x nav-mini-toogle"></i></li>
            </ol>
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
    <div class="main-sidebar">
        <div class="sidebar-container">
            <div class="main-sidebar-switch">
                <i class="fa fa-2x fa-angle-double-left"></i>
            </div>
            <ul class="chat-list">
            </ul>
            <div class="chat-list-filter">
                <input type="text" placeholder="">
            </div>
            <div class="chat-container">
            </div>
            <div class="no-chat">
                <img src="${rs}/core/img/baozou/bz001.png">
                <span>No Chat Selected</span>
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
</body>
</html>