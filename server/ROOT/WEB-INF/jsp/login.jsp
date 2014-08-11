<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="_setup.jsp" %>
<!DOCTYPE html>
<html ng-app>
<head lang="en">
    <meta charset="UTF-8">
    <title>${msg['page.login.title']}</title>
    <link rel="shortcut icon" href="${rs}/core/img/favicon.ico"/>
    <link rel="stylesheet" href="${rs}/core/css/reset.css"/>
    <link rel="stylesheet" href="${rs}/core/css/octopus.css"/>
    <link rel="stylesheet" href="${rs}/core/css/font-awesome.css">
    <link rel="stylesheet" href="${rs}/core/css/module/login.css">
</head>
<body>
<div class="login-content" ng-controller="LoginCtrl">
    <form ng-submit="login()" class="login-form">
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
        <input type="submit" class="login-submit" value="登 陆"/>

        <div class="switch-form reg">注册新用户</div>
    </form>
</div>
<div class="reg-content" ng-controller="RegCtrl">
    <form ng-submit="reg()" class="reg-form">
        <div class="form-title">用户名</div>
        <div class="form-item">
            <input type="text" name="regname" placeholder="2-20位(字母+数字)">
            <i class="fa fa-check-circle fa-lg chk-i chk-ok"></i>
            <i class="fa fa fa-spinner fa-spin-1s fa-lg chk-i chk-ing"></i>
            <i class="fa fa-times-circle fa-lg chk-i chk-fail"></i>
            <span class="fail-tip">2-20位(字母+数字)</span>
        </div>
        <div class="form-title">昵称</div>
        <div class="form-item">
            <input type="text" name="regalias" placeholder="2-20位(字母+数字+中文字符)">
            <i class="fa fa-check-circle fa-lg chk-i chk-ok"></i>
            <i class="fa fa fa-spinner fa-spin-1s fa-lg chk-i chk-ing"></i>
            <i class="fa fa-times-circle fa-lg chk-i chk-fail"></i>
            <span class="fail-tip">2-20位(字母+数字)</span>
        </div>
        <div class="form-title">密码</div>
        <div class="form-item">
            <input type="password" name="regpassword" placeholder="6-20位(字母+数字+@)">
            <i class="fa fa-check-circle fa-lg chk-i chk-ok"></i>
            <i class="fa fa fa-spinner fa-spin-1s fa-lg chk-i chk-ing"></i>
            <i class="fa fa-times-circle fa-lg chk-i chk-fail"></i>
            <span class="fail-tip">2-20位(字母+数字)</span>
        </div>
        <div class="form-title">邀请码</div>
        <div class="form-item">
            <input type="text" name="reginviteCode" placeholder="没有邀请码,无法注册">
            <i class="fa fa-check-circle fa-lg chk-i chk-ok"></i>
            <i class="fa fa fa-spinner fa-spin-1s fa-lg chk-i chk-ing"></i>
            <i class="fa fa-times-circle fa-lg chk-i chk-fail"></i>
        </div>
        <input type="submit" class="reg-submit" value="注 册"/>
        <div class="switch-form login">返回登录</div>
    </form>
</div>
<%@include file="_msg.jsp" %>
<script language="JavaScript" src="${rs}/core/js/jquery.js"></script>
<script language="JavaScript" src="${rs}/core/js/underscore.js"></script>
<script language="JavaScript" src="${rs}/core/js/angular.js"></script>
<script language="JavaScript" src="${rs}/core/js/zlib/core.js"></script>
<script language="JavaScript" src="${rs}/core/js/zlib/log.js"></script>
<script language="JavaScript" src="${rs}/core/js/zlib/err.js"></script>
<script language="JavaScript" src="${rs}/core/js/zlib/browser.js"></script>
<script language="JavaScript" src="${rs}/core/js/zlib/http.js"></script>
<script language="JavaScript" src="${rs}/core/js/module/login.js"></script>
</body>
</html>