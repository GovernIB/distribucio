<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="multiple">${registres != null}</c:set>
<c:set var="nRegistres">${multiple ? fn:length(registres) : 0 }</c:set>
<c:set var="titol">
	<c:choose>
		<c:when test="${multiple}"><spring:message arguments="${nRegistres}" code="registre.admin.reintentar.processament.titol.multiple"/></c:when>
		<c:otherwise><spring:message code="registre.admin.reintentar.processament.titol"/></c:otherwise>
	</c:choose>
</c:set>
<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<dis:modalHead/>
	
	<script type="text/javascript">

		var multiple = ${registres != null};

		$(document).ready(function() {
			$("button[name='btnReintentarProcessamentSubmit']").click(function(){
				if (multiple) {
					processaAnotacions();
				}
		    });
		});
		
	</script>
</head>
<body>

	<c:if test="${registres != null}">
		<dis:processamentMultiple 
			registres="${registres}"
			start="true"
			btnSubmit="button[name='btnReintentarProcessamentSubmit']"
			form="#reintentarProcessamentCommand"
			postUrl="/registreAdmin/reintentarProcessamentAjax/"
			deselectUrl="/registreUser/deselect"></dis:processamentMultiple>
	</c:if>
	
	<form:form action="" class="form-horizontal" modelAttribute="reintentarProcessamentCommand">
		<div id="modal-botons" class="well">
			<button name="btnReintentarProcessamentSubmit" type="${multiple ? 'button' : 'submit' }" class="btn btn-success"><span class="fa fa-cog"></span> <spring:message code="registre.detalls.accio.reintentar"/></button>
			<a href="<c:url value="/registreAdmin"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>

