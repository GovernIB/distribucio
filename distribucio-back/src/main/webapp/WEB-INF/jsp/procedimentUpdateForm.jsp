<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="procediment.actualitzacio.titol"/></title>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>	
	<dis:modalHead/>
	
	<script>

		var intervalProgres;
		var title="<spring:message code="procediment.actualitzacio.titol"/>";
		var content="<spring:message code="procediment.actualitzacio.cancelarActu"/>";
		var acceptar="<spring:message code="comu.boto.acceptar"/>";
		var cancelar="<spring:message code="comu.boto.cancelar"/>";

		var isUpdating = '${isUpdatingProcediments}' == 'true';
		
		$(document).ready(function(e) {
			 
			$('button[name=actualitzarBtn]').click( function(e){
				$('.loading').fadeIn();
				$('#divErrMsg').hide();
				$('.progress').fadeOut();
				$('#actualitzacioInfo').fadeIn();
				$('.confirmacio').fadeOut();
				$("button[name=actualitzarBtn]", window.parent.document).attr('disabled', true).find('.fa-refresh').addClass("fa-spin");			
				if (!isUpdating) {
					$.post($(this).attr('action'));
					isUpdating = true;
				}
				refreshProgres();
			});
			
			$('.close', parent.document).on('click', function(e) {	
				return tancarConfirmacio(e);
			});
			$('button[name=btnTancaActualitzacio]').click( function(e) {
				return tancarConfirmacio(e);
			});
						
			if (isUpdating) {
				//$('.modal-body .div-dades-carregant').show();
				$('.loading').fadeIn();
				$('#actualitzacioInfo').fadeIn();
				$('.confirmacio').fadeOut();
				$("button[name=actualitzarBtn]").attr('disabled', true).find('.fa-refresh').addClass("fa-spin");
				refreshProgres();
			}
		});
				
		/** Tanca comprovant si s'està actualitzant per mostrar una confirmació. */
		function tancarConfirmacio(e) {
			let tancar = false;
			if (isUpdating) {
				if (confirm(content)) {
					tancar = true;
				}
			} else {
				tancar = true;
			}
			if (tancar) {
				// Recarrega la finestra de procediments
            	window.top.location.reload();
			} else {
				e.preventDefault();
				e.stopPropagation();
				return false;
			}
		}

		function refreshProgres() {
			intervalProgres =  setInterval(function(){ getProgres(); }, 500);
		}

		function getProgres() {
			$.ajax({
				type: 'GET',
				url: "<c:url value='/procediment/actualitzar/progres'/>",
				success: function(data) {
					if (data) {
						$('#divErrMsg').hide();
						writeInfo(data);
						if (data.estat == 'FINALITZAT' || data.estat == 'ERROR') {
							clearInterval(intervalProgres);
							isUpdating = false;
							$('#bar').css('width', '100%');
							$('#bar').attr('aria-valuenow', 100);
							$('#bar').html('100%');
							$('.loading').hide();
			    			$("button[name=actualitzarBtn]", window.parent.document).attr('disabled', false).find('.fa-refresh').removeClass("fa-spin");
						} else {
							$('.progress').show();
							$('#bar').css('width', data.progres + '%');
							$('#bar').attr('aria-valuenow', data.progres);
							$('#bar').html(data.progres + '%');
						}
					}
				},
				error: function() {
					console.error("error obtenint progrés...");
					$('#divErrMsg').show();
					$('#errMsg').html("Error consultant progrés.");

					clearInterval(intervalProgres);
					$('.loading').hide();
				}
			});
		}

		/* Escriu la informació de progrés rebuda. */
		function writeInfo(data) {
			if (data) {
				let estat = '-';
				switch(data.estat) {
					case 'INICIALITZANT': estat = "<spring:message code='procediment.actualitzacio.progres.estat.INICIALITZANT'></spring:message>"; break;
					case 'ACTUALITZANT': estat = "<spring:message code='procediment.actualitzacio.progres.estat.ACTUALITZANT'></spring:message>"; break;
					case 'FINALITZAT': estat = "<spring:message code='procediment.actualitzacio.progres.estat.FINALITZAT'></spring:message>"; break;
					case 'ERROR': estat = "<spring:message code='procediment.actualitzacio.progres.estat.ERROR'></spring:message>"; break;
				}
				$('#estat').html(estat);
				$('#unitatsTotal').html(data.unitatsTotal);
				$('#unitatsProcessades').html(data.unitatsProcessades);
				if (data.errorMsg != null) {					
					$('#divErrMsg').show();
					$('#errMsg').html(data.errorMsg);
				} else {
					$('#divErrMsg').hide();
				}
			}
		}

	</script>
</head>
<body>
	<c:if test="${not isUpdatingProcediments}">
		<div class="confirmacio">
			<h4><spring:message code="procediment.actualitzacio.confirmacio"/></h4>
		</div>
	</c:if>

	<c:set var="formAction"><dis:modalUrl value="/procediment/actualitzar"/></c:set>
	<form:form action="${formAction}" id="formUpdateAuto" method="post" cssClass="form-horizontal" role="form">
		<div class="progress" style="display: none">
			<div id="bar" class="progress-bar" role="progressbar progress-bar-striped active" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">0%</div>
		</div>
		<div id="actualitzacioInfo" style="overflow: auto; max-height: 80vh;">
			<ul>
				<li><spring:message code="procediment.actualitzacio.progres.estat"></spring:message>: 
					<span id="estat">-</span></li>
				<li><spring:message code="procediment.actualitzacio.progres.unitatsTotals"></spring:message>: 
					<span id="unitatsTotal">-</span></li>
				<li><spring:message code="procediment.actualitzacio.progres.unitatsProcessades"></spring:message>: 
					<span id="unitatsProcessades">-</span></li>
			</ul>
			<div id="divErrMsg" class="alert alert-danger" style="display: none;">
				<span class="fa fa-warning text-danger"></span>
				<span id="errMsg">-</span>
			</div>
						
		</div>
		<div class="loading" style="display: none">
			<div style="display: flex; justify-content: center;">
				<span class="fa fa-circle-o-notch fa-2x fa-spin fa-fw"></span>
			</div>
		</div>
		<div id="modal-botons" class="well">
			<button type="button"
					name="actualitzarBtn"
					class="btn btn-success">
				<span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.actualitzar"/>
			</button>
   			<button name="btnTancaActualitzacio" 
   					type="button" class="btn btn-default" data-modal-cancel="false">
				 <spring:message code="comu.boto.tancar"/>
			</button>
			   
		</div>		
	</form:form>
</body>
</html>
