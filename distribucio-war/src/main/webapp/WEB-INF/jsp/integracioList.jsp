<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="integracio.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
<script>
$(document).ready(function() {
    $('#btnRefresh').click(function() {
    	refrescarInformacio();
    });
    $('#btnDelete').click(function() {
    	esborrarEntrades();
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
	});
}

</script>
	
	
</head>
<body>
	<ul class="nav nav-tabs" role="tablist">
		<c:forEach var="integracio" items="${integracions}">
			<li id="integracioTab_${integracio.codi}"
				<c:if test="${integracio.codi == codiActual}"> class="active"</c:if>>
				<a href="<c:url value="/integracio/${integracio.codi}"/>"><spring:message code="${integracio.nom}"/>
				
					<span id="integracioErrors_${integracio.codi}" 
						class="badge small" style="background-color: #d9534f; display: ${integracio.numErrors > 0? 'inline' : 'none'}">
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
		data-search-enabled="true"
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
		<button id="btnDelete" type="button" class="btn btn-danger pull-left" style="margin-top: 25px; margin-bottom: 20px; margin-right: 10px;"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></button>
	
</body>