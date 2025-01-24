<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%
	pageContext.setAttribute(
			"isRolActualAdministrador",
			es.caib.distribucio.back.helper.RolHelper.isRolActualAdministrador(request));
	pageContext.setAttribute(
			"isRolActualAdminLectura",
			es.caib.distribucio.back.helper.RolHelper.isRolActualAdminLectura(request));
%>
<html>
<head>
	<title><spring:message code="servei.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<c:if test="${requestLocale == 'en'}">
		<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script> 
	</c:if>
	<script src="<c:url value="/js/select2-locales/select2_${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_ca.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	<style type="text/css">
		span.badge {
			font-size: 1.2rem !important;
			padding-right: 1.2rem !important;
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
		
		button#netejarFiltre, 
		button#filtrar {
			width: 50%;
		}
	</style>
	
	<script>
		function formatSelectUnitat(item) {
			if (!item.id) {
			    return item.text;
			}
			if (item.data && item.data.estat=="V"){
				return item.text;
			} else {
				return $("<span>" + item.text + " <span class='fa fa-exclamation-triangle text-warning' title=\"<spring:message code='unitat.filtre.avis.obsoleta'/>\"></span></span>");
			}
		}
	</script>
</head>
<body>
	<form:form action="" method="post" cssClass="well" modelAttribute="serveiFiltreCommand">
		<div class="row">
			<div class="col-md-3">
				<dis:inputText name="codiSia" inline="true" placeholderKey="servei.list.columna.codiSia"/>
			</div>
			<div class="col-md-3">
				<dis:inputText name="nom" inline="true" placeholderKey="registre.admin.list.filtre.servei"/>
			</div>			
			<div class="col-md-3">
				<c:url value="/unitatajax/unitat" var="urlConsultaInicial"/>
				<c:url value="/unitatajax/senseEntitat" var="urlConsultaLlistat"/>
				<dis:inputSuggest 
					name="unitatOrganitzativa"
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					inline="true" 
					placeholderKey="servei.list.columna.unitatOrganitzativa"
					suggestValue="codi"
					suggestText="codiAndNom" 
					optionTemplateFunction="formatSelectUnitat" /> 
			</div>
			<div class="col-md-3">
				<dis:inputSelect name="estat"  netejar="true" optionEnum="ServeiEstatEnumDto" placeholderKey="servei.list.columna.estat" emptyOption="true" inline="true"/>			
			</div>
		</div>
		<div class="row">		
			<div class="col-md-9"></div>
			<div class="col-md-3 d-flex justify-content-end">
				<button style="display:none" type="submit" name="accio" value="filtrar" ><span class="fa fa-filter"></span></button>
				<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
				<button type="submit" name="accio" value="filtrar" class="ml-2 btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
			</div>
		</div>
	</form:form>

<script id="botonsTemplate" type="text/x-jsrender">
	<c:if test="${isRolActualAdministrador}">
		<div class="f-right"> 
			<a href="<c:url value='/servei/actualitzar'/>" 
					id="actualitzarServeis" 
					data-toggle="modal" 
					data-maximized="true" 
					data-refresh-tancar="true" 
					data-data-table-id="serveis" 
					class="btn btn-default">
				<span id="span-refresh" class="fa fa-refresh"></span>&nbsp; <spring:message code="procediment.taula.actualitzar"/> ...
			</a>
		</div>
	</c:if>
</script>
<table
	id="serveis"
	data-refresh-tancar="true"
	data-toggle="datatable"
	data-url="<c:url value="/servei/datatable"/>"
	data-filter="#serveiFiltreCommand"
	data-botons-template="#botonsTemplate"
	data-default-order="1"
	data-default-dir="asc"
	class="table table-striped table-bordered">
	<thead>
		<tr>
			<th data-col-name="id" data-visible="false" width="4%">#</th>
			<th data-col-name="codiSia" data-orderable="true"><spring:message code="servei.list.columna.codiSia"/></th>
			<th data-col-name="nom" data-orderable="true"><spring:message code="servei.list.columna.nom"/></th>
			<th data-col-name="unitatOrganitzativa.codi" data-template="#uoTemplate">
				<spring:message code="servei.list.columna.unitatOrganitzativa"/>
				<script id="uoTemplate" type="text/x-jsrender">

					{{if unitatOrganitzativa.estat!='V'}}
						<span class="fa fa-warning text-warning  pull-right" style="margin-top: 3px;" title="<spring:message code="unitat.filtre.avis.obsoleta"/>"></span>
					{{/if}}
 
					{{:unitatOrganitzativa.codi}} -  {{:unitatOrganitzativa.denominacio}}

				</script>
			</th>
			<th data-col-name="estat" data-orderable="true"><spring:message code="servei.list.columna.estat"/></th> 
		</tr>
	</thead>
</table>

</body>