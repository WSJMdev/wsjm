<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
</head>
<body>

<c:if test="${userId ne null}">
   <h1>로그인 성공입니다</h1>
</c:if>
</body>
<!-- 사용자 id와 redirect_url을 통해 파라미터로 code를 받아온다. -->
</html>