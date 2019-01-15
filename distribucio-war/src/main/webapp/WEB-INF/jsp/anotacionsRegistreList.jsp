<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<dis:blocIconaContingutNoms/>
<html>
<head>
	<title><spring:message code="anotacions.admin.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<script>
$(document).ready(function() {
	$('#unitatOrganitzativa').on('change', function (e) {
		var rutaBusties = $('#bustia').data('url-llistat');
		var rutaFragmentada = rutaBusties.split('/');
		var darrerFragment = rutaFragmentada[rutaFragmentada.length - 1];
		if (this.value == null || this.value.length == 0) {
			$('#bustia').data('url-llistat', rutaBusties.replace(darrerFragment, 'null'));
			$('#bustia').prop('disabled', true);
			$('#bustia').val('').change();
		} else {
			$('#bustia').data('url-llistat', rutaBusties.replace(darrerFragment, this.value));
			$('#bustia').prop('disabled', false);
		}
	});
	$('#netejarFiltre').click(function(e) {
	});
	$('#unitatOrganitzativa').trigger('change');
});
</script>
</head>
<body>
	<form:form action="" method="post" cssClass="well" commandName="anotacioRegistreFiltreCommand">
		<div class="row">
			<div class="col-md-3">
				<dis:inputText name="nom" inline="true" placeholderKey="contingut.admin.filtre.nom"/>
			</div>		
			<div class="col-md-3" style="margin-bottom: 15px">
				<c:url value="/unitatajax/unitat" var="urlConsultaInicial"/>
				<c:url value="/unitatajax/unitats" var="urlConsultaLlistat"/>
				<dis:inputSuggest 
					name="unitatOrganitzativa" 
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					inline="true" 
					placeholderKey="contingut.admin.filtre.uo"
					suggestValue="codi"
					suggestText="nom" />
			</div>
			<div class="col-md-3" style="margin-bottom: 15px">
				<c:url value="/anotacionsRegistre/ajaxBustia" var="urlConsultaInicial"/>
				<c:url value="/anotacionsRegistre/ajaxBusties/null" var="urlConsultaLlistat"/>
				<dis:inputSuggest 
					name="bustia" 
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					minimumInputLength="0"
					inline="true" 
					placeholderKey="contingut.admin.filtre.bustia"
					suggestValue="id"
					suggestText="nom" />
			</div>
<!-- 			<div class="col-md-3"> -->
<%-- 				<dis:inputText name="bustia" inline="true" placeholderKey="contingut.admin.filtre.bustia"/> --%>
<!-- 			</div> -->
		</div>
		<div class="row">
			<div class="col-md-3">
				<dis:inputDate name="dataCreacioInici" inline="true" placeholderKey="contingut.admin.filtre.data.inici"/>
			</div>
			<div class="col-md-3">
				<dis:inputDate name="dataCreacioFi" inline="true" placeholderKey="contingut.admin.filtre.data.fi"/>
			</div>
			<div class="col-md-3">
				<dis:inputSelect name="estat" inline="true" netejar="false" optionEnum="RegistreProcesEstatEnumDto" placeholderKey="contingut.admin.filtre.estat" emptyOption="true"/>
			</div>
			<div class="col-md-3 pull-right">
				<div class="pull-right">
					<button id="netejarFiltre"  type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	<table
		id="taulaDades"
		data-toggle="datatable"
		data-url="<c:url value="/anotacionsRegistre/datatable"/>"
		data-filter="#anotacioRegistreFiltreCommand"
		data-default-order="4"
		data-default-dir="desc"
		class="table table-bordered table-striped">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false">#</th>
				<th data-col-name="bustia" data-visible="false">#</th>
				<th data-col-name="registre" data-visible="false">#</th>
				<th data-col-name="nom" data-template="#cellNomTemplate" width="25%">
					<spring:message code="contingut.admin.columna.nom"/>
					<script id="cellNomTemplate" type="text/x-jsrender">
						<span class="fa ${iconaAnotacioRegistre}"></span>
						{{:nom}}
					</script>
				</th>
				<th data-col-name="createdDate" data-converter="datetime" width="10%"><spring:message code="contingut.admin.columna.creat.el"/></th>
				<th data-col-name="procesEstat" data-orderable="false" width="10%" data-template="#cellEstatTemplate">
					<spring:message code="bustia.pendent.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if procesEstat == 'ARXIU_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.ARXIU_PENDENT"/>
						{{else procesEstat == 'REGLA_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.REGLA_PENDENT"/>
						{{else procesEstat == 'BUSTIA_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.BUSTIA_PENDENT"/>
						{{else procesEstat == 'DISTRIBUIT_PROCESSAT'}}
							<spring:message code="registre.proces.estat.enum.DISTRIBUIT_PROCESSAT"/>
						{{else procesEstat == 'DISTRIBUIT_BACKOFFICE'}}
							<spring:message code="registre.proces.estat.enum.DISTRIBUIT_BACKOFFICE"/>
						{{/if}}
					</script>
				</th>
				<th data-col-name="path" data-template="#cellPathTemplate" data-orderable="false">
					<spring:message code="contingut.admin.columna.situacio"/>
					<script id="cellPathTemplate" type="text/x-jsrender">
						{{if path}}
							{{for path}}/
								{{if bustia}}{{if #getIndex() == 0}}<span class="fa ${iconaUnitat}" title="<spring:message code="contingut.icona.unitat"/>"></span>{{else}}<span class="fa ${iconaBustia}" title="<spring:message code="contingut.icona.bustia"/>"></span>{{/if}}{{/if}}
								{{:nom}}
							{{/for}}
						{{/if}}
					</script>
				</th>
				<th data-col-name="interessatsResum" data-orderable="false"><spring:message code="contingut.admin.columna.interessats"/></th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="contingutAdmin/{{:id}}/info" data-toggle="modal" data-maximized="true"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="contingut.admin.boto.detalls"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>