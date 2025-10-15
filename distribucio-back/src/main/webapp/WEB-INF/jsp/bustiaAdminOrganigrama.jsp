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
	<link href="<c:url value="/webjars/jstree/3.2.1/dist/themes/default/style.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/webjars/jstree/3.2.1/dist/jstree.min.js"/>"></script>
	
	<script type="text/javascript">

	function generarExcel() {
	    fetch('${unitatCodiUrlPrefix}bustiaAdminOrganigrama/excelUsuarisPerBustia')
	        .then(r => r.text())
	        .then(taskId => {
	            document.getElementById("msg").innerHTML = 
	                '<span class="text-info"><i class="fa fa-spinner fa-spin"></i> Generando Excel...</span>';

	            let interval = setInterval(() => {
	                fetch('${unitatCodiUrlPrefix}bustiaAdminOrganigrama/excelStatus/' + taskId)
	                    .then(r => r.json())
	                    .then(ready => {
	                        if (ready) {
	                            clearInterval(interval);

	                            // 👉 Lanzamos la descarga automáticamente
	                            window.location.href = '${unitatCodiUrlPrefix}bustiaAdminOrganigrama/excelDownload/' + taskId;

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

	function formatSelectUnitatFiltre(item) {
		return formatSelectUnitatItem($('#unitatIdFiltre'), item);
	}

	function formatSelectUnitat(item) {
		return formatSelectUnitatItem($('#unitatId'), item);
	}
	


	function changedCallback(e, data) {
		$('#panellInfo').css('visibility', '');
		$('#panellInfo').css('display', 'none');
		$(".datatable-dades-carregant").css("display", "block");
	
		var bustiaId = data.node.id;
		
		var permisUrl = "bustiaAdmin/" + bustiaId + "/permis";
		$('#permis-boto-nou').attr('href', permisUrl + '/new');
		$('#permisos').webutilDatatable('refresh-url', permisUrl  + '/datatable');
	
		$('#permisos').off('draw.dt');
		$('#permisos').on( 'draw.dt', function () {
	 		$.each($('#permisos .dropdown-menu a'), function( key, permisionLink ) {
		 		var link = $(permisionLink).attr('href');
		 		var replaced = link.replace("bustiaIdString", bustiaId);
		 		$(permisionLink).attr('href', replaced);
		 	});
		});
			
		var bustiaUrl = "bustiaAdminOrganigrama/" + bustiaId;
		var bustiaNomSel = $('#nom', $('#panellInfo'));
		var unitatSel = $('#unitatId', $('#panellInfo'));
	 
		$.ajax({
		    type: 'GET',
		    url: bustiaUrl,
		    success: function(bustiaDto) {
	
		        // setting bustia id and pare id
		        $('#id', $('#panellInfo')).val(bustiaDto.id);
		        $('#pareId', $('#panellInfo')).val(bustiaDto.pare.id);
	
		        // setting selected bustia name and unitat
		        bustiaNomSel.val(bustiaDto.nom);
		        var newOption = new Option(bustiaDto.unitatOrganitzativa.codiAndNom, bustiaDto.unitatOrganitzativa.id, false, true);
		        unitatSel.append(newOption).trigger('change');
	
		        // showing activate or desactivate button depending on whether bustia is active or not
		        var isActiva = bustiaDto.activa;
		        if (isActiva) {
		            $('#activarBtn').hide();
		            $('#desactivarBtn').show();
		            // Si és per defecte i hi ha més d'una bústia llavors mostra la modal per seleccionar la nova per defecte
		            if (bustiaDto.perDefecte && bustiaDto.unitatOrganitzativa.bustiesCount > 1) {
		            	$('#desactivarBtn').attr('onclick', 'desactivarDefault()');
		            } else {
		            	$('#desactivarBtn').attr('onclick', 'desactivar()');
		            }
		        } else {
		            $('#activarBtn').show();
		            $('#desactivarBtn').hide();
		        }
	
		        if (bustiaDto.perDefecte) {
		            $('#marcarPerDefecteBtn').hide();
		        } else {
		            $('#marcarPerDefecteBtn').show();
		        }
		        
		        // showing obsolete panel if unitat of this bustia is obsoleta
		        if (bustiaDto.unitatOrganitzativa.tipusTransicio != null) {
		            $('#panelUnitatObsoleta').show();
		        } else {
		            $('#panelUnitatObsoleta').hide();
		        }
		        // setting last historico unitats
		        $("#lastHistoricosUnitats").empty();
				$.each( bustiaDto.unitatOrganitzativa.lastHistoricosUnitats, function( key, newUnitat ) {
					$("#lastHistoricosUnitats").append('<li>'+newUnitat.denominacio+' ('+newUnitat.codi+')'+'</li>');
		            });
		    },
		    complete: function() {
		        $('#panellInfo').css('display', 'block');
		        $(".datatable-dades-carregant").css("display", "none");
		    }
	
		});
		var otherBustiesOfUnitatObsoletaUrl = "bustiaAdminOrganigrama/" + bustiaId +"/otherBustiesOfUnitatObsoleta";
		$.ajax({
			type : 'GET',
			url : otherBustiesOfUnitatObsoletaUrl,
			success : function(otherBustiesOfUnitatObsoleta) {
				// showing obsolete panel if unitat of this bustia is obsoleta
				if (!$.isEmptyObject(otherBustiesOfUnitatObsoleta)) {
					$('#otherBustiesOfUnitatObsoletaPanel').show();
				} else {
					$('#otherBustiesOfUnitatObsoletaPanel').hide();
				}
				$("#otherBustiesOfUnitatObsoleta").empty();
			$.each( otherBustiesOfUnitatObsoleta, function( key, otherBustia ) {
				$("#otherBustiesOfUnitatObsoleta").append('<li>'+otherBustia.nom+'</li>');
						});
			},
			complete : function() {
				$('#panellInfo').css('display', 'block');
				$(".datatable-dades-carregant").css("display", "none");
			}
		});
	};

	function deleteBustia() {
		if (confirm('<spring:message code="contingut.confirmacio.esborrar.node"/>')) {
	  location.href="${unitatCodiUrlPrefix}bustiaAdminOrganigrama/" + $('#id').val() + "/delete";		
		}
	}

	function marcarPerDefecte() {
		location.href = "bustiaAdminOrganigrama/" + $('#id').val() + "/default";
	}

	function moureAnotacions(tipusVista) {
		var linkMoureAnotacions = document.getElementById("moureAnotacionsBtn").href="bustiaAdmin/" + $('#id').val() + "/moureAnotacions/" + tipusVista;
		linkMoureAnotacions.click();
	}

	function activar() {
		$('#panellInfo').css('visibility', '');
		$('#panellInfo').css('display', 'none');
		$(".datatable-dades-carregant").css("display", "block");

		var enableUrl = "bustiaAdminOrganigrama/" + $('#id').val() + "/enable";

		$.ajax({
					type : 'GET',
					url : enableUrl,
					success : function() {

						var bustiaId = $('#id', $('#panellInfo')).val();

			var fullaSel = $('#arbreUnitatsOrganitzatives li#' + bustiaId + ' a');

						$(fullaSel).removeClass("fullesAtributCssClass");

						// showing desactivate and hiding activate button 
						$('#activarBtn').hide();
						$('#desactivarBtn').show();

						$('#contingut-missatges *').remove();
			$('#contingut-missatges').append(' <div class="alert alert-success"> <button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button> <spring:message code="bustia.controller.activat.ok"/> </div>');

					},
					complete : function() {
						$('#panellInfo').css('display', 'block');
						$(".datatable-dades-carregant").css("display", "none");
					}
		});

	}

	function desactivar() {

		$('#panellInfo').css('visibility', '');
		$('#panellInfo').css('display', 'none');
		$(".datatable-dades-carregant").css("display", "block");

		var disableUrl = "bustiaAdminOrganigrama/" + $('#id').val() + "/disable";

		$.ajax({
					type : 'GET',
					url : disableUrl,
					success : function() {

						var bustiaId = $('#id', $('#panellInfo')).val();

						var fullaSel = $('#arbreUnitatsOrganitzatives li#' + bustiaId + ' a');

						$(fullaSel).addClass("fullesAtributCssClass");

						// showing activate and hiding disactivate button 
						$('#desactivarBtn').hide();
						$('#activarBtn').show();

						$('#contingut-missatges *').remove();
						$('#contingut-missatges').append(' <div class="alert alert-success"> <button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button> <spring:message code="bustia.controller.desactivat.ok"/> </div>');

					},
					complete : function() {
						$('#panellInfo').css('display', 'block');
						$(".datatable-dades-carregant").css("display", "none");
					}
				});

	}

	function desactivarDefault() {
		$('#desactivarBtn').attr('href', 'bustiaAdmin/' + $('#id').val() + "/default/disable");
		$('#desactivarBtn');
	}

	
	function checkSelectedNodes() {
		// Se declara esta función para evitar error JS en el arbre.tag durante la llamada a la misma
	}
	function paintSelectedNodes() {
		// Se declara esta función para evitar error JS en el arbre.tag durante la llamada a la misma
	}
	
	$(document).ready(
		function() {
			$("input:visible:enabled:not([readonly]),textarea:visible:enabled:not([readonly]),select:visible:enabled:not([readonly])").first().focus();

			if ($('#nom').val() || $('#unitatIdFiltre').val()) {
				$('#arbreUnitatsOrganitzatives').jstree('open_all');
			}

		$("#header").append("<div style='float: right;'><button id='canviVistaBusties' class='btn btn-primary'><spring:message code='bustia.canvi.vista'/></button></div>");

		$("#canviVistaBusties").click(function(){
			window.location.replace(webutilContextPath() + "/bustiaAdmin");
		});

        var bustiaModifiedId = ${empty bustiaModifiedId ? 'null' : bustiaModifiedId};
        if (bustiaModifiedId != null) {
            var $arbre = $('#arbreUnitatsOrganitzatives');

            function openAndSelectNode() {
                var tree = $arbre.jstree(true);
                if (tree && tree.get_node(bustiaModifiedId)) {
                    tree.open_node(bustiaModifiedId, function () {
                        tree.activate_node(bustiaModifiedId);
                    });
                    console.log("Nodo abierto y seleccionado:", bustiaModifiedId);
                } else {
                    console.warn("No se encontró el nodo con ID:", bustiaModifiedId);
                }
            }

            // Si jsTree ya está inicializado
            if ($arbre.hasClass("jstree")) {
                console.log("Árbol ya inicializado — ejecutando directamente");
                openAndSelectNode();
            } else {
                console.log("Esperando evento ready.jstree");
                $arbre.on('ready.jstree', function (e, data) {
                    openAndSelectNode();
                })
            }
        }
	});
	</script>
	
	<style>
	.fullesAtributCssClass {
	    opacity: 0.5;
	}
	</style>
</head>
<body>
	<!------------------------- FILTER ------------------------>
	<form:form action="" method="post" cssClass="well" modelAttribute="bustiaFiltreOrganigramaCommand">
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
                    id="unitatIdFiltre"
					name="unitatId"
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					inline="true"
					placeholderKey="bustia.form.camp.unitat"
					suggestValue="id" 
					suggestText="codiAndNom"
					optionTemplateFunction="formatSelectUnitatFiltre"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6 row">
				<div class="col-md-4" style="padding-left: 30px;">
					<dis:inputCheckbox name="unitatObsoleta" inline="true" textKey="bustia.list.filtre.obsolataUnitat"/>
				</div>	
				<div class="col-md-4" style="padding-left: 30px;">
					<dis:inputCheckbox name="perDefecte" inline="true" textKey="bustia.list.filtre.perDefecte"/>
				</div>
				<div class="col-md-4" style="padding-left: 30px;">
					<dis:inputCheckbox name="activa" inline="true" textKey="bustia.list.filtre.activa"/>
				</div>					
			</div>
			<div class="col-md-6 pull-right">				
				<div class="pull-right">		
				
				
											
					<div id="msg" style="min-height:20px; margin-top:10px;"></div>		
<!-- 					<button style="display:none" type="submit" name="accio" value="filtrar" ><span class="fa fa-filter"></span></button> -->
<%-- 					<a href="${unitatCodiUrlPrefix}bustiaAdminOrganigrama/excelUsuarisPerBustiaAntic" class="btn btn-success">  --%>
<%-- 						<span class="fa fa-file-excel-o"></span>&nbsp;<spring:message code="bustia.usuaris" /> --%>
<!-- 					</a>		 -->
					<button type="button" class="btn btn-success" onclick="generarExcel()">
    					<span class="fa fa-file-excel-o"></span>&nbsp;<spring:message code="bustia.usuaris" />
					</button>					
					
					
					
					
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	
	<div class="row">
		<!------------------------- TREE ------------------------>
		<div class="col-md-5">
 			<c:set var="fullesAtributInfoText"><spring:message code="contingut.enviar.info.bustia.defecte"/></c:set> 
 			<c:set var="fillsAtributInfoText"><span style="padding-top: 4.5px; padding-left: 2px;" class="fa fa-warning text-danger pull-right" title="<spring:message code="unitat.arbre.unitatObsoleta"/>"></span></c:set> 
 			
 			
			<div style="padding-bottom: 10px;">
 				<button class="btn btn-default" onclick="$('#arbreUnitatsOrganitzatives').jstree('open_all');"><span class="fa fa-caret-square-o-down"></span> <spring:message code="unitat.arbre.expandeix"/></button> 
 				<button class="btn btn-default" onclick="$('#arbreUnitatsOrganitzatives').jstree('close_all');"><span class="fa fa-caret-square-o-up"></span> <spring:message code="unitat.arbre.contreu"/></button> 
 				<c:if test="${isRolActualAdministrador}">
 					<a style="float: right;" id="bustia-boto-nova" class="btn btn-default" href="bustiaAdminOrganigrama/new" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-plus"></span>&nbsp;<spring:message code="bustia.list.boto.nova.bustia"/></a> 
				</c:if>
			</div>
 			

			<dis:arbre id="arbreUnitatsOrganitzatives" atributId="codi" atributNom="nom" arbre="${arbreUnitatsOrganitzatives}" fulles="${busties}" fullesAtributId="id" fullesAtributNom="nom" 
				fullesAtributPare="unitatCodi" fullesAtributInfo="perDefecte" fullesAtributInfoText="${fullesAtributInfoText}"  fullesIcona="fa fa-inbox fa-lg" 
				changedCallback="changedCallback" isArbreSeleccionable="${false}" isFullesSeleccionable="${true}" isOcultarCounts="${true}" fullesAtributCssClassCondition="inactiva" 
				fillsAtributInfoCondition="obsoleta" fillsAtributInfoText="${fillsAtributInfoText}"
				/>
				
		</div>
		<!------------------------- FORM ------------------------>
		<div class="col-md-7" id="panellInfo"<c:if test="${empty unitatCodi}"> style="visibility:hidden"</c:if>>
			<div class="panel panel-default">
				<div class="panel-heading">
					<h2><spring:message code="bustia.form.titol.modificar"/><small><%-- ${bustia.nom} --%></small></h2>
				</div>
				<div class="panel-body">
					<div class="panel panel-danger" id="panelUnitatObsoleta" style="display: none;">
						<div class="panel-heading">
							<span class="fa fa-warning text-danger"></span>
							<spring:message code="bustia.list.unitatObsoleta"/> 
						</div>
						<div class="panel-body">
							<div class="row">
								<label class="col-xs-4 text-right"><spring:message
										code="bustia.form.novesUnitats" /></label>
								<div class="col-xs-8">
									<ul style="padding-left: 17px;" id="lastHistoricosUnitats">
									</ul>
								</div>
							</div>
								<div class="row" id="otherBustiesOfUnitatObsoletaPanel" style="display: none;">
									<label class="col-xs-4 text-right"><spring:message
			 								code="bustia.form.altresBustiesAfectades" /></label> 
									<div class="col-xs-8">
			 							<ul style="padding-left: 17px;" id="otherBustiesOfUnitatObsoleta"> 
			 							</ul> 
			 						</div> 
			 					</div> 
						</div>
					</div>

					<c:set var="formAction"><dis:modalUrl value="/bustiaAdminOrganigrama/modify"/></c:set>
					<form:form action="${formAction}" method="post" modelAttribute="bustiaCommand" role="form">
						<form:hidden path="id"/>
						<form:hidden path="pareId"/>
						
						<dis:inputSuggest 
							name="unitatId" 
							urlConsultaInicial="${urlConsultaInicial}" 
							urlConsultaLlistat="${urlConsultaLlistat}" 
							inline="false"
							placeholderKey="bustia.form.camp.unitat"
							suggestValue="id"
							suggestText="codiAndNom"
							textKey="bustia.form.camp.unitat"
							required="true" 
							optionTemplateFunction="formatSelectUnitat"/>
						<br/>
						<br/>
						<dis:inputText name="nom" textKey="bustia.form.camp.nom" required="true"/>
						<br/>
						<div class="panel panel-default" style="margin-top: 45px;">
							<div class="panel-heading">
								<h2><spring:message code="bustia.permis.titol"/><small><%-- ${permis.nom} --%></small></h2>
							</div>
							<div class="panel-body">
							  <c:if test="${isRolActualAdministrador}">
								<div class="text-right boto-nou-permis-organigrama" data-toggle="botons-titol">
									<a class="btn btn-default" id="permis-boto-nou" href="" data-toggle="modal" data-datatable-id="permisos"><span class="fa fa-plus"></span>&nbsp;<spring:message code="permis.list.boto.nou.permis"/></a>
								</div>
							  </c:if>
								<table id="permisos" data-toggle="datatable" data-url="<c:url value="/permis/datatable"/>" data-search-enabled="false" data-paging-enabled="false" data-default-order="1" data-default-dir="asc" class="table table-striped table-bordered">
									<thead>
										<tr>
											<th data-col-name="principalTipus" data-renderer="enum(PrincipalTipusEnumDto)"><spring:message code="permis.list.columna.tipus"/></th>
											<th data-col-name="principalNom"><spring:message code="entitat.permis.columna.principal"/></th>
											<th data-col-name="principalDescripcio"><spring:message code="entitat.permis.columna.descripcio"/></th>
                                            <th data-col-name="tipusPermis" data-renderer="enum(TipusPermisEnumDto)"><spring:message code="bustia.permis.columna.acces"/></th>
											<c:if test="${isRolActualAdministrador}">
											<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
												<script id="cellAccionsTemplate" type="text/x-jsrender">
														<div class="dropdown">
															<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
															<ul class="dropdown-menu">
																<li><a href="bustiaAdmin/bustiaIdString/permis/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
																<li><a href="bustiaAdmin/bustiaIdString/permis/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="entitat.permis.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
															</ul>
														</div>
												</script>
											</th>
											</c:if>
										</tr>
									</thead>
								</table>
							</div>
						</div>
						<c:if test="${isRolActualAdministrador}">			 
						<div class="d-flex justify-content-center">
							<c:set var="tipusVista" value="organigrama"/>
							<div class="">
								<a id="moureAnotacionsBtn" onclick="moureAnotacions('${tipusVista}')" href="" class="btn btn-default" data-toggle="modal" data-maximized="true"><span class="fa fa-share"></span>
									&nbsp;&nbsp;<spring:message code="bustia.list.accio.moure.anotacions"/>
								</a> 
							</div>		
							<div class="ml-6">
								<button id="marcarPerDefecteBtn" type="button" onclick="marcarPerDefecte()" class="btn btn-default"><span class="fa fa-check-square-o"></span> <spring:message code="bustia.list.accio.per.defecte"/></button>
							</div>		
							
							<div class="ml-6">
								<button id="activarBtn" type="button" onclick="activar()" style="display: none;" class="btn btn-default"><span class="fa fa-check"></span> <spring:message code="comu.boto.activar"/></button>
								<a id="desactivarBtn" onclick="desactivar()" href="" class="btn btn-default" data-toggle="modal" data-maximized="true" data-refresh-pagina="true"><span class="fa fa-times"></span> <spring:message code="comu.boto.desactivar"/></a>
							</div>
						
							<div class="ml-6" style="margin-left: 15px;">
								<button type="button" class="btn btn-default" onclick="deleteBustia()"><span class="fa fa-trash-o"></span> <spring:message code="contingut.admin.boto.esborrar"/></button>
							</div>
						
							<div class="ml-6">
								<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
							</div>
						</div>	
						</c:if>					
					</form:form>
				</div>
			</div>
		</div>
		<div class="col-md-7 datatable-dades-carregant" style="display: none; text-align: center; margin-top: 100px;">
			<span class="fa fa-circle-o-notch fa-spin fa-3x"></span>
		</div>
	</div>

</body>