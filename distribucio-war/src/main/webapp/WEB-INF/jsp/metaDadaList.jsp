<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="metadada.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/webjars/Sortable/1.4.2/Sortable.min.js"/>"></script>
	
<script type="text/javascript">
$(document).ready(function() {
	$('#metadades').on('dragupdate.dataTable', function (event, itemId, index) {
		$.ajax({
 			url: "<c:url value="/ajax/metaDada/"/>" + itemId + "/move/" + index,
			async: false
		});
	});
	
	$("#nouBoto").detach().appendTo('#header')
});
</script>	
</head>
<body>
	<div id="nouBoto" style="float: right;">
		<a class="btn btn-default" href="metaDada/new" data-toggle="modal" data-datatable-id="metadades"><span class="fa fa-plus"></span>&nbsp;<spring:message code="metadada.list.boto.nova"/></a>
	</div> 
	<table  id="metadades"
			data-toggle="datatable" 
			data-url="<c:url value="metaDada/datatable"/>" 
			data-default-order="0" 
			data-default-dir="asc" 
			data-info-type="search"
			data-drag-enabled="true"
			class="table table-striped table-bordered">
		<thead>
			<tr>
				<th data-col-name="ordre" data-visible="false"></th>
				<th data-col-name="codi" data-orderable="false"><spring:message code="metadada.list.columna.codi"/></th>
				<th data-col-name="nom" data-orderable="false"><spring:message code="metadada.list.columna.nom"/></th>
				<th data-col-name="tipus"  data-orderable="false" data-renderer="enum(MetaDadaTipusEnumDto)">
					<spring:message code="metadada.list.columna.tipus"/>
				</th>
				<th data-col-name="activa" data-orderable="false" data-template="#cellActivaTemplate" >
					<spring:message code="metadada.list.columna.activa"/>
					<script id="cellActivaTemplate" type="text/x-jsrender">
						{{if activa}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<c:choose>
									<c:when test="${consultar}">
										<li><a href="metaDada/{{:id}}" data-toggle="modal"><span class="fa fa-search"></span>&nbsp;&nbsp;<spring:message code="comu.boto.consultar"/></a></li>
									</c:when>
									<c:otherwise>
										<li><a href="metaDada/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
									</c:otherwise>
								</c:choose>
								{{if !activa}}
								<li><a href="metaDada/{{:id}}/enable" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
								{{else}}
								<li><a href="metaDada/{{:id}}/disable" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
								{{/if}}
								<li><a href="metaDada/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="metadada.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<div class="clearfix"></div>
</body>