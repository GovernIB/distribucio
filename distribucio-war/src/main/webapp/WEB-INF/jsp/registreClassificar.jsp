<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="titol"><spring:message code="bustia.pendent.classificar.titol"/></c:set>
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
	<dis:modalHead/>
<script>

//Funció per advertir a l'usuari dels procediments que estàn obsolets
function formatProcedimentSelect(item) {
	const procedimentSplit = item.text.split(" => ");
	if (procedimentSplit[1] == '<spring:message code="procediment.estat.enum.EXTINGIT"/>'.toUpperCase()) {
		return $("<span>" + procedimentSplit[0] + " <span class='fa fa-exclamation-triangle text-warning' title='No es troba al llistat de procediments de Rolsac'> </span> </span>");
	}else {
		return $("<span>" + procedimentSplit[0] + " </span>");
	}
}

$(document).ready(function() {
	if (${fn:length(procediments)} > 0) {
		$('#accio-classificar').removeAttr('disabled');
	}
});
</script>
</head>
<body>
	<div class="alert alert-warning" role="alert">
		<span class="fa fa-warning"></span>
		<spring:message code="bustia.pendent.classificar.warning"/>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><spring:message code="bustia.pendent.classificar.detalls"/></h3>
		</div>
		<table class="table table-bordered">
			<tbody>
				<tr>
					<td width="15%"><strong><spring:message code="registre.detalls.camp.tipus"/></strong></td>
					<td width="35%"><spring:message code="registre.anotacio.tipus.enum.${registre.registreTipus}"/></td>
					<td width="15%"><strong><spring:message code="registre.detalls.camp.idioma"/></strong></td>
					<td width="35%">${registre.idiomaDescripcio} (${registre.idiomaCodi})</td>
				</tr>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.numero"/></strong></td>
					<td>${registre.numero}</td>
					<td><strong><spring:message code="registre.detalls.camp.origen.num"/></strong></td>
					<td>${registre.numeroOrigen}</td>
				</tr>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.data"/></strong></td>
					<td><fmt:formatDate value="${registre.data}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
					<td><strong>
						<c:if test="${registre.registreTipus == 'ENTRADA'}"><spring:message code="registre.detalls.camp.desti"/></c:if>
						<c:if test="${registre.registreTipus == 'SORTIDA'}"><spring:message code="registre.detalls.camp.origen"/></c:if>
					</strong></td>
					<td>${registre.unitatAdministrativaDescripcio} (${registre.unitatAdministrativa})</td>
				</tr>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.oficina"/></strong></td>
					<td>${registre.oficinaDescripcio} (${registre.oficinaCodi})</td>
					<td><strong><spring:message code="registre.detalls.camp.assumpte.tipus"/></strong></td>
					<td>${registre.assumpteTipusDescripcio} (${registre.assumpteTipusCodi})</td>
				</tr>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.llibre"/></strong></td>
					<td>${registre.llibreDescripcio} (${registre.llibreCodi})</td>
					<td><strong><spring:message code="registre.detalls.camp.assumpte.codi"/></strong></td>
					<td>(${registre.assumpteCodi})</td>
				</tr>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.extracte"/></strong></td>
					<td colspan="3">${registre.extracte}</td>
				</tr>
			</tbody>
		</table>
	</div>
	<form:form action="" method="post" cssClass="form-horizontal" commandName="registreClassificarCommand">
		<c:if test="${isPermesModificarTitol}">
			<dis:inputText name="titol" textKey="bustia.pendent.classificar.camp.titol" />
		</c:if>
		<form:hidden path="contingutId"/>
		<c:choose>
			<c:when test="${empty procediments}">
				<dis:inputFixed name="codiProcediment" textKey="bustia.pendent.classificar.camp.codi.procediment">
					<p class="text-danger">
						<spring:message code="bustia.pendent.classificar.no.procediments"/>
					</p>
				</dis:inputFixed>
			</c:when>
			<c:otherwise>
				<dis:inputSelect 
					name="codiProcediment" 
					textKey="bustia.pendent.classificar.camp.codi.procediment" 
					optionItems="${procediments}" 
					optionValueAttribute="codiSia" 
					optionTextAttribute="codiNomEstat" 
					emptyOption="true"
					required="false"
					placeholderKey="bustia.pendent.classificar.camp.codi.procediment"
					optionMinimumResultsForSearch="0" 
					optionTemplateFunction="formatProcedimentSelect"/>
			</c:otherwise>
		</c:choose>
		<div id="modal-botons" class="well">
			<button id="accio-classificar" type="submit" class="btn btn-success" disabled="disabled"><span class="fa fa-inbox"></span> <spring:message code="bustia.pendent.classificar.submit"/></button>
			<a href="#" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>