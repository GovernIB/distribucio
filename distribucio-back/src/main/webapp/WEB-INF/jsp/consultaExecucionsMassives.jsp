<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%
pageContext.setAttribute(
		"isRolActualAdministrador",
		es.caib.distribucio.back.helper.RolHelper.isRolActualAdministrador(request));
%>
<html>
<head>
	<title>${titolConsulta}</title>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<dis:modalHead/>
<style>
	.linea-exm {
		margin-bottom:8px;
	}
	.in-line-badge{
		margin-left: 0px;
		margin-right: 5px;
	}
	.progress {
		margin-bottom: 10px;
	}
	.one-line {
		display: inline-flex;
	}
	.massiu-dades {
		width: 100%;
	}
	.icona-carrega {
		text-align: center;
	}
	span.fa-cog {
		margin: 2px 0.5rem 0 0;
	}
	.linea-exm div span.caret {
		margin: 8px 0 0 2px; 
	}
</style>
<script type="text/javascript">

	let refreshInterval;

	$(document).ready(function() {
		$('.collapsable').on('show.bs.collapse', function () {
			loadContinguts($(this).data("exmid"));
		});
		$('.collapsable').on('hidden.bs.collapse', function () {
			unloadContinguts($(this).data("exmid"));
		});
		$("button[name=refrescar]").click(function() {
			window.location.href = '<c:url value="/modal/massiva/consulta/${pagina}"/>';
		});
		$("button[name=nextPage]").click(function() {
			window.location.href = '<c:url value="/modal/massiva/consulta/${pagina + sumador}"/>';
		});
		$("button[name=previousPage]").click(function() {
			window.location.href = '<c:url value="/modal/massiva/consulta/${pagina - 1}"/>';
		});
		$("input[name=refrescarDeu]").click(function(event) {
			if ($(this).prop('checked')) {
				refreshInterval = setInterval(reloadPage, 10000);
			} else {
				clearInterval(refreshInterval);
			}
		});

		<c:if test="${fn:length(idsDesplegats)>0}">
			<c:forEach var="idMass" items="${idsDesplegats}">
				loadContinguts(${idMass});
			</c:forEach>
		</c:if>

		<c:if test="${isRefrescant}">
			refreshInterval = setInterval(reloadPage, 10000);
		</c:if>
	});

	function reloadPage() {
		let isChecked = $("input[name=refrescarDeu]").prop('checked');
		if (isChecked) {
			window.location.href = '<c:url value="/modal/massiva/consulta/${pagina}"/>?isRefrescant=true';
		} else {
			window.location.href = '<c:url value="/modal/massiva/consulta/${pagina}"/>';
		}
	}
	
	function loadContinguts(exm_id) {
		$.ajax({
			type: 'GET',
			url: '<c:url value="/massiva/consultaContingut/' + exm_id + '"/>',
			success: function(data) {
				mostrarContinguts(data, exm_id);
				mostraDetall(data);
				$("#collapse_"+exm_id).addClass("in");
				$("#collapse_"+exm_id).attr("aria-expanded", "true");
			},
			error: function() {
				console.log("error consultant continguts");
			}
		});
	}

	function unloadContinguts(exm_id) {
		$.ajax({
			type: 'GET',
			url: '<c:url value="/massiva/unloadContingut/' + exm_id + '"/>',
			success: function(data) {
				$("#collapse_"+exm_id).removeClass("in");
				$("#collapse_"+exm_id).attr("aria-expanded", "false");
			},
			error: function() {
				console.log("error consultant continguts");
			}
		});
	}
	
	function mostrarContinguts(continguts, exm_id) {

		let elementTipus = continguts[0].elementTipus;
		let elementTipusTranslated;
		
		console.log(elementTipus)
		
		if (elementTipus == 'REGISTRE') {
			elementTipusTranslated = "<spring:message code='accio.massiva.element.tipus.enum.REGISTRE'/>"
		} else if (elementTipus == 'ANNEX') {
			elementTipusTranslated = "<spring:message code='accio.massiva.element.tipus.enum.ANNEX'/>"
		} else if (elementTipus == 'REGLA') {
			elementTipusTranslated = "<spring:message code='accio.massiva.element.tipus.enum.REGLA'/>"
		}
		
		$('#continguts_' + exm_id).empty();
		var html_cont =
			'<table class="table table-striped table-bordered" id="taula_cont_' + exm_id + '">' + 
			'<thead>' +
			'  <tr>' +
			'    <th class="massiu-contingut col-md-4">' + elementTipusTranslated + '</th>' +
			'    <th class="massiu-estat col-md-2"><spring:message code='accio.massiva.contingut.header.estat'/></th>' +
			'    <th class="massiu-contingut col-md-2"><spring:message code='accio.massiva.contingut.header.dataCreacio'/></th>' +
			'    <th class="massiu-contingut col-md-2"><spring:message code='accio.massiva.contingut.header.dataInici'/></th>' +
			'    <th class="massiu-contingut col-md-2"><spring:message code='accio.massiva.contingut.header.dataFi'/></th>' +
			'  </tr>' +
			'</thead>' +
			'<tbody>';

		for (var i in continguts) {
			var contingut = continguts[i];
			html_cont += '<tr class="' + (contingut.estat == "ERROR" ? ' danger' : '') + '">';
			html_cont += '<td>' + contingut.elementNom + '</td>';
			var estat = "";
			if (contingut.estat == "CANCELADA"){
				estat = "<span class='fa fa-check-circle'></span><label style='padding-left: 10px'><spring:message code='accio.massiva.estat.CANCELADA'/></label>";
			} else if (contingut.estat == "ERROR"){
				if (contingut.error) {
					var escaped = contingut.error.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/'/g, '&apos;').replace(/"/g, '&quot;');
					estat = "<span class='fa fa-exclamation-circle'></span><label class='msg-error' data-msg-error='" + escaped + "' style='cursor: pointer;padding-left: 10px'><spring:message code='accio.massiva.estat.ERROR'/></label>";
				} else {
					estat = "<span class='fa fa-exclamation-circle'></span><label class='msg-error' style='padding-left: 10px'><spring:message code='accio.massiva.estat.ERROR'/></label>";
				}
			} else if (contingut.estat == "FINALITZADA"){
				estat = "<span class='fa fa-check-circle'></span><label style='padding-left: 10px'><spring:message code='accio.massiva.estat.FINALITZADA'/></label>";

                if (contingut.missatge) {
                    if (contingut.execucioMassiva.tipus === "BACKOFFICE") {
                        estat += "<br/><span style='padding-right: 10px' class='fa fa-info-circle text-info' title='" + contingut.missatge + "'/>" + contingut.missatge;
                    } else {
                        estat += "<span style='padding-left: 10px' class='fa fa-info-circle text-info' title='" + contingut.missatge + "'/>";
                    }
                }
            } else if (contingut.estat == "PROCESSANT"){
				estat = "<span class='fa fa-circle-o-notch fa-spin'></span><label style='padding-left: 10px'><spring:message code='accio.massiva.estat.PROCESSANT'/>";
			} else if (contingut.estat == "PENDENT"){
				estat = "<span class='fa fa-circle-o-notch fa-spin'></span><label style='padding-left: 10px'><spring:message code='accio.massiva.estat.PENDENT'/>";
			} else if (contingut.estat == "PAUSADA"){
				estat = "<span class='fa fa-stop-circle-o'></span><label style='padding-left: 10px'><spring:message code='accio.massiva.estat.PAUSADA'/>";
			}
			
			html_cont += '<td>' + estat + '</td>';
			html_cont += '<td>' + (contingut.dataCreacioAmbFormat != undefined ? contingut.dataCreacioAmbFormat : '') + '</td>';
			html_cont += '<td>' + (contingut.dataIniciAmbFormat != undefined ? contingut.dataIniciAmbFormat : '') + '</td>';
			html_cont += '<td>' + (contingut.dataFiAmbFormat != undefined ? contingut.dataFiAmbFormat : '') + '</td>';
			html_cont += '</tr>';
		}
		html_cont += '</tbody></table>';
		$('#continguts_' + exm_id).html(html_cont);
	}
	var changeTooltipPosition = function(event) {
	 	$('div.tooltip').css({left: 20});
	};
	var showTooltip = function(event) {
		$('div.tooltip').remove();
		$("<div class='tooltip'>" + $(this).data("msg-error") + "</div>").css({
	 		position: "absolute",
			display: "none",
			right: 20,
			top: event.pageY,
			top:event.pageY+4,
			"background-color": "#FFFFCA",
			color: "#000023",
			opacity: 0.90,
			"background-clip": "padding-box",
			border: "1px solid rgba(0, 0, 0, 0.15)",
			"border-radius": "4px",
			"box-shadow": "0 6px 12px rgba(0, 0, 0, 0.176)",
			"font-size": "14px",
			"list-style": "outside none none",
			margin: "0",
			"min-width": "160px",
			padding: "10px",
			"text-align": "left",
		    "word-wrap": "break-word",
		    "z-index": "1000"
		}).appendTo("body").fadeIn(200);
		changeTooltipPosition(event);
		$('div.tooltip').bind({
			mouseleave: hideTooltip
		});							
	};
	var hideTooltip = function(event) {
		var el = document.elementFromPoint(event.pageX, event.pageY);
   		$('div.tooltip').remove();
	};
	function mostraDetall(continguts) {
		$(".msg-error").unbind();
		$(".msg-error").bind({
			   mousemove : changeTooltipPosition,
			   mouseenter : showTooltip
		});
	}
</script>
</head>
<body>

<div style="padding: 0px 0px 15px 2px;">
	<input type='checkbox' name='refrescarDeu' <c:if test="${isRefrescant}">checked='checked'</c:if> >&nbsp;<b>Refrescar cada 10s.</b>
</div>

<div class="panel panel-default">
    <div class="panel-heading" role="tab">
    	<div class="row">
			<div class="col-md-2"><strong><spring:message code="accio.massiva.header.nom"/></strong></div>
			<div class="col-md-2"><strong><spring:message code="accio.massiva.header.execucio"/></strong></div>
			<div class="col-md-1"><strong><spring:message code="accio.massiva.header.error"/></strong></div>
			<div class="col-md-2"><strong><spring:message code="accio.massiva.header.dataInici"/></strong></div>
			<div class="col-md-2"><strong><spring:message code="accio.massiva.header.dataFi"/></strong></div>
			<div class="col-md-2"><strong><spring:message code="accio.massiva.header.usuari"/></strong></div>
			<div class="col-md-1"><strong><spring:message code="accio.massiva.header.accions"/></strong></div>
		</div>
	</div>
</div>

 <c:forEach var="exm" items="${execucionsMassives}">
 
 <c:choose>
 	<c:when test="${exm.errors > 0}">
 		<c:set var="modebg" value="danger"/>
 	</c:when>
 	<c:when test="${exm.estat == 'CANCELADA'}">
 		<c:set var="modebg" value="warning"/>
 	</c:when> 	
 	<c:otherwise>
 		<c:set var="modebg" value="default"/>
 	</c:otherwise>
 </c:choose>
 	
  <div class="panel panel-${modebg} linea-exm">
    <div class="panel-heading collapsable" role="tab" id="heading_${exm.id}">
        <div data-toggle="collapse" data-target="#collapse_${exm.id}" style="cursor: pointer;">
        	<div class="row">
	        	<div class="col-xs-2">
		          	<spring:message code="accio.massiva.tipus.${exm.tipus}"/>
	          	</div>
	          	<div class="col-xs-2 one-line" id="barra_${exm.id}">
		          	<div><span class="mass-badge badge in-line-badge">${fn:length(exm.contingutIds)}</span></div> 
		          	<div class="massiu-dades" id="pbar_${exm.id}">
		          		<div class="progress">
	    					<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="${exm.executades}" aria-valuemin="0" aria-valuemax="100" style="width: ${exm.executades}%">
	    	  					<span><div class="value">${exm.executades}%</div></span>
	    	  				</div>
		          		</div>
		          	</div>
	          	</div>
	          	<div class="col-xs-1" id="errors_${exm.id}">
		          	${exm.errors}
	          	</div>
	          	<div class="col-xs-2">
		          	<fmt:formatDate value="${exm.dataInici}" pattern="dd/MM/yyyy HH:mm:ss"/>
	          	</div>
	          	<div class="col-xs-2" id="dataFi_${exm.id}">
		          	<fmt:formatDate value="${exm.dataFi}" pattern="dd/MM/yyyy HH:mm:ss"/>
	          	</div>
	          	<div class="col-xs-2">
		          	${exm.usuari.nom}
	          	</div>
	          	<c:if test="${isRolActualAdministrador && (exm.estat == 'PENDENT' || exm.estat == 'PROCESSANT' || exm.estat == 'PAUSADA')}">
		          	<div class="col-xs-1">
			         	<div id="div-btn-accions" class="dropdown">
								<button id="btn-accions" class="btn btn-primary" data-toggle="dropdown" style="display:flex;">
									<span class="fa fa-cog"></span>
									<span class="hidden_dis"><spring:message code="comu.boto.accions"/></span>
									<span class="caret"></span></button>
								<ul class="dropdown-menu">
								<li>
									<a id="detall-button" href='<c:url value="../cancelar/${exm.id}/${pagina}"/>'>
										<span class="fa fa-times"></span>&nbsp;<spring:message code="accio.massiva.header.accion.cancelar"/>
									</a>
								</li>
								<c:choose>
									<c:when test="${exm.estat == 'PAUSADA' && exm.emcPausat}">
										<li>
											<a id="detall-button" href='<c:url value="../reprendre/${exm.id}/${pagina}"/>'>
												<span class="fa fa-play"></span>&nbsp;<spring:message code="accio.massiva.header.accion.reprendre"/>
											</a>
										</li>
									</c:when>
									<c:otherwise>
										<li>
											<a id="detall-button" href='<c:url value="../pausar/${exm.id}/${pagina}"/>'>
												<span class="fa fa-stop-circle-o"></span>&nbsp;<spring:message code="accio.massiva.header.accion.pausar"/>
											</a>
										</li>
									</c:otherwise>
								</c:choose>
							</ul>
			          	</div>
			        </div>
	          	</c:if>
          	</div>
        </div>
    </div>
    <div id="collapse_${exm.id}" data-exmid="${exm.id}" class="panel-collapse collapse collapsable" role="tabpanelCANCELADAlledby="heading_${exm.id}">
      <div class="panel-body" id="continguts_${exm.id}">
      	<div class="icona-carrega">
      		<i class="fa fa-circle-o-notch fa-3 fa-spin" aria-hidden="true"></i>
      	</div>
      </div>
    </div>
  </div>
 </c:forEach>
<div id="modal-botons" class="well">
	<a href="<c:url value="/massiu/portafirmes"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	<button type="button" class="btn btn-primary" name="previousPage" value="previousPage"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.previous"/></button>
	<button type="button" class="btn btn-primary" name="nextPage" value="nextPage"><spring:message code="comu.boto.next"/>&nbsp;<span class="fa fa-arrow-right"></span></button>
	<button type="button" class="btn btn-warning" name="refrescar" value="refrescar"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.refrescar"/></button>
</div>
</body>
</html>

