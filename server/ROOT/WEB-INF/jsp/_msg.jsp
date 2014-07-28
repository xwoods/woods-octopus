<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<ul id="__msg__" class="hdn">
    <c:forEach var="m" items="${msg}">
        <li class="${fn:replace(m.key,'.','_')}" key="${m.key}">${m.value}</li>
    </c:forEach>
</ul>
