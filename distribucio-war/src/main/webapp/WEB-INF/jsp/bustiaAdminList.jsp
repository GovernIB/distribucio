<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<dis:blocIconaContingutNoms/>
<html>
<head>
	<title><spring:message code="bustia.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	<script type="text/javascript">

	$(document).ready(
		function() {
			$("#header").append("<div style='float: right;'><button id='canviVistaBusties' class='btn btn-primary'><spring:message code='bustia.canvi.vista'/></button></div>");
		
			$("#canviVistaBusties").click(function(){
				window.location.replace("/distribucio/bustiaAdminOrganigrama");
			});
		});
	</script>
</head>
<body>
	<form:form action="" method="post" cssClass="well" commandName="bustiaFiltreCommand">
		<div class="row">
			<div class="col-md-5">
				<dis:inputText name="nom" inline="true" placeholderKey="bustia.list.filtre.nom"/>
			</div>
			<div class="col-md-2">
<%-- 				<dis:inputText name="unitatCodi" inline="true" placeholderKey="bustia.list.filtre.unitat.codidir3"/> --%>
				<c:url value="/unitatajax/unitat" var="urlConsultaInicial"/>
				<c:url value="/unitatajax/unitats" var="urlConsultaLlistat"/>
				<dis:inputSuggest 
					name="unitatId" 
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					inline="true" 
					placeholderKey="bustia.form.camp.unitat"
					suggestValue="id"
					suggestText="nom" />
			</div>
			<div class="col-md-2" style="padding-left: 30px;">
				<dis:inputCheckbox name="unitatObsoleta" inline="true" textKey="bustia.list.filtre.obsolataUnitat"/>
			</div>
			<div class="col-md-3 pull-right">
				<div class="pull-right">
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	
	<table id="busties" data-toggle="datatable" data-url="<c:url value="/bustiaAdmin/datatable"/>" data-filter="#bustiaFiltreCommand" data-default-order="1" data-default-dir="asc" class="table table-bordered table-striped" data-botons-template="#botonsTemplate">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false">#</th>
				<th data-col-name="unitatCodi" data-visible="false"></th>
				<th data-col-name="entitat.codi" data-visible="false"></th>
				<th data-col-name="nom" data-template="#nomTemplate">
					<spring:message code="bustia.list.columna.nom"/>
					<script id="nomTemplate" type="text/x-jsrender">
						{{:nom}}
						{{if unitatOrganitzativa.estat=='E'||unitatOrganitzativa.estat=='A' || unitatOrganitzativa.estat=='T'}}
							<a href="${unitatCodiUrlPrefix}bustiaAdmin/{{:id}}/bustiaTransicioInfo" data-toggle="modal">
								<span class="fa fa-warning text-danger pull-right" title="<spring:message code="bustia.obsoleta"/>"></span>
							</a>
						{{/if}}
					</script>
				</th>
				<th data-col-name="unitatOrganitzativa.codi"><spring:message code="bustia.list.columna.unitat.codi"/></th>
				<th data-col-name="unitatOrganitzativa.denominacio"><spring:message code="bustia.list.columna.unitat.nom"/></th>			
				<th data-col-name="perDefecte" data-template="#cellPerDefecteTemplate">
					<spring:message code="bustia.list.columna.per.defecte"/>
					<script id="cellPerDefecteTemplate" type="text/x-jsrender">
						{{if perDefecte}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="activa" data-template="#cellActivaTemplate">
					<spring:message code="bustia.list.columna.activa"/>
					<script id="cellActivaTemplate" type="text/x-jsrender">
						{{if activa}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="permisosCount" data-template="#cellPermisosCountTemplate" data-orderable="false" width="10%">
					<script id="cellPermisosCountTemplate" type="text/x-jsrender">
						<a href="${unitatCodiUrlPrefix}bustiaAdmin/{{:id}}/permis" class="btn btn-default"><span class="fa fa-file-alt"></span>&nbsp;<spring:message code="bustia.list.boto.permisos"/>&nbsp;<span class="badge">{{:permisosCount}}</span></a>
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="${unitatCodiUrlPrefix}bustiaAdmin/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="${unitatCodiUrlPrefix}bustiaAdmin/{{:id}}/default" data-toggle="ajax"><span class="fa fa-check-square-o"></span>&nbsp;&nbsp;<spring:message code="bustia.list.accio.per.defecte"/></a></li>
								{{if !activa}}
									<li><a href="${unitatCodiUrlPrefix}bustiaAdmin/{{:id}}/enable" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
								{{else}}
									<li><a href="${unitatCodiUrlPrefix}bustiaAdmin/{{:id}}/disable" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
								{{/if}}
									<li><a href="${unitatCodiUrlPrefix}bustiaAdmin/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="bustia.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="botonsTemplate" type="text/x-jsrender">
		<p style="text-align:right"><a id="bustia-boto-nova" class="btn btn-default" href="${unitatCodiUrlPrefix}bustiaAdmin/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="bustia.list.boto.nova.bustia"/></a></p>
	</script>
</body>