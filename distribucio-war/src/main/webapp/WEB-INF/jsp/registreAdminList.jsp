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
	<title><spring:message code="anotacions.admin.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>

<style>
table.dataTable tbody > tr.selected, table.dataTable tbody > tr > .selected {
	background-color: #fcf8e3;
	color: #666666;
}
table.dataTable thead > tr.selectable > :first-child, table.dataTable tbody > tr.selectable > :first-child {
	cursor: pointer;
}
</style>
	
<script>
$(document).ready(function() {
	$('#unitatOrganitzativa').on('change', function (e) {
		var rutaBusties = $('#bustia').data('url-llistat');
		var rutaFragmentada = rutaBusties.split('/');
		var darrerFragment = rutaFragmentada[rutaFragmentada.length - 1];
		if (this.value == null || this.value.length == 0) {
			$('#bustia').data('url-llistat', rutaBusties.replace(darrerFragment, 'null'));
			//$('#bustia').prop('disabled', true);
			$('#bustia').val('').change();
		} else {
			$('#bustia').data('url-llistat', rutaBusties.replace(darrerFragment, this.value));
			$('#bustia').val('').change();
			//$('#bustia').prop('disabled', false);
		}
	});
	$('#netejarFiltre').click(function(e) {

		$('#nomesAmbErrorsBtn').removeClass('active');
		$('#estat').val(null).trigger('change');
		
	});
	$('#unitatOrganitzativa').trigger('change');

	$('#nomesAmbErrorsBtn').click(function() {
		nomesAmbErrors = !$(this).hasClass('active');
		// Modifica el formulari
		$('#nomesAmbErrors').val(nomesAmbErrors);
	})

	$('#taulaDades').on( 'draw.dt', function () {
		$('#seleccioAll').on('click', function() {
			$.get(
					"registreAdmin/select",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taulaDades').webutilDatatable('refresh');
					}
			);
			return false;
		});
		$('#seleccioNone').on('click', function() {
			$.get(
					"registreAdmin/deselect",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taulaDades').webutilDatatable('select-none');
						$('#taulaDades').webutilDatatable('refresh');
					}
			);
			return false;
		});
	}).on('selectionchange.dataTable', function (e, accio, ids) {
		$.get(
				"registreAdmin/" + accio,
				{ids: ids},
				function(data) {
					$("#seleccioCount").html(data);
				}
		);
	});
	
});
</script>
</head>
<body>
	<form:form action="" method="post" cssClass="well" commandName="anotacioRegistreFiltreCommand">
		<div class="row">
			<div class="col-md-3">
				<dis:inputText name="nom" inline="true" placeholderKey="contingut.admin.filtre.nom"/>
			</div>		
			<div class="col-md-3">
				<dis:inputText name="numeroOrigen" inline="true" placeholderKey="bustia.list.filtre.origen.num"/>
			</div>				
			<div class="col-md-3" style="margin-bottom: 15px">
				<c:url value="/unitatajax/unitat" var="urlConsultaInicial"/>
				<c:url value="/unitatajax/unitats" var="urlConsultaLlistat"/>
				<dis:inputSuggest 
					name="unitatOrganitzativa" 
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					inline="true" 
					placeholderKey="contingut.admin.filtre.uo"
					suggestValue="codi"
					suggestText="nom" />
			</div>
			<div class="col-md-3" style="margin-bottom: 15px">
				<c:url value="/registreAdmin/ajaxBustia" var="urlConsultaInicial"/>
				<c:url value="/registreAdmin/ajaxBusties/null" var="urlConsultaLlistat"/>
				<dis:inputSuggest 
					name="bustia" 
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					minimumInputLength="0"
					inline="true" 
					placeholderKey="contingut.admin.filtre.bustia"
					suggestValue="id"
					suggestText="nom" />
			</div>
		</div>
		<div class="row">
			<div class="col-md-3">
				<dis:inputDate name="dataCreacioInici" inline="true" placeholderKey="contingut.admin.filtre.data.inici"/>
			</div>
			<div class="col-md-3">
				<dis:inputDate name="dataCreacioFi" inline="true" placeholderKey="contingut.admin.filtre.data.fi"/>
			</div>
			<div class="col-md-3">
				<div class="row">
					<div class="col-md-10">
						<dis:inputSelect name="estat" inline="true" netejar="false" optionEnum="RegistreProcesEstatEnumDto" placeholderKey="contingut.admin.filtre.estat" emptyOption="true"/>
					</div>
					<div class="col-md-2">
						<button id="nomesAmbErrorsBtn" title="<spring:message code="contingut.admin.filtre.nomesAmbErrors"/>" class="btn btn-default <c:if test="${nomesAmbErrors}">active</c:if>" data-toggle="button"><span class="fa fa-warning"></span></button>
						<dis:inputHidden name="nomesAmbErrors"/>
					</div>
				</div>
			</div>	
			<div class="col-md-3">
				<dis:inputText name="backCodi" inline="true" placeholderKey="bustia.list.filtre.back.codi"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-2 pull-right">
				<div class="pull-right">
					<button style="display:none" type="submit" name="accio" value="filtrar" ><span class="fa fa-filter"></span></button>
					<button id="netejarFiltre"  type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	<script id="botonsTemplate" type="text/x-jsrender">
		<div class="text-right">
			<div class="btn-group">
				<button id="seleccioAll" title="<spring:message code="bustia.pendent.contingut.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></button>
				<button id="seleccioNone" title="<spring:message code="bustia.pendent.contingut.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></button>
				<div class="btn-group">
					<a href="registreAdmin/reintentarProcessamentMultiple" class="btn btn-default" aria-haspopup="true" aria-expanded="false">
  						<span id="seleccioCount" class="badge">${fn:length(seleccio)}</span> <spring:message code="registre.detalls.accio.reintentar"/></span>
					</a>
				</div>
			</div>
		</div>
	</script>	
	<table
		id="taulaDades"
		data-toggle="datatable"
		data-url="<c:url value="/registreAdmin/datatable"/>"
		data-filter="#anotacioRegistreFiltreCommand"
		data-botons-template="#botonsTemplate"
		data-selection-enabled="true"
		data-default-order="5"
		data-default-dir="desc"
		class="table table-bordered table-striped">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false">#</th>
				<th data-col-name="bustia" data-visible="false">#</th>
				<th data-col-name="registre" data-visible="false">#</th>
				<th data-col-name="nom" data-template="#cellNomTemplate" width="25%">
					<spring:message code="contingut.admin.columna.nom"/>
					<script id="cellNomTemplate" type="text/x-jsrender">
						<span class="fa ${iconaAnotacioRegistre}"></span>
						{{:nom}} 
					</script>
				</th>
				<th data-col-name="numeroOrigen"><spring:message code="bustia.list.filtre.origen.num"/></th>				
				<th data-col-name="createdDate" data-converter="datetime" width="10%"><spring:message code="contingut.admin.columna.creat.el"/></th>
				<th data-col-name="procesError" data-visible="false">#</th>
				<th data-col-name="procesEstat" data-orderable="false" width="10%" data-template="#cellEstatTemplate">
					<spring:message code="bustia.pendent.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if procesEstat == 'ARXIU_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.ARXIU_PENDENT"/>
						{{else procesEstat == 'REGLA_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.REGLA_PENDENT"/>
						{{else procesEstat == 'BUSTIA_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.BUSTIA_PENDENT"/>
						{{else procesEstat == 'BUSTIA_PROCESSADA'}}
							<spring:message code="registre.proces.estat.enum.BUSTIA_PROCESSADA"/>
						{{else procesEstat == 'BACK_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.BACK_PENDENT"/>
						{{else procesEstat == 'BACK_REBUDA'}}
							<spring:message code="registre.proces.estat.enum.BACK_REBUDA"/>
						{{else procesEstat == 'BACK_PROCESSADA'}}
							<spring:message code="registre.proces.estat.enum.BACK_PROCESSADA"/>
						{{else procesEstat == 'BACK_REBUTJADA'}}
							<spring:message code="registre.proces.estat.enum.BACK_REBUTJADA"/>
						{{else procesEstat == 'BACK_ERROR'}}
							<spring:message code="registre.proces.estat.enum.BACK_ERROR"/>
						{{/if}}

						{{if procesError != null}}
							<span class="fa fa-warning text-danger" title="{{:procesError}}"></span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="path" data-template="#cellPathTemplate" data-orderable="false">
					<spring:message code="contingut.admin.columna.situacio"/>
					<script id="cellPathTemplate" type="text/x-jsrender">
						{{if path}}
							{{for path}}/
								{{if bustia}}{{if #getIndex() == 0}}<span class="fa ${iconaUnitat}" title="<spring:message code="contingut.icona.unitat"/>"></span>{{else}}<span class="fa ${iconaBustia}" title="<spring:message code="contingut.icona.bustia"/>"></span>{{/if}}{{/if}}
								{{:nom}}
							{{/for}}
						{{/if}}
					</script>
				</th>
				<th data-col-name="interessatsAndRepresentantsResum" data-orderable="false"><spring:message code="contingut.admin.columna.interessats"/></th>
				<th data-col-name="backCodi" data-orderable="true"><spring:message code="contingut.admin.columna.backoffice"/></th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="contingutAdmin/{{:id}}/detall" data-toggle="modal" data-maximized="true"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="contingut.admin.boto.detalls"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>