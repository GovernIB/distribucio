<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="multiple">${annexos != null}</c:set>
<c:set var="nAnnexos">${multiple ? fn:length(annexos) : 0 }</c:set>
<c:set var="titol">
	<c:choose>
		<c:when test="${multiple}"><spring:message arguments="${nAnnexos}" code="annex.admin.processament.multiple.titol.multiple"/></c:when>
		<c:otherwise><spring:message code="annex.admin.processament.multiple.titol"/></c:otherwise>
	</c:choose>
</c:set>
<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<dis:modalHead/>
	
	<script type="text/javascript">

		var multiple = ${annexos != null};

		$(document).ready(function() {
			$("button[name='btnProcessamentAnnexosMultipleSubmit']").click(function(){
				if (multiple) {
					processaAnotacions();
				}
		    });
		});
		
	</script>
</head>
<body>

	<c:if test="${annexos != null}">
		<dis:seleccioMultiple 
			items="${annexos}" 
			itemId="id"
			itemUrl="/registreUser/registreAnnex"  
			itemUrlParam1="registreId"
			itemUrlParam2="id"
			itemKey="fitxerNom"
			itemText="fitxerNom"
			missatgeHeader="annexosSeleccionats.annexos.seleccionats"/>
	</c:if>
	
	<form:form class="form-horizontal" modelAttribute="processamentAnnexosMultiple">
		<div id="modal-botons" class="well">
			<button name="btnProcessamentAnnexosMultipleSubmit" type="submit" class="btn btn-success"><span class="fa fa-cog"></span> <spring:message code="registre.detalls.accio.reintentar"/></button>
			<a href="<c:url value="/registreAdmin"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>

