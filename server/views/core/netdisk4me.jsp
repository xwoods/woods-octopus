<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/_setup.jsp" %>
<div class="module-content module-netdisk4me">
    <div class="netdisk">
        <div class="netdisk-toolbar">
            <ul class="netdisk-toolbar-btns default-btns-upload">
                <li>
                    <span class="fa fa-upload fa-lg"></span>
                    上传
                </li>
            </ul>
            <ul class="netdisk-toolbar-btns default-btns-newfolder">
                <li>
                    <span class="fa fa-folder-o fa-lg"></span>
                    新建文件夹
                </li>
            </ul>
            <ul class="netdisk-toolbar-btns select-btns">
                <li>
                    <span class="fa fa-download fa-lg"></span>
                    下载
                </li>
                <li>
                    <span class="fa fa-trash fa-lg"></span>
                    删除
                </li>
                <li>
                    <span class="fa fa-share-alt fa-lg"></span>
                    分享
                </li>
                <li>
                    <span class="fa fa-pencil-square-o fa-lg"></span>
                    重命名
                </li>
                <li>
                    <span class="fa fa-arrows-alt fa-lg"></span>
                    移动
                </li>
            </ul>
            <ul class="netdisk-toolbar-btns list-btns">
                <li class="active">
                    <span class="fa fa-th-large fa-lg"></span>
                </li>
                <li>
                    <span class="fa fa-th-list fa-lg"></span>
                </li>
            </ul>
        </div>
        <div class="netdisk-crumbs">
            <ul>
                <li>
                    我的网盘
                </li>
                <li>
                    测试目录1
                </li>
                <li>
                    测试目录2
                </li>
            </ul>
        </div>
        <div class="netdisk-list-toolbar">
            <input type="checkbox">
        </div>
        <div class="netdisk-list">
            <ul>
                <li fnm="llt.gif" fsz="1670691">
                    <div class="file-type zui-file-icon gif"></div>
                    <div class="file-size">1.7 MB</div>
                    <div class="file-nm">llt.gif</div>
                </li>
                <li fnm="llt.gif" fsz="3556">
                    <div class="file-type zui-file-icon zip"></div>
                    <div class="file-size">22 MB</div>
                    <div class="file-nm">test.zip</div>
                </li>
                <li fnm="llt.gif" fsz="1670691">
                    <div class="file-type zui-file-icon jpg"></div>
                    <div class="file-size">35 KB</div>
                    <div class="file-nm">haha.jpg</div>
                </li>
                <li fnm="llt.gif" fsz="1670691">
                    <div class="file-type zui-file-icon doc"></div>
                    <div class="file-size">25.6 KB</div>
                    <div class="file-nm">2013.doc</div>
                </li>
                <li fnm="llt.gif" fsz="1670691">
                    <div class="file-type zui-file-icon xls"></div>
                    <div class="file-size">35 KB</div>
                    <div class="file-nm">2014.xls</div>
                </li>
            </ul>
        </div>
    </div>
</div>