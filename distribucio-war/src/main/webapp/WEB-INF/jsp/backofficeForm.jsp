<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty backofficeCommand.id}"><c:set var="titol"><spring:message code="backoffice.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="backoffice.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<dis:modalHead/>
	
	<script type="text/javascript">

		$(document).ready(function() {
		
			$('#tipus').change(function(){
				if ($(this).val() == 'SISTRA')
					$('#backofficeTempsEntreIntentsBlock').show();
				else
					$('#backofficeTempsEntreIntentsBlock').hide();
			});
		});
	</script>
	
	
	
</head>
<body>
	<c:set var="formAction"><dis:modalUrl value="/backoffice/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="backofficeCommand">
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<dis:inputSelect name="tipus" textKey="backoffice.form.camp.tipus" optionItems="${backofficeTipusEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" required="true"/>
		<dis:inputText name="codi" textKey="backoffice.form.camp.codi" required="true" comment="backoffice.form.camp.codi.comment"/>
		<dis:inputText name="nom" textKey="backoffice.form.camp.nom" required="true"/>
		<dis:inputText name="url" textKey="backoffice.form.camp.url" required="true"/>
		<dis:inputText name="usuari" textKey="backoffice.form.camp.usuari"/>
		<dis:inputText name="contrasenya" textKey="backoffice.form.camp.contrasenya"/>
		<block id="backofficeTempsEntreIntentsBlock" style='display:${backofficeCommand.tipus == "SISTRA" ? "inline" : "none"}'>
			<dis:inputText name="intents" textKey="backoffice.form.camp.intents"/>
			<dis:inputText name="tempsEntreIntents" textKey="backoffice.form.camp.temps.entre.intents" comment="backoffice.form.camp.temps.entre.intents.info"/>
		</block>

		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/backoffice"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
