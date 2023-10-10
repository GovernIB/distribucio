<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%
pageContext.setAttribute(
		"contingutAdminOpcionsEsborratEnumOptions",
		es.caib.distribucio.back.helper.EnumHelper.getOptionsForEnum(
				es.caib.distribucio.back.command.ContingutFiltreCommand.ContenidorFiltreOpcionsEsborratEnum.class,
				"contingut.admin.opcions.esborrat.enum."));
pageContext.setAttribute(
		"isRolActualAdministrador",
		es.caib.distribucio.back.helper.RolHelper.isRolActualAdministrador(request));
pageContext.setAttribute(
		"isRolActualAdminLectura",
		es.caib.distribucio.back.helper.RolHelper.isRolActualAdminLectura(request));
%>
<dis:blocIconaContingutNoms/>
<html>
<head>
	<title><spring:message code="contingut.admin.titol"/></title>
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
	
	<script type="text/javascript">
		$(document).ready(function() {
			$("input:visible:enabled:not([readonly]),textarea:visible:enabled:not([readonly]),select:visible:enabled:not([readonly])").first().focus();
		});
	</script>
</head>
<body>
	<form:form action="" method="post" cssClass="well" commandName="contingutFiltreCommand">
		<div class="row">
			<div class="col-md-7">
				<dis:inputText name="nom" inline="true" placeholderKey="contingut.admin.filtre.nom"/>
			</div>
			<div class="col-md-3">
				<dis:inputSelect name="tipus" optionEnum="ContingutTipusEnumDto" emptyOption="true" placeholderKey="contingut.admin.filtre.tipus" inline="true"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-2">
				<dis:inputDate name="dataCreacioInici" inline="true" placeholderKey="contingut.admin.filtre.data.inici"/>
			</div>
			<div class="col-md-2">
				<dis:inputDate name="dataCreacioFi" inline="true" placeholderKey="contingut.admin.filtre.data.fi"/>
			</div>
			<div class="col-md-3">
				<dis:inputSelect name="opcionsEsborrat" optionItems="${contingutAdminOpcionsEsborratEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" emptyOption="false" inline="true"/>
			</div>
			<div class="col-md-5 d-flex justify-content-end">
				<button style="display:none" type="submit" name="accio" value="filtrar" ><span class="fa fa-filter"></span></button>
				<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
				<button type="submit" name="accio" value="filtrar" class="ml-2 btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
			</div>
		</div>
	</form:form>
	<table
		id="taulaDades"
		data-toggle="datatable"
		data-url="<c:url value="/contingutAdmin/datatable"/>"
		data-default-order="7"
		data-default-dir="desc"
		class="table table-bordered table-striped">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false">#</th>
				<th data-col-name="bustia" data-visible="false">#</th>
				<th data-col-name="registre" data-visible="false">#</th>
				<th data-col-name="esborrat" data-visible="false">#</th>
				<th data-col-name="alerta" data-visible="false">#</th>
				<th data-col-name="nom" data-template="#cellNomTemplate" width="25%">
					<spring:message code="bustia.pendent.columna.contingut"/>
					<script id="cellNomTemplate" type="text/x-jsrender">
						{{if registre}}<span class="fa ${iconaAnotacioRegistre}"></span>{{else bustia}}<span class="fa ${iconaBustia}"></span>{{/if}}
						{{:nom}}
						{{if esborrat}}<span class="fa fa-trash-o pull-right" title="<spring:message code="contingut.admin.columna.esborrat"/>"></span>{{/if}}
						{{if error}}<span class="fa fa-warning text-danger pull-right" title="<spring:message code="bustia.pendent.registre.estat.error"/>"></span>{{/if}}
						{{if alerta}}
							<span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="contingut.errors.registre.regles.segonpla"/>"></span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="createdBy.nom" width="10%"><spring:message code="bustia.pendent.columna.remitent"/></th>
				<th data-col-name="createdDate" data-converter="datetime" width="10%"><spring:message code="contingut.admin.info.camp.data.creacio"/></th>
				<th data-col-name="path" data-template="#cellPathTemplate" data-orderable="false" width="30%">
					<spring:message code="bustia.pendent.columna.localitzacio"/>
					<script id="cellPathTemplate" type="text/x-jsrender">
						{{if path}}
							{{for path}}/
								{{if bustia}}{{if #getIndex() == 0}}<span class="fa ${iconaUnitat}" title="<spring:message code="contingut.icona.unitat"/>"></span>{{else}}<span class="fa ${iconaBustia}" title="<spring:message code="contingut.icona.bustia"/>"></span>{{/if}}{{/if}}
								{{:nom}}
							{{/for}}
						{{/if}}
					</script>
				</th>
				<c:if test="${isRolActualAdministrador}">
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown d-flex justify-content-center ">
							<button class="btn btn-primary" data-toggle="dropdown">
								<span class="fa fa-cog"></span>
								<span class="hidden_dis"><spring:message code="comu.boto.accions"/>
								<span class="caret"></span>
							</button>
							<ul class="dropdown-menu">
								<li><a href="contingutAdmin/{{:id}}/detall" data-toggle="modal" data-maximized="true"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="contingut.admin.boto.detalls"/></a></li>
								<li><a href="contingutAdmin/{{:id}}/log" data-toggle="modal" data-maximized="true"><span class="fa fa-list"></span>&nbsp;&nbsp;<spring:message code="comu.boto.historial"/></a></li>
								{{if esborrat}}
								<li><a href="contingutAdmin/{{:id}}/undelete" data-toggle="ajax"><span class="fa fa-undo"></span>&nbsp;&nbsp;<spring:message code="contingut.admin.boto.recuperar"/></a></li>
								<li><a href="contingutAdmin/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="contingut.admin.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								{{/if}}
							</ul>
						</div>
					</script>
				</th>
				</c:if>
			</tr>
		</thead>
	</table>
</body>