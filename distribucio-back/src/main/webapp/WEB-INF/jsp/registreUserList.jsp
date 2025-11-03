<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<dis:blocIconaContingutNoms/>
<html>
<head>
	<title><spring:message code="bustia.user.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<c:if test="${requestLocale == 'en'}">
		<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script> 
	</c:if>
	<script src="<c:url value="/js/select2-locales/select2_${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/webjars/Sortable/1.4.2/Sortable.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<style>
#registreFiltreCommand {
	margin-bottom: 15px;
}
table.dataTable tbody > tr.selected, table.dataTable tbody > tr > .selected {
	background-color: #fcf8e3;
	color: #666666;
}
table.dataTable thead > tr.selectable > :first-child, table.dataTable tbody > tr.selectable > :first-child {
	cursor: pointer;
}
table.dataTable tbody tr.selected a, table.dataTable tbody th.selected a, table.dataTable tbody td.selected a  {
    color: #333;
}
.list-info {
	display: block;
	padding: 3px 20px;
	clear: both;
	white-space: nowrap;
}
.alliberat {
	background: #FF9C59 !important;
	border-color: #FF9C59 !important;
}

.dataTables_length {
	display: flex;
}

.llegenda_paginador {
	display: flex;
	align-items: center;
	position: relative;
	left: 20px;
}

.item_llegenda {
	margin: 0 10px 0 10px;
	display: flex;
	align-items: center;
}
.item_llegenda span:nth-child(2){
	margin-left: 4px;
}
.item_color {
	width: 10px;
	height: 10px;
}
.llegenda_coneixement .item_color{
	background-color: #5bc0de;
}
.lleganda_tramitacio .item_color{
	background-color: #f99957;
}
.llegenda_reactivat .item_color{
	background-color: #c3c2c1;
}

/* div.extracteColumn { */
/*     word-wrap: break-word; */
/*     overflow-wrap: break-word; */
/*     overflow-wrap: anywhere; */
/* } */
li[id^="anotacio_"] {
	cursor: pointer;
}

span.badge {
	font-size: 1.2rem !important;
/* 	padding-right: 1.2rem !important; */
}

span.fa-comments {
	font-size: 2rem !important;
 	padding-right: 5px;
}

span.fa-cog {
	margin: 2px 0 0 0; 
}

tbody tr.selectable td span.caret {
	margin: 8px 0 0 2px; 
}

span.select2-container {
	width: 100% !important;
}

button#nomesAmbErrorsBtn, 
button#nomesAmbEsborranysBtn, 
button#mostrarInactivesBtn,
button#mostrarSenseAssignarBtn {
	width: 100% !important;
}

button#nomesAmbErrorsBtn span.fa-warning, 
button#nomesAmbEsborranysBtn span.fa-warning, 
button#mostrarInactivesBtn i{
	position: relative !important;
	margin-left: -5px !important;
}

button#mostrarInactivesBtn i.fa-ban, button#mostrarInactivesBtn i.fa-inbox {
	position: absolute !important;
	font-size: 2.5rem;
	margin-left: -5px !important;
}

button#mostrarInactivesBtn i.fa-inbox {
	position: absolute !important;
	font-size: 1.5rem;
	margin-left: -5px !important;
}

button#netejarFiltre, 
button#filtrar {
	width: 50%;
}

/*
.datepicker td, 
.datepicker th {
	 width: 4rem !important;
}
*/
 
.btn-default .badge {
/*   padding-right: 2rem !important; */
}

.fila-desactivada {
  position: relative;
}

.overlay-desactivada {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 5;
}

.icona-rellotge {
  font-size: 24px;
  color: #f0ad4e;
  pointer-events: auto;
  z-index: 10;
  cursor: help;
}

.fila-desactivada.selected {
	background-color: #f9f9f9 !important;
}
</style>
<script>
$.views.helpers({
	hlpIsPermesReservarAnotacions: ${isPermesReservarAnotacions},
	hlpIsPermesReservarAnotacionsAndAgafat: isPermesReservarAnotacionsAndAgafat,
	hlpIsPermesAssignarAnotacions: ${isPermesAssignarAnotacions},
	upper: toUpperCase
});

function toUpperCase(val) {
	return val.toUpperCase();
};

function isPermesReservarAnotacionsAndAgafat(agafat, agafatPer) {
	return !${isPermesReservarAnotacions} || !agafat || (agafat && toUpperCase(agafatPer.codi) == toUpperCase('${pageContext.request.userPrincipal.name}'));
}

var mostrarInactives = '${registreFiltreCommand.mostrarInactives}' === 'true';
var bustiesInactives = [];
var tipusDocumentacioFisica = '${tipusDocumentacio}';
//Funció per donar format als items de la select de bústies segons si estan actives o no
function formatSelectBustia(item) {
	if (bustiesInactives.includes(item.id))
		return $("<span>" + item.text + " <span class='fa fa-exclamation-triangle text-warning' title=\"<spring:message code='bustia.list.avis.bustia.inactiva'/>\"></span></span>");
	else
		return item.text;
}

function formatSelectTipusDocumentacio(item) {
	if (item.text == '<spring:message code="registre.tipus.doc.fisica.enum.PAPER"/>'){
		return $("<span><span class='fa fa-archive text-danger'></span> " + item.text + " </span>");
	}else if (item.text == '<spring:message code="registre.tipus.doc.fisica.enum.DIGIT_PAPER"/>'){
		return $("<span><span class='fa fa-file-code-o text-warning'></span> <span class='fa fa-archive text-warning'></span> " + item.text + " </span>");
	}else if (item.text == '<spring:message code="registre.tipus.doc.fisica.enum.DIGIT"/>'){
		return $("<span><span class='fa fa-file-code-o text-success'></span> " + item.text + " </span>");
	}else {
		return '<spring:message code="bustia.list.filtre.tipusDocFisica"/>';
	}
}
var mostrarSenseAssignar = '${mostrarSenseAssignar}' === 'true';
$(document).ready(function() {
	$("input:visible:enabled:not([readonly]),textarea:visible:enabled:not([readonly]),select:visible:enabled:not([readonly])").first().focus();
	$("#contingutBusties").addClass('active');
	$("#canviVistaReenvios").removeClass('active');
	
	$('#netejarFiltre').click(function(e) {
		$('#bustia').val('');
		$('#procesEstatSimple').val('PENDENT').change();
		$('#mostrarInactives').val(false).change();
		$('#mostrarInactivesBtn').removeClass('active');
		$('#tipusDocFisica').val('').change();
		$('#enviatPerEmail').val(null).change();
	});

	var selectButtonsInitialized = false;

	$('#taulaDades').on( 'draw.dt', function (datatable) {
		$.get( "registreUser/getNumPendents").done(function( data ) {
			$('#bustia-pendent-count').text(data);
		})
		
		if (!selectButtonsInitialized) {
			selectButtonsInitialized = true;

			$('#seleccioAll').on('click', function() {
				$.get(
						"registreUser/select",
						function(data) {
							$("#seleccioCount").html(data);
							$('#taulaDades').webutilDatatable('refresh');
						}
				);
				return false;
			});
			$('#seleccioNone').on('click', function() {
				$.get(
						"registreUser/deselect",
						function(data) {
							$("#seleccioCount").html(data);
							$('#taulaDades').webutilDatatable('select-none');
						}
				);
				return false;
			});
		}
		$("tr", this).each(function(){
			if ($(this).find("#detall-button").length > 0) {
				var pageInfo = $('#taulaDades').dataTable().api().table().page.info();
				var registreTotal = pageInfo.recordsTotal;
				var registreNumero = $(this).data('rowIndex');
				// Afegeix els paràmetres a l'enllaç dels detalls
				var url = new URL(window.location);
				var params = url.searchParams;
				params.set("registreNumero", registreNumero);
				params.set("registreTotal", registreTotal);
				var sort = $('#taulaDades').dataTable().fnSettings().aaSorting
				if (sort.length > 0) {
					params.set("ordreColumn", $($('#taulaDades').dataTable().api().column(sort[0][0]).header()).data('colName'))
					params.set("ordreDir", sort[0][1]);						
				}			
				var $a = $($(this).find("#detall-button"));
				$a.attr('href', $a.attr('href') + '?' + params.toString());
				// Afegeix els paràmetres a l'enllaç de la fila
				$(this).data('href', $(this).data('href') + '?' + params.toString());
				
				if (${isEnviarConeixementActiu}) {
					//tramitació/coneixement
					var isPerConeixement = $('#taulaDades').dataTable().api().row($(this)).data()['perConeixement'];
					if (isPerConeixement) {
						$(this).find("td:eq(0)").css('background-color', '#5bc0de');
					} else {
						$(this).find("td:eq(0)").css('background-color', '#ff9c59');
					}
				}
				
				var isReactivat = $('#taulaDades').dataTable().api().row($(this)).data()['reactivat'];
				if (isReactivat) {
					$(this).css('background-color', '#c3c2c1');
				}
				
				var isPendentExecucioMassiva = $('#taulaDades').dataTable().api().row($(this)).data()['pendentExecucioMassiva'];
				if (isPendentExecucioMassiva) {
					const $row = $(this);
					
					// Desactivam la fila
					$row.addClass('fila-desactivada');

					// Desactivam boyo i enllac
					$row.find('button, a').attr('disabled', true).css('pointer-events', 'none');

					var title = "<spring:message code="accio.massiva.icona.pendent"/>";
					
					// Afegir overlay i rellotge
					if ($row.find('.overlay-desactivada').length === 0) {
					  $row.append('<div class="overlay-desactivada"> ' +
									'<span class="fa fa-clock-o icona-rellotge" title="' + title + '"></span> ' +
								   '</div>');	
					}
				}
			}
		});
		
		//Llegenda paginador
		var $paginador = $('.dataTables_length');
		if ($paginador.find('.llegenda_paginador').length == 0) {
			var $paginador_container = $paginador.closest('.row');
			$paginador_container.find('div:first').addClass('col-md-6').removeClass('col-md-3');
			$paginador_container.find('div:nth-child(2)').addClass('col-md-6').removeClass('col-md-9');
			var llegenda = '<div class="llegenda_paginador">\
								<div class="item_llegenda">\
									<span><spring:message code="contingut.enviar.info.llegenda"/>:</span>\
								</div>';
								if (${isEnviarConeixementActiu} ) {
									llegenda += '<div class="item_llegenda lleganda_tramitacio">\
													<span class="item_color"></span>\
													<span><spring:message code="contingut.enviar.info.llegenda.processar"/></span>\
												</div>\
												<div class="item_llegenda llegenda_coneixement">\
													<span class="item_color"></span>\
													<span><spring:message code="contingut.enviar.info.llegenda.coneixement"/></span>\
												</div>';
								}
								llegenda += '<div class="item_llegenda llegenda_reactivat">\
									<span class="item_color"></span>\
									<span><spring:message code="contingut.enviar.info.llegenda.reactivat"/></span>\
								</div>\
							</div>';
			$paginador.append(llegenda);
		}
	} ).on('selectionchange.dataTable', function (e, accio, ids) {
		$.get(
				"registreUser/" + accio,
				{ids: ids},
				function(data) {
					$(".seleccioCount").html(data);
				}
		);
	});
	
	$('#mostrarInactivesBtn').click(function() {
		mostrarInactives = !$(this).hasClass('active');
		// Modifica el formulari
		$('#mostrarInactives').val(mostrarInactives).change();
		$(this).blur();
	});
	$('#mostrarInactives').change(function() {
		var actual = $('#bustia').val();
		var bustiaPerDefecte = '${bustiaPerDefecte}'
		$('#bustia').select2('val', '', true);
		$('#bustia option[value!=""]').remove();
		var baseUrl = "<c:url value='/registreUser/bustiesPermeses'/>?mostrarInactives=" + $(this).val();
		$.get(baseUrl)
			.done(function(data) {
				bustiesInactives = [];
				for (var i = 0; i < data.length; i++) {
					$('#bustia').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
					if (!data[i].activa) {
						bustiesInactives.push(data[i].id.toString());
					}
				}
				actual = actual != '' ? actual : parseInt(bustiaPerDefecte);
				$('#bustia').val(actual).change();
				if (bustiaPerDefecte != '')
					$('#filtrar').submit();
			})
			.fail(function() {
				alert("<spring:message code="error.jquery.ajax"/>");
			});
	});
	$('#mostrarInactives').change();
	
	$('#showModalProcesEstatButton').click(function(e) {
		$('#modalProcesEstat').modal();
		e.stopPropagation();
	});
	
	$('form').submit(function() {
		$.get(
				"registreUser/deselect",
				function(data) {
					$("#seleccioCount").html(data);
					$('#taulaDades').webutilDatatable('select-none');
				}
		);
		return false;
	});
	
	if (${isPermesAssignarAnotacions}) {
		var baseUrl = "<c:url value='/registreUser/assignar/usuaris'/>";
		$.get(baseUrl)
			.done(function(data) {
				$('#usuariAssignatCodi').select2('val', '', true);
				$('#usuariAssignatCodi option[value!=""]').remove();
				for (var i = 0; i < data.length; i++) {
					if ('${registreFiltreCommand.usuariAssignatCodi}' == data[i].codi)
						$('#usuariAssignatCodi').append('<option value="' + data[i].codi + '" selected>' + data[i].nom + '</option>');
					else
						$('#usuariAssignatCodi').append('<option value="' + data[i].codi + '">' + data[i].nom + '</option>');
				}
			})
			.fail(function() {
				alert("<spring:message code="error.jquery.ajax"/>");
			});
		
		$('#usuariAssignatCodi').on('change', function() {
			usuariAssignat = $(this).val();
			mostrarSenseAssignar = $('#mostrarSenseAssignarBtn').hasClass('active');
			
			if (usuariAssignat && mostrarSenseAssignar) {
				$('#mostrarSenseAssignarBtn').removeClass('active');
				$('#mostrarSenseAssignar').val(false).change();
				setCookie("${nomCookieSenseAssignar}", false);
			}
		});
		
		$('#mostrarSenseAssignarBtn').on('click', function() {
			mostrarSenseAssignar = !$(this).hasClass('active');
			// Modifica el formulari
			$('#mostrarSenseAssignar').val(mostrarSenseAssignar).change();
			$(this).blur();
			// Estableix el valor de la cookie
			setCookie("${nomCookieSenseAssignar}", mostrarSenseAssignar);
			
			$('#usuariAssignatCodi').val('');
			$('#usuariAssignatCodi').change();
			
			// Refresca la taula
			$('#taulaDades').webutilDatatable('refresh');
		});
		
		$('#mostrarSenseAssignar').change();
	}
	
	$(document).on('hidden.bs.modal', function (event) {
		var data = sessionStorage.getItem('selectedElements');
		if (data != null) {
			// Deseleccionar elements si s'ha realitzat una acció múltiple i les anotacions s'han mogut
			$(".seleccioCount").html(data);
			$('#taulaDades').webutilDatatable('refresh');
			
			sessionStorage.removeItem('selectedElements');
		}
	});
});

function bloquejar(anotacioId) {
	var currentAnotacio = $('#anotacio_' + anotacioId);
	$.get(
		"registreUser/" + anotacioId + "/bloquejar",
		function(success) {
			if (success)
				$('tr[id="row_' + anotacioId + '"]').find('button').addClass('alliberat');
			currentAnotacio.find('a').replaceWith('<a onClick="alliberar(' + anotacioId + ')"><span class="fa fa-unlock"></span>&nbsp;&nbsp;<spring:message code="comu.boto.alliberar"/></a>');
		}
	);
}

function alliberar(anotacioId, agafat, agafatPerCodi) {
	var agafatPerAltreUsuari = agafat && agafatPerCodi != '${pageContext.request.userPrincipal.name}';
	if ((agafatPerAltreUsuari && confirm("<spring:message code="bustia.pendent.accio.agafar.confirm.1"/>" + agafatPerCodi + ". <spring:message code="bustia.pendent.accio.agafar.confirm.2"/>")
			|| !agafatPerAltreUsuari)) {
		var currentAnotacio = $('#anotacio_' + anotacioId);
		$.get(
			"registreUser/" + anotacioId + "/alliberar",
			function(success) {
				if (success)
					$('tr[id="row_' + anotacioId + '"]').find('button').removeClass('alliberat');
				$('.opt_classificar_' + anotacioId + ', .opt_separator_' + anotacioId + ', .opt_reenviar_' + anotacioId + ', .opt_processar_' + anotacioId).removeClass('hidden');
				$('.opt_agafat_' + anotacioId).remove();
				currentAnotacio.find('a').replaceWith('<a onClick="bloquejar(' + anotacioId + ')"><span class="fa fa-lock"></span>&nbsp;&nbsp;<spring:message code="comu.boto.bloquejar"/></a>');
			}
		);
	}
}
</script>
</head>
<body>


	<form:form action="" method="post" cssClass="well" modelAttribute="registreFiltreCommand">
		<button id="filtrar" type="submit" name="accio" value="filtrar" class="btn btn-primary" style="display:none"></button>
		<div class="row">
			<div class="col-md-2">
				<dis:inputText name="numero" inline="true" placeholderKey="bustia.list.filtre.numero"/>
			</div>
			<div class="col-md-2">
				<dis:inputText name="titol" inline="true" placeholderKey="bustia.list.filtre.titol"/>
			</div>
			
			<div class="col-md-3">
				<dis:inputText name="numeroOrigen" inline="true" placeholderKey="bustia.list.filtre.origen.num"/>
			</div>
			<div class="col-md-3">
				<c:url value="/userajax/remitent" var="urlConsultaInicial"/>
				<c:url value="/userajax/remitent" var="urlConsultaLlistat"/>
				<dis:inputSuggest 
					name="remitent" 
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					placeholderKey="bustia.list.filtre.remitent"
					suggestValue="nom"
					suggestText="codiAndNom"
					inline="true"/>
			</div>
			<div class="col-md-2">
				<dis:inputSelect name="procesEstatSimple"  netejar="false" optionEnum="RegistreProcesEstatSimpleEnumDto" placeholderKey="bustia.list.filtre.estat" emptyOption="true" inline="true"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-2">
				<dis:inputDate name="dataRecepcioInici" inline="true" placeholderKey="bustia.list.filtre.data.rec.inical"/>
			</div>
			<div class="col-md-2">
				<dis:inputDate name="dataRecepcioFi" inline="true" placeholderKey="bustia.list.filtre.data.rec.final"/>
			</div>
			<div class="col-md-3">
				<c:url value="/unitatajax/unitat" var="urlConsultaInicial"/>
				<c:url value="/unitatajax/nomesUnitatsAmbBusties" var="urlConsultaLlistat"/>
				<dis:inputSuggest 
					name="unitatId"
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					inline="true" 
					placeholderKey="contingut.admin.filtre.uo"
					suggestValue="id"
					suggestText="codiAndNom" 
					optionTemplateFunction="formatSelectUnitat" />
			</div>
			<div class="col-md-3">
				<div class="row">
					<div class="col-md-10">
						<dis:inputSelect 
							name="bustia" 
							optionItems="${replacedByJquery}" 
							optionValueAttribute="id" 
							optionTextAttribute="nom" 
							emptyOption="true" 
							placeholderKey="bustia.list.filtre.bustia" 
							inline="true"
							optionMinimumResultsForSearch="0" 
							optionTemplateFunction="formatSelectBustia" />
					</div>
					<div class="col-md-2" style="padding-left: 0;">
						<button id="mostrarInactivesBtn" title="<spring:message code="bustia.list.filtre.mostrarInactives"/>" class="btn btn-default btn-sm<c:if test="${registreFiltreCommand.mostrarInactives}"> active</c:if>" data-toggle="button">
							<span class="fa-stack" aria-hidden="true">
								<i class="fa fa-inbox fa-stack-1x"></i>
		    	    			<i class="fa fa-ban fa-stack-2x"></i>
		   					</span>
						</button>
						<dis:inputHidden name="mostrarInactives"/>
					</div>
				</div>
			</div>
			<div class="col-md-2">
				<dis:inputText name="interessat" inline="true" placeholderKey="bustia.list.filtre.interessat"/>
			</div>	
		</div>
		<div class="row">	
			<div class="col-md-2">
				<dis:inputSelect name="enviatPerEmail" optionEnum="RegistreEnviatPerEmailEnumDto" placeholderKey="bustia.list.filtre.back.email" emptyOption="true" inline="true"/>
			</div>		
			<div class="col-md-4">			
				<dis:inputSelect 
					name="tipusDocFisica" 
					netejar="false" 
					optionItems="${tipusDocumentacio}" 
					optionValueAttribute="value" 
					optionTextKeyAttribute="text" 
					placeholderKey="bustia.list.filtre.tipusDocFisica" 
					emptyOption="true" 
					inline="true" 
					optionTemplateFunction="formatSelectTipusDocumentacio"/>
			</div>	
			<div class="col-md-3">			
				<c:url value="/procedimentajax/procediment" var="urlConsultaInicial"/>
				<c:url value="/procedimentajax/procediments" var="urlConsultaLlistat"/>
				<dis:inputSuggest 
					name="procedimentCodi"
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					inline="true" 
					placeholderKey="registre.admin.list.filtre.procediment"
					suggestValue="codiSia"
					suggestText="codiNom" />
			</div>
			<c:if test="${isPermesAssignarAnotacions}">
				<div class="col-md-3">
					<div class="row">
					<div class="col-md-10">
						<dis:inputSelect 
							name="usuariAssignatCodi" 
							optionItems="${replacedByJquery}" 
							optionValueAttribute="codi" 
							optionTextAttribute="nom" 
							emptyOption="true" 
							placeholderKey="bustia.list.filtre.usuari.assignat" 
							inline="true"
							optionMinimumResultsForSearch="0" />
						</div>
						<div class="col-md-2" style="padding-left: 0;">
							<button id="mostrarSenseAssignarBtn" title="<spring:message code="bustia.list.filtre.sense.assignar"/>" class="btn btn-default btn-sm<c:if test="${registreFiltreCommand.mostrarSenseAssignar}"> active</c:if>" data-toggle="button">
								<span class="fa-stack" aria-hidden="true">
									<i class="fa fa-user fa-stack-1x"></i>
			    	    			<i class="fa fa-ban fa-stack-2x"></i>
			   					</span>
							</button>
							<dis:inputHidden name="mostrarSenseAssignar"/>
						</div>
					</div>
				</div>
			</c:if>
			<c:if test="${! isPermesAssignarAnotacions}">
				<div class="col-md-2 d-flex">
					<button id="netejarFiltre" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button id="filtrar" type="submit" name="accio" value="filtrar" class="ml-2 btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</c:if>
		</div>
		<c:if test="${isPermesAssignarAnotacions}">
			<div class="row">
				<div class="col-md-2 d-flex" style="float: right;">
					<button id="netejarFiltre" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button id="filtrar" type="submit" name="accio" value="filtrar" class="ml-2 btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</c:if>
	</form:form>
	
	<c:set var="rol" value="user"/>	
	<script id="botonsTemplate" type="text/x-jsrender">
		<div class="text-right">
			<div class="btn-group">
				<button id="seleccioAll" title="<spring:message code="bustia.pendent.contingut.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></button>
				<button id="seleccioNone" title="<spring:message code="bustia.pendent.contingut.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></button>
				
					<button class="btn btn-default" data-toggle="dropdown"><span class="badge seleccioCount">${fn:length(seleccio)}</span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
					<ul class="dropdown-menu dropdown-left-medium">
						<li><a href="massiva/classificar/${rol}" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
							<span class="fa fa-inbox"></span> <spring:message code="bustia.pendent.accio.classificar"/>
						</a></li>
						<li><a href="massiva/reenviar/${rol}" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
							<span class="fa fa-send"></span> <spring:message code="bustia.pendent.accio.reenviar"/>
						</a></li>
						<li><a href="massiva/marcarProcessat/${rol}" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
							<span class="fa fa-check-circle-o"></span> <spring:message code="bustia.pendent.accio.marcar.processat"/>
						</a></li>
						<li><a href="massiva/marcarPendent/${rol}" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
							<span class="fa fa-undo"></span> <spring:message code="registre.user.accio.marcar.pendent"/>
						</a></li>
						<li><a href="massiva/enviarViaEmail/${rol}" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
							<span class="fa fa-envelope"></span> <spring:message code="bustia.pendent.accio.enviarViaEmail"/>
						</a></li>
						<li><a href="massiva/enviarIProcessar/${rol}" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
							<span class="fa fa-envelope"></span>+<span class="fa fa-check-circle-o"></span>
							<spring:message code="bustia.pendent.accio.enviarIProcessar"/>
						</a></li>
						<li><a href="registreComun/exportar/${rol}?format=ods">
							<span class="fa fa-download"></span> <spring:message code="registre.user.accio.grup.exportar.filtre.anotacio.ods"/>
						</a></li>	
						<li><a href="registreComun/exportar/${rol}?format=csv">
							<span class="fa fa-download"></span> <spring:message code="registre.user.accio.grup.exportar.filtre.anotacio.csv"/>
						</a></li>			
					</ul>
					
			</div>
		</div>
	</script>
	<script id="rowhrefTemplate" type="text/x-jsrender">registreUser/registre/{{:id}}</script>
	<table 
		id="taulaDades" 
		class="table table-bordered table-striped" 
		data-toggle="datatable"
		data-url="<c:url value="/registreUser/datatable"/>"
		data-filter="#registreFiltreCommand"
		data-botons-template="#botonsTemplate"
		data-selection-enabled="true"
		data-default-order="18"
		data-default-dir="desc"
		data-rowhref-template="#rowhrefTemplate" 
		data-rowhref-toggle="modal"
		data-rowhref-maximized="true"
		data-refresh-tancar="true">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"></th>
				<th data-col-name="potModificar" data-visible="false"></th>
				<th data-col-name="error" data-visible="false"></th>
				<th data-col-name="alerta" data-visible="false"></th>
				<th data-col-name="enviatPerEmail" data-visible="false"></th>
				<th data-col-name="enviamentsPerEmail" data-visible="false"></th>
				<th data-col-name="procesEstatSimple"  data-visible="false"></th>
				<th data-col-name="perConeixement"  data-visible="false"></th>
				<th data-col-name="pendentExecucioMassiva"  data-visible="false"></th>
				<th data-col-name="reactivat"  data-visible="false"></th>
				<th data-col-name="agafat" data-visible="false"></th>
				<th data-col-name="agafatPer.codi" data-visible="false"></th>
				<th data-col-name="motiuRebuig" data-visible="false"></th>
				<th data-col-name="documentacioFisicaCodi" data-orderable="true" data-template="#docFisTemplate" data-visible="false"></th>	
			
<!-- 				<th data-col-name="procesError" data-visible="false">#</th> -->
				<th data-col-name="numero"><spring:message code="bustia.pendent.columna.numero"/></th>			
				
<!-- 				<th data-col-name="numero"><spring:message code="bustia.pendent.columna.numero"/></th> -->
<!-- 				<th data-col-name="extracte" data-template="#extracteTemplate"> -->
				<th data-col-name="extracte"><spring:message code="bustia.pendent.columna.titol"/>											
				
<!-- 					<script id="extracteTemplate" type="text/x-jsrender"> -->
<!-- 						<div class="extracteColumn"> -->
<!-- 							{{:extracte}} -->
<!-- 						</div> -->
<!-- 					</script> -->
				</th>			
				<th data-col-name="numeroOrigen"><spring:message code="bustia.list.filtre.origen.num"/></th>
														
<!-- 				<th data-col-name="numeroOrigen"><spring:message code="bustia.list.filtre.origen.num"/></th> -->
				<th data-col-name="darrerMovimentUsuari" data-orderable="true" data-template="#darrerMovimentTemplate">

<!-- 				<th data-col-name="darrerMovimentUsuari" data-orderable="false" data-template="#darrerMovimentTemplate"> -->
					<spring:message code="bustia.pendent.columna.remitent"/>
					<script id="darrerMovimentTemplate" type="text/x-jsrender">
						{{if darrerMovimentUsuari}}
	 						{{if darrerMovimentOrigenUoAndBustia}}
 								<div align="left">
									/<span class="fa fa-sitemap" title="{{:darrerMovimentOrigenUoAndBustia}}"/>/<span class="fa fa-inbox" title="{{:darrerMovimentOrigenUoAndBustia}}"/>
 								</div>
								<div align="left">
									{{:darrerMovimentUsuari.nom}}
								</div>
 							{{else}}
 								<span class="fa fa-home" title=""></span>
 							{{:oficinaDescripcio}}<br/>({{:darrerMovimentUsuari.nom}})
 							{{/if}}
 						{{else}}
							<span class="fa fa-ban" title="<spring:message code="bustia.pendent.columna.remitent.buit"/>"></span>
 						{{/if}}
					</script>
				</th>
				<th data-col-name="data" data-converter="datetime"><spring:message code="bustia.pendent.columna.data"/></th>

<!-- 				<th data-col-name="data" data-converter="datetime"><spring:message code="bustia.pendent.columna.data"/></th> -->
				<th data-col-name="procesEstat" data-orderable="true" data-template="#estatTemplate">
			
<!-- 				<th data-col-name="procesEstat" data-orderable="true" data-template="#estatTemplate"> -->
					<spring:message code="bustia.pendent.columna.estat"/> <span class="fa fa-list" id="showModalProcesEstatButton" title="<spring:message code="bustia.user.proces.estat.legend"/>" style="cursor:over; opacity: 0.5"></span>
					<script id="estatTemplate" type="text/x-jsrender">
						<div class="d-flex">
						<div>
						{{if procesEstat == 'ARXIU_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.ARXIU_PENDENT"/>

							<span {{if reintentsEsgotat}} style="color: #a94442" {{else}} style="color: #8a6d3b" {{/if}} title="<spring:message code="contingut.registre.reintents.msg.seHanRealizat"/> {{:procesIntents}} <spring:message code="contingut.registre.reintents.msg.intentsDeUnMaximDe"/> {{:maxReintents}} <spring:message code="contingut.registre.reintents.msg.deGuardarAnnexosAlArxiu"/>">
								(<spring:message code="contingut.registre.reintents.msg.reintent"/> {{:procesIntents}}/{{:maxReintents}})
							</span>
						{{else procesEstat == 'REGLA_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.REGLA_PENDENT"/>
							{{if regla != null}}
								<br> <span class="regla-nom" style="font-size:1rem">{{:regla.nom}}</span>
							{{else}}
								<span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="registre.admin.list.icon.annexos.estat.pendent.regla.sense.regla"/>"> </span>
							{{/if}}
						{{else procesEstat == 'BUSTIA_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.BUSTIA_PENDENT"/>
						{{else procesEstat == 'BUSTIA_PROCESSADA'}}
							<spring:message code="registre.proces.estat.enum.BUSTIA_PROCESSADA"/>
						{{else procesEstat == 'BACK_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.BACK_PENDENT"/>
							<br> <span class="back-codi" style="font-size:1rem">{{:backCodi}}</span>
						{{else procesEstat == 'BACK_COMUNICADA'}}
							<spring:message code="registre.proces.estat.enum.BACK_COMUNICADA"/>
							<br> <span class="back-codi" style="font-size:1rem">{{:backCodi}}</span>
						{{else procesEstat == 'BACK_REBUDA'}}
							<spring:message code="registre.proces.estat.enum.BACK_REBUDA"/>
							<br> <span class="back-codi" style="font-size:1rem">{{:backCodi}}</span>
						{{else procesEstat == 'BACK_PROCESSADA'}}
							{{if backCodi == null}}
								<spring:message code="registre.proces.estat.enum.BACK_PROCESSADA"/>
							{{else backCodi != null}}
								<spring:message code="registre.proces.estat.detall.BACK_PROCESSADA"/>
								<br> <span class="" style="font-size:1rem">{{:backCodi}}</span>
							{{/if}}
						{{else procesEstat == 'BACK_REBUTJADA'}}
							<spring:message code="registre.proces.estat.enum.BACK_REBUTJADA"/>
							<br> <span class="back-codi" style="font-size:1rem">{{:backCodi}}</span>
						{{else procesEstat == 'BACK_ERROR'}}
							<spring:message code="registre.proces.estat.enum.BACK_ERROR"/>		
							<br> <span class="back-codi" style="font-size:1rem">{{:backCodi}}</span>					
						{{/if}}
						</div>
						{{if motiuRebuig}}
							<div class="d-flex" style="align-items: end;">
    							<span class="fa fa-exclamation-circle text-warning" title="{{:motiuRebuig}}"></span>
							</div>						
						{{/if}}
						</div>
					</script>
				</th>
				<th data-col-name="procesError" data-orderable="false" data-template="#procesErrorTemplate">

<!-- 				<th data-col-name="procesError" data-orderable="false" data-template="#procesErrorTemplate"> -->
					<spring:message code="bustia.pendent.columna.avisos"/>
					<script id="procesErrorTemplate" type="text/x-jsrender">
						<center>
						<div class="llista-avisos d-flex">
						<p>
						{{if enviatPerEmail}}
							<span class="fa fa-envelope" title="<spring:message code="contingut.registre.enviatPerEmail"/>:
							{{for enviamentsPerEmail}} {{>}} 
							{{/for}}"></span>
						{{/if}}
						{{if documentacioFisicaCodi == '1'}}
							<span class="fa fa-archive" style="color: #D9534F;" title="<spring:message code="registre.tipus.doc.fisica.enum.PAPER"/>"/>
							
						{{else documentacioFisicaCodi == '2'}}
							<span class="fa fa-file-code-o" style="color: #F0AD4E;" title="<spring:message code="registre.tipus.doc.fisica.enum.DIGIT_PAPER"/>"/>
							<span class="fa fa-archive" style="color: #F0AD4E;" title="<spring:message code="registre.tipus.doc.fisica.enum.DIGIT_PAPER"/>"/>
							
						{{else documentacioFisicaCodi == '3'}}
							<span class="fa fa-file-code-o" style="color: #5CB85C;" title="<spring:message code="registre.tipus.doc.fisica.enum.DIGIT"/>"/>
							
						{{/if}}
						{{if annexosEstatEsborrany > 0}}
							<span class="fa fa-exclamation-circle text-warning" title="<spring:message code="registre.admin.list.icon.annexos.estat.esborrany"/>"></span>
							
						{{/if}}
						{{if alerta}}
							<span class="fa fa-sticky-note-o text-warning" title="<spring:message code="contingut.errors.registre.regles.segonpla"/>"></span>
							
						{{/if}}
						{{if procesError != null}}
							{{if procesEstat == 'ARXIU_PENDENT'}}
								<span class="fa fa-warning text-danger" title="<spring:message code="registre.proces.estat.enum.ARXIU_PENDENT.error"/>"></span>
							{{else procesEstat == 'REGLA_PENDENT'}}
								<span class="fa fa-warning text-danger" title="<spring:message code="registre.proces.estat.enum.REGLA_PENDENT.error"/>"></span>
							{{else procesEstat == 'BACK_PENDENT'}}
								<span class="fa fa-warning text-danger" title="<spring:message code="registre.proces.estat.enum.BACK_PENDENT.error"/>"></span>
							{{else procesEstat == 'BACK_ERROR'}}
								<span class="fa fa-warning text-danger" title="<spring:message code="registre.proces.estat.enum.BACK_ERROR.error"/>"></span>
							{{else}}
								<span class="fa fa-warning text-danger" title="<spring:message code="registre.proces.estat.enum.default"/>"></span>
							{{/if}}
						{{/if}}
						</p>
						</div>
						</center>
					</script>
				</th>
				<th data-col-name="path" data-template="#cellPathTemplate" data-orderable="false">
				
<!-- 				<th data-col-name="path" data-template="#cellPathTemplate" data-orderable="false"> -->
					<spring:message code="bustia.pendent.columna.localitzacio"/>
					<script id="cellPathTemplate" type="text/x-jsrender">
						{{for path}}/
							{{if bustia}}{{if #getIndex() == 0}}<span class="fa ${iconaUnitat}" title="<spring:message code="contingut.icona.unitat"/>"></span>{{else}}<span class="fa ${iconaBustia}" title="<spring:message code="contingut.icona.bustia"/>"></span>{{/if}}{{/if}}
							{{:nom}}
						{{/for}}
						{{if !bustiaActiva}}
							<span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="bustia.list.avis.bustia.inactiva"/>"></span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="interessatsResum" data-orderable="false">				
					<spring:message code="bustia.pendent.columna.interessats"/>
				</th>	
				<th data-col-name="agafatPer.nom" data-visible="${isPermesAssignarAnotacions}">
+					<spring:message code="bustia.pendent.columna.agafat"/>
+				</th>
				<th data-col-name="numComentaris" data-orderable="false" data-template="#cellPermisosTemplate">							
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						<a href="./contingut/{{:id}}/comentaris" data-toggle="modal" data-refresh-tancar="true" data-modal-id="comentaris{{:id}}" class="btn btn-default">
							<span class="fa fa-lg fa-comments"></span>
							<span class="badge">{{:numComentaris}}</span>
						</a>

					</script>
				</th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsContingutTemplate">
					<script id="cellAccionsContingutTemplate" type="text/x-jsrender">
						<div id="div-btn-accions" class="dropdown">
							<button id="btn-accions" class="btn btn-primary" data-toggle="dropdown" style="display:flex;">
								<span class="fa fa-cog"></span>
								<span class="hidden_dis"><spring:message code="comu.boto.accions"/></span>
								<span class="caret"></span></button>
							<ul class="dropdown-menu dropdown-left-high">
								<li>
									<a id="detall-button"
										href="registreUser/registre/{{:id}}"
											data-toggle="modal" data-maximized="true"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a>
								</li>
								<li><a href="./contingut/{{:id}}/log" data-toggle="modal" data-maximized="true"><span class="fa fa-list"></span>&nbsp;<spring:message code="comu.boto.historial"/></a></li>
								{{if alerta}}
									<li><a href="./registreUser/pendent/{{:id}}/alertes" data-toggle="modal"><span class="fa fa-sticky-note-o"></span>&nbsp;<spring:message code="bustia.pendent.accio.llistat.alertes"/></a></li>
								{{/if}}
								<li role="separator" class="divider"></li>
                            {{if potModificar}}
								{{!-- CLASSIFICAR ---}}
								<li{{if !~hlpIsPermesReservarAnotacionsAndAgafat(agafat, agafatPer)}} class="opt_classificar_{{:id}} hidden"{{/if}}{{if procesEstat == 'ARXIU_PENDENT'}} class="disabled" {{/if}}><a {{if procesEstat != 'ARXIU_PENDENT'}} href="./registreUser/classificar/{{:id}}" {{/if}}  data-toggle="modal"><span class="fa fa-inbox"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.classificar"/> ...</a></li>
								<li role="separator" class="divider opt_separator_{{:id}}{{if !~hlpIsPermesReservarAnotacionsAndAgafat(agafat, agafatPer)}}  hidden"{{/if}}"></li>
                            {{/if}}
								{{!-- VIA MAIL ---}}
								<li{{if procesEstat == 'ARXIU_PENDENT' && !reintentsEsgotat}} class="disabled" {{/if}}><a {{if !(procesEstat == 'ARXIU_PENDENT' && !reintentsEsgotat)}} href="./registreUser/enviarViaEmail/{{:id}}" {{/if}} data-toggle="modal"><span class="fa fa-envelope"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.enviarViaEmail"/>...</a></li>
                            {{if potModificar}}
								{{!-- REENVIAR ---}}
								<li{{if !~hlpIsPermesReservarAnotacionsAndAgafat(agafat, agafatPer)}} class="opt_reenviar_{{:id}} hidden"{{/if}}><a href="./registreUser/pendent/{{:id}}/reenviar" data-toggle="modal" data-maximized="true"><span class="fa fa-send"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.reenviar"/>...</a></li>
								{{!-- PROCESSAR ---}}
								{{if procesEstatSimple == 'PENDENT'}}
									<li{{if !~hlpIsPermesReservarAnotacionsAndAgafat(agafat, agafatPer)}} class="opt_processar_{{:id}} hidden"{{/if}} {{if !(procesEstat == 'BUSTIA_PENDENT' || (procesEstat == 'ARXIU_PENDENT' && reintentsEsgotat) || procesEstat == 'BACK_REBUTJADA')}} class="disabled" {{/if}}><a data-refresh-tancar="true" {{if procesEstat == 'BUSTIA_PENDENT' || (procesEstat == 'ARXIU_PENDENT' && reintentsEsgotat)  || procesEstat == 'BACK_REBUTJADA'}} href="./registreUser/pendent/{{:id}}/marcarProcessat" {{/if}} data-toggle="modal"><span class="fa fa-check-circle-o"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.marcar.processat"/>...</a></li>
								{{/if}}
								{{if procesEstat == 'BUSTIA_PROCESSADA'}}
									<li ><a href="./registreUser/{{:id}}/marcarPendent" data-toggle="modal"><span class="fa fa-undo"></span>&nbsp;&nbsp;<spring:message code="registre.user.accio.marcar.pendent"/>...</a></li>
								{{/if}}
                            {{/if}}
								<li>
									{{!-- DESCARREGAR ZIP ---}}
									<a href="<c:url value="/contingut/registre/{{:id}}/descarregarZip/DOCUMENT_ORIGINAL"/>">
										<span class="fa fa-download"></span> <spring:message code="registre.annex.descarregar.zip.vo"/>
									</a>
								</li>
								<li>
									{{!-- DESCARREGAR ZIP ---}}
									<a href="<c:url value="/contingut/registre/{{:id}}/descarregarZip/DOCUMENT"/>">
										<span class="fa fa-download"></span> <spring:message code="registre.annex.descarregar.zip.cai"/>
									</a>
								</li>
								{{if ~hlpIsPermesReservarAnotacions}}
									<li role="separator" class="divider"></li>
									{{if !agafat}}
										<li id="anotacio_{{:id}}"><a onClick="bloquejar({{:id}})"><span class="fa fa-lock"></span>&nbsp;&nbsp;<spring:message code="comu.boto.bloquejar"/></a></li>
										{{if ~hlpIsPermesAssignarAnotacions}}
											<li ><a href="./registreUser/assignar/{{:id}}" data-toggle="modal"><span class="fa fa-user-plus"></span>&nbsp;&nbsp;<spring:message code="registre.user.accio.assignar"/> ...</a></li>
										{{/if}}
									{{else}}
										<li id="anotacio_{{:id}}"><a onClick="alliberar({{:id}}, {{:agafat}}, '{{:agafatPer.codi}}')"><span class="fa fa-unlock"></span>&nbsp;&nbsp;<spring:message code="comu.boto.alliberar"/></a></li>	
										{{if ~hlpIsPermesAssignarAnotacions}}
											<li ><a href="./registreUser/assignar/{{:id}}" data-toggle="modal"><span class="fa fa-user-plus"></span>&nbsp;&nbsp;<spring:message code="registre.user.accio.reassignar"/> ...</a></li>
										{{/if}}
										{{if ~upper(agafatPer.codi) != ~upper('${pageContext.request.userPrincipal.name}')}}									
											<li class="opt_agafat_{{:id}} list-info"><spring:message code="bustia.pendent.accio.agafatper"/>&nbsp;&nbsp;{{:agafatPer.codi}}</li>									
										{{/if}}
									{{/if}}
								{{/if}}
							</ul>
						</div>
					</script>
				</th>
				<th data-col-name="bustiaActiva" data-visible="false"></th>
				<th data-col-name="reintentsEsgotat" data-visible="false"></th>
				<th data-col-name="procesIntents" data-visible="false"></th>
				<th data-col-name="maxReintents" data-visible="false"></th>
				<th data-col-name="darrerMovimentOrigenUoAndBustia" data-visible="false" data-orderable="false"></th>
				<th data-col-name="oficinaDescripcio" data-visible="false" data-orderable="false"></th>
				<th data-col-name="backCodi" data-visible="false"></th>
			</tr>
		</thead>
	</table>
	
	<!-- Modal pels estats del processament -->
	<div id="modalProcesEstat" class="modal fade">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="<spring:message code="comu.boto.tancar"/>"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title"><span class="fa fa-list"></span> <spring:message code="bustia.user.proces.estat.legend"></spring:message></h4>
				</div>
				<div class="modal-body">
					<ul>
						<c:set var="enumValues" value="<%=es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum.values()%>"/>
						<c:forEach items="${enumValues}" var="enumValue">
						  	<li>
						  		<strong><spring:message code="registre.proces.estat.enum.${enumValue}"/></strong> :
						  		<br/>
						  		<span><spring:message code="registre.proces.estat.enum.${enumValue}.info"/></span>
						  	</li>
						</c:forEach>
					</ul>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.tancar"/></button>
				</div>
			</div>
		</div>
	</div>
	
</body>
</html>