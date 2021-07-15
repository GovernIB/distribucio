<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<% 
	pageContext.setAttribute(
				"isRolActualAdministrador",
				es.caib.distribucio.war.helper.RolHelper.isRolActualAdministrador(request));
%>


<c:if test="${registre.agafatPer.codi == pageContext.request.userPrincipal.name}"><c:set var="registreAgafatPerUsuariActual" value="${true}"/></c:if>
<html>
<head>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/webjars/pdf-js/2.5.207/build/pdf.js"/>"></script>
	<!------------------------------------------- Contant interessats sense incloure representants --------------------------------------------->
	<c:set var="countInteressats" value="0"/>
	<c:if test="${not empty registre.interessats}">
		<c:forEach var="interessat" items="${registre.interessats}" varStatus="status">
			<c:if test="${empty interessat.representat}">
				<c:set var="countInteressats" value="${countInteressats + 1}"/>
			</c:if>
		</c:forEach>
	</c:if>
	<title><spring:message code="contingut.admin.info.titol"/></title>
	<dis:modalHead/>
	
	<style>
body {
	min-height: 400px;
}
.tab-content {
    margin-top: 0.8em;
}
.icona-doc {
	color: #666666
}
.file-dt {
	margin-top: 9px;
}
.file-dd {
	margin-top: 3px;
}
tr.odd {
	background-color: #f9f9f9;
}
tr.detall {
/* 	background-color: cornsilk; */
}
tr.clicable {
	cursor: pointer;
}

@media (min-width: 768px){
.dl-horizontal dt {
   text-overflow: clip !important;
}}

#resum-annexos .dl-horizontal dt {
    width: 250px;
}

#resum-annexos .dl-horizontal dd {
    margin-left: 280px;
}

#dropAccions {
	display: flex;
	align-items: center;
}

#dropAccions label:nth-child(1) {
	width: 200px;
	height: 30px;
	margin: 0 10% 0 0;
}

#dropAccions > label.tramitacio {
	background-color: #f99957;
}

#dropAccions > label.coneixement {
	background-color: #5bc0de;
}

#dropAccions ul.dropdown-menu {
	left: auto;
    right: 0;
    margin-right: -10px;
}
#resum-annexos-container {
	display: flex;
}

#resum-viewer {
	display: none;
	margin-left: 1%;
	width: 100%;
}

.invalid-format td {
	cursor: auto !important;
	opacity: 0.4;
}

.invalid-format td:nth-child(7), .invalid-format td:nth-child(8) {
	opacity: 1;
}

#container {
	padding-top: 1%;
}

#resum-annexos > table > tbody td {
	cursor: pointer;
}

.viewer-content {
	width: 100%;
	padding-top: 1% !important;
}

.viewer-content > .dl-horizontal, .viewer-firmes-container > .dl-horizontal {
	margin-bottom: 0;
}

.viewer-firmes hr {
	margin-top: 5px !important;
	margin-bottom: 5px !important;
}

.viewer-padding {
	padding: 0% 2% 0% 2%;
}

.line {
	width: 90px;
	height: 3px;
	background-color: black;
	margin-top: -6px;
}

.rmodal_loading {
    background: rgba( 255, 255, 255, .8 ) 
                url('<c:url value="/img/loading.gif"/>') 
                50% 50% 
                no-repeat;
}
#avanzarPagina:focus {
	outline:0;
}

#avanzarPagina:focus[aria-pressed="false"] {
	background-color: #fff;
}
.list-info {
	display: block;
	padding: 3px 20px;
	clear: both;
	white-space: nowrap;
}
.alliberat {
	background: #FF9C59 !important;
	border-color: #FF9C59 !important;
}
</style>
<script type="text/javascript">
	// <![CDATA[

	$(document).ready(function() {
    	var vistaMovimentsCookie = getCookie("vistaMoviments");
    	var isVistaMoviments = (vistaMovimentsCookie == "" || !JSON.parse(vistaMovimentsCookie))? false : true;
		$(".desplegable").click(function(){
			$(this).find("span").toggleClass("fa-caret-up");
			$(this).find("span").toggleClass("fa-caret-down");
		});
	    $("#collapse-justificant").on('show.bs.collapse', function(data){    
		    if (!$(this).data("loaded")) {
		        var registreId = $(this).data("registreId"); 
		        $("#collapse-justificant").append("<div style='text-align: center; margin-bottom: 60px; margin-top: 60px;''><span class='fa fa-circle-o-notch fa-spin fa-3x'/></div>");
		        $("#collapse-justificant").load("<c:url value="/nodeco/contingut/"/>" + "/registre/" + registreId + "/registreJustificant/" + isVistaMoviments);
		        $(this).data("loaded", true);
		    }
	    });
	    $(".collapse-annex").on('show.bs.collapse', function(data){  
		    if (!$(this).data("loaded")) {	
		    	var registreId = $(this).data("registreId"); 
		        var annexId = $(this).data("annexId");
		        $(this).append("<div style='text-align: center; margin-bottom: 60px; margin-top: 60px;''><span class='fa fa-circle-o-notch fa-spin fa-3x'/></div>");
		        $(this).load("<c:url value="/nodeco/registreUser/registreAnnex/"/>" + "/" + registreId + "/" + annexId + "/" + isVistaMoviments);
		        $(this).data("loaded", true);
		    }
	    });
		$('.arxiuInfoTab').on('shown.bs.tab', function(data){
			if (!$(this).data("loaded")) {	
		    	var registreId = $(this).data("registreId"); 
		        $('#arxiuInfo').load("<c:url value="/nodeco/contingut/"/>" + "/registre/" + registreId + "/arxiuInfo/" + isVistaMoviments);
		        $(this).data("loaded", true);
		    }
		});		    

    	$( ".processarBtn" ).on( "click", function() {

	        $('.processamentInfo').css('display', 'none');
	        $(".datatable-dades-carregant").css("display", "block");
    	});

    	$( "a#accioReenviar" ).on( "click", function() {
			var url = new URL(window.location);
			var params = url.searchParams;
    		//<c:if test="${registreTotal!=null}">
			var registreTotal = ${registreTotal};
			var avanzar = $('#avanzarPagina').hasClass('active');
    		// Afegeix els paràmetres a l'enllaç dels detalls
			if (${registreNumero} < registreTotal) {
			params.set("registreTotal", registreTotal);
			params.set("ordreColumn", '${ordreColumn}');
			params.set("ordreDir", '${ordreDir}');
			params.set("avanzarPagina", avanzar);
			}
			//</c:if>
				
         	// if is in modal window
            if ( self !== top ) {
            	location.href = '<c:url value="/modal/registreUser/pendent/${registre.id}/reenviar"/>?' + params.toString();
            } else {
            	location.href = '<c:url value="/registreUser/pendent/${registre.id}/reenviar"/>?' + params.toString();
	        }
    		
    	});
    	$( "a#accioClassificar" ).on( "click", function() {

         	// if is in modal window
            if ( self !== top ) {
            	location.href = "<c:url value="/modal/registreUser/classificar/${registre.id}"/>";
            } else {
            	location.href = "<c:url value="/registreUser/classificar/${registre.id}"/>";
	        }
    	});    	
    	$( "a#accioMarcarProcessat" ).on( "click", function() {

         	// if is in modal window
            if ( self !== top ) {
            	location.href = "<c:url value="/modal/registreUser/pendent/${registre.id}/marcarProcessat"/>";
            } else {
            	location.href = "<c:url value="/registreUser/pendent/${registre.id}/marcarProcessat"/>";
	        }
    	});   

				

    	//<c:if test="${registreTotal!=null}">
	    	$('[name=btnAnterior],[name=btnSeguent]').click(function(){
	    		try {
	    			var registreNumero = $(this).data('registreNumero');
	    			var registreTotal = ${registreTotal};
				console.log(registreNumero);
					// Afegeix els paràmetres a l'enllaç dels detalls
					var url = new URL(window.location);
					var params = url.searchParams;
					params.set("registreTotal", registreTotal);
					params.set("ordreColumn", '${ordreColumn}');
					params.set("ordreDir", '${ordreDir}');
	    			// Navega al registre
	    			<c:choose>
	    				<c:when test="${isRolActualAdministrador}">
			    			location.href = '<c:url value="/registreAdmin/navega/"/>' + registreNumero  +'?' + params.toString();
	    				</c:when>
	    				<c:otherwise>
			    			location.href = '<c:url value="/registreUser/navega/"/>' + registreNumero  +'?' + params.toString();
	    				</c:otherwise>
	    			</c:choose>
	    		} catch(e) {
	    			console.error("Error en la navegació de registre: " + e);
	    		}
	    		return false;
	    	});
    	//</c:if>
	});
    // ]]>
	var previousAnnex;
	function showViewer(event, annexId, observacions, dataCaptura, origen) {
		if (event.target.cellIndex === undefined || event.target.cellIndex === 6 || event.target.cellIndex === 7) return;
        var resumViewer = $('#resum-viewer');
        var resumAnnexos = $('#resum-annexos');
        var vistaMovimentsCookie = getCookie("vistaMoviments");
    	var isVistaMoviments = (vistaMovimentsCookie == "" || !JSON.parse(vistaMovimentsCookie))? false : true;
		// Mostrar/amagar visor
		if (!resumViewer.is(':visible')) {
			resumViewer.slideDown(500);
			resumAnnexos.removeAttr("style");
		} else if (previousAnnex == undefined || previousAnnex == annexId) {
			closeViewer();
			event.srcElement.parentElement.style = "background: #fffff";
    		previousAnnex = annexId;
			return;
		}
		resetBackground();
		event.srcElement.parentElement.style = "background: #f9f9f9";
		previousAnnex = annexId;
		
        // Mostrar contingut capçalera visor
        resumViewer.find('*').not('#container').remove();
        var viewerContent = '<div class="panel-heading"><spring:message code="registre.detalls.pipella.previsualitzacio"/> \
        					 <span class="fa fa-close" style="float: right; cursor: pointer;" onClick="closeViewer()"></span>\
        					 </div>\
        					 <div class="viewer-content viewer-padding">\
        						<dl class="dl-horizontal">\
		        					<dt style="text-align: left;"><spring:message code="registre.annex.detalls.camp.eni.data.captura"/>: </dt><dd>' + dataCaptura + '</dd>\
		        					<dt style="text-align: left;"><spring:message code="registre.annex.detalls.camp.eni.origen"/>: </dt><dd>' + origen + '</dd>\
		        					<dt style="text-align: left;"><spring:message code="registre.annex.detalls.camp.observacions"/>: </dt><dd>' + observacions + '</dd>\
	        					</dl>\
        					 </div>';
        resumViewer.prepend(viewerContent);
        
        // Recupera i mostrar contingut firmes
        $.get(
				"<c:url value="/registreUser/registreAnnexFirmes/${registreId}/"/>" + annexId + "/" + isVistaMoviments,
				function(data) {
					if (data.firmes && data.firmes.length > 0) {
						var nieList = "", nomList = "";
						var viewerContent = '<div class="viewer-firmes viewer-padding">\
												<hr>\
					    						<div class="viewer-firmes-container">';
					    data.firmes.forEach(function(firma) {
	    					nieList += '[';
	    					firma.detalls.forEach(function(firmaDetall, index) {
								if (firmaDetall.responsableNif != undefined && firmaDetall.responsableNif != null)	
									nieList += firmaDetall.responsableNif + (index !== (firma.detalls.length -1) ? ', ' : '');
								if (firmaDetall.responsableNom != undefined && firmaDetall.responsableNom != null)
									nomList += firmaDetall.responsableNom + (index !== (firma.detalls.length -1) ? ', ' : '');
								if (firmaDetall.responsableNif == null && firma.autofirma != null)
									nieList += '<spring:message code="registre.annex.detalls.camp.firma.autoFirma"/> <span class="fa fa-info-circle" title="<spring:message code="registre.annex.detalls.camp.firma.autoFirma.info" />"></span>';
								
							});
	    					nieList += ']';
					    });

    					viewerContent += '<dl class="dl-horizontal">\
							   				<dt style="text-align: left;"><spring:message code="registre.annex.detalls.camp.firmants"/>:</dt>\
							   				<dd>' + nieList + (nomList != "" ? ' - ' +  nomList : '') + '</dd>\
							   			  </dl>\
							   			  </div><hr></div>';
   						$(viewerContent).insertAfter('.viewer-content');
					}
				}
		);

	    // Amagar columnes taula
	    var tableAnnexos = resumAnnexos.find('table');
	    tableAnnexos.find('tr').each(function() {
	    	$(this).children("th:eq(2), th:eq(3), th:eq(4), td:eq(2), td:eq(3), td:eq(4)").hide();
	    });

	    // Recuperar i mostrar document al visor
		var urlDescarrega = "<c:url value="/modal/contingut/registre/${registreId}/annex/"/>" + annexId + "/arxiu/content/DOCUMENT";
		$('#container').attr('src', '');
		$('#container').addClass('rmodal_loading');
		showDocument(urlDescarrega);
	}

	function showDocument(arxiuUrl) {
		// Fa la petició a la url de l'arxiu
		$.ajax({
			type: 'GET',
			url: arxiuUrl,
			responseType: 'arraybuffer',
			success: function(response) {

				if (response.error) {
					$('#container').removeClass('rmodal_loading');
					$("#resum-viewer .viewer-padding:last").before('<div class="viewer-padding"><div class="alert alert-danger"><spring:message code="registre.annex.detalls.carregar.error"/>: '+ response.errorMsg +'</div></div>');
				} else {
		            var blob = base64toBlob(response.contingut, response.contentType);
		            var file = new File([blob], response.contentType, {type: response.contentType});
		            link = URL.createObjectURL(file);
		            
		            var viewerUrl = "<c:url value="/webjars/pdf-js/2.5.207/web/viewer.html"/>" + '?file=' + encodeURIComponent(link);
				    $('#container').removeClass('rmodal_loading');
				    $('#container').attr('src', viewerUrl);
				}
			    
			},
			error: function(xhr, ajaxOptions, thrownError) {
				$('#container').removeClass('rmodal_loading');
				alert(thrownError);
			}
		});
	}

	// Amagar visor
	function closeViewer() {
		var resumAnnexos = $('#resum-annexos');
		$('#resum-viewer').slideUp(500, function(){
			resumAnnexos.css('width', '100%');
		
			// Mostrar columnes taula
			var tableAnnexos = resumAnnexos.find('table');
		    tableAnnexos.find('tr').each(function() {
		    	$(this).children("th:eq(2), th:eq(3), th:eq(4), td:eq(2), td:eq(3), td:eq(4)").show();
		    	$(this).removeAttr('style');
		    });
		});
	}
	
	function resetBackground() {
		var tableAnnexos = $('#resum-annexos').find('table');
		tableAnnexos.find('tr').each(function() {
	    	$(this).removeAttr('style');
	    });
	}
</script>

</head>
<body>
	<dis:blocContenidorPath contingut="${registre}"/>
	
	<c:if test="${isContingutAdmin == null}">
		<div class="dropdown" style="float: right;" id="dropAccions">
			<c:if test="${!cookie['vistaMoviments'].value && isEnviarConeixementActiu}">
				<label class="${registre.perConeixement ? 'coneixement' : 'tramitacio'}" title="<spring:message code="${registre.perConeixement ? 'bustia.pendent.info.coneixement' : 'bustia.pendent.info.tramitacio'}"/>"></label>
			</c:if>
			<button id="avanzarPagina" title="<spring:message code="bustia.pendent.accio.avansar"/>" class="btn btn-default btn-sm ${registreNumero >= registreTotal ? 'disabled' : 'active'}" data-toggle="button">
				<span class="fa-stack" aria-hidden="true">
					<i class="fa fa-forward"></i>
		    	</span>
			</button>&nbsp;
			<button class="btn btn-primary ${(!cookie['vistaMoviments'].value && isPermesReservarAnotacions && registre.agafat ? 'alliberat' : '')}" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
			<ul class="dropdown-menu">
				<c:if test="${!cookie['vistaMoviments'].value && (!isPermesReservarAnotacions || !registre.agafat)}">
					<c:choose>
						<c:when test="${registre.procesEstat != 'ARXIU_PENDENT'}">
							<li><a id="accioClassificar" href="#"><span class="fa fa-inbox"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.classificar"/> ...</a></li>
						</c:when>
						<c:otherwise>
							<li class="disabled"><a><span class="fa fa-inbox"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.classificar"/> ...</a></li>
						</c:otherwise>
					</c:choose>
					<li><a id="accioReenviar" href="#"><span class="fa fa-send"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.reenviar"/>...</a></li>
					<c:if test="${registre.procesEstatSimple == 'PENDENT'}">
						<c:choose>
							<c:when test="${registre.procesEstat == 'BUSTIA_PENDENT' || (registre.procesEstat == 'ARXIU_PENDENT' && registre.reintentsEsgotat)}">
								<li><a id="accioMarcarProcessat" href="#"><span class="fa fa-check-circle-o"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.marcar.processat"/>...</a></li>
							</c:when>
							<c:otherwise>
								<li class="disabled"><a><span class="fa fa-check-circle-o"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.marcar.processat"/>...</a></li>
							</c:otherwise>
						</c:choose>	
					</c:if>
				</c:if>
				<li>
					<a href="<c:url value="/contingut/registre/${registre.id}/descarregarZip"/>">
						<span class="fa fa-download"></span> <spring:message code="registre.annex.descarregar.zip"/>
					</a>
				</li>
				<li role="separator" class="divider"></li>
				<li><a href="<c:url value="/contingut/${registre.id}/log/${!cookie['vistaMoviments'].value ? '' : 'moviments'}"/>" data-toggle="modal"><span class="fa fa-list"></span>&nbsp;&nbsp;<spring:message code="comu.boto.historial"/></a></li>
				<c:if test="${!cookie['vistaMoviments'].value && isPermesReservarAnotacions}">
					<li role="separator" class="divider"></li>
					<c:choose>
						<c:when test="${!registre.agafat}">
							<li><a href="<c:url value="/registreUser/${registre.id}/bloquejar"/>"><span class="fa fa-lock"></span>&nbsp;&nbsp;<spring:message code="comu.boto.bloquejar"/></a></li>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${!registreAgafatPerUsuariActual}">
									<li><a href="<c:url value="/registreUser/${registre.id}/alliberar"/>" data-confirm="<spring:message code="bustia.pendent.accio.agafar.confirm.1"/> ${registre.agafatPer.codi}. <spring:message code="bustia.pendent.accio.agafar.confirm.2"/>"><span class="fa fa-unlock"></span>&nbsp;&nbsp;<spring:message code="comu.boto.alliberar"/></a></li>
									<li class="list-info"><spring:message code="bustia.pendent.accio.agafatper"/>&nbsp;&nbsp;${registre.agafatPer.codi}</li>
								</c:when>
								<c:otherwise>
									<li><a href="<c:url value="/registreUser/${registre.id}/alliberar"/>"><span class="fa fa-unlock"></span>&nbsp;&nbsp;<spring:message code="comu.boto.alliberar"/></a></li>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</c:if>
			</ul>
		</div>	

	</c:if>

	
	
	<!--------------------------------------------------- TABLIST ------------------------------------------------------>
	<ul class="nav nav-tabs" role="tablist">
		<li class="active" role="presentation">
			<a href="#resum" aria-controls="resum" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.resum"/></a>
		</li>
		<li role="presentation">
			<a href="#informacio" aria-controls="informacio" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.informacio"/></a>
		</li>
		<li role="presentation">
			<a href="#interessats" aria-controls="interessats" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.interessats"/>&nbsp;<span class="badge">${countInteressats}</span></a>
		</li>
		<li role="presentation">
			<a href="#annexos" aria-controls="annexos" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.annexos"/>&nbsp;<span class="badge">${fn:length(registre.annexos)}</span></a>
		</li>
		<c:if test="${not empty registre.expedientArxiuUuid}">
			<li role="presentation">
				<a href="#arxiuInfo" class="arxiuInfoTab" aria-controls="arxiuInfo" role="tab" data-toggle="tab" data-registre-id="${registre.id}"><spring:message code="registre.detalls.pipella.arxiu.info"/></a>
			</li>
		</c:if>
		
		
		
		<c:if test="${registre.procesEstat == 'ARXIU_PENDENT' || registre.procesEstat == 'REGLA_PENDENT' || registre.procesEstat == 'BACK_PENDENT' || (registre.procesEstat == 'BUSTIA_PROCESSADA' && registre.procesError!= null)}">
			<li role="presentation">
				<a href="#processamentAutomatic"  role="tab" data-toggle="tab">
					<spring:message code="registre.detalls.pipella.proces"/>
					<c:if test="${registre.procesError != null}"><span class="fa fa-warning text-danger"></span></c:if>
				</a>
			</li>
		</c:if>
		<c:if test="${registre.procesEstat == 'BACK_REBUDA' || registre.procesEstat == 'BACK_PROCESSADA' || registre.procesEstat == 'BACK_REBUTJADA' || registre.procesEstat == 'BACK_ERROR'}">
			<li role="presentation">
				<a href="#processamentBackoffice"  role="tab" data-toggle="tab">
					<spring:message code="registre.detalls.pipella.proces.backoffice"/>
				</a>
			</li>
		</c:if>		
		
	</ul>
	<div class="tab-content">
	
	
		<!------------------------------------------- TABPANEL RESUM --------------------------------------------->
		<div class="tab-pane active in" id="resum" role="tabpanel">
			<table class="table table-bordered">
			<tbody>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.numero"/></strong></td>
					<td colspan="3">
						<c:choose>
							<c:when test="${isRolActualAdministrador}">
								${registre.identificador}
							</c:when>
							<c:otherwise>
								${registre.numero}
							</c:otherwise>
						</c:choose>
						
						<a href="<c:url value="/modal/contingut/registre/${registre.id}/justificant"/>" class="btn btn-default btn-sm pull-right">
							<span class="fa fa-download" title="<spring:message code="registre.annex.detalls.camp.fitxer.descarregar"/>"></span>
							<spring:message code="registre.annex.detalls.camp.justificant"/>
						</a>						
					</td>
					<td><strong><spring:message code="registre.detalls.camp.data"/></strong></td>
					<td><fmt:formatDate value="${registre.data}" pattern="dd/MM/yyyy HH:mm:ss"/></td>					
				</tr>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.oficina"/></strong></td>
					<td colspan="3">${registre.oficinaDescripcio} (${registre.oficinaCodi})</td>				
					<td><strong><spring:message code="registre.detalls.camp.proces.presencial"/></strong></td>
					<td class="${registre.presencial}"><spring:message code="boolean.${registre.presencial}"/></td>			
							
				</tr>		
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.extracte"/></strong></td>
					<td colspan="5">${registre.extracte}</td>
				</tr>							
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.procediment"/></strong></td>
					<td colspan="5">${registre.procedimentCodi}</td>
				</tr>				
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.observacions"/></strong></td>
					<td colspan="5">${registre.observacions}</td>
				</tr>						
				<tr>
					<td style="width:16%;"><strong><spring:message code="registre.detalls.camp.origen.num"/></strong></td>
					<td style="width:16%;">${registre.numeroOrigen}</td>
					<td style="width:16%;"><strong><spring:message code="registre.detalls.camp.origen.data"/></strong></td>
					<td style="width:16%;"><fmt:formatDate value="${registre.dataOrigen}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
					<td style="width:16%;"><strong><spring:message code="registre.detalls.camp.origen.oficina"/></strong></td>
					<td style="width:17%;">${registre.oficinaOrigenDescripcio} ${registre.oficinaOrigenCodi!=null?'(':''}${registre.oficinaOrigenCodi}${registre.oficinaOrigenCodi!=null?')':''}</td>
				</tr>				

			</tbody>
			</table>
	
			<!------------------- INTERESSATS ------------------->
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title"><spring:message code="registre.detalls.pipella.interessats"/></h3>
				</div>
				<c:choose>
					<c:when test="${not empty registre.interessats}">
						<table class="table table-bordered">
							<thead>
								<tr>
									<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.tipus"/></th>
									<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.document"/></th>
									<th><spring:message code="registre.detalls.camp.interessat.nom"/></th>
									<th style="width: 50px;"></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="interessat" items="${registre.interessats}" varStatus="status">
									<c:if test="${empty interessat.representat}">
										<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if>>
											<td>
												<spring:message code="registre.interessat.tipus.enum.${interessat.tipus}"/>
											</td>
											<td>${interessat.documentTipus}: ${interessat.documentNum}</td>
											<c:choose>
												<c:when test="${interessat.tipus == 'PERSONA_FIS'}">
													<td>${interessat.nom} ${interessat.llinatge1} ${interessat.llinatge2}</td>
												</c:when>
												<c:otherwise>
													<td>${interessat.raoSocial}</td>
												</c:otherwise>
											</c:choose>
											<td>
												<c:if test="${interessat.tipus != 'ADMINISTRACIO'}">
													<button type="button" class="btn btn-default desplegable" href="#detalls_resum_interessats_${status.index}" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_resum_interessats_${status.index}">
														<span class="fa fa-caret-down"></span>
													</button>
												</c:if>
											</td>
										</tr>
										<tr class="collapse detall" id="detalls_resum_interessats_${status.index}">
											<td colspan="4">
												<div class="row">
													<div class="col-xs-6">
														<dl class="dl-horizontal">
															<dt><spring:message code="interessat.form.camp.pais"/></dt><dd>${interessat.pais} <c:if test="${not empty interessat.paisCodi}">(${interessat.paisCodi})</c:if></dd>
															<dt><spring:message code="interessat.form.camp.provincia"/></dt><dd>${interessat.provincia} <c:if test="${not empty interessat.provinciaCodi}">(${interessat.provinciaCodi})</c:if></dd>											
															<dt><spring:message code="interessat.form.camp.municipi"/></dt><dd>${interessat.municipi} <c:if test="${not empty interessat.municipiCodi}">(${interessat.municipiCodi})</c:if></dd>
															<dt><spring:message code="interessat.form.camp.adresa"/></dt><dd>${interessat.adresa}</dd>
															<dt><spring:message code="interessat.form.camp.codiPostal"/></dt><dd>${interessat.codiPostal}</dd>
															<dt><spring:message code="interessat.form.camp.codiDire"/></dt><dd>${interessat.codiDire}</dd>
														</dl>
													</div>
													<div class="col-xs-6">
														<dl class="dl-horizontal">
															<dt><spring:message code="interessat.form.camp.email"/></dt><dd>${interessat.email}</dd>
															<dt><spring:message code="interessat.form.camp.telefon"/></dt><dd>${interessat.telefon}</dd>
															<dt><spring:message code="registre.interessat.detalls.camp.emailHabilitat"/></dt><dd>${interessat.emailHabilitat}</dd>
															<dt><spring:message code="registre.interessat.detalls.camp.canalPreferent"/></dt><dd><c:if test="${not empty interessat.canalPreferent}"><spring:message code="registre.interessat.detalls.camp.canalPreferent.${interessat.canalPreferent}"/></c:if></dd>
															<dt><spring:message code="interessat.form.camp.observacions"/></dt><dd>${interessat.observacions}</dd>
														</dl>
													</div>
													<c:if test="${not empty interessat.representant}">
														<c:set var="representant" value="${interessat.representant}"/>
														<div class="col-xs-12">
															<table class="table table-bordered">
																<thead>
																	<tr><th colspan="4"><spring:message code="registre.interessat.detalls.camp.representant"/></th></tr>
																	<tr>
																		<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.tipus"/></th>
																		<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.document"/></th>
																		<th><spring:message code="registre.detalls.camp.interessat.nom"/></th>
																		<th style="width: 50px;"></th>
																	</tr>
																</thead>
																<tbody>
																	<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if>>
																		<td>
																			<spring:message code="registre.interessat.tipus.enum.${representant.tipus}"/>
																		</td>
																		<td>${representant.documentTipus}: ${representant.documentNum}</td>
																		<c:choose>
																			<c:when test="${representant.tipus == 'PERSONA_FIS'}">
																				<td>${representant.nom} ${representant.llinatge1} ${representant.llinatge2}</td>
																			</c:when>
																			<c:otherwise>
																				<td>${representant.raoSocial}</td>
																			</c:otherwise>
																		</c:choose>
																		<td>
																			<c:if test="${representant.tipus != 'ADMINISTRACIO'}">
																				<button type="button" class="btn btn-default desplegable" href="#detalls_resum_${status.index}_rep" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_resum_${status.index}_rep">
																					<span class="fa fa-caret-down"></span>
																				</button>
																			</c:if>
																		</td>
																	</tr>
																	<tr class="collapse detall" id="detalls_resum_${status.index}_rep">
																		<td colspan="4">
																			<div class="row">
																				<div class="col-xs-6">
																					<dl class="dl-horizontal">
																						<dt><spring:message code="interessat.form.camp.pais"/></dt><dd>${representant.pais} <c:if test="${not empty representant.paisCodi}">(${representant.paisCodi})</c:if></dd>
																						<dt><spring:message code="interessat.form.camp.provincia"/></dt><dd>${representant.provincia} <c:if test="${not empty representant.provinciaCodi}">(${representant.provinciaCodi})</c:if></dd>											
																						<dt><spring:message code="interessat.form.camp.municipi"/></dt><dd>${representant.municipi} <c:if test="${not empty representant.municipiCodi}">(${representant.municipiCodi})</c:if></dd>
																						<dt><spring:message code="interessat.form.camp.adresa"/></dt><dd>${representant.adresa}</dd>
																						<dt><spring:message code="interessat.form.camp.codiPostal"/></dt><dd>${representant.codiPostal}</dd>
																						<dt><spring:message code="interessat.form.camp.codiDire"/></dt><dd>${representant.codiDire}</dd>
																					</dl>
																				</div>
																				<div class="col-xs-6">
																					<dl class="dl-horizontal">
																						<dt><spring:message code="interessat.form.camp.email"/></dt><dd>${representant.email}</dd>
																						<dt><spring:message code="interessat.form.camp.telefon"/></dt><dd>${representant.telefon}</dd>
																						<dt><spring:message code="registre.interessat.detalls.camp.emailHabilitat"/></dt><dd>${representant.emailHabilitat}</dd>
																						<dt><spring:message code="registre.interessat.detalls.camp.canalPreferent"/></dt><dd><c:if test="${not empty representant.canalPreferent}"><spring:message code="registre.interessat.detalls.camp.canalPreferent.${representant.canalPreferent}"/></c:if></dd>
																						<dt><spring:message code="interessat.form.camp.observacions"/></dt><dd>${representant.observacions}</dd>
																					</dl>
																				</div>
																			</div>
																		</td>						
																	</tr>
																</tbody>
															</table>
														</div>
													</c:if>
												</div>
											</td>						
										</tr>
									</c:if>
								</c:forEach>
							</tbody>
						</table>
					</c:when>
					<c:otherwise>
						<div class="panel-body">
							<spring:message code="registre.interessat.buit"/>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
			
			
			<!------------------- ANNEXOS ------------------->
			<div id="resum-annexos-container">
			<div class="panel panel-default" id="resum-annexos" style="width: 100%">
				<div class="panel-heading">
					<h3 class="panel-title"><spring:message code="registre.detalls.pipella.annexos"/></h3>
				</div>
				<c:choose>
					<c:when test="${not empty registre.annexos}">

				
						<table class="table table-bordered">
						<thead>
							<tr>
								<th style="width: 300px;"><spring:message code="registre.annex.detalls.camp.titol"/></th>
								<th style="width: 180px;"><spring:message code="registre.annex.detalls.camp.eni.tipus.documental"/></th>
								<th style="width: 450px;"><spring:message code="registre.annex.detalls.camp.observacions"/></th>
								<th><spring:message code="registre.annex.detalls.camp.eni.data.captura"/></th>
								<th style="width: 250px;"><spring:message code="registre.annex.detalls.camp.eni.origen"/></th>
								<th style="width: 250px;"><spring:message code="registre.annex.detalls.camp.eni.estat.elaboracio"/></th>
								<th style="width: 50px;"><spring:message code="registre.annex.detalls.camp.fitxer"/></th>
								<th style="width: 50px;"></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="annex" items="${registre.annexos}" varStatus="status">
									<tr title="<spring:message code="registre.annex.detalls.previsualitzar"/>" <c:choose><c:when test="${annex.fitxerExtension == 'pdf' }">onclick="showViewer(event, ${annex.id}, '${annex.observacions}', '${annex.dataCaptura}', '${annex.origenCiutadaAdmin}')"</c:when><c:otherwise>class="invalid-format"</c:otherwise></c:choose>>
										<td>${annex.titol}</td>
										<td><c:if test="${not empty annex.ntiTipusDocument}"><spring:message code="registre.annex.detalls.camp.ntiTipusDocument.${annex.ntiTipusDocument}"/></c:if></td>
										<td>${annex.observacions}</td>
										<td><c:if test="${not empty annex.dataCaptura}"><fmt:formatDate value="${annex.dataCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:if></td>
	
										<td><c:if test="${not empty annex.origenCiutadaAdmin}">${annex.origenCiutadaAdmin}</c:if></td>
										<td><c:if test="${not empty annex.ntiElaboracioEstat}"><spring:message code="registre.annex.detalls.camp.ntiElaboracioEstat.${annex.ntiElaboracioEstat}"/></c:if></td>
										<td>
											<a href="<c:url value="/modal/contingut/registre/${registreId}/annex/${annex.id}/arxiu/DOCUMENT"/>" class="btn btn-default btn-sm pull-right arxiu-download">
												<span class="fa fa-download" title="<spring:message code="registre.annex.detalls.camp.fitxer.descarregar"/>"></span>
											</a>
										</td>												
										<td>
											<button type="button" class="btn btn-default desplegable" href="#detalls_resum_annexos_${status.index}" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_resum_annexos_${status.index}">
												<span class="fa fa-caret-down"></span>
											</button>
										</td>	
									</tr>	
									<tr class="collapse detall" id="detalls_resum_annexos_${status.index}">
									
										<script type="text/javascript">
											$(document).ready(function() {
												$("#detalls_resum_annexos_${status.index}").on('show.bs.collapse', function(event){
													$("#collapse-resum-firmes-<c:out value='${annex.id}'/>").collapse("show");
												});
											});
									</script>	
									
									<td colspan="8">		
										<c:if test="${annex.ambFirma}">
											<div class="panel panel-default">
												<div class="panel-heading">
													<h3 class="panel-title">
														<span class="fa fa-certificate"></span>
														<spring:message code="registre.annex.detalls.camp.firmes"/>
														<button id="collapse-resum-btn-firmes-${annex.id}" class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-resum-firmes-${annex.id}"><span class="fa fa-chevron-down"></span></button>
													</h3>
												</div>
												<div id="collapse-resum-firmes-${annex.id}" class="panel-collapse collapse collapse-resum-firmes" role="tabpanel"> 
													<script type="text/javascript">
														$(document).ready(function() {
															var vistaMovimentsCookie = getCookie("vistaMoviments");
														    var isVistaMoviments = (vistaMovimentsCookie == "" || !JSON.parse(vistaMovimentsCookie))? false : true;
														    $("#collapse-resum-firmes-<c:out value='${annex.id}'/>").on('show.bs.collapse', function(event){  	
															    if (!$(this).data("loaded")) {
															        $(this).append("<div style='text-align: center; margin-bottom: 60px; margin-top: 60px;''><span class='fa fa-circle-o-notch fa-spin fa-3x'/></div>");
															        $(this).load("<c:url value="/nodeco/registreUser/registreAnnexFirmes/"/>/" + ${registreId} + "/" + ${annex.id} + "/true/" + isVistaMoviments);
															        $(this).data("loaded", true);
															    }
															    event.stopPropagation();
														    });
														});
													</script>													
												</div> 
											</div>
										</c:if>											
									</td>
								</tr>									
								
							</c:forEach>
						</tbody>
						</table>
						

					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${not empty annexosErrorMsg}">
								<div class="panel-body">
									<div class="alert alert-danger">
										${annexosErrorMsg}
									</div>
								</div>						
							</c:when>
							<c:otherwise>
								<div class="panel-body">
									<spring:message code="registre.annex.buit"/>
								</div>
							</c:otherwise>
						</c:choose>				
					</c:otherwise>
				</c:choose>				
			</div>	
				<div class="panel panel-default" id="resum-viewer">
					<iframe id="container" class="viewer-padding" width="100%" height="540" frameBorder="0"></iframe>
				</div>     
			</div>	
		</div>
	
		<!------------------------------------------- TABPANEL INFORMACIO --------------------------------------------->
		<div class="tab-pane" id="informacio" role="tabpanel">
			<table class="table table-bordered">
			<tbody>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.tipus"/></strong></td>
					<td><spring:message code="registre.anotacio.tipus.enum.${registre.registreTipus}"/></td>
				</tr>
				<c:if test="${isRolActualAdministrador}">
					<tr>
						<td><strong><spring:message code="contingut.admin.info.camp.contingut"/></strong></td>
						<td>
							<dis:blocIconaContingut contingut="${registre}"/>
							${registre.nom}
						</td>
					</tr>
				</c:if>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.numero"/></strong></td>
					<td>
						<c:choose>
							<c:when test="${isRolActualAdministrador}">
								${registre.identificador}
							</c:when>
							<c:otherwise>
								${registre.numero}
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.data"/></strong></td>
					<td><fmt:formatDate value="${registre.data}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
				</tr>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.proces.estat"/></strong></td>
					<td class="${registre.procesEstat}">
						<spring:message code="registre.proces.estat.enum.${registre.procesEstat}"/>
						<c:if test="${! empty registre.procesError }">
							<c:choose>
								<c:when test="${registre.procesEstat ==  'ARXIU_PENDENT'} ">
									<span class="fa fa-warning text-danger" title="<spring:message code="registre.proces.estat.enum.ARXIU_PENDENT.error"/>:<c:out value="${registre.procesError}" escapeXml="true"/>"></span>
								</c:when>
								<c:when test="${registre.procesEstat ==  'REGLA_PENDENT'}">
									<span class="fa fa-warning text-danger" title="<spring:message code="registre.proces.estat.enum.REGLA_PENDENT.error"/>:<c:out value="${registre.procesError}" escapeXml="true"/>"></span>
								</c:when>
								<c:when test="${registre.procesEstat ==  'BACK_PENDENT'}">
									<span class="fa fa-warning text-danger" title="<spring:message code="registre.proces.estat.enum.BACK_PENDENT.error"/>:<c:out value="${registre.procesError}" escapeXml="true"/>"></span>
								</c:when>
								<c:when test="${registre.procesEstat ==  'BACK_ERROR'}">
									<span class="fa fa-warning text-danger" title="<spring:message code="registre.proces.estat.enum.BACK_ERROR.error"/>:<c:out value="${registre.procesError}" escapeXml="true"/>"></span>
								</c:when>
								<c:otherwise>
									<span class="fa fa-warning text-danger" title="<spring:message code="registre.proces.estat.enum.default"/>:<c:out value="${registre.procesError}" escapeXml="true"/>"></span>
								</c:otherwise>
							</c:choose>
						</c:if>						
					</td>
				</tr>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.proces.presencial"/></strong></td>
					<td class="${registre.presencial}"><spring:message code="boolean.${registre.presencial}"/></td>
				</tr>						
			</tbody>
			</table>
			<div class="row">
				<div class="col-sm-6">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title"><spring:message code="registre.detalls.titol.obligatories"/></h3>
						</div>
						<table class="table table-bordered">
							<tbody>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.oficina"/></strong></td>
									<td>${registre.oficinaDescripcio} (${registre.oficinaCodi})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.llibre"/></strong></td>
									<td>${registre.llibreDescripcio} (${registre.llibreCodi})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.extracte"/></strong></td>
									<td>${registre.extracte}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.docfis"/></strong></td>
									<td>${registre.documentacioFisicaCodi} - ${registre.documentacioFisicaDescripcio}</td>
								</tr>
								<tr>
									<td><strong>
										<c:if test="${registre.registreTipus == 'ENTRADA'}"><spring:message code="registre.detalls.camp.desti"/></c:if>
										<c:if test="${registre.registreTipus == 'SORTIDA'}"><spring:message code="registre.detalls.camp.origen"/></c:if>
									</strong></td>
									<td>${registre.unitatAdministrativaDescripcio} (${registre.unitatAdministrativa})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.assumpte.tipus"/></strong></td>
									<td>${registre.assumpteTipusDescripcio} (${registre.assumpteTipusCodi})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.idioma"/></strong></td>
									<td>${registre.idiomaDescripcio} (${registre.idiomaCodi})</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
				<div class="col-sm-6">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title"><spring:message code="registre.detalls.titol.opcionals"/></h3>
						</div>
						<table class="table table-bordered">
							<tbody>
								<tr>
									<td colspan="2"><strong><spring:message code="registre.detalls.camp.assumpte.codi"/></strong></td>
									<td colspan="2">(${registre.assumpteCodi})</td>
								</tr>
								<tr>
									<td colspan="2"><strong><spring:message code="registre.detalls.camp.procediment"/></strong></td>
									<td colspan="2">${registre.procedimentCodi}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.refext"/></strong></td>
									<td>${registre.referencia}</td>
									<td><strong><spring:message code="registre.detalls.camp.numexp"/></strong></td>
									<td>${registre.expedientNumero}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.transport.tipus"/></strong></td>
									<td>${registre.transportTipusDescripcio} ${registre.transportTipusCodi!=null?'(':''}${registre.transportTipusCodi}${registre.transportTipusCodi!=null?')':''}</td>
									<td><strong><spring:message code="registre.detalls.camp.transport.num"/></strong></td>
									<td>${registre.transportNumero}</td>
								</tr>
								<tr>
									<td colspan="2"><strong><spring:message code="registre.detalls.camp.origen.oficina"/></strong></td>
									<td colspan="2">${registre.oficinaOrigenDescripcio} ${registre.oficinaOrigenCodi!=null?'(':''}${registre.oficinaOrigenCodi}${registre.oficinaOrigenCodi!=null?')':''}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.origen.num"/></strong></td>
									<td>${registre.numeroOrigen}</td>
									<td><strong><spring:message code="registre.detalls.camp.origen.data"/></strong></td>
									<td><fmt:formatDate value="${registre.dataOrigen}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
								</tr>
								<tr>
									<td colspan="2"><strong><spring:message code="registre.detalls.camp.observacions"/></strong></td>
									<td colspan="2">${registre.observacions}</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">
						<spring:message code="registre.detalls.titol.seguiment"/>
						<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-seguiment"><span class="fa fa-chevron-down"></span></button>
					</h3>
				</div>
				<div id="collapse-seguiment" class="panel-collapse collapse" role="tabpanel" aria-labelledby="dadesSeguiment">
					<table class="table table-bordered">
					<tbody>
						<c:if test="${not empty registre.entitatCodi}">
							<tr>
								<td><strong><spring:message code="registre.detalls.camp.entitat"/></strong></td>
								<td>${registre.entitatDescripcio} (${registre.entitatCodi})</td>
							</tr>
						</c:if>
						<c:if test="${not empty registre.aplicacioCodi}">
							<tr>
								<td><strong><spring:message code="registre.detalls.camp.aplicacio"/></strong></td>
								<td>${registre.aplicacioCodi} ${registre.aplicacioVersio}</td>
							</tr>
						</c:if>
						<c:if test="${not empty registre.usuariCodi}">
							<tr>
								<td><strong><spring:message code="registre.detalls.camp.usuari"/></strong></td>
								<td>${registre.usuariNom} (${registre.usuariCodi})</td>
							</tr>
						</c:if>
						<tr>
							<td><strong><spring:message code="registre.detalls.camp.distribucio.alta"/></strong></td>
							<td><fmt:formatDate value="${registre.createdDate}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
						</tr>
					</tbody>
					</table>
				</div>
			</div>
<!-- 			JUSTIFICANT -->
			<c:if test="${not empty registre.justificantArxiuUuid}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<spring:message code="registre.detalls.titol.justificant"/>
							<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-justificant"><span class="fa fa-chevron-down"></span></button>
						</h3>
					</div>
					<div id="collapse-justificant" class="panel-collapse collapse" role="tabpanel" aria-labelledby="justificant" data-registre-id="${registre.id}">

					</div>
				</div>
			</c:if>
<!-- 			FI JUSTIFICANT -->
		</div>
		
		<!------------------------------------------- TABPANEL INTERESSATS --------------------------------------------->
		<div class="tab-pane" id="interessats" role="tabpanel">
			<c:choose>
				<c:when test="${not empty registre.interessats}">
					<table class="table table-bordered">
						<thead>
							<tr>
								<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.tipus"/></th>
								<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.document"/></th>
								<th><spring:message code="registre.detalls.camp.interessat.nom"/></th>
								<th style="width: 50px;"></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="interessat" items="${registre.interessats}" varStatus="status">
								<c:if test="${empty interessat.representat}">
									<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if>>
										<td>
											<spring:message code="registre.interessat.tipus.enum.${interessat.tipus}"/>
										</td>
										<td>${interessat.documentTipus}: ${interessat.documentNum}</td>
										<c:choose>
											<c:when test="${interessat.tipus == 'PERSONA_FIS'}">
												<td>${interessat.nom} ${interessat.llinatge1} ${interessat.llinatge2}</td>
											</c:when>
											<c:otherwise>
												<td>${interessat.raoSocial}</td>
											</c:otherwise>
										</c:choose>
										<td>
											<c:if test="${interessat.tipus != 'ADMINISTRACIO'}">
												<button type="button" class="btn btn-default desplegable" href="#detalls_${status.index}" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_${status.index}">
													<span class="fa fa-caret-down"></span>
												</button>
											</c:if>
										</td>
									</tr>
									<tr class="collapse detall" id="detalls_${status.index}">
										<td colspan="4">
											<div class="row">
												<div class="col-xs-6">
													<dl class="dl-horizontal">
														<dt><spring:message code="interessat.form.camp.pais"/></dt><dd>${interessat.pais} <c:if test="${not empty interessat.paisCodi}">(${interessat.paisCodi})</c:if></dd>
														<dt><spring:message code="interessat.form.camp.provincia"/></dt><dd>${interessat.provincia} <c:if test="${not empty interessat.provinciaCodi}">(${interessat.provinciaCodi})</c:if></dd>											
														<dt><spring:message code="interessat.form.camp.municipi"/></dt><dd>${interessat.municipi} <c:if test="${not empty interessat.municipiCodi}">(${interessat.municipiCodi})</c:if></dd>
														<dt><spring:message code="interessat.form.camp.adresa"/></dt><dd>${interessat.adresa}</dd>
														<dt><spring:message code="interessat.form.camp.codiPostal"/></dt><dd>${interessat.codiPostal}</dd>
														<dt><spring:message code="interessat.form.camp.codiDire"/></dt><dd>${interessat.codiDire}</dd>
													</dl>
												</div>
												<div class="col-xs-6">
													<dl class="dl-horizontal">
														<dt><spring:message code="interessat.form.camp.email"/></dt><dd>${interessat.email}</dd>
														<dt><spring:message code="interessat.form.camp.telefon"/></dt><dd>${interessat.telefon}</dd>
														<dt><spring:message code="registre.interessat.detalls.camp.emailHabilitat"/></dt><dd>${interessat.emailHabilitat}</dd>
														<dt><spring:message code="registre.interessat.detalls.camp.canalPreferent"/></dt><dd><c:if test="${not empty interessat.canalPreferent}"><spring:message code="registre.interessat.detalls.camp.canalPreferent.${interessat.canalPreferent}"/></c:if></dd>
														<dt><spring:message code="interessat.form.camp.observacions"/></dt><dd>${interessat.observacions}</dd>
													</dl>
												</div>
												<c:if test="${not empty interessat.representant}">
													<c:set var="representant" value="${interessat.representant}"/>
													<div class="col-xs-12">
														<table class="table table-bordered">
															<thead>
																<tr><th colspan="4"><spring:message code="registre.interessat.detalls.camp.representant"/></th></tr>
																<tr>
																	<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.tipus"/></th>
																	<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.document"/></th>
																	<th><spring:message code="registre.detalls.camp.interessat.nom"/></th>
																	<th style="width: 50px;"></th>
																</tr>
															</thead>
															<tbody>
																<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if>>
																	<td>
																		<spring:message code="registre.interessat.tipus.enum.${representant.tipus}"/>
																	</td>
																	<td>${representant.documentTipus}: ${representant.documentNum}</td>
																	<c:choose>
																		<c:when test="${representant.tipus == 'PERSONA_FIS'}">
																			<td>${representant.nom} ${representant.llinatge1} ${representant.llinatge2}</td>
																		</c:when>
																		<c:otherwise>
																			<td>${representant.raoSocial}</td>
																		</c:otherwise>
																	</c:choose>
																	<td>
																		<c:if test="${representant.tipus != 'ADMINISTRACIO'}">
																			<button type="button" class="btn btn-default desplegable" href="#detalls_${status.index}_rep" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_${status.index}_rep">
																				<span class="fa fa-caret-down"></span>
																			</button>
																		</c:if>
																	</td>
																</tr>
																<tr class="collapse detall" id="detalls_${status.index}_rep">
																	<td colspan="4">
																		<div class="row">
																			<div class="col-xs-6">
																				<dl class="dl-horizontal">
																					<dt><spring:message code="interessat.form.camp.pais"/></dt><dd>${representant.pais} <c:if test="${not empty representant.paisCodi}">(${representant.paisCodi})</c:if></dd>
																					<dt><spring:message code="interessat.form.camp.provincia"/></dt><dd>${representant.provincia} <c:if test="${not empty representant.provinciaCodi}">(${representant.provinciaCodi})</c:if></dd>											
																					<dt><spring:message code="interessat.form.camp.municipi"/></dt><dd>${representant.municipi} <c:if test="${not empty representant.municipiCodi}">(${representant.municipiCodi})</c:if></dd>
																					<dt><spring:message code="interessat.form.camp.adresa"/></dt><dd>${representant.adresa}</dd>
																					<dt><spring:message code="interessat.form.camp.codiPostal"/></dt><dd>${representant.codiPostal}</dd>
																					<dt><spring:message code="interessat.form.camp.codiDire"/></dt><dd>${representant.codiDire}</dd>
																				</dl>
																			</div>
																			<div class="col-xs-6">
																				<dl class="dl-horizontal">
																					<dt><spring:message code="interessat.form.camp.email"/></dt><dd>${representant.email}</dd>
																					<dt><spring:message code="interessat.form.camp.telefon"/></dt><dd>${representant.telefon}</dd>
																					<dt><spring:message code="registre.interessat.detalls.camp.emailHabilitat"/></dt><dd>${representant.emailHabilitat}</dd>
																					<dt><spring:message code="registre.interessat.detalls.camp.canalPreferent"/></dt><dd><c:if test="${not empty representant.canalPreferent}"><spring:message code="registre.interessat.detalls.camp.canalPreferent.${representant.canalPreferent}"/></c:if></dd>
																					<dt><spring:message code="interessat.form.camp.observacions"/></dt><dd>${representant.observacions}</dd>
																				</dl>
																			</div>
																		</div>
																	</td>						
																</tr>
															</tbody>
														</table>
													</div>
												</c:if>
												<!-- ------------------------ -->
											</div>
										</td>						
									</tr>
								</c:if>
							</c:forEach>
						</tbody>
					</table>
				</c:when>
				<c:otherwise>
					<div class="row col-xs-12">
						<div class="well">
							<spring:message code="registre.interessat.buit"/>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
		
		<!------------------------------------------- TABPANEL ANNEXOS --------------------------------------------->
		<div class="tab-pane" id="annexos" role="tabpanel">
			<c:choose>
				<c:when test="${not empty registre.annexos}">
					<c:forEach var="annex" items="${registre.annexos}" varStatus="status">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h3 class="panel-title">
									<span class="fa fa-file"></span>
									${annex.titol}
									<c:if test="${annex.fitxerArxiuUuid == null }">
										<span class="fa fa-warning text-warning" title="<spring:message code="registre.annex.detalls.camp.arxiu.uuid.buit.avis"/>"></span>
									</c:if>
									<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-annex-${status.index}"><span class="fa fa-chevron-down"></span></button>
								</h3>
							</div>
 							<div id="collapse-annex-${status.index}" class="panel-collapse collapse collapse-annex" role="tabpanel" aria-labelledby="dadesAnnex${status.index}" data-registre-id="${registre.id}" data-annex-id="${annex.id}"> 

 							</div> 
						</div>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${not empty annexosErrorMsg}">
							<div class="row col-xs-12">
								<div class="alert alert-danger">
									${annexosErrorMsg}
								</div>
							</div>						
						</c:when>
						<c:otherwise>
							<div class="row col-xs-12">
								<div class="well">
									<spring:message code="registre.annex.buit"/>
								</div>
							</div>
						</c:otherwise>
					</c:choose>				
				</c:otherwise>
			</c:choose>
		</div>
		
		
		<c:if test="${not empty registre.expedientArxiuUuid}">
			<!------------------------------------------- TABPANEL ARXIU INFO --------------------------------------------->
			<div class="tab-pane" id="arxiuInfo" role="tabpanel" data-loaded=false>
				<div style='text-align: center; margin-bottom: 60px; margin-top: 60px;''><span class='fa fa-circle-o-notch fa-spin fa-3x'/></div>
			</div>
		</c:if>
	
		<c:if test="${registre.procesEstat == 'ARXIU_PENDENT' || registre.procesEstat == 'REGLA_PENDENT' || registre.procesEstat == 'BACK_PENDENT' || (registre.procesEstat == 'BUSTIA_PROCESSADA' && registre.procesError!= null)}">
	
			<!------------------------------ TABPANEL PROCESSAMENT_AUTOMATIC ------------------------------------->
			<div class="tab-pane" id="processamentAutomatic" role="tabpanel">
			
			
				<!------ REINTENTAR PROCESSAMENT ------>
				<c:if test="${registre.procesError != null }">
					<div class="alert well-sm alert-danger alert-dismissable">
						<span class="fa fa-exclamation-triangle"></span>
						<spring:message code="registre.detalls.info.errors"/> 
						<c:if test="${isRolActualAdministrador}">
							<c:if test="${registre.procesEstat == 'ARXIU_PENDENT' || registre.procesEstat == 'REGLA_PENDENT' || (registre.procesEstat == 'BUSTIA_PROCESSADA' && registre.procesError!= null)}">
								<a href="../../registreAdmin/registre/${registre.id}/reintentar" class="btn btn-xs btn-default pull-right processarBtn"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentar"/></a>
							</c:if>
							<c:if test="${registre.procesEstat == 'BACK_PENDENT'}">						
								<a href="../../registreAdmin/registre/${registre.id}/reintentarEnviamentBackoffice" class="btn btn-xs btn-default pull-right processarBtn"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentarEnviamentBackoffice"/></a>
							</c:if>
						</c:if>
					</div>
				</c:if>
				<c:if test="${isRolActualAdministrador && (registre.procesEstat == 'BACK_PENDENT' && registre.procesError == null && registre.procesIntents > 0)}">
					<a href="../registre/${registre.id}/reintentarEnviamentBackoffice" class="btn btn-xs btn-default pull-right processarBtn" style="margin-right: 10px;"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentarEnviamentBackoffice"/></a>
			    </c:if>

				<!------ PROCESSAMENT INFO ------>
				<div class="processamentInfo">
				
					<dl class="dl-horizontal">
					
						<dt><spring:message code="registre.detalls.camp.proces.estat"/></dt>
						<dd>${registre.procesEstat}</dd>
						
						<c:if test="${not empty registre.procesData}">
							<dt><spring:message code="registre.detalls.camp.proces.data"/></dt>
							<dd><fmt:formatDate value="${registre.procesData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>				
						</c:if>	
						
						<c:if test="${registre.procesEstat == 'BACK_PENDENT' && not empty registre.backRetryEnviarData}">
							<dt><spring:message code="registre.detalls.camp.proces.data.back.proxima.intent"/></dt>
							<dd><fmt:formatDate value="${registre.backRetryEnviarData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>				
						</c:if>						
						
						<dt><spring:message code="registre.detalls.camp.proces.intents"/></dt>
						<dd>${registre.procesIntents}</dd>
						
					</dl>
					
					<c:if test="${not empty registre.procesError}">
						<pre style="height:300px"><c:out value="${registre.procesError}" escapeXml="true"/></pre>
					</c:if>
				</div>
			</div>
		</c:if>
		
		
		<c:if test="${registre.procesEstat == 'BACK_REBUDA' || registre.procesEstat == 'BACK_PROCESSADA' || registre.procesEstat == 'BACK_REBUTJADA' || registre.procesEstat == 'BACK_ERROR'}">
			
			
			<!------------------------------ TABPANEL PROCESSAMENT_BACKOFFICE ------------------------------------->
			<div class="tab-pane" id="processamentBackoffice" role="tabpanel">
				
			    <c:if test="${isRolActualAdministrador == true && (registre.procesEstat == 'BACK_REBUTJADA' || registre.procesEstat == 'BACK_ERROR')}">
					<a href="<c:url value="/registreUser/registre/${registre.id}/reintentarEnviamentBackoffice"/>" class="btn btn-xs btn-default pull-right processarBtn" style="margin-right: 10px;"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentarEnviamentBackoffice"/></a>
			    </c:if>	
				<div class="processamentInfo">
					<dl class="dl-horizontal">
					
						<dt><spring:message code="registre.detalls.camp.proces.estat"/></dt>
						<dd>${registre.procesEstat}</dd>
						
						<dt><spring:message code="registre.detalls.camp.proces.data.back.pendent"/></dt>
						<dd><fmt:formatDate value="${registre.backPendentData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
						
						<dt><spring:message code="registre.detalls.camp.proces.data.back.rebuda"/></dt>
						<dd><fmt:formatDate value="${registre.backRebudaData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
						
						<c:choose>
						   <c:when test = "${registre.procesEstat == 'BACK_PROCESSADA'}">
						      <dt><spring:message code="registre.detalls.camp.proces.data.back.processada"/></dt>
						      <dd><fmt:formatDate value="${registre.backProcesRebutjErrorData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
						   </c:when>
						   
						   <c:when test = "${registre.procesEstat == 'BACK_REBUTJADA'}">
						      <dt><spring:message code="registre.detalls.camp.proces.data.back.rebutjada"/></dt>
						      <dd><fmt:formatDate value="${registre.backProcesRebutjErrorData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
						   </c:when>
						   
						   <c:when test = "${registre.procesEstat == 'BACK_ERROR'}">
						      <dt><spring:message code="registre.detalls.camp.proces.data.back.error"/></dt>
						      <dd><fmt:formatDate value="${registre.backProcesRebutjErrorData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
						   </c:when>         
						</c:choose>	
									
					</dl>
					
					<c:choose>
					   <c:when test = "${not empty registre.procesError}">
							<pre style="height:300px"><c:out value="${registre.procesError}" escapeXml="true"/></pre>
					   </c:when>
					   <c:when test = "${registre.backObservacions != null}">
							<pre style="height:300px">${registre.backObservacions}</pre>
					   </c:when>
					</c:choose>			
				</div>
			</div>
		</c:if>	
		
		<div class="col-md-12 datatable-dades-carregant" style="display: none; text-align: center; margin-top: 50px;">
			<span class="fa fa-circle-o-notch fa-spin fa-3x"></span>
		</div>
		
		
		
	</div>
	
	<div id="modal-botons" class="well">
		<c:if test="${registreNumero != null && registreTotal != null}">
			<button name="btnAnterior" class="btn btn-default pull-left" 
							data-registre-numero="${registreNumero - 1}"
							${registreNumero <= 1 ? "disabled='disabled'" : "" }>
						&lt;&lt; <spring:message code="comuns.boto.previous"/></button>
			<button name="btnNavegacio" class="btn btn-default pull-left" disabled="disabled"> ${registreNumero} / ${registreTotal}</button>
			<button name="btnSeguent" class="btn btn-default pull-left" 
							data-registre-numero="${registreNumero + 1}"
							${registreNumero >= registreTotal ? "disabled='disabled'" : "" }>
						<spring:message code="comuns.boto.next"/> &gt;&gt;</button>
		</c:if>
		<a href="<c:url value="/registreUser"/>" class="btn btn-default modal-tancar" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>