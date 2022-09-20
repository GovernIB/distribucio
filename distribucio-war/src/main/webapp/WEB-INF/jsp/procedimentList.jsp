<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
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
	<title><spring:message code="procediment.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script>
		function actualitzarProcediments() {
			$("#span-refresh").addClass('fa-circle-o-notch');
			$("#span-refresh").addClass('fa-spin')
			window.location.href = '<c:url value="/procediment/actualitzar"/>';
		}
	</script>
</head>
<body>
	<form:form action="" method="post" cssClass="well" commandName="procedimentFiltreCommand">
		<div class="row">
			<div class="col-md-3">
				<dis:inputText name="codiSia" inline="true" placeholderKey="procediment.list.columna.codiSia"/>
			</div>
			<div class="col-md-3">
				<c:url value="/procedimentajax/procediment" var="urlConsultaInicial"/>
				<c:url value="/procedimentajax/procediments" var="urlConsultaLlistat"/>
				<dis:inputSuggest 
					name="nom"
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					inline="true" 
					placeholderKey="registre.admin.list.filtre.procediment"
					suggestValue="nom"
					suggestText="codiNom" 
					optionTemplateFunction="formatSelectUnitat" />
			</div>			
			<div class="col-md-3">
				<dis:inputText name="unitatOrganitzativa.codi" inline="true" placeholderKey="procediment.list.columna.unitatOrganitzativa.codiDir3"/>
			</div>
		</div>
		<div class="row">		
			<div class="col-md-2 pull-right">
				<div class="pull-right">
					<button style="display:none" type="submit" name="accio" value="filtrar" ><span class="fa fa-filter"></span></button>
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>

<script id="botonsTemplate" type="text/x-jsrender">
	<div class="f-right"> 
		<button onclick="actualitzarProcediments()" id="actualitzarProcediments" class="btn btn-default"><span id="span-refresh" class="fa fa-refresh"></span>&nbsp; <spring:message code="procediment.taula.actualitzar"/></button>
	</div>
</script>
<table
	id="procediments"
	data-refresh-tancar="true"
	data-toggle="datatable"
	data-url="<c:url value="/procediment/datatable"/>"
	data-filter="#procedimentFiltreCommand"
	data-botons-template="#botonsTemplate"
	data-default-order="1"
	data-default-dir="asc"
	class="table table-striped table-bordered">
	<thead>
		<tr>
			<th data-col-name="id" data-visible="false" width="4%">#</th>
			<th data-col-name="codiSia" data-orderable="true"><spring:message code="procediment.list.columna.codiSia"/></th>
			<th data-col-name="nom" data-orderable="true"><spring:message code="procediment.list.columna.nom"/></th>
			<th data-col-name="unitatOrganitzativa.codi" data-orderable="true"><spring:message code="procediment.list.columna.unitatOrganitzativa.codiDir3"/></th> 
		</tr>
	</thead>
</table>

</body>