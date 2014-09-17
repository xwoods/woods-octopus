<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/_setup.jsp" %>
<div class="module-content module-invite">
    <div class="invite-add-form">
        <div>
            <b>用户名</b><input type="text" ng-model="userName">
        </div>
        <div>
            <b>性别</b>
            <select ng-model="isMale">
                <option value="true">男</option>
                <option value="false">女</option>
            </select>
        </div>
        <div>
            <b>域选择</b>
            <select class="domain-list">
            </select>
            <button class="domain-add">加入该域</button>
        </div>
        <div>
            <b>域</b><input type="text" ng-model="domainList" disabled>
        </div>
        <div>
            <b>域(显示)</b><input type="text" ng-model="domainNameList" disabled>
        </div>
        <div>
            <button class="invite-add">添加邀请信息</button>
        </div>
    </div>
    <div class="invite-url">
        <b></b> 邀请链接 : <span></span>
    </div>
    <div class="invite-table">
    </div>
</div>