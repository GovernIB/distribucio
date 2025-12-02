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
<c:url value="/unitatajax/unitat" var="urlConsultaInicial"/>
<c:url value="/unitatajax/unitats" var="urlConsultaLlistat"/>
<c:url value="/unitatajax/unitatSuperior" var="urlConsultaInicialUnitatSuperior"/>
<c:url value="/unitatajax/unitatsSuperiors" var="urlConsultaLlistatUnitatsSuperiors"/>
<html>
<head>
	<title><spring:message code="bustia.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<c:if test="${requestLocale == 'en'}">
		<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.full.min.js"/>"></script> 
	</c:if>
	<script src="<c:url value="/js/select2-locales/select2_${requestLocale}.full.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	<script type="text/javascript">
	
	function generarExcel() {
	    fetch('${unitatCodiUrlPrefix}bustiaAdmin/excelUsuarisPerBustia')
	        .then(r => r.text())
	        .then(taskId => {
	            document.getElementById("msg").innerHTML = 
	                '<span class="text-info"><i class="fa fa-spinner fa-spin"></i> Generando Excel...</span>';

	            let interval = setInterval(() => {
	                fetch('${unitatCodiUrlPrefix}bustiaAdmin/excelStatus/' + taskId)
	                    .then(r => r.json())
	                    .then(ready => {
	                        if (ready) {
	                            clearInterval(interval);

	                            // 👉 Lanzamos la descarga automáticamente
	                            window.location.href = '${unitatCodiUrlPrefix}bustiaAdmin/excelDownload/' + taskId;

	                            // 👉 Limpiamos inmediatamente el mensaje
	                            document.getElementById("msg").innerHTML = '';
	                        }
	                    });
	            }, 2000);
	        });
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
	
	function formatSelectUnitatSuperior(item) {
		return formatSelectUnitatItem($('#codiUnitatSuperior'), item);
	}

	function formatSelectUnitat(item) {
		return formatSelectUnitatItem($('#unitatId'), item);
	}
		

	$(document).ready(
		function() {
			$("input:visible:enabled:not([readonly]),textarea:visible:enabled:not([readonly]),select:visible:enabled:not([readonly])").first().focus();

			$("#header").append("<div style='float: right;'><button id='canviVistaBusties' class='btn btn-primary'><spring:message code='bustia.canvi.vista'/></button></div>");
		
			$("#canviVistaBusties").click(function(){
				window.location.replace(webutilContextPath() + "/bustiaAdminOrganigrama");
			});

            $('#permisBtn').click(function() {
                permis = !$(this).hasClass('active');
                // Modifica el formulari
                $('#permis').val(permis);
            })
		});
	</script>
</head>
<body>
	<form:form action="" method="post" cssClass="well" modelAttribute="bustiaFiltreCommand">
		<div class="row">
			<div class="col-md-4">
				<dis:inputText name="nom" inline="true" placeholderKey="bustia.list.filtre.nom"/>
			</div>
			<div class="col-md-4">
				<dis:inputSuggest
					name="codiUnitatSuperior" 
					urlConsultaInicial="${urlConsultaInicialUnitatSuperior}" 
					urlConsultaLlistat="${urlConsultaLlistatUnitatsSuperiors}" 
					inline="true"
					placeholderKey="unitat.list.filtre.codiUnitatSuperior"
					suggestValue="codi"
					suggestText="codiAndNom"
					minimumInputLength="0"
					optionTemplateFunction="formatSelectUnitatSuperior"/>
			</div>
			<div class="col-md-4">
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
		</div>
		<div class="row">
			<div class="row col-md-8">
				<div class="col-md-3" style="padding-left: 30px;">
					<dis:inputCheckbox name="unitatObsoleta" inline="true" textKey="bustia.list.filtre.obsolataUnitat"/>
				</div>
				<div class="col-md-3" style="padding-left: 30px;">
					<dis:inputCheckbox name="perDefecte" inline="true" textKey="bustia.list.filtre.perDefecte"/>
				</div>
				<div class="col-md-3" style="padding-left: 30px;">
					<dis:inputCheckbox name="activa" inline="true" textKey="bustia.list.filtre.activa"/>
				</div>
                <div class="col-md-3">
                    <button id="permisBtn" style="width: 45px;" title="<spring:message code="bustia.list.filtre.permis"/>" class="btn btn-default <c:if test="${bustiaFiltreCommand.permis}">active</c:if>" data-toggle="button"><span class="fa fa-warning"></span></button>
                    <dis:inputHidden name="permis"/>
                </div>
			</div>
			<div class="col-md-3 pull-right">
				<div class="pull-right">
				
				
					<div id="msg" style="min-height:20px; margin-top:10px;"></div>		
<!-- 						<button style="display:none" type="submit" name="accio" value="filtrar" ><span class="fa fa-filter"></span></button> -->
<%-- 						<a href="${unitatCodiUrlPrefix}bustiaAdmin/excelUsuarisPerBustia" class="btn btn-success">  --%>
<%-- 							<span class="fa fa-file-excel-o"></span>&nbsp;<spring:message code="bustia.usuaris" /> --%>
<!-- 						</a> -->
					<button type="button" class="btn btn-success" onclick="generarExcel()">
    					<span class="fa fa-file-excel-o"></span>&nbsp;<spring:message code="bustia.usuaris" />
					</button>	
					
					
					
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
				<th data-col-name="entitat.codi" data-visible="false"></th>
				<th data-col-name="unitatOrganitzativa.bustiesCount" data-visible="false"></th>
				<th data-col-name="nom" data-template="#nomTemplate">
					<spring:message code="bustia.list.columna.nom"/>
					<script id="nomTemplate" type="text/x-jsrender">
						{{:nom}}
						{{if unitatOrganitzativa.estat=='E'||unitatOrganitzativa.estat=='A' || unitatOrganitzativa.estat=='T'}}
							<a href="${unitatCodiUrlPrefix}bustiaAdmin/{{:id}}/transicioInfo" data-toggle="modal">
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
				<c:if test="${isRolActualAdministrador}">
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
				<c:set var="tipusVista" value="list"/>
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="${unitatCodiUrlPrefix}bustiaAdmin/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								{{if !perDefecte}}
								    <li><a href="${unitatCodiUrlPrefix}bustiaAdmin/{{:id}}/default" data-toggle="ajax"><span class="fa fa-check-square-o"></span>&nbsp;&nbsp;<spring:message code="bustia.list.accio.per.defecte"/></a></li>
								{{/if}}
								{{if !activa}}
									<li><a href="${unitatCodiUrlPrefix}bustiaAdmin/{{:id}}/enable" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
								{{else}}
									{{if perDefecte && unitatOrganitzativa.bustiesCount > 1}}
										<li><a href="${unitatCodiUrlPrefix}bustiaAdmin/{{:id}}/default/disable" data-toggle="modal"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
									{{else}}
										<li><a href="${unitatCodiUrlPrefix}bustiaAdmin/{{:id}}/disable" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
									{{/if}}
								{{/if}}
								<li><a href="${unitatCodiUrlPrefix}bustiaAdmin/{{:id}}/moureAnotacions/${tipusVista}" data-toggle="modal" data-maximized="true"><span class="fa fa-share"></span>&nbsp;&nbsp;<spring:message code="bustia.list.accio.moure.anotacions"/></a></li>
								<li><a href="${unitatCodiUrlPrefix}bustiaAdmin/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="bustia.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
				</c:if>
			</tr>
		</thead>
	</table>
	<script id="botonsTemplate" type="text/x-jsrender">
	<c:if test="${isRolActualAdministrador}">
		<p style="text-align:right"><a id="bustia-boto-nova" class="btn btn-default" href="${unitatCodiUrlPrefix}bustiaAdmin/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="bustia.list.boto.nova.bustia"/></a></p>
	</c:if>
	</script>
</body>