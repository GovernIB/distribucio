<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty backofficeCommand.id}">
		<c:set var="titol"><spring:message code="backoffice.form.titol.crear"/></c:set>
		<c:set var="nou" value="true"></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="backoffice.form.titol.modificar"/></c:set>		
		<c:set var="nou" value="false"></c:set>
	</c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<dis:modalHead/>
	
	<script type="text/javascript">
		$(document).ready(function() {
		    $("#div-alert").css("display", "none"); 
			$("input:visible:enabled:not([readonly]),textarea:visible:enabled:not([readonly]),select:visible:enabled:not([readonly])").first().focus();
			if (${nou != true}) {
				$("#codi").attr('readonly', true);
			}		    
		    $("#url").bind("change paste keyup", function() {
		    	if ($(this).val() == '${backofficeCommand.url}') {
	    	   		$("#btn-provar").css("pointer-events", "auto");
	    	   		$("#btn-provar").css("opacity", "1");
		    	}else {
	    	   		$("#btn-provar").css("pointer-events", "none");
		    		$("#btn-provar").css("opacity", "0.5");
		    	}
	    	});		    
		    $("#nom").bind("change paste keyup", function() {
		    	if ($(this).val() == '${backofficeCommand.nom}') {
	    	   		$("#btn-provar").css("pointer-events", "auto");
	    	   		$("#btn-provar").css("opacity", "1");
		    	}else {
	    	   		$("#btn-provar").css("pointer-events", "none");
		    		$("#btn-provar").css("opacity", "0.5");
		    	}
	    	});		    
		    $("#tipus").bind("change paste keyup", function() {
		    	if ($(this).val() == '${backofficeCommand.tipus}') {
	    	   		$("#btn-provar").css("pointer-events", "auto");
	    	   		$("#btn-provar").css("opacity", "1");
		    	}else {
	    	   		$("#btn-provar").css("pointer-events", "none");
		    		$("#btn-provar").css("opacity", "0.5");
		    	}
	    	});
		    $('button[name="btn-provar-failed"]').click(function(e) {
		    	 e.preventDefault();
		    	 e.stopPropagation();
				 $("#div-alert").css("display", "block");
				 $("#div-alert").removeClass("alert-success");
				 $("#div-alert").addClass("alert-danger");
			     $("#div-alert").html("<spring:message code='backoffice.controller.provar.error' arguments='${backofficeCommand.codi};- url: ${backofficeCommand.url}' htmlEscape='false' argumentSeparator=';'/>");
		    });
		    $('button[name="btn-provar-success"]').click(function(e) {
		    	 e.preventDefault();
		    	 e.stopPropagation();
				 $("#div-alert").css("display", "block");
				 $("#div-alert").removeClass("alert-danger");
				 $("#div-alert").addClass("alert-success");
			     $("#div-alert").html("<spring:message code='backoffice.controller.provar.ok' arguments='${backofficeCommand.nom}'/>");
		    });
		    $('button[name="btn-provar"]').click(function(e) {
		    	 e.preventDefault();
		    	 e.stopPropagation();
				 $("#div-alert").css("display", "none");
		    })
		});
	</script>
	
</head>
<body>
	<div id="#modal-missatges"></div>
	<div id="div-alert" class="alert "></div>
	<c:set var="formAction"><dis:modalUrl value="/backoffice/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="backofficeCommand">
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<dis:inputText name="codi" textKey="backoffice.form.camp.codi" required="true" comment="backoffice.form.camp.codi.comment"/>
		<dis:inputText name="nom" textKey="backoffice.form.camp.nom" required="true"/>
		<dis:inputText name="url" textKey="backoffice.form.camp.url" required="true"/>  
		<dis:inputSelect 
				name="tipus" 
				textKey="backoffice.form.camp.tipus" 
				optionEnum="BackofficeTipusEnumDto" 
				optionValueAttribute="id" 
				optionTextAttribute="nom" 
				placeholderKey="backoffice.form.camp.tipus"
				required="true"
				optionMinimumResultsForSearch="0"/>
		<dis:inputText name="usuari" textKey="backoffice.form.camp.usuari" comment="backoffice.form.camp.usuari.comment"/>
		<dis:inputText name="contrasenya" textKey="backoffice.form.camp.contrasenya" comment="backoffice.form.camp.contrasenya.comment"/>
					
		<div id="modal-botons" class="well">				
			<c:if test="${!nou}">
				<c:set var="backId" value="${backofficeCommand.id}"/>
				<button id="btn-provar" name="btn-provar" value="${backId}" class="btn btn-primary"><span id="span-spinner" class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.provar"/></button>
				<button type="button" id="btn-provar-failed" name="btn-provar-failed" class="btn btn-primary d-none"></button>
				<button type="button" id="btn-provar-success" name="btn-provar-success" class="btn btn-primary d-none"></button>
			</c:if> 
			<button id="btn-submit" type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a id="btn-cancel" href="<c:url value="/backoffice"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div> 

		
	</form:form>
</body>
</html>
