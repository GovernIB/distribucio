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
		<c:when test="${multiple}"><spring:message arguments="${nRegistres}" code="registre.user.marcar.pendent.titol.multiple"/></c:when>
		<c:otherwise><spring:message code="registre.user.marcar.pendent.titol"/></c:otherwise>
	</c:choose>
</c:set>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/css/jstree.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jstree.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<dis:modalHead/>
	
	<script type="text/javascript">

		var multiple = ${registres != null};

		$(document).ready(function() {
			$("input:visible:enabled:not([readonly]),textarea:visible:enabled:not([readonly]),select:visible:enabled:not([readonly])").first().focus();
			
			$("button[name='btnMarcarPendentSubmit']").click(function(){
				if (multiple) {
					processaAnotacions();
				}
		    });
		});
		
	</script>
</head>
<body>

	<c:if test="${registres != null}">
		<dis:seleccioMultiple 
			items="${registres}" 
			itemId="id"
			itemUrl="/registreUser/registre"  
			itemUrlParam1="id"
			itemKey="numero"
			itemText="extracte" 
			missatgeHeader="registresSeleccionats.anotacions.seleccionades"/>
	</c:if>
	
	<form:form class="form-horizontal" modelAttribute="marcarPendentCommand">
		<dis:inputTextarea required="true" name="motiu" textKey="registre.user.marcar.pendent.camp.motiu"/>
		<div id="modal-botons" class="well">
			<button name="btnMarcarPendentSubmit" type="submit" class="btn btn-success"><span class="fa fa-undo"></span> <spring:message code="registre.user.marcar.pendent.boto"/></button>
			<a href="<c:url value="/registreUser"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>

