<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:set var="titol"><spring:message code="error.detall.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<dis:modalHead/>
</head>
<body>
	<c:if test="${not empty error}">
		<dl class="dl-horizontal">
			<dt><spring:message code="error.detall.camp.data"/></dt>
			<dd><fmt:formatDate value="${error.data}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
			
			<dt><spring:message code="error.detall.camp.titol"/></dt>
			<dd>${error.titol}</dd>	
			
			<dt><spring:message code="error.detall.camp.tipus"/></dt>
			<dd>${error.tipus}</dd>			
		
			<dt><spring:message code="error.detall.camp.descripcio"/></dt>
			<dd>${error.descripcio}</dd>
	
				
		</dl>
		<c:if test="${not empty error.stacktrace}">
			<div class="panel-body" >
				<pre style="height:300px">${error.stacktrace}</pre>
			</div>
		</c:if>
	</c:if>
<!-- 	<div id="modal-botons"> -->
<%-- 		<a href="<c:url value="/error"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a> --%>
<!-- 	</div> -->
</body>