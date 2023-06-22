<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%
	pageContext.setAttribute(
			"isRolActualAdministrador",
			es.caib.distribucio.war.helper.RolHelper.isRolActualAdministrador(request));
	pageContext.setAttribute(
			"isRolActualAdminLectura",
			es.caib.distribucio.war.helper.RolHelper.isRolActualAdminLectura(request));
%>
<html>
<head>
	<title><spring:message code="backoffice.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.5/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<c:if test="${requestLocale == 'en'}">
		<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script> 
	</c:if>
	<script src="<c:url value="/js/select2-locales/select2_${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	<style>
	td {
		overflow-wrap: break-word;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}
</style>
	<script>
		$(document).ready(
		function() {
			$("#nouBoto").detach().appendTo('#header')
		});
	</script>
	
</head>
<body>
  <c:if test="${isRolActualAdministrador}">
	<div id="nouBoto" style="float: right;">
		<a class="btn btn-default" href="backoffice/new" data-toggle="modal" data-datatable-id="backoffice"><span class="fa fa-plus"></span>&nbsp;<spring:message code="backoffice.boto.nou"/></a>
	</div>
  </c:if>
	<table 
		id="backoffice"
		data-toggle="datatable" 
		data-url="<c:url value="/backoffice/datatable"/>" 
		data-info-type="search" 
		class="table table-striped table-bordered"
		style= "table-layout: fixed;"	
		width="80%">
		<thead>
			<tr>
				<th data-col-name="codi" data-orderable="true"><spring:message code="backoffice.list.columna.codi"/></th>
				<th data-col-name="nom" data-orderable="false"><spring:message code="backoffice.list.columna.nom"/></th>
				<th data-col-name="url" data-orderable="false"><spring:message code="backoffice.list.columna.url"/></th>
				<th data-col-name="tipus" data-orderable="true"><spring:message code="backoffice.list.columna.tipus"/></th>
  				<c:if test="${isRolActualAdministrador}">
					<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="backoffice/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="backoffice/{{:id}}/provar"><span class="fa fa-cog"></span>&nbsp;&nbsp;<spring:message code="comu.boto.provar"/></a></li>
								<li><a href="backoffice/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="backoffice.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
					</th>
				</c:if>
			</tr>
		</thead>
	</table>
</body>