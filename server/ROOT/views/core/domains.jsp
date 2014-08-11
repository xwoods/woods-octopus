<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/_setup.jsp" %>
<div class="module-content module-domains">
    <div class="domain-form" >
        <form class="domain-add">
            <div class="form-title">域名称</div>
            <div class="form-item">
                <input type="text" name="name" placeholder="字母+数字">
                <i class="fa fa-check-circle fa-lg chk-i chk-ok"></i>
                <i class="fa fa fa-spinner fa-spin-1s fa-lg chk-i chk-ing"></i>
                <i class="fa fa-times-circle fa-lg chk-i chk-fail"></i>
            </div>
            <div class="form-title">域别名</div>
            <div class="form-item">
                <input type="text" name="alias" placeholder="任意字符">
                <i class="fa fa-check-circle fa-lg chk-i chk-ok"></i>
                <i class="fa fa fa-spinner fa-spin-1s fa-lg chk-i chk-ing"></i>
                <i class="fa fa-times-circle fa-lg chk-i chk-fail"></i>
            </div>
            <div class="form-title">域信息</div>
            <div class="form-item">
                <textarea name="about" placeholder="介绍本域是干什么的"></textarea>
            </div>
            <button class="form-submit ok">添加</button>
            <button class="form-submit cancel">取消</button>
        </form>
    </div>
    <div class="domain-table">

    </div>
</div>