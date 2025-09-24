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
	<title><spring:message code="bustia.user.list.moviments.titol"/></title>
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

button:focus {
	outline:0;
}

button:focus[aria-pressed="false"] {
	background-color: #fff;
}
.botons > div > span {
	background-color: #efefef;
	padding: 5px;
}

#div-btn-accions button span {
    word-wrap: break-word;
    overflow-wrap: break-word;
    overflow-wrap: anywhere;
	font-size: 1.2rem;
	width: 0%; 
} 

#div-btn-accions button {
	white-space: normal;
	word-wrap: break-word;
	font-size: 1.5rem;
	width: 100%; 
}

span.badge {
	font-size: 1.2rem !important;
/* 	padding-right: 1.2rem !important; */
}

span.fa-comments {
	font-size: 2rem !important;
	margin-right: 2rem
}

span.fa-cog {
	margin: 4px 1.5rem 0 0; 
}

tbody tr.selectable td #div-btn-accions #btn-accions span.caret {
	margin: 8px 0 0 2px; 
}

span.select2-container {
	width: 100% !important;
}

button#nomesAmbErrorsBtn, 
button#nomesAmbEsborranysBtn, 
button#mostrarInactivesBtn, 
button#mostrarInactivesOrigenBtn {
	width: 100% !important;
}

button#nomesAmbErrorsBtn span.fa-warning, 
button#nomesAmbEsborranysBtn span.fa-warning, 
button#mostrarInactivesBtn i, 
button#mostrarInactivesOrigenBtn i{
	position: relative !important;
	margin-left: -5px !important;
}

button#mostrarInactivesBtn i.fa-ban, 
button#mostrarInactivesBtn i.fa-inbox,
button#mostrarInactivesOrigenBtn i.fa-ban, 
button#mostrarInactivesOrigenBtn i.fa-inbox {
	position: absolute !important;
	font-size: 2.5rem;
	margin-left: -5px !important;
}

button#mostrarInactivesBtn i.fa-inbox,
button#mostrarInactivesOrigenBtn i.fa-inbox {
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

</style>
<script>
var mostrarInactives = '${registreFiltreCommand.mostrarInactives}' === 'true';
var bustiesInactives = [];
//Funció per donar format als items de la select de bústies segons si estan actives o no
function formatSelectBustia(item) {
	if (bustiesInactives.includes(item.id))
		return $("<span>" + item.text + " <span class='fa fa-exclamation-triangle text-warning' title=\"<spring:message code='bustia.list.avis.bustia.inactiva'/>\"></span></span>");
	else
		return item.text;
}

$(document).ready(function() {
	$("input:visible:enabled:not([readonly]),textarea:visible:enabled:not([readonly]),select:visible:enabled:not([readonly])").first().focus();
	$("#canviVistaReenvios").addClass('active');
	$("#contingutBusties").removeClass('active');
	
	$('#netejarFiltre').click(function(e) {
		$('#bustia').val('');
		$('#bustiaOrigen').val('');
		$('#procesEstatSimple').val('PENDENT').change();
		$('#mostrarInactives').val(false).change();
		$('#mostrarInactivesBtn').removeClass('active');
		$('#mostrarInactivesOrigen').val(false).change();
		$('#mostrarInactivesOrigenBtn').removeClass('active');
		$('#tipusDocFisica').val('').change();
	});

	$('#taulaDades').on( 'draw.dt', function () {
		$(".botons > div > span").hide();
		$.get( "./getNumPendents").done(function( data ) {
			$('#bustia-pendent-count').text(data);
		})
		$("tr", this).each(function(){
			if ($(this).find("#detall-button").length > 0) {
				var pageInfo = $('#taulaDades').dataTable().api().table().page.info();
				var registreTotal = pageInfo.recordsTotal;
				var registreNumero = $(this).data('rowIndex');
				registreNumero = (registreNumero > 10) ? 1 : registreNumero;
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
				params.set("isVistaMoviments", true);
				var currentRowData = $('#taulaDades').dataTable().api().row(registreNumero - 1).data();
				var destiLogic = null;
				if (currentRowData != undefined)
					destiLogic = currentRowData.destiLogic;
				params.set("destiLogic", destiLogic);
						
				var $a = $($(this).find("#detall-button"));
				$a.attr('href', $a.attr('href') + '?' + params.toString());
				// Afegeix els paràmetres a l'enllaç de la fila
				$(this).data('href', $(this).data('href') + '?' + params.toString());
			}
		});
	} ).on('selectionchange.dataTable', function (e, accio, ids) {
		$.get(
				"../registreUser/" + accio + "/moviments",
				{ids: ids},
				function(data) {
					if (data == 0)
						$(".botons > div > span").hide();
					else
						$(".botons > div > span").show();
					$(".seleccioCount").html(data + " <spring:message code="bustia.pendent.contingut.seleccio.moviments"/>");
				}
		);
	});
	
	$('#taulaDades').on( 'draw.dt', function () {
	$('#seleccioAll').on('click', function() {
		$.get(
				"../registreUser/select/moviments",
				function(data) {
					$("#seleccioCount").html(data);
					$('#taulaDades').webutilDatatable('refresh');
				}
		);
		return false;
	});
	$('#seleccioNone').on('click', function() {
		$.get(
				"../registreUser/deselect/moviments",
				function(data) {
					$("#seleccioCount").html(data);
					$('#taulaDades').webutilDatatable('select-none');
					$('#taulaDades').webutilDatatable('refresh');
				}
		);
		return false;
	});
	});
	$('#mostrarInactivesBtn').click(function() {
		mostrarInactives = !$(this).hasClass('active');
		// Modifica el formulari
		$('#mostrarInactives').val(mostrarInactives).change();
		$(this).blur();
	});
	$('#mostrarInactives').change(function() {
		//>>> Valor actual bústia destí
		var actual = $('#bustia').val();
		var bustiaPerDefecte = '${bustiaPerDefecte}'
		//>>> Bústia destí
		$('#bustia').select2('val', '', true);
		$('#bustia option[value!=""]').remove();
		var baseUrl = "<c:url value='/registreUser/busties'/>?mostrarInactives=" + $(this).val();
		$.get(baseUrl)
			.done(function(data) {
				bustiesInactives = [];
				for (var i = 0; i < data.length; i++) {
					//>>> Bústia destí
					$('#bustia').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
					if (!data[i].activa) {
						bustiesInactives.push(data[i].id.toString());
					}
				}
				$('#bustia').val(actual).change();
				$('#filtrar').submit();
			})
			.fail(function() {
				alert("<spring:message code="error.jquery.ajax"/>");
			});
	});
	$('#mostrarInactivesOrigenBtn').click(function() {
		mostrarInactives = !$(this).hasClass('active');
		// Modifica el formulari
		$('#mostrarInactivesOrigen').val(mostrarInactives).change();
		$(this).blur();
	});
	$('#mostrarInactivesOrigen').change(function() {
		//>>> Valor actual bústia origen
		var actualOrigen = $('#bustiaOrigen').val();
		//>>> Bústia origen
		$('#bustiaOrigen').select2('val', '', true);
		$('#bustiaOrigen option[value!=""]').remove();
		var baseUrl = "<c:url value='/registreUser/bustiesOrigen'/>?mostrarInactivesOrigen=" + $(this).val();
		$.get(baseUrl)
			.done(function(data) {
				bustiesInactives = [];
				for (var i = 0; i < data.length; i++) {
					//>>> Bústia origen
					$('#bustiaOrigen').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
					if (!data[i].activa) {
						bustiesInactives.push(data[i].id.toString());
					}
				}
				$('#bustiaOrigen').val(actualOrigen).change();
			})
			.fail(function() {
				alert("<spring:message code="error.jquery.ajax"/>");
			});
	});
	$('#mostrarInactives').change();
	$('#mostrarInactivesOrigen').change();
	
	$('#showModalProcesEstatButton').click(function(e) {
		$('#modalProcesEstat').modal();
		e.stopPropagation();
	});
	
	$('form').submit(function() {
		$.get(
			"../registreUser/deselect/moviments",
			function(data) {
				$("#seleccioCount").html(data);
				$('#taulaDades').webutilDatatable('select-none');
				$('#taulaDades').webutilDatatable('refresh');
			}
		);
		return false;
	});
	
});
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
				<div class="row">
					<div class="col-md-10">
						<dis:inputSelect 
									name="bustiaOrigen" 
									optionItems="${bustiesUsuari}" 
									optionValueAttribute="id" 
									optionTextAttribute="nom" 
									emptyOption="true" 
									placeholderKey="bustia.list.filtre.bustia.origen" 
									inline="true"
									optionMinimumResultsForSearch="0" 
									optionTemplateFunction="formatSelectBustia" />
					</div>
					<div class="col-md-2" style="padding-left: 0;">
						<button id="mostrarInactivesOrigenBtn" title="<spring:message code="bustia.list.filtre.mostrarInactives"/>" class="btn btn-default btn-sm<c:if test="${registreFiltreCommand.mostrarInactives}"> active</c:if>" data-toggle="button">
							<span class="fa-stack" aria-hidden="true">
								<i class="fa fa-inbox fa-stack-1x"></i>
					   	  			<i class="fa fa-ban fa-stack-2x"></i>
							</span>
						</button>
						<dis:inputHidden name="mostrarInactivesOrigen"/>
					</div>
				</div>
			</div>
			<div class="col-md-3">
				<div class="row">
					<div class="col-md-10">
						<dis:inputSelect 
									name="bustia" 
									optionItems="${bustiesUsuari}" 
									optionValueAttribute="id" 
									optionTextAttribute="nom" 
									emptyOption="true" 
									placeholderKey="bustia.list.filtre.bustia.desti" 
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
		</div>
		<div class="row">
			<div class="col-md-2">
				<dis:inputSelect name="enviatPerEmail" optionEnum="RegistreEnviatPerEmailEnumDto" placeholderKey="bustia.list.filtre.back.email" emptyOption="true" inline="true"/>
			</div>
			<div class="col-md-2">
				<dis:inputText name="interessat" inline="true" placeholderKey="bustia.list.filtre.interessat"/>
			</div>	
			<div class="col-md-3"></div>
			<div class="col-md-3"></div>
			<div class="col-md-2 d-flex">
				<button id="netejarFiltre" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
				<button id="filtrar" type="submit" name="accio" value="filtrar" class="ml-2 btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
			</div>
		</div>
	</form:form>
	
	
	
	<script id="botonsTemplate" type="text/x-jsrender">
		<div class="text-right">
			<span class="fa fa-info-circle" data-toggle="tooltip" title="<spring:message code="bustia.pendent.contingut.seleccio.info.1"/>"></span>
			<div class="btn-group">
				<button id="seleccioAll" title="<spring:message code="bustia.pendent.contingut.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></button>
				<button id="seleccioNone" title="<spring:message code="bustia.pendent.contingut.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></button>
				
					<button class="btn btn-default" data-toggle="dropdown"><span class="badge seleccioCount">${fn:length(seleccio)} <spring:message code="bustia.pendent.contingut.seleccio.moviments"/></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
					<ul class="dropdown-menu">
						<li>
							<a href="../massiva/reenviar/user?isVistaMoviments=true" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
								<spring:message code="bustia.pendent.accio.reenviar"/>
								<span class="fa fa-info-circle" title="<spring:message code="bustia.pendent.contingut.seleccio.info.3"/>"></span>
							</a>
						</li>
						<li>
							<a href="../massiva/enviarViaEmail/user?isVistaMoviments=true" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
								<spring:message code="bustia.pendent.accio.enviarViaEmail"/>
								<span class="fa fa-info-circle" title="<spring:message code="bustia.pendent.contingut.seleccio.info.2"/>"></span>
							</a>
						</li>
					</ul>
					
			</div>
		</div>
	</script>
	<script id="rowhrefTemplate" type="text/x-jsrender">registre/{{:id}}</script>
	<table 
		id="taulaDades" 
		class="table table-bordered table-striped"style="width:100%"
		data-toggle="datatable"
		data-url="<c:url value="/registreUser/moviments/datatable"/>"
		data-filter="#registreFiltreCommand"
		data-botons-template="#botonsTemplate"
		data-selection-enabled="true"
		data-default-order="13"
		data-default-dir="desc"
		data-rowhref-template="#rowhrefTemplate" 
		data-rowhref-toggle="modal"
		data-rowhref-maximized="true"
		data-refresh-tancar="true">
		<thead>
			<tr>
				<th data-col-name="movimentId" data-visible="false"></th>
				<th data-col-name="error" data-visible="false"></th>
				<th data-col-name="alerta" data-visible="false"></th>
				<th data-col-name="regla" data-visible="false"></th>
				<th data-col-name="enviatPerEmail" data-visible="false"></th>
				<th data-col-name="enviamentsPerEmail" data-visible="false"></th>
				<th data-col-name="procesEstatSimple"  data-visible="false">
				<th data-col-name="procesError" data-visible="false">#</th>
				<th data-col-name="destiLogic" data-visible="false">#</th>
				<th data-col-name="numero"style="max-width: 10%; min-width: 70px" data-template="#contingutTemplate">
					<spring:message code="bustia.pendent.columna.numero"/>
					<script id="contingutTemplate" type="text/x-jsrender">
						<span class="fa fa-book" title="<spring:message code="bustia.pendent.tipus.enum.REGISTRE"/>"></span>
						{{:numero}}
						{{if alerta}}
							<span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="contingut.errors.registre.regles.segonpla"/>"></span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="extracte" style="max-width: 10%; min-width: 70px" style="min-width:150px;">
					<spring:message code="bustia.pendent.columna.titol"/>
				</th>							
				<th data-col-name="numeroOrigen" style="max-width: 10%; min-width: 50px"><spring:message code="bustia.list.filtre.origen.num"/></th>
				<th data-col-name="darrerMovimentUsuari.nom"style="max-width: 10%; min-width: 50px" data-orderable="true"><spring:message code="bustia.pendent.columna.remitent"/></th>
				<th data-col-name="data" style="min-width: 55px" data-converter="datetime" ><spring:message code="bustia.pendent.columna.data"/></th>
				
				<th data-col-name="procesError" style="max-width: 10%; min-width: 50px" data-orderable="false" data-template="#procesErrorTemplate">
					<spring:message code="comu.error"/>
					<script id="procesErrorTemplate" type="text/x-jsrender">
						<center>
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
						</center>
					</script>
				</th>
				<th data-col-name="pathInicial" style="max-width: 10%; min-width: 70px" data-template="#cellPathInicialTemplate" width="15%" data-orderable="false">
					<spring:message code="bustia.pendent.columna.localitzacio.inicial"/>
					<script id="cellPathInicialTemplate" type="text/x-jsrender">
						{{if pathInicial}}
							{{for pathInicial}}/
								{{if bustia}}{{if #getIndex() == 0}}<span class="fa ${iconaUnitat}" title="<spring:message code="contingut.icona.unitat"/>"></span>{{else}}<span class="fa ${iconaBustia}" title="<spring:message code="contingut.icona.bustia"/>"></span>{{/if}}{{/if}}
								{{:nom}}
							{{/for}}
							{{if !bustiaActiva}}
								<span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="bustia.list.avis.bustia.inactiva"/>"></span>
							{{/if}}
						{{/if}}
					</script>
				</th>
				<%--
				<th data-col-name="id" data-orderable="false" data-template="#cellMovimentsContingutTemplate" width="5%">
					<spring:message code="bustia.pendent.columna.moviments"/>
					<script id="cellMovimentsContingutTemplate" type="text/x-jsrender">
					<center>
						<a href="../contingut/{{:id}}/log#moviments"  class="btn btn-success center" data-toggle="modal" data-maximized="true"><span class="fa fa-list"></span></a>
					</center>
					</script>
				</th>
				 --%>
				<th data-col-name="path" style="max-width: 10%; min-width: 70px" data-template="#cellPathTemplate" width="15%" data-orderable="false">
					<spring:message code="bustia.pendent.columna.localitzacio.actual"/>
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
				<th data-col-name="interessatsResum" style="max-width: 10%; min-width: 70px" data-orderable="false">
					<spring:message code="bustia.pendent.columna.interessats"/>
				</th>
				<th data-col-name="procesEstat" data-orderable="true" style="max-width: 10%; min-width: 70px"  data-template="#estatTemplate">
					<spring:message code="bustia.pendent.columna.estat"/> <span class="fa fa-list" id="showModalProcesEstatButton" title="<spring:message code="bustia.user.proces.estat.legend"/>" style="cursor:over; opacity: 0.5"></span>
					<script id="estatTemplate" type="text/x-jsrender">
						{{if enviatPerEmail}}
							<span class="fa fa-envelope" title="<spring:message code="contingut.registre.enviatPerEmail"/>:
							{{for enviamentsPerEmail}} {{>}} 
							{{/for}}"></span>
						{{/if}}
						{{if procesEstat == 'ARXIU_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.ARXIU_PENDENT"/>
						{{else procesEstat == 'REGLA_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.REGLA_PENDENT"/>
							<br> <span class="regla-nom" style="font-size:1rem">{{:regla.nom}}</span>
						{{else procesEstat == 'BUSTIA_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.BUSTIA_PENDENT"/>
						{{else procesEstat == 'BUSTIA_PROCESSADA'}}
							<spring:message code="registre.proces.estat.enum.BUSTIA_PROCESSADA"/>
						{{else procesEstat == 'BACK_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.BACK_PENDENT"/>
							<br> <span class="back-codi" style="font-size:1rem">{{:backCodi}}</span>
						{{else procesEstat == 'BACK_REBUDA'}}
							<spring:message code="registre.proces.estat.enum.BACK_REBUDA"/>
							<br> <span class="back-codi" style="font-size:1rem">{{:backCodi}}</span>
						{{else procesEstat == 'BACK_COMUNICADA'}}
							<spring:message code="registre.proces.estat.enum.BACK_COMUNICADA"/>
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
					</script>
				</th>			
				<th data-col-name="numComentaris" style="max-width: 10%; min-width: 70px" data-orderable="false" data-template="#cellPermisosTemplate" width="5%">
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						<a href="../contingut/{{:id}}/comentaris/?isVistaMoviments=true" data-toggle="modal" data-refresh-tancar="true" data-modal-id="comentaris{{:id}}" class="btn btn-default"><span class="fa fa-lg fa-comments"></span>&nbsp;<span class="badge">{{:numComentaris}}</span></a>
					</script>
				</th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsContingutTemplate" width="5%">
					<script id="cellAccionsContingutTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<span class="hidden_dis"><spring:message code="comu.boto.accions"/></span>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu dropdown-left-high">
								<li>
									<a id="detall-button"
										href="./registre/{{:id}}"
											data-toggle="modal" data-maximized="true"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a>
								</li>
								<li><a href="../contingut/{{:id}}/log/moviments" data-toggle="modal" data-maximized="true"><span class="fa fa-list"></span>&nbsp;<spring:message code="comu.boto.historial"/></a></li>
								{{if alerta}}
									<li><a href="../registreUser/pendent/{{:id}}/alertes" data-toggle="modal"><span class="fa fa-exclamation-triangle"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.llistat.alertes"/></a></li>
								{{/if}}
								<li><a href="../registreUser/pendent/{{:id}}/{{:destiLogic}}/reenviar" data-toggle="modal" data-maximized="true"><span class="fa fa-send"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.reenviar"/>...</a></li>
								<li role="separator" class="divider"></li>
								<li {{if procesEstat == 'ARXIU_PENDENT'}} class="disabled" {{/if}}><a {{if procesEstat != 'ARXIU_PENDENT'}} href="./enviarViaEmail/{{:id}}?isVistaMoviments=true" {{/if}} data-toggle="modal"><span class="fa fa-envelope"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.enviarViaEmail"/>...</a></li>
								<li>
									<a href="<c:url value="/contingut/registre/{{:id}}/descarregarZip/DOCUMENT_ORIGINAL"/>">
										<span class="fa fa-download"></span> <spring:message code="registre.annex.descarregar.zip.vo"/>
									</a>
								</li>
								<li>
									<a href="<c:url value="/contingut/registre/{{:id}}/descarregarZip/DOCUMENT"/>">
										<span class="fa fa-download"></span> <spring:message code="registre.annex.descarregar.zip.cai"/>
									</a>
								</li>
							</ul>
						</div>
					</script>
				</th>
				<th data-col-name="bustiaActiva" data-visible="false"></th>
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