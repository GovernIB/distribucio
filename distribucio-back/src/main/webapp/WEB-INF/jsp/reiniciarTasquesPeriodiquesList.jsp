<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="decorator.menu.reinici.scheduler"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<rip:modalHead/>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#tasquesTable').on('draw.dt', function (e, settings) {
				webutilModalAdjustHeight();
			});
		});
		function actualitzarTaula() {
			$('#tasquesTable').webutilDatatable('refresh');
		}
		function reiniciarTasca(codi) {
			if (!codi || codi==null) codi='totes';
			$.ajax({
				type: 'GET',
				url: '<c:url value="/scheduled/restart/' + codi + '"/>',
				async: true,
				success: function(data) {
					webutilRefreshMissatges();
					setTimeout(actualitzarTaula, 1000);
				},
				error: function() {
					console.log("Error en la petició AJAX reiniciant la tasca.");
				}
			});
		}
	</script>
</head>

<body>

	<table 
		id="tasquesTable" 
		data-toggle="datatable"
		data-url="<c:url value="/scheduled/datatable"/>" 
		data-search-enabled="false"
		data-botons-template="#tasquesTableBotoTots"
		data-save-state="true"
		data-default-order="0"
		data-default-dir="asc"
		data-paging-enabled="false"
		class="table table-striped table-bordered" 
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="codi" data-orderable="false" data-template="#cellTascaCodiTemplate" width="30%">
					<spring:message code="monitor.tasques.tasca"/>
					<script id="cellTascaCodiTemplate" type="text/x-jsrender">
					{{if codi == 'guardarAnotacionsPendents'}}
						<spring:message code="monitor.tasques.tasca.codi.guardarAnotacionsPendents"/>
					{{else codi == 'enviarAlBackoffice'}}
						<spring:message code="monitor.tasques.tasca.codi.enviarAlBackoffice"/>
					{{else codi == 'aplicarReglesBackoffice'}}
						<spring:message code="monitor.tasques.tasca.codi.aplicarReglesBackoffice"/>
					{{else codi == 'tancarContenidors'}}
						<spring:message code="monitor.tasques.tasca.codi.tancarContenidors"/>
					{{else codi == 'enviarEmailsNoAgrupats'}}
						<spring:message code="monitor.tasques.tasca.codi.enviarEmailsNoAgrupats"/>
					{{else codi == 'enviarEmailsAgrupats'}}
						<spring:message code="monitor.tasques.tasca.codi.enviarEmailsAgrupats"/>
					{{else codi == 'calcularDadesHistoriques'}}
						<spring:message code="monitor.tasques.tasca.codi.calcularDadesHistoriques"/>
					{{else codi == 'esborrarDadesAntigues'}}
						<spring:message code="monitor.tasques.tasca.codi.esborrarDadesAntigues"/>
					{{else codi == 'reintentarProcessament'}}
						<spring:message code="monitor.tasques.tasca.codi.reintentarProcessament"/>
					{{else codi == 'actualitzarProcediments'}}
						<spring:message code="monitor.tasques.tasca.codi.actualitzarProcediments"/>
					{{else codi == 'actualitzarServeis'}}
						<spring:message code="monitor.tasques.tasca.codi.actualitzarServeis"/>
					{{else codi == 'execucionsMassives'}}
						<spring:message code="monitor.tasques.tasca.codi.execucionsMassives"/>
					{{/if}}
					</script>
				</th>
				<th data-col-name="estat" data-orderable="false" data-template="#cellTascaEstatTemplate" width="15%">
					<spring:message code="monitor.tasques.estat"/>
					<script id="cellTascaEstatTemplate" type="text/x-jsrender">
					{{if estat == 'EN_EXECUCIO'}}
						<spring:message code="monitor.tasques.estat.EN_EXECUCIO"/>
					{{else estat == 'EN_ESPERA'}}
						<spring:message code="monitor.tasques.estat.EN_ESPERA"/>
					{{else estat == 'ERROR'}}
						<spring:message code="monitor.tasques.estat.ERROR"/>
					{{/if}}
					</script>
				</th>
				<th data-col-name="dataInici" data-converter="datetime" data-orderable="false" width="15%"><spring:message code="monitor.tasques.darrer.inici"/></th>
				<th data-col-name="dataFi" data-converter="datetime" data-orderable="false" width="15%"><spring:message code="monitor.tasques.fi.execucio"/></th>
				<th data-col-name="properaExecucio" data-converter="datetime" data-orderable="false" width="15%"><spring:message code="monitor.tasques.propera.execucio"/></th>
				<th data-col-name="codi" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<a href="Javascript:reiniciarTasca('{{:codi}}');" class="btn btn-warning"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="monitor.tasques.boto.reiniciar"/></a>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="tasquesTableBotoTots" type="text/x-jsrender">
	<div style="float: right;">
		<a href="Javascript:actualitzarTaula();" class="btn btn-success" style="margin-right: 45px;"><span class="fa fa-list"></span>&nbsp;<spring:message code="monitor.tasques.boto.refresh.taula"/></a>
		<a href="Javascript:reiniciarTasca();" class="btn btn-warning" style="margin-right: 45px;"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="monitor.tasques.boto.reiniciar.totes"/></a>
	</div>
	</script>
	<div id="modal-botons">
		<a href="#" class="btn btn-default" data-modal-cancel="true"><spring:message code="accio.massiva.boto.tancar"/></a>
	</div>
</body>
</html>