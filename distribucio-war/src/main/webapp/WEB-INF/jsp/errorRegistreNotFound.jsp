<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:set var="errorTitol"><spring:message code="error.titol.not.found"/></c:set>

<html>
<head>
	<title>${errorTitol}</title>
	<meta name="title-icon-class" content="fa fa-warning"/>

</head>
<body>
	
	<div class="alert alert-danger">
		<c:choose>
			<c:when test="${not empty currentContainingBustia}">
				<spring:message code="registre.detalls.movedToAnotherBustia" />: ${currentContainingBustia}
			</c:when>
			<c:otherwise>
				<spring:message code="registre.detalls.notFound" />
			</c:otherwise>
		</c:choose>




	</div>

</body>
</html>
