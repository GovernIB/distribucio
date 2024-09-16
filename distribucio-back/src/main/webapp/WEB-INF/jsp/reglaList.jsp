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
<dis:blocIconaContingutNoms/>
<html>
<head>
	<title><spring:message code="regla.list.titol"/></title>
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
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/webjars/Sortable/1.4.2/Sortable.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	
	<style>
		.icon {
			cursor: auto;
		}
		.icon-bustia:hover {
			background-color: #5bc0de;
	    	border-color: #46b8da;
		}
		.icon-backoffice:hover {
			background-color: #d9534f;
		    border-color: #d43f3a;
		}
		.icon-unitat:hover {
		    background-color: #5cb85c;
		    border-color: #4cae4c;
		}
		
		.inactiva {
			cursor: not-allowed;
			color: grey !important;
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
	var bustiesInactives = [];

	//Funció per donar format als items de la select de bústies segons si estan actives o no
	function formatSelectBustia(item) {
		if (bustiesInactives.includes(item.id))
			return $("<span>" + item.text + " <span class='fa fa-exclamation-triangle text-warning' title=\"<spring:message code='bustia.list.avis.bustia.inactiva'/>\"></span></span>");
		else
			return item.text;
	}
	
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
		$(document).on('hidden.bs.modal', function (event) {			
			var data = sessionStorage.getItem('selectedElements');
			if (data != null) {
				// Deseleccionar elements si s'ha realitzat una acció múltiple i les anotacions s'han mogut
				$(".seleccioCount").html(data);
				$('#regles').webutilDatatable('refresh');
				
				sessionStorage.removeItem('selectedElements');
			}
		});
		
		$("span#select2-activa-container span.select2-selection__clear").css("display", "none");
		$("input:visible:enabled:not([readonly]),textarea:visible:enabled:not([readonly]),select:visible:enabled:not([readonly])").first().focus();

		$('#regles').on('dragupdate.dataTable', function (event, itemId, index) {
			$.ajax({
				url: "ajax/regla/" + itemId + "/move/" + index,
				async: false
			});
		});		
		
		$('form').submit(function() {				
			$.get(
					"regla/deselect",
					function(data) {
						$("#seleccioCount").html(data);
						$('#regles').webutilDatatable('select-none');
					}
			);
			return false;
		});
		
		var selectButtonsInitialized = false;
		$('#regles').on( 'draw.dt', function () {
			if (!selectButtonsInitialized) {					
				selectButtonsInitialized = true;				
				
				$('#seleccioAll').on('click', function(e) {					
					$.get(
							"regla/select",
							function(data) {
								$("#seleccioCount").html(data);
								$('#regles').webutilDatatable('refresh');
							}
					);
					return false;
				});					
				
				$('#seleccioNone').on('click', function() {						
					$.get(
							"regla/deselect",
							function(data) {
								$("#seleccioCount").html(data);
								$('#regles').webutilDatatable('select-none');
							}
					);
					return false;
				});
			}
			$("tr", this).each(function(){					
				if ($(this).find("#detall-button").length > 0) {
					var pageInfo = $('#regles').dataTable().api().table().page.info();
					var registreTotal = pageInfo.recordsTotal;
					var registreNumero = $(this).data('rowIndex');
					// Afegeix els paràmetres a l'enllaç dels detalls
					var url = new URL(window.location);
					var params = url.searchParams;
					params.set("registreNumero", registreNumero);
					params.set("registreTotal", registreTotal);
					var sort = $('#regles').dataTable().fnSettings().aaSorting
					if (sort.length > 0) {
						params.set("ordreColumn", $($('#regles').dataTable().api().column(sort[0][0]).header()).data('colName'))
						params.set("ordreDir", sort[0][1]);
					}			
					var $a = $($(this).find("#detall-button"));
					$a.attr('href', $a.attr('href') + '?' + params.toString());
					// Afegeix els paràmetres a l'enllaç de la fila
					$(this).data('href', $(this).data('href') + '?' + params.toString());
				}
			});
		}).on('selectionchange.dataTable', function (e, accio, ids) {				
			$.get(
					"regla/" + accio,
					{ids: ids},
					function(data) {
						$("#seleccioCount").html(data);
					}
			);
		});		
	});
	
	function deselectItems() {
		$.get(
				"regla/deselect",
				function(data) {
					$("#seleccioCount").html(data);
					$('#regles').webutilDatatable('select-none');
				}
		);
	}
	
	</script>
</head>
<body>	
	<form:form action="" method="post" cssClass="well" modelAttribute="reglaFiltreCommand">
		<div class="row">
			<div class="col-md-3">
				<dis:inputText name="nom" inline="true" placeholderKey="bustia.list.filtre.nom"/>
			</div>		
			<div class="col-md-3">
				<dis:inputText name="codiAssumpte" inline="true" placeholderKey="regla.form.camp.assumpte.codi"/>
			</div>			
			<div class="col-md-3">
				<dis:inputText name="codiSIA" inline="true" placeholderKey="regla.list.columna.procediment.single.codi"/>
			</div>
			<div class="col-md-3">
				<dis:inputSelect name="tipus" optionEnum="ReglaTipusEnumDto" emptyOption="true" placeholderKey="regla.list.columna.tipus" inline="true"/>
			</div>	
		</div>
		<div class="row">
			<div class="col-md-3">		
				<c:url value="/bustiaajax/bustia" var="urlConsultaInicial"/>
				<c:url value="/bustiaajax/llistaBusties" var="urlConsultaLlistat"/>			
				<dis:inputSuggest 
					name="bustiaId" 
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					suggestValue="id"
					suggestText="nom" 
					placeholderKey="bustia.list.filtre.bustia" 
					inline="true"
					optionTemplateFunction="formatSelectBustia" />  
			</div>
			<div class="col-md-3">
				<c:url value="/unitatajax/unitat" var="urlConsultaInicial"/>
				<c:url value="/unitatajax/unitats" var="urlConsultaLlistat"/>
				<dis:inputSuggest 
					name="unitatId" 
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					inline="true" 
					placeholderKey="bustia.form.camp.unitat"
					suggestValue="id"
					suggestText="codiAndNom" 
					optionTemplateFunction="formatSelectUnitat"/>
			</div>
			<div class="col-md-3">
				<dis:inputSelect 
					name="backofficeId" 
					placeholderKey="bustia.list.filtre.backoffice" 
					optionItems="${backoffices}" 
					emptyOption="true" 
					optionValueAttribute="id" 
					optionTextAttribute="nom" 
					inline="true"
					optionMinimumResultsForSearch="0"/>
			</div>		
			<div class="col-md-3">
				<dis:inputSelect 
					name="activa" 
					optionEnum="ReglaFiltreActivaEnumDto" 
					emptyOption="true" 
					placeholderKey="regla.list.columna.totes" 
					inline="true"/>
				<%-- <button id="mostrarInactivesBtn" style="width: 45px;" title="<spring:message code="regla.list.columna.activa"/>" class="btn btn-default btn-sm<c:if test="${reglaFiltreCommand.activa}"> active</c:if>" data-toggle="button">
					<span class="fa-stack" aria-hidden="true">
						<i class="fa fa-inbox fa-stack-1x"></i>
    	    			<i class="fa fa-ban fa-stack-2x"></i>
   					</span>
				</button>
				<dis:inputHidden name="activa"/> --%> 
			</div>
		</div>
		<div class="row">
			<div class="col-md-3">
				<dis:inputText name="codiServei" inline="true" placeholderKey="regla.list.columna.servei.single.codi"/>
			</div>		
			<div class="col-md-3">
				<dis:inputSelect name="presencial" optionEnum="ReglaPresencialEnumDto" emptyOption="true" placeholderKey="regla.list.columna.presencial" inline="true"/>
			</div>
			<div class="col-md-6"></div>
			<div class="col-md-3 d-flex pull-right justify-content-end">
				<button style="display:none" type="submit" name="accio" value="filtrar" ><span class="fa fa-filter"></span></button>
				<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
				<button type="submit" name="accio" value="filtrar" class="ml-2 btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
			</div>
		</div>
	</form:form>

	<script id="botonsTemplate" type="text/x-jsrender">
	  <c:if test="${isRolActualAdministrador}">
		<div class="text-right">

			<a class="btn btn-default" href="regla/new" data-toggle="modal" data-datatable-id="regles"><span class="fa fa-plus"></span>&nbsp;<spring:message code="regla.list.boto.nova"/></a>
			<a class="btn btn-primary" href="regla/simular" data-toggle="modal" data-datatable-id="regles"><span class="fa fa-cog"></span>&nbsp;<spring:message code="regla.list.boto.simular"/></a>

			<div class="btn-group">
				<button id="seleccioAll" title="<spring:message code="bustia.pendent.contingut.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></button>
				<button id="seleccioNone" title="<spring:message code="bustia.pendent.contingut.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></button>
				<button class="btn btn-default" data-toggle="dropdown"><span id="seleccioCount" class="badge">${fn:length(seleccio)}</span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
				<ul class="dropdown-menu dropdown-left">
					<c:if test="${isRolActualAdministrador}">					
						<li><a href="regla/enableMultiple" data-toggle="ajax">
							<span class="fa fa-check"></span>
							<spring:message code="comu.boto.activar"/>
						</a></li>	
						<li><a href="regla/disableMultiple" data-toggle="ajax">
							<span class="fa fa-times"></span>
							<spring:message code="comu.boto.desactivar"/>
						</a></li>	
						<li><a href="regla/deleteMultiple" data-toggle="ajax" data-confirm="<spring:message code="entitat.list.confirmacio.esborrar"/>">
							<span class="fa fa-trash-o"></span>
							<spring:message code="comu.boto.esborrar"/>
						</a></li>
					</c:if>							
				</ul>
			</div>			
		</div>
	  </c:if>
	</script>		
	<table 
		id="regles" 
		data-toggle="datatable" 
		data-url="<c:url value="/regla/datatable"/>" 
		data-filter="#reglaFiltreCommand" 
		data-drag-enabled="${isRolActualAdministrador}"  
		data-default-order="0" 
		data-default-dir="asc" 
		class="table table-striped table-bordered" 
		style="width:100%"
		data-botons-template="#botonsTemplate"
		data-selection-enabled="true"		
		data-refresh-tancar="true"	
		>

		<thead>
			<tr>
				<th data-col-name="ordre" data-visible="false"></th>
				<th data-col-name="nom" data-orderable="false" data-template="#nomTemplate">
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
				<th data-col-name="assumpteCodiFiltre" data-orderable="false"><spring:message code="regla.list.columna.assumpte.codi"/></th>
				<th data-col-name="procedimentCodiFiltre" data-orderable="false" width="10%"> <spring:message code="regla.list.columna.procediment.codi"/></th>
				<th data-col-name="serveiCodiFiltre" data-orderable="false" width="10%"> <spring:message code="regla.list.columna.servei.codi"/></th>
				<!-- <th data-col-name="codiAndNomUnitatOrganitzativa" data-orderable="false"><spring:message code="regla.list.columna.unitat.organitzativa"/></th> -->
				
				<th data-col-name="estatUnitatOrganitzativa" data-visible="false"></th>
				<th data-col-name="codiAndNomUnitatOrganitzativa" data-orderable="false" data-template="#procesErrorTemplate">
					<spring:message code="regla.list.columna.unitat.organitzativa"/>
					<script id="procesErrorTemplate" type="text/x-jsrender">
						{{:codiAndNomUnitatOrganitzativa}}		
						{{if codiAndNomUnitatOrganitzativa!=null}}				
							{{if estatUnitatOrganitzativa!='V'}}																
								<span class="fa fa-warning" title="<spring:message code="regla.list.columna.unitat.organitzativa"/>"> </span>							
							{{/if}}
						{{/if}}						
					</script>
				</th>			
				
				<th data-col-name="bustiaFiltreNom" data-orderable="false"><spring:message code="regla.list.columna.bustia.nom"/></th>
				
				
				<th data-col-name="presencial" data-orderable="true" data-template="#presencialTemplate">
					<spring:message code="regla.list.columna.presencial"/>
					<script id="presencialTemplate" type="text/x-jsrender">
						{{if presencial == 'SI'}}
							<spring:message code="regla.presencial.enum.SI"/>
						{{else presencial == 'NO'}}
							<spring:message code="regla.presencial.enum.NO"/>
						{{/if}}
					</script>
				</th>
				
				<th data-col-name="tipus" data-orderable="false" data-template="#tipusTemplate">
					<spring:message code="regla.list.columna.destinacio"/>
					<script id="tipusTemplate" type="text/x-jsrender">
						{{if tipus == 'BUSTIA'}}
							{{:bustiaDestiNom}}
							<button class="btn btn-info btn-xs pull-right icon icon-bustia"><spring:message code="regla.list.columna.icona.bustia"/></button>
						
						{{else tipus == 'BACKOFFICE'}}
							{{:backofficeDestiNom}}
							<button class="btn btn-danger btn-xs pull-right icon icon-backoffice"><spring:message code="regla.list.columna.icona.backoffice"/></button>					
							
						{{else tipus == 'UNITAT'}}
							{{:unitatDestiNom}}
							<button class="btn btn-success btn-xs pull-right icon icon-unitat"><spring:message code="regla.list.columna.icona.unitat"/></button>					
						{{/if}}
						
					
					</script>
				</th>

				<th data-col-name="backofficeDestiNom" data-visible="false"></th>
				<th data-col-name="bustiaDestiNom" data-visible="false"></th>
				<th data-col-name="unitatDestiNom" data-visible="false"></th>
				
				<th data-col-name="activa" data-template="#cellActivaTemplate" data-orderable="false">
					<spring:message code="regla.list.columna.activa"/>
					<script id="cellActivaTemplate" type="text/x-jsrender">
						{{if activa}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="aturarAvaluacio" data-template="#cellAturarAvaluacioTemplate" data-orderable="true">
					<spring:message code="regla.list.columna.aturarAvaluacio"/>
					<script id="cellAturarAvaluacioTemplate" type="text/x-jsrender">
						{{if aturarAvaluacio}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<!-- <th data-col-name="presencial" data-template="#cellPresencialTemplate" data-orderable="false">
					<spring:message code="regla.list.columna.presencial"/>
					<script id="cellPresencialTemplate" type="text/x-jsrender">
						{{if presencial}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>-->
				<c:if test="${isRolActualAdministrador}">
					<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
						<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown d-flex justify-content-center">
							<button class="btn btn-primary" data-toggle="dropdown">
								<span class="fa fa-cog"></span>
								<span class="hidden_dis"><spring:message code="comu.boto.accions"/></span>
								<span class="caret"></span>
							</button>
							<ul class="dropdown-menu">
								<li><a href="regla/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								{{if !activa}}
								<li><a href="#" class="inactiva" title="Acció no disponible si està inactiva"><span class="fa fa-cog"></span>&nbsp;&nbsp;<spring:message code="regla.list.accio.aplicar.manualment"/></a></li>
								{{else}}
								<li><a href="regla/{{:id}}/aplicar" data-confirm="<spring:message code="regla.list.accio.aplicar.manualment.confirm"/>"><span class="fa fa-cog"></span>&nbsp;&nbsp;<spring:message code="regla.list.accio.aplicar.manualment"/></a></li>
								{{/if}}
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
				</c:if>
			</tr>
		</thead>
	</table>
</body>