<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="integracio.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
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
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	
<script>
$(document).ready(function() {
    $('#btnRefresh').click(function() {
    	refrescarInformacio();
    });
    $('#btnDelete').click(function() {
    	$('#trash-btn-esborrar').css("display", "none");
    	$('#spin-btn-esborrar').css("display", "block");
    	esborrarEntrades();
    });
    $('#netejarFiltre').click(function(e) {
    	$('#codi').val('USUARIS').change();
    	$('#data').val('').change();
    	$('#descripcio').val('').change();
    	$('#usuari').val('').change();
    	$('#estat').val('').change();
    })
});

function refrescarInformacio() {
	webutilClearMissatges();
	$.ajax({
		url: "<c:url value='/integracio/integracions'/>"
	}).done(function(data, status){
		// Refresca la taula
		$('#missatges-integracions').webutilDatatable('refresh')
		if (data && Array.isArray(data)) {
			data.forEach(function(integracio) {
				$errors = $('#integracioErrors_' + integracio.codi)
				if (integracio.numErrors > 0) {
					$errors.html(integracio.numErrors);
					$errors.show();
				} else{
					$errors.hide();
				}
			});
		}
		// Refresca els missatges
		webutilRefreshMissatges();
	});
}


function esborrarEntrades() {
	webutilClearMissatges();
	$.ajax({
		url: "<c:url value='/integracio'/>/${codiActual}/esborrar"
	}).done(function(){
		refrescarInformacio()
    	$('#spin-btn-esborrar').css("display", "none");
    	$('#trash-btn-esborrar').css("display", "block");
	});
}

</script>
	
	
</head>
<body>
	<form:form action="/distribucio/integracio/${integracioFiltreCommand.codi}" method="post" cssClass="well" commandName="integracioFiltreCommand">
		
		<button id="filtrar" type="submit" name="accio" value="filtrar" class="btn btn-primary" style="display:none"></button>
		
		<div class="row">
			<div class="col-md-3">
				<dis:inputDate name="data" inline="true" placeholderKey="integracio.list.filtre.data"/>
			</div>
			<div class="col-md-3">
				<dis:inputText name="descripcio" inline="true" placeholderKey="integracio.list.filtre.descripcio"/>
			</div>			
			<div class="col-md-3">
				<dis:inputText name="usuari" inline="true" placeholderKey="integracio.list.filtre.usuari"/>
			</div>
			<div class="col-md-3">
				<dis:inputSelect name="estat" inline="true" placeholderKey="integracio.list.filtre.estat" optionEnum="IntegracioAccioEstatEnumDto" emptyOption="true"/>
			</div>
			<div class="col-md-4 pull-right">
				<div class="pull-right">
					<button id="netejarFiltre" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button id="filtrar" type="submit" name="accio" value="filtrar" class="ml-2 btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>	
		</div>
	</form:form>




	<ul class="nav nav-tabs" role="tablist">
		<c:forEach var="integracio" items="${integracions}">
			<li id="integracioTab_${integracio.codi}"
				<c:if test="${integracio.codi == integracioFiltreCommand.codi}"> class="active"</c:if>>
				<a href="<c:url value="/integracio/${integracio.codi}"/>"><spring:message code="${integracio.nom}"/>
				
					<span id="integracioErrors_${integracio.codi}" 
						class="badge small" 
						style="background-color: #d9534f; display: ${integracio.numErrors > 0? 'inline' : 'none'}"
						title="${integracio.numErrors} errors a les darreres ${numeroHoresPropietat} hores">
							${integracio.numErrors}
					</span>
				</a>
			</li>
		</c:forEach>
	</ul>
	<br/>
	<table
		id="missatges-integracions"
		data-toggle="datatable"
		data-url="<c:url value="/integracio/datatable"/>" 
		data-search-enabled="false"
		data-info-type="search"
		data-default-order="2" 
		data-default-dir="desc"
		class="table table-striped table-bordered"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="excepcioMessage" data-visible="false"></th>
				<th data-col-name="excepcioStacktrace" data-visible="false"></th>
				<th data-col-name="data" data-orderable="true" data-converter="datetime" width="150px"><spring:message code="integracio.list.columna.data"/></th>				
				<th data-col-name="descripcio" data-orderable="true"><spring:message code="integracio.list.columna.descripcio"/></th>
				<th data-col-name="tipus" data-orderable="true"><spring:message code="integracio.list.columna.tipus"/></th>
				<th data-col-name="codiUsuari" data-orderable="true"><spring:message code="integracio.list.columna.usuari"/></th>
				<th data-col-name="codiEntitat" data-orderable="true"><spring:message code="integracio.list.columna.entitat"/></th>
				<th data-col-name="tempsResposta" data-template="#cellTempsTemplate" data-orderable="true">
					<spring:message code="integracio.list.columna.temps.resposta"/>
					<script id="cellTempsTemplate" type="text/x-jsrender">{{:tempsResposta}} ms</script>
				</th>
				<th data-col-name="estat" data-template="#cellEstatTemplate" data-orderable="true">
					<spring:message code="integracio.list.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estat == 'OK'}}
							<span class="label label-success"><span class="fa fa-check"></span>&nbsp;{{:estat}}</span>
						{{else}}
							<span class="label label-danger" title="{{html:excepcioMessage}}"><span class="fa fa-warning"></span>&nbsp;{{:estat}}</span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a href="<c:url value='/integracio'/>/${codiActual}/{{:id}}" class="btn btn-default" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	
		<button id="btnRefresh" type="button" class="btn btn-info pull-right" style="margin-top: 25px; margin-bottom: 20px; margin-right: 10px;"><span class="fa fa-refresh"></span>&nbsp;&nbsp;<spring:message code="comu.boto.refrescar"/></button>
		<button id="btnDelete" type="button" class="btn btn-danger pull-left" style="margin-top: 25px; margin-bottom: 20px; margin-right: 10px; display: flex"><span id="trash-btn-esborrar" class="fa fa-trash-o"></span><span id="spin-btn-esborrar" class="fa fa-circle-o-notch fa-spin d-none"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></button>
	
</body>