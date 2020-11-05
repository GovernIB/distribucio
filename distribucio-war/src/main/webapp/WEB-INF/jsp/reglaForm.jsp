<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty reglaCommand.id}"><c:set var="titol"><spring:message code="regla.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="regla.form.titol.modificar"/></c:set></c:otherwise>
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
	$('#tipus').on('change', function () {
		$('div#camps_tipus_BUSTIA').css('display', 'none');
		$('div#camps_tipus_BACKOFFICE').css('display', 'none');
		$('div#camps_tipus_UNITAT').css('display', 'none');
		$('div#camps_tipus_' + $(this).val()).css('display', '');
	});
	$('#tipus').trigger('change');	
	$('#backofficeTipus').change(function(){
		if ($(this).val() == 'SISTRA')
			$('#backofficeTempsEntreIntentsBlock').show();
		else
			$('#backofficeTempsEntreIntentsBlock').hide();
	});
});
</script>
</head>
<body>


	<c:if test="${reglaDto.unitatOrganitzativa.tipusTransicio != null}">

		<div class="panel panel-danger">
			<div class="panel-heading">
				<span class="fa fa-warning text-danger"></span>
				<spring:message code="regla.list.unitatObsoleta" />
			</div>
			<div class="panel-body">
				<div class="row">
					<label class="col-xs-4 text-right"><spring:message
							code="regla.form.novesUnitats" /></label>
					<div class="col-xs-8">
						<ul style="padding-left: 17px;">
							<c:forEach items="${reglaDto.unitatOrganitzativa.lastHistoricosUnitats}"
								var="newUnitat" varStatus="loop">
								<li>${newUnitat.denominacio} (${newUnitat.codi})</li>
							</c:forEach>
						</ul>
					</div>
				</div>
				<c:if test="${!empty reglesOfOldUnitatWithoutCurrent}">
					<div class="row">
						<label class="col-xs-4 text-right"><spring:message
								code="regla.form.altresReglesAfectades" /></label>
						<div class="col-xs-8">
							<ul style="padding-left: 17px;">
								<c:forEach items="${reglesOfOldUnitatWithoutCurrent}"
									var="regla" varStatus="loop">
									<li>${regla.nom}</li>
								</c:forEach>
							</ul>
						</div>
					</div>
				</c:if>
			</div>
		</div>
	</c:if>






	<c:set var="formAction"><dis:modalUrl value="/regla/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="reglaCommand" role="form">
		<form:hidden path="id"/>
		
		<dis:inputText name="nom" textKey="regla.form.camp.nom" required="true"/>
		<dis:inputTextarea name="descripcio" textKey="regla.form.camp.descripcio"/>
		
		<legend><spring:message code="regla.form.legend.filtre"/></legend>
		<c:url value="/unitatajax/unitat" var="urlConsultaInicial"/>
		<c:url value="/unitatajax/unitats" var="urlConsultaLlistat"/>
		<dis:inputSuggest 
			name="unitatId" 
			textKey="bustia.form.camp.unitat"
			urlConsultaInicial="${urlConsultaInicial}" 
			urlConsultaLlistat="${urlConsultaLlistat}" 
			inline="false" 
			placeholderKey="bustia.form.camp.unitat"
			suggestValue="id"
			suggestText="codiAndNom"
			optionTemplateFunction="formatSelectUnitat"/>
		<dis:inputSelect name="bustiaFiltreId" textKey="regla.form.camp.bustia" optionItems="${busties}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true"/>
		<dis:inputText name="procedimentCodi" textKey="regla.form.camp.procediment.codi" comment="regla.form.camp.procediment.codi.info"/>
		<dis:inputText name="assumpteCodi" textKey="regla.form.camp.assumpte.codi" required="false"/>
		

		<legend><spring:message code="regla.form.legend.accio"/></legend>
		<dis:inputSelect name="tipus" textKey="regla.form.camp.tipus" optionItems="${reglaTipusEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" required="true"/>
		<div id="camps_tipus_BUSTIA">
			<dis:inputSelect name="bustiaId" textKey="regla.form.camp.bustia" optionItems="${busties}" optionValueAttribute="id" optionTextAttribute="nom" required="true"/>
		</div>
		<div id="camps_tipus_BACKOFFICE">
			<dis:inputSelect name="backofficeDestiId" textKey="regla.form.camp.backoffice" optionItems="${backoffices}" optionValueAttribute="id" optionTextAttribute="nom" required="true" emptyOption="true"/>
		</div>
		<div id="camps_tipus_UNITAT">
			<dis:inputSuggest 
				name="unitatDestiId" 
				textKey="bustia.form.camp.unitat"
				urlConsultaInicial="${urlConsultaInicial}" 
				urlConsultaLlistat="${urlConsultaLlistat}" 
				inline="false" 
				placeholderKey="bustia.form.camp.unitat"
				suggestValue="id"
				suggestText="codiAndNom"
				optionTemplateFunction="formatSelectUnitat"/>
		</div>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/regla"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
	
	
</body>
</html>
