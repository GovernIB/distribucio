<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>


<html>
<head>
	<c:choose>
		<c:when test="${errorTitol != null}">
			<title>
				${errorTitol}
			</title>
		</c:when>
		<c:otherwise>
			<title>
				Error
			</title>
		</c:otherwise>
	</c:choose>
	<meta name="title-icon-class" content="fa fa-warning"/>
</head>
<body>
	
	<div class="alert alert-danger">
		${missatgeError}
	</div>

</body>
</html>
