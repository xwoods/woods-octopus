<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="_setup.jsp" %>
<!DOCTYPE html>
<html ng-app>
<head lang="en">
    <meta charset="UTF-8">
    <title>${msg['page.login.title']}</title>
    <link rel="shortcut icon" href="${rs}/img/favicon.ico" />
    <link rel="stylesheet" href="${rs}/css/reset.css"/>
    <link rel="stylesheet" href="${rs}/css/font-awesome.css">
    <link rel="stylesheet" href="${rs}/css/login.css">
</head>
<body>
<div class="login-content" ng-controller="LoginCtrl">
    <form ng-submit="login()" class="login-form" >
        <div class="login-input">
            <input placeholder="域名" ng-model="domain" autocomplete="on" autofocus="autofocus" name="domain"/>
            <i class="fa fa-home fa-2x login-input-tip"></i>
        </div>
        <div class="login-input">
            <input placeholder="用户名" ng-model="name" autocomplete="on" name="username"/>
            <i class="fa fa-user fa-2x login-input-tip"></i>
        </div>
        <div class="login-input">
            <input placeholder="密码" type="password" ng-model="password" name="password"/>
            <i class="fa fa-lock fa-2x login-input-tip"></i>
        </div>
        <input type="submit" class="login-submit" value="登 陆" />
    </form>
</div>
<%@include file="_msg.jsp" %>
<script language="JavaScript" src="${rs}/js/jquery.js"></script>
<script language="JavaScript" src="${rs}/js/jquery-ui.js"></script>
<script language="JavaScript" src="${rs}/js/zlib/core.js"></script>
<script language="JavaScript" src="${rs}/js/zlib/log.js"></script>
<script language="JavaScript" src="${rs}/js/zlib/err.js"></script>
<script language="JavaScript" src="${rs}/js/zlib/browser.js"></script>
<script language="JavaScript" src="${rs}/js/zlib/http.js"></script>
<script language="JavaScript" src="${rs}/js/underscore.js"></script>
<script language="JavaScript" src="${rs}/js/angular.js"></script>
<script language="JavaScript" src="${rs}/js/module/login.js"></script>
</body>
</html>