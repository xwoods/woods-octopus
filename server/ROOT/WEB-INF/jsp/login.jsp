<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="_setup.jsp" %>
<!DOCTYPE html>
<html ng-app>
<head lang="en">
    <meta charset="UTF-8">
    <title>${msg['page.login.title']}</title>
    <link rel="shortcut icon" href="${rs}/core/img/favicon.ico" />
    <link rel="stylesheet" href="${rs}/core/css/reset.css"/>
    <link rel="stylesheet" href="${rs}/core/css/octopus.css"/>
    <link rel="stylesheet" href="${rs}/core/css/font-awesome.css">
    <link rel="stylesheet" href="${rs}/core/css/module/login.css">
</head>
<body>
<div class="login-content" ng-controller="LoginCtrl">
    <form ng-submit="login()" class="login-form" >
        <div class="login-input">
            <input placeholder="域名" ng-model="domain" autofocus="autofocus" name="domain"/>
            <i class="fa fa-home fa-2x login-input-tip"></i>
        </div>
        <div class="login-input">
            <input placeholder="用户名" ng-model="name" name="username"/>
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
<script language="JavaScript" src="${rs}/core/js/jquery.js"></script>
<script language="JavaScript" src="${rs}/core/js/angular.js"></script>
<script language="JavaScript" src="${rs}/core/js/zlib/core.js"></script>
<script language="JavaScript" src="${rs}/core/js/zlib/log.js"></script>
<script language="JavaScript" src="${rs}/core/js/zlib/err.js"></script>
<script language="JavaScript" src="${rs}/core/js/zlib/browser.js"></script>
<script language="JavaScript" src="${rs}/core/js/zlib/http.js"></script>
<script language="JavaScript" src="${rs}/core/js/module/login.js"></script>
</body>
</html>