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
	<c:if test="${requestLocale == 'en'}">
		<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script> 
	</c:if>
	<script src="<c:url value="/js/select2-locales/select2_${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<dis:modalHead/>
	
	<style>
		.bc-red-error{
			border-color: #a94442;
		}
		.color-red{
			color: #a94442;
		}
		.comentari-codi-missatge-espai {
			margin-top: 5px;
		}
	</style>
	
	<script type="text/javascript">
		$(document).ready(function() {
			var nom = $("#nom").val();
			var url = $("#url").val();
			var tipus = $("#tipus").val();
			$("#contingut-missatges").hide();
			$("#div-alert").hide();
			if (${codiEmpySpace == true}) {
				$("#codi").addClass("bc-red-error");
				$('<p class="comentari-codi-missatge-espai color-red"><span class="fa fa-exclamation-triangle"></span>&nbsp;<span id="codi-espai"><spring:message code="backoffice.form.camp.codi.espai.blanc"/></span></p>').insertBefore($(".comentari-codi"));
			}
		    $('button[name=btn-provar]').click(function(e) {
		    	 e.preventDefault();
		    	 e.stopPropagation();
				 $("#div-alert").css("display", "none");
				 $("button[name='btn-provar']", window.parent.document).find('#fa-refresh').addClass('fa-circle-o-notch');
				 $("button[name='btn-provar']", window.parent.document).find('#fa-refresh').addClass('fa-spin');		
				 $("button[name='btn-provar']", window.parent.document).attr('disabled', true).find('.fa-refresh').addClass("fa-spin");		
				 //webutilClearMissatges('#modal-missatges');
				
				// Consulta les dades
				$.ajax({
					url: "/distribucio/backoffice/" + e.target.value + "/provarajax", 
		            type : 'GET',
		            dataType : 'json',
		            success : function(data) {
						 //webutilClearMissatges('#modal-missatges');
						 $("#div-alert").css("display", "block");
		    			 if (data == false) {
		    				 $("#div-alert").removeClass("alert-success");
		    				 $("#div-alert").addClass("alert-danger");
		    			     $("#div-alert").html("<spring:message code='backoffice.controller.provar.error' arguments='${backofficeCommand.codi};- url: ${backofficeCommand.url}' htmlEscape='false' argumentSeparator=';'/>");
		    			 }else if (data == true) {
		    				 $("#div-alert").removeClass("alert-danger");
		    				 $("#div-alert").addClass("alert-success");
		    			     $("#div-alert").html("<spring:message code='backoffice.controller.provar.ok' arguments='${backofficeCommand.nom}'/>");		    				 
		    			 }
		            },
		            error: function (request, status, error) {
		            	// Mostra l'error
		            	//webutilMissatgeError(request.responseText, '#modal-missatges');
		            },
		            complete: function() {
		            	// Treu els spinners
		    			$("button[name='btn-provar']", window.parent.document).attr('disabled', false).find('.fa-refresh').removeClass("fa-spin");
		    			$("button[name='btn-provar']", window.parent.document).find('#fa-refresh').removeClass('fa-circle-o-notch');
					 	$("button[name='btn-provar']", window.parent.document).find('#fa-refresh').removeClass('fa-spin');
		            }
		        });
		    });
			
		    //$("#div-alert").css("display", "none"); 
			$("input:visible:enabled:not([readonly]),textarea:visible:enabled:not([readonly]),select:visible:enabled:not([readonly])").first().focus();
			if (${nou != true}) {
				$("#codi").attr('readonly', true);
			}		    
		    $("#url").bind("change paste keyup", function() {
		    	if ($(this).val() == url && $("#nom").val() == nom && $("#tipus").val() == tipus) {
		    		$("button[name='btn-provar']", window.parent.document).attr('disabled', false).find('.fa-refresh').removeClass("fa-spin");
		    	}else {
		    		$("button[name='btn-provar']", window.parent.document).attr('disabled', true).find('.fa-refresh').removeClass("fa-spin");
		    	}
	    	});		    
		    $("#nom").bind("change paste keyup", function() {
		    	if ($(this).val() == nom && $("#url").val() == url && $("#tipus").val() == tipus) {
		    		$("button[name='btn-provar']", window.parent.document).attr('disabled', false).find('.fa-refresh').removeClass("fa-spin");
		    	}else {
		    		$("button[name='btn-provar']", window.parent.document).attr('disabled', true).find('.fa-refresh').removeClass("fa-spin");
		    	}
	    	});		    
		    $("#tipus").bind("change paste keyup", function() {
		    	if ($(this).val() == tipus && $("#nom").val() == nom && $("#url").val() == url) {
		    		$("button[name='btn-provar']", window.parent.document).attr('disabled', false).find('.fa-refresh').removeClass("fa-spin");
		    	}else {
		    		$("button[name='btn-provar']", window.parent.document).attr('disabled', true).find('.fa-refresh').removeClass("fa-spin");
		    	}
	    	});
		});
	</script>
	
</head>
<body>
	<div id="div-alert" class="alert "></div>
	<c:set var="formAction"><dis:modalUrl value="/backoffice/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="backofficeCommand">
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
				<button id="btn-provar" name="btn-provar" value="${backId}" class="btn btn-primary"><span id="fa-refresh" class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.provar"/></button>
			</c:if> 
			<button id="btn-submit" type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a id="btn-cancel" href="<c:url value="/backoffice"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div> 

		
	</form:form>
</body>
</html>
