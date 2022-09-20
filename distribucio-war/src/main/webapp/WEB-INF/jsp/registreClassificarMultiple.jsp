<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="titol"><spring:message code="bustia.pendent.classificar.multiple.titol"/></c:set>
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
	<dis:modalHead/>
<script>
var registres = [];
<c:forEach var="registre" items="${registres}">
registres.push({id: ${registre.id}});
</c:forEach>
var processSuccess = 0;
var processError = 0;
var inicialitzarClassificacio = function(element) {
	$span = $('span.fa', element);
	$span.removeClass('fa-inbox');
	$span.addClass('fa-circle-o-notch fa-spin');
	$modal = $(element).closest('div.modal').parent();
	$modal.attr('data-nohide', 'true');
	$botoCancel = $('#accio-cancel', $(element).parent());
	$botoCancel.attr('disabled', 'disabled');
	$('select#codiProcediment').attr('disabled', 'disabled');
	$botoClassificar = $(element);
	$botoClassificar.attr('disabled', 'disabled');
	$('#registres-list h3.panel-title span.state-icon').addClass('fa-circle-o-notch fa-spin text-muted');
}
var processarRegistres = function(index, element) {
	if (index < registres.length) {
		let registre = registres[index];
		let codiProcediment = $('select#codiProcediment').val();
		let classificarUrl = 'classificarMultiple/' + registre.id + '/' + codiProcediment;
		$.get(classificarUrl, function(response) {
			actualitzarEstatRegistre(index, true, response);
			processarRegistres(index + 1, element);
		}).fail(function() {
			actualitzarEstatRegistre(index, false);
			processarRegistres(index + 1, element);
		});
		return false;
	} else {
		finalitzarClassificacio(element);
	}
}
var actualitzarEstatRegistre = function(index, success, response) {
	if (success) {
		processSuccess++;
	} else {
		processError++;
	}
	let percentSuccess = 100 * processSuccess / registres.length;
	let percentError = 100 * processError / registres.length;
	$('#classificacio-progress-success').css('width', percentSuccess + '%');
	$('#classificacio-progress-error').css('width', percentError + '%');
	$registreIcon = $('#registres-list h3.panel-title').children('span.state-icon').eq(index);
	$registreIcon.removeClass('fa-circle-o-notch fa-spin text-muted');
	if (success) {
		if (response.resultat == 'SENSE_CANVIS') {
			$registreIcon.addClass('fa-check text-muted');
		} else if (response.resultat == 'REGLA_BUSTIA' || response.resultat == 'REGLA_UNITAT') {
			$registreIcon.addClass('fa-check text-success');
			$reglaBustia = $('#registres-list h3.panel-title').children('span.regla-bustia').eq(index);
			$reglaBustia.append(' ' + response.bustiaUnitatOrganitzativa.denominacio + ' / ' + response.bustiaNom);
			$reglaBustia.removeClass('text-hide');
		} else if (response.resultat == 'REGLA_BACKOFFICE') {
			$registreIcon.addClass('fa-check text-success');
			$('#registres-list h3.panel-title').children('span.regla-backoffice').eq(index).removeClass('text-hide');
		} else if (response.resultat == 'REGLA_ERROR') {
			$registreIcon.addClass('fa-check text-danger');
		}
	} else {
		$registreIcon.addClass('fa-warning text-warning');
	}
}
var finalitzarClassificacio = function(element) {
	$span = $('span.fa', element);
	$span.removeClass('fa-circle-o-notch fa-spin ');
	$span.addClass('fa-inbox');
	$botoClassificar = $(element);
	$botoClassificar.removeClass('btn-success');
	$botoClassificar.addClass('btn-default');
	$botoCancel = $('#accio-cancel', $(element).parent());
	$botoCancel.removeAttr('disabled');
	$botoCancel.text('<spring:message code="comu.boto.tancar"/>');
	$modal = $(element).closest('div.modal').parent();
	$modal.removeAttr('data-nohide');
	/*$('button.close', $modal).click();*/
}
$(document).ready(function() {
	if (${fn:length(procediments)} > 0) {
		$('#accio-classificar').removeAttr('disabled');
	}
	$(window.frameElement).load(function() {
		var $modalFooter = $('.modal-footer', $(this).parent().parent());
		var $botoClassificar = $('button#accio-classificar', $modalFooter);
		
	    $("#registreClassificarCommand").submit(function(event){
			event.preventDefault();
			event.stopPropagation();
			inicialitzarClassificacio(this);
			processarRegistres(0, this);
			return false;
	    });

		
	});
});
</script>
<style>
#registres-list {
	max-height: 400px;
	overflow-y: scroll;
}
#registres-list table {
	border-bottom: 1px solid #dddddd;
}
</style>
</head>
<body>
	<div class="alert alert-warning well-sm" role="alert">
		<span class="fa fa-warning"></span>
		<spring:message code="bustia.pendent.classificar.warning"/>
	</div>
	<p><spring:message code="bustia.pendent.classificar.multiple.seleccionades"/>: ${fn:length(registres)}</p>
	<div id="registres-list" class="panel panel-default" role="tablist" aria-multiselectable="true">
		<c:forEach var="registre" items="${registres}">
			<div class="panel-heading">
				<h3 class="panel-title" role="button" data-toggle="collapse" data-parent="#registres-list" data-target="#registre-info-${registre.id}" aria-expanded="false" aria-controls="registre-info-${registre.id}">
					<span class="state-icon fa"></span>
					${registre.numero}
					<span class="regla-backoffice text-muted text-hide"> - <span class="fa fa-paper-plane"></span> <spring:message code="bustia.pendent.classificar.multiple.regla.backoffice.aplicada"/></span>
					<span class="regla-bustia text-muted text-hide"> - <span class="fa fa-paper-plane"></span> <spring:message code="bustia.pendent.classificar.multiple.regla.bustia.aplicada"/></span>
					<span class="fa fa-caret-down pull-right"></span>
				</h3>
			</div>
			<div id="registre-info-${registre.id}" class="panel-collapse collapse registre-info">
			<table class="table table-bordered table-condensed">
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
		</c:forEach>
	</div>
	<div id="classificacio-progress" class="progress">
		<div id="classificacio-progress-success" class="progress-bar progress-bar-success" style="width: 0%">
			<span></span>
		</div>
		<div id="classificacio-progress-error" class="progress-bar progress-bar-danger" style="width: 0%">
			<span></span>
		</div>
	</div>
	<form:form cssClass="form-horizontal" commandName="registreClassificarCommand">
		<c:choose>
			<c:when test="${empty procediments}">
				<dis:inputFixed name="codiProcediment" textKey="bustia.pendent.classificar.camp.codi.procediment">
					<p class="text-danger">
						<spring:message code="bustia.pendent.classificar.no.procediments"/>
						"${registres[0].pare.nom}"
					</p>
				</dis:inputFixed>
			</c:when>
			<c:otherwise>
				<dis:inputSelect name="codiProcediment" textKey="bustia.pendent.classificar.camp.codi.procediment" optionItems="${procediments}" optionValueAttribute="codiSia" optionTextAttribute="nom" required="true"/>
			</c:otherwise>
		</c:choose>
		<div id="modal-botons" class="well">
			<button id="accio-classificar" class="btn btn-success" disabled="disabled" data-nosubmit="true"><span class="fa fa-inbox"></span> <spring:message code="bustia.pendent.classificar.submit"/></button>
			<a id="accio-cancel" href="#" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>