<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<dis:blocIconaContingutNoms/>
<html>
<head>
	<title><spring:message code="regla.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/webjars/Sortable/1.4.2/Sortable.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<script type="text/javascript">
$(document).ready(function() {
	$('#regles').on('dragupdate.dataTable', function (event, itemId, index) {
		$.ajax({
			url: "ajax/regla/" + itemId + "/move/" + index,
			async: false
		});
	});
});
</script>
</head>
<body>

	

	
	<form:form action="" method="post" cssClass="well" commandName="reglaFiltreCommand">
		<div class="row">
			<div class="col-md-4">
				<dis:inputText name="nom" inline="true" placeholderKey="bustia.list.filtre.nom"/>
			</div>
			<div class="col-md-2">
				<dis:inputSelect name="tipus" optionEnum="ReglaTipusEnumDto" emptyOption="true" placeholderKey="regla.list.columna.tipus" inline="true"/>
			</div>			
			<div class="col-md-2">
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
			<div class="col-md-2">
				<dis:inputText name="backofficeCodi" inline="true" placeholderKey="bustia.list.filtre.backofficeCodi"/>
			</div>
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
		<p style="text-align:right"><a class="btn btn-default" href="regla/new" data-toggle="modal" data-datatable-id="regles"><span class="fa fa-plus"></span>&nbsp;<spring:message code="regla.list.boto.nova"/></a></p>
	</script>
	<table id="regles" data-toggle="datatable" data-url="<c:url value="/regla/datatable"/>" data-filter="#reglaFiltreCommand" data-drag-enabled="true"  data-default-order="0" data-default-dir="asc" class="table table-striped table-bordered" style="width:100%"
	data-botons-template="#botonsTemplate">

		<thead>
			<tr>
				<th data-col-name="ordre" data-visible="false"></th>
				<th data-col-name="nom" data-orderable="false"data-template="#nomTemplate">
					<spring:message code="regla.list.columna.nom"/>
					<script id="nomTemplate" type="text/x-jsrender">
						{{:nom}}
						{{if unitatOrganitzativa != null}}
							{{if unitatOrganitzativa.estat=='E'||unitatOrganitzativa.estat=='A' || unitatOrganitzativa.estat=='T'}}
								<span class="fa fa-warning text-danger pull-right" title="<spring:message code="regla.obsoleta"/>"></span>
							{{/if}}
						{{/if}}
					</script>
				</th>
				<th data-col-name="tipus" data-orderable="false" data-renderer="enum(ReglaTipusEnumDto)">
					<spring:message code="regla.list.columna.tipus"/>
				</th>
				<th data-col-name="assumpteCodi" data-orderable="false"><spring:message code="regla.list.columna.assumpte.codi"/></th>
				<th data-col-name="procedimentCodi" data-orderable="false"><spring:message code="regla.list.columna.procediment.codi"/></th>
				<th data-col-name="unitatOrganitzativa.codiAndNom" data-orderable="false"><spring:message code="regla.list.columna.unitat.organitzativa"/></th>
				<th data-col-name="backofficeCodi" data-orderable="false"><spring:message code="regla.list.columna.codi.backoffice"/></th>
				<th data-col-name="activa" data-template="#cellActivaTemplate" data-orderable="false">
					<spring:message code="regla.list.columna.activa"/>
					<script id="cellActivaTemplate" type="text/x-jsrender">
						{{if activa}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="regla/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="regla/{{:id}}/up" data-toggle="ajax"><span class="fa fa-arrow-up"></span>&nbsp;&nbsp;<spring:message code="comu.boto.amunt"/></a></li>
								<li><a href="regla/{{:id}}/down" data-toggle="ajax"><span class="fa fa-arrow-down"></span>&nbsp;&nbsp;<spring:message code="comu.boto.avall"/></a></li>
								{{if !activa}}
								<li><a href="regla/{{:id}}/enable" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
								{{else}}
								<li><a href="regla/{{:id}}/disable" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
								{{/if}}
								<li><a href="regla/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="entitat.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>