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
	<title><spring:message code="annexos.admin.titol"/></title>
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
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	
	<style type="text/css">
		table.dataTable thead > tr.selectable > :first-child, table.dataTable tbody > tr.selectable > :first-child {
			cursor: pointer;
		}
		table.dataTable tbody tr.selected a, table.dataTable tbody th.selected a, table.dataTable tbody td.selected a  {
		    color: #333;
		}
		span.badge {
			font-size: 1.2rem !important;
			padding-right: 1.2rem !important;
		}
		
		span.fa-cog {
			margin: 2px 0.5rem 0 0; 
		}
		
		tbody tr.selectable td #div-btn-accions #btn-accions span.caret {
			margin: 8px 0 0 2px; 
		}
		
		span.select2-container {
			width: 100% !important;
		}
	</style>
	
	<script type="text/javascript">
		$(document).ready(function() {
			$('#netejarFiltre').click(function(e) {
				$('#arxiuEstat').val('ESBORRANY').change();
			});
			$(document).on('hidden.bs.modal', function (event) {				
				var data = sessionStorage.getItem('selectedElements');
				if (data != null) {
					// Deseleccionar elements si s'ha realitzat una acció múltiple i les anotacions s'han mogut
					$(".seleccioCount").html(data);
					$('#taulaDades').webutilDatatable('refresh');
					
					sessionStorage.removeItem('selectedElements');
				}
			});
			$("input:visible:enabled:not([readonly]),textarea:visible:enabled:not([readonly]),select:visible:enabled:not([readonly])").first().focus();			
// 			$("#taulaDades").dataTable();

			$('form').submit(function() {				
				$.get(
						"annexosAdmin/deselect",
						function(data) {
							$("#seleccioCount").html(data);
							$('#taulaDades').webutilDatatable('select-none');
						}
				);
				return false;
			});
			
			var selectButtonsInitialized = false;
			$('#taulaDades').on( 'draw.dt', function () {
				if (!selectButtonsInitialized) {					
					selectButtonsInitialized = true;
					$('#seleccioAll').on('click', function(e) {
						debugger;
						$.get(
								"annexosAdmin/select",
								function(data) {
									$("#seleccioCount").html(data);
									$('#taulaDades').webutilDatatable('refresh');
								}
						);
						return false;
					});
					$('#seleccioNone').on('click', function() {						
						$.get(
								"annexosAdmin/deselect",
								function(data) {
									$("#seleccioCount").html(data);
									$('#taulaDades').webutilDatatable('select-none');
								}
						);
						return false;
					});
				}
				$("tr", this).each(function(){					
					if ($(this).find("#detall-button").length > 0) {
						var pageInfo = $('#taulaDades').dataTable().api().table().page.info();
						var registreTotal = pageInfo.recordsTotal;
						var registreNumero = $(this).data('rowIndex');
						// Afegeix els paràmetres a l'enllaç dels detalls
						var url = new URL(window.location);
						var params = url.searchParams;
						params.set("registreNumero", registreNumero);
						params.set("registreTotal", registreTotal);
						var sort = $('#taulaDades').dataTable().fnSettings().aaSorting
						if (sort.length > 0) {
							params.set("ordreColumn", $($('#taulaDades').dataTable().api().column(sort[0][0]).header()).data('colName'))
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
						"annexosAdmin/" + accio,
						{ids: ids},
						function(data) {
							$("#seleccioCount").html(data);
						}
				);
			});		

			var baseUrl = "<c:url value="/annexosAdmin/copies/"/>";
			if (/;jsessionid/.test(baseUrl))
				baseUrl = baseUrl.substring(0, baseUrl.indexOf(";jsessionid"));
			
			recuperarNumeroCopies(baseUrl);
			
			$('#numero').on('blur', function() {
				var numeroAnotacio = $(this).val();
				if (numeroAnotacio) {
					recuperarNumeroCopies(baseUrl + numeroAnotacio);
				}
			});
		});
		
		function recuperarNumeroCopies(baseUrl) {
			$.get(baseUrl)
			.done(function(resultat) {
				$('#numeroCopia').select2('val', '', true);
				$('#numeroCopia option[value!=""]').remove();
				
				if (resultat) {
					var copies = resultat["copies"];
					var registreTrobat = resultat["registreTrobat"];
					
					$(copies).each(function(index, numeroCopia) {
						if (numeroCopia == 0) {
							$('#numeroCopia').append('<option value="' + numeroCopia + '"' + (registreTrobat ? "selected" : "" ) + ' ><spring:message code="annex.admin.filtre.numerocopia.original"/></option>');
						} else {
							$('#numeroCopia').append('<option value="' + numeroCopia + '"><spring:message code="annex.admin.filtre.numerocopia.copia" arguments="' + numeroCopia + '"/></option>');
						}
					});
				}
				var select2Options = {
						language: "${requestLocale}",
				        theme: 'bootstrap',
						allowClear: true
				}
				$('#numeroCopia').select2(select2Options);
			})
			.fail(function() {
				alert("<spring:message code="error.jquery.ajax"/>");
			});
		}
	</script>
</head>
<body>
	<form:form action="" method="post" cssClass="well" modelAttribute="annexosFiltreCommand">
		<div class="row">
			<div class="col-md-3">
				<dis:inputText name="numero" inline="true" placeholderKey="annex.admin.filtre.numero"/>
			</div>	
			<div class="col-md-2">
				<dis:inputSelect name="numeroCopia" netejar="true" emptyOption="true" placeholderKey="annex.admin.filtre.numerocopia" inline="true"/>
			</div>
			<div class="col-md-2">
				<dis:inputSelect name="arxiuEstat" optionEnum="AnnexEstat" netejar="false" emptyOption="true" placeholderKey="annex.admin.filtre.estat" inline="true"/>
			</div>
			<div class="col-md-3">
				<dis:inputSelect name="tipusFirma" optionEnum="ArxiuFirmaTipusEnumDto" emptyOption="true" placeholderKey="annex.admin.filtre.tipusFirma" inline="true"/>
			</div>
			<div class="col-md-2">
				<dis:inputText name="fitxerTipusMime" inline="true" placeholderKey="annex.admin.filtre.fitxerTipusMime"/>
			</div>	
		</div>
		<div class="row">
			<div class="col-md-3">
				<dis:inputText name="titol" inline="true" placeholderKey="annex.admin.filtre.titol"/>
			</div>	
			<div class="col-md-2">
				<dis:inputDate name="dataRecepcioInici" inline="true" placeholderKey="annex.admin.filtre.data.inici"/>
			</div>
			<div class="col-md-2">
				<dis:inputDate name="dataRecepcioFi" inline="true" placeholderKey="annex.admin.filtre.data.inici"/>
			</div>
			<div class="col-md-3">
				<dis:inputText name="fitxerNom" inline="true" placeholderKey="annex.admin.filtre.fitxerNom"/>
			</div>	
			<div class="col-md-2 d-flex justify-content-end">
				<button id="netejarFiltre" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
				<button type="submit" name="accio" value="filtrar" class="ml-2 btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
			</div>
		</div>
	</form:form>
	<c:set var="rol" value="admin"/>
	
	<script id="botonsTemplate" type="text/x-jsrender">
		<div class="text-right">
			<div class="btn-group">
				<button id="seleccioAll" title="<spring:message code="bustia.pendent.contingut.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></button>
				<button id="seleccioNone" title="<spring:message code="bustia.pendent.contingut.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></button>
				<button class="btn btn-default" data-toggle="dropdown"><span id="seleccioCount" class="badge">${fn:length(seleccio)}</span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
				<ul class="dropdown-menu dropdown-left">
					<c:if test="${isRolActualAdministrador}">					
						<li><a href="massiva/guardarDefinitiu" data-toggle="modal" data-maximized="true">
							<span class="fa fa-pencil-square"></span>
							<spring:message code="annexos.admin.boto.guardar.definitiu.multiple"/>
						</a></li>	
					</c:if>							
				</ul>
			</div>
		</div>
	</script>
	<script id="rowhrefTemplate" type="text/x-jsrender">./registreUser/registreAnnex/{{:registreId}}/{{:id}}</script>
	<table
		id="taulaDades"
		data-refresh-tancar="true"
		data-toggle="datatable"
		data-url="<c:url value="/annexosAdmin/datatable"/>"
		data-filter="#annexosFiltreCommand"
		data-botons-template="#botonsTemplate"
		data-selection-enabled="true"
		data-default-order="5"
		data-default-dir="desc"		
		class="table table-bordered table-striped"	
		data-rowhref-template="#rowhrefTemplate" 	
		data-rowhref-toggle="modal"
		data-rowhref-maximized="true"
		>
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"></th>
				<th data-col-name="numeroCopia" data-visible="false"></th>
				<th data-col-name="registreId" data-visible="false"></th>
				<th data-col-name="registreNumero" data-template="#cellRegistreNumeroTemplate" width="20%"><spring:message code="annexos.admin.columna.registreNumero"/>
					<script id="cellRegistreNumeroTemplate" type="text/x-jsrender">
						{{if numeroCopia == 0}}
                			{{:registreNumero}} (<spring:message code="annexos.admin.columna.original"/>)
						{{else}}
							{{:registreNumero}} (<spring:message code="annexos.admin.columna.copia"/> {{:numeroCopia}})
						{{/if}}
           			 </script>
           		</th>
				<th data-col-name="titol" width="20%"><spring:message code="annexos.admin.columna.titol"/></th>
				<th data-col-name="dataAnotacio" width="10%" data-converter="datetime"><spring:message code="annexos.admin.columna.data"/></th>
				<th data-col-name="fitxerNom" width="25%"><spring:message code="annexos.admin.columna.fitxerNom"/></th>	
				<th data-col-name="arxiuEstat" data-template="#cellArxiuEstatTemplate" width="10%"><spring:message code="annexos.admin.columna.arxiuEstat"/>
					<script id="cellArxiuEstatTemplate" type="text/x-jsrender">
                		{{if arxiuEstat == 'ESBORRANY'}}
                    		<spring:message code="annex.estat.ESBORRANY"/>
                		{{else arxiuEstat == 'DEFINITIU'}}
                    		<spring:message code="annex.estat.DEFINITIU"/>	
               		 	{{else}}
                    		<span class="fa fa-warning text-warning" title="<spring:message code="registre.annex.detalls.camp.arxiu.uuid.buit.avis"/>"></span>
               		 	{{/if}}
           			 </script>
				</th>
				<th data-col-name="fitxerTipusMime" width="10%"><spring:message code="annexos.admin.columna.fitxerTipusMime"/></th>
				<th data-col-name="signaturaInfo" width="10%"><spring:message code="annexos.admin.columna.signaturaInfo"/></th>
				<th data-col-name="tipusFirma" data-visible="false"></th>	
				<th data-col-name="fitxerExtension" data-visible="false"></th>
				<th data-col-name="firmaCsv" data-visible="false"></th>												
				
				<c:if test="${isRolActualAdministrador}">
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div id="div-btn-accions" class="dropdown">
							<button id="btn-accions" class="btn btn-primary" data-toggle="dropdown" style="display:flex; width:100%;">
								<span class="fa fa-cog"></span>
								<span class="hidden_dis"><spring:message code="comu.boto.accions"/></span>
								<span class="caret"></span>
							</button>
							<ul class="dropdown-menu">
								<li><a data-refresh-tancar="true" id="detall-button" href="registreAdmin/{{:registreId}}/detall" data-toggle="modal" data-maximized="true"><span class="fa fa-dot-circle-o"></span>&nbsp;&nbsp;<spring:message code="annexos.admin.boto.detalls.anotacio"/></a></li>								

								<li><a data-refresh-tancar="true" id="detall-annex-button" href="registreUser/registreAnnex/{{:registreId}}/{{:id}}?isVistaMoviments=false" data-toggle="modal" data-maximized="true"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="annexos.admin.boto.detalls.annex"/></a></li>
								
								<li><a href="contingut/registre/{{:registreId}}/annex/{{:id}}/arxiu/DOCUMENT_ORIGINAL" data-maximized="true"><span class="fa fa-download"></span>&nbsp;&nbsp;<spring:message code="annexos.admin.boto.descarregar.original"/></a></li>
								
								{{if fitxerExtension=='csv'}}
									<li><a href="contingut/registre/{{:registreId}}/annex/{{:id}}/arxiu/DOCUMENT" data-maximized="true"><span class="fa fa-download"></span>&nbsp;&nbsp;<spring:message code="annexos.admin.boto.descarregar.imprimible"/></a></li>
								{{/if}}

								{{if firmaCsv is not empty}}	
									{{if '${concsvBaseUrl}' is not empty}}								
										<li><a href="${concsvBaseUrl}/view.xhtml?hash={{:firmaCsv}}" target="_blank" data-maximized="true"><span class="fa fa-external-link"></span>&nbsp;&nbsp;<spring:message code="registre.annex.detalls.camp.firmaCsv.consv"/></a></li>
									{{/if}}
								{{/if}}		

								{{if arxiuEstat!='DEFINITIU'}}
									<li><a href="annexosAdmin/{{:id}}/guardarDefinitiu"><span class="fa fa-pencil-square"></span>&nbsp;&nbsp;<spring:message code="annexos.admin.boto.guardar.definitiu"/></a></li>								
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