<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
<meta id="_csrf" name="_csrf" content="${_csrf.token}" />
<meta id="_csrf_header" name="_csrf_header" content="${_csrf.headerName}" />

<script src='https://code.jquery.com/jquery-3.3.1.min.js'></script>
<script type="text/javascript">

/* session 상태확인 */
var userId = '<%=(String)session.getAttribute("userId")%>';
if(userId =="null"){ 
	console.log("세션 null상태"); 
 } else { 
	console.log(userId);
 }

/* /boardDelete에서 오는 msg */
var msg = '${msg}'
if(msg == 'sessionFin') {
	alert("로그인이 풀렸으니 재로그인해주세요.")
} 

</script>
</head>
<body>
<form action='<c:url value='userCheck'/>' method="post">
	<div>
		<label>아이디</label>
		<input type="text" id="memberId" name="memberId">	
	</div>
	<div>
		<label>비밀번호</label>
		<input type="password" id="memberPass" name="memberPass">
	</div>
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	<input type="submit" value="로그인">		
	<input type="button" value="회원가입" onclick="location.href='users'">
</form>

</body>
</html>