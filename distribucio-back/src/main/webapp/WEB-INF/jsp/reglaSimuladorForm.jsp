<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="regla.simulador.form.titol"/></c:set>

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
<script type="text/javascript">




function formatSelectUnitatItem(select, item) {
	if (!item.id) {
	    return item.text;
	}
	valida = true;
	if (item.data) {
		valida = item.data.estat =="V";
	} else {
		if ($(select).val() == item.id) {
			// Consulta si no és vàlida per afegir la icona de incorrecta.
			$.ajax({
				url: $(select).data('urlInicial') +'/' + item.id,
				async: false,
				success: function(resposta) {
					valida = resposta.estat == "V";
				}
			});	
		}			
	}
	if (valida)
		return item.text;
	else
		return $("<span>" + item.text + " <span class='fa fa-exclamation-triangle text-warning' title=\"<spring:message code='unitat.filtre.avis.obsoleta'/>\"></span></span>");
}


function formatSelectUnitat(item) {
	return formatSelectUnitatItem($('#unitatId'), item);
}




$(document).ready(function() {
	$("input:visible:enabled:not([readonly]),textarea:visible:enabled:not([readonly]),select:visible:enabled:not([readonly])").first().focus();

    $("#simularBtn").click(function(e) {
    	$("#reglaSimuladorDiv").empty();
        $("#reglaSimuladorDiv").append("<div style='text-align: center; margin-bottom: 60px; margin-top: 60px;''><span class='fa fa-circle-o-notch fa-spin fa-3x'/></div>");
        webutilModalAdjustHeight();
    });
        
});





</script>
</head>
<body>

	<c:set var="formAction"><dis:modalUrl value="/regla/simular"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="registreSimulatCommand" role="form">

		<c:url value="/unitatajax/unitat" var="urlConsultaInicial"/>
		<c:url value="/unitatajax/unitats" var="urlConsultaLlistat"/>
		<dis:inputSuggest 
			name="unitatId" 
			textKey="regla.simulador.form.camp.unitat"
			urlConsultaInicial="${urlConsultaInicial}" 
			urlConsultaLlistat="${urlConsultaLlistat}" 
			inline="false" 
			placeholderKey="bustia.form.camp.unitat"
			suggestValue="id"
			suggestText="codiAndNom"
			optionTemplateFunction="formatSelectUnitat"
			required="true"/>
		<dis:inputSelect name="bustiaId" textKey="regla.simulador.form.camp.bustia" optionItems="${busties}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true" optionMinimumResultsForSearch="0"/>
		<dis:inputText name="procedimentCodi" textKey="regla.simulador.form.camp.procediment.codi"/>
		<dis:inputText name="assumpteCodi" textKey="regla.simulador.form.camp.assumpte.codi" required="false"/>
		<dis:inputSelect name="presencial" textKey="regla.form.camp.presencial" optionEnum="ReglaPresencialEnumDto" emptyOption="true" placeholderKey="regla.list.columna.presencial"/>
		
		<div class="col-xs-4"></div><div class="col-xs-8">
			<div class="row">
				<div class="col-sm-6">
					<button id="simularBtn" type="submit" class="btn btn-primary" style="margin-bottom: 35px;"><span class="fa fa-cog"></span> <spring:message code="comu.boto.simular"/></button>
				</div>
				<div class="col-sm-6" style="text-align:right">
					<b><spring:message code="regla.list.etiqueta.avaluarTotes"/>: </b> ${(avaluarTotes == true) ? 'Sí' : 'No'}			
				</div>
			</div>
		</div>
		
		<div id="modal-botons">
			<a href="<c:url value="/regla"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
	
	<div id="reglaSimuladorDiv">
		<c:if test="${not empty simulatAccions}">
			<table id="reglaSimuladorTable" class="table table-striped table-bordered">
				<thead>
					<tr>
						<th><spring:message code="regla.simulador.list.columna.ordre"/></th>
						<th><spring:message code="regla.simulador.list.columna.descripcio"/></th>
						<th><spring:message code="regla.simulador.list.columna.regla"/></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="simulatAccion" items="${simulatAccions}" varStatus="status">
						<tr>
							<td>${status.count} </td>
							<td><spring:message code='regla.simulador.accio.${simulatAccion.accion}'/> <c:if test="${simulatAccion.param!=null}">"${simulatAccion.param}"</c:if></td>
							<td>${simulatAccion.reglaNom}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
	</div>
	
	
	
	
	
	
</body>
</html>
