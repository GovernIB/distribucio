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
<dis:blocIconaContingutNoms/>
<c:url value="/unitatajax/unitat" var="urlConsultaInicial"/>
<c:url value="/unitatajax/unitatsWithoutArrel" var="urlConsultaLlistat"/>
<html>
<head>
	<title><spring:message code="unitat.list.titol"/></title>
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

		function formatSelectUnitatItem(select, item) {
			if (!item.id) {
			    return item.text;
			}
			valida = true;
			if (item.data) {
				valida = item.data.estat =="V";
			} else {
				if ($(select).val() == item.id) {
					// Consulta si no és vàlida per afegir la icona de incorrecta.
					$.ajax({
						url: $(select).data('urlInicial') +'/' + item.id,
						async: false,
						success: function(resposta) {
							valida = resposta.estat == "V";
						}
					});	
				}			
			}
			if (valida)
				return item.text;
			else
				return $("<span>" + item.text + " <span class='fa fa-exclamation-triangle text-warning' title=\"<spring:message code='unitat.filtre.avis.obsoleta'/>\"></span></span>");
		}
	
		function formatSelectUnitat(item) {
			return formatSelectUnitatItem($('#codiUnitatSuperior'), item);
		}

	
		$(document).ready(function() {
			$("input:visible:enabled:not([readonly]),textarea:visible:enabled:not([readonly]),select:visible:enabled:not([readonly])").first().focus();

			$('#netejarFiltre').click(function(e) {
				$('#estat').val('VIGENTE').change();
			});
		});
	</script>
	
</head>
<body>

		<form:form action="" method="post" cssClass="well" commandName="unitatOrganitzativaFiltreCommand">
			<div class="row">
				<div class="col-md-4">
					<dis:inputText name="codi" inline="true" placeholderKey="unitat.list.filtre.codi"/>
				</div>
				<div class="col-md-4">
					<dis:inputText name="denominacio" inline="true" placeholderKey="unitat.list.filtre.denominacio"/>
				</div>
				<div class="col-md-4">
					<dis:inputSuggest
						name="codiUnitatSuperior" 
						urlConsultaInicial="${urlConsultaInicial}" 
						urlConsultaLlistat="${urlConsultaLlistat}" 
						inline="true"
						placeholderKey="unitat.list.filtre.unitatSuperior"
						suggestValue="codi"
						suggestText="codiAndNom"
						optionTemplateFunction="formatSelectUnitat"/>
				</div>
			</div>
			<div class="row">
				<div class="col-md-4">
					<dis:inputSelect name="estat" netejar="false" optionEnum="UnitatOrganitzativaEstatEnumDto" placeholderKey="unitat.list.filtre.estat" emptyOption="true" inline="true"/>
				</div>		
				<div class="col-md-4 pull-right">
					<div class="pull-right">
						<button id="filtrar" type="submit" name="accio" value="filtrar" class="btn btn-primary" style="display:none"></button>
						<button id="netejarFiltre" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
						<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
					</div>
				</div>
			</div>			

		</form:form>

		<table id="unitatsOrganitzatives" 
				data-toggle="datatable" 
				data-url="<c:url value="/unitatOrganitzativa/datatable"/>" 
				data-filter="#unitatOrganitzativaFiltreCommand" 
				data-default-order="1" 
				data-default-dir="asc" 
				data-botons-template="#botonsTemplate" 
				class="table table-bordered table-striped">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false">#</th>	
				<th data-col-name="tipusTransicio" data-visible="false">#</th>				
				<th data-col-name="codi" width="80px" data-template="#codiTemplate">
					<spring:message code="unitat.list.columna.codi"/>
					<script id="codiTemplate" type="text/x-jsrender">
						{{:codi}} 
						{{if estat=='E'||estat=='A'||estat=='T'}}
							{{if tipusTransicio == 'DIVISIO'}}
								<a href="${unitatCodiUrlPrefix}unitatOrganitzativa/{{:id}}/unitatTransicioInfo" data-toggle="modal">
									<span class="fa fa-warning text-danger pull-right" style="margin-top: 3px;" title="<spring:message code="unitat.obsoleta.tipusTransicio.DIVISIO"/>"></span>
								</a>
							{{else tipusTransicio == 'FUSIO'}}
								<a href="${unitatCodiUrlPrefix}unitatOrganitzativa/{{:id}}/unitatTransicioInfo" data-toggle="modal">
									<span class="fa fa-warning text-danger pull-right" style="margin-top: 3px;" title="<spring:message code="unitat.obsoleta.tipusTransicio.FUSIO"/>"></span>
								</a>
							{{else tipusTransicio == 'SUBSTITUCIO'}}
								<a href="${unitatCodiUrlPrefix}unitatOrganitzativa/{{:id}}/unitatTransicioInfo" data-toggle="modal">
									<span class="fa fa-warning text-danger pull-right" style="margin-top: 3px;" title="<spring:message code="unitat.obsoleta.tipusTransicio.SUBSTITUCIO"/>"></span>
								</a>
							{{/if}}

						{{/if}}
					</script>
				</th>					
				<th data-col-name="denominacio"><spring:message code="unitat.list.columna.denominacio"/></th>
				
				<th data-col-name="codiIDenominacioUnitatSuperior"><spring:message code="unitat.list.columna.unitatPare"/></th>
				
				<th data-col-name="codiUnitatArrel" data-orderable="false"><spring:message code="unitat.list.columna.unitatArrel"/></th>
				
				<th data-col-name="estat" data-template="#estatTemplate">
					<spring:message code="unitat.list.columna.estat"/>
					<script id="estatTemplate" type="text/x-jsrender">
						{{if estat =='V'}}
							<spring:message code="unitat.estat.vigente"/>
						{{else estat =='E'}}
							<spring:message code="unitat.estat.extinguido"/>
						{{else estat =='A'}}
							<spring:message code="unitat.estat.anulado"/>
						{{else estat =='T'}}
							<spring:message code="unitat.estat.transitorio"/>
						{{/if}}
					</script>
				</th>
				
			</tr>
		</thead>
	</table>
	<script id="botonsTemplate" type="text/x-jsrender">
		<div style="float: right">
			<a href="<c:url value="/unitatOrganitzativa/mostrarArbre"/>" data-toggle="modal"  class="btn btn-default"><span class="fa fa-sitemap"></span> <spring:message code="unitat.list.boto.mostrarArbre"/></a>
			<c:if test="${isRolActualAdministrador}">
				<a href="<c:url value="/unitatOrganitzativa/synchronizeGet"/>" data-toggle="modal" class="btn btn-default"><span class="fa fa-refresh"></span> <spring:message code="unitat.list.boto.synchronize"/></a>
			</c:if>
		</div>
	</script>

</body>