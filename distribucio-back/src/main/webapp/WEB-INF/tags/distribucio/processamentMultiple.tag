<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ attribute name="registres" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="start" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ attribute name="form" required="true" rtexprvalue="true"%>
<%@ attribute name="btnSubmit" required="true" rtexprvalue="true"%>
<%@ attribute name="btnTancar" required="false" rtexprvalue="true"%>
<%@ attribute name="postUrl" required="true" rtexprvalue="true"%>
<%@ attribute name="deselectUrl" required="false" rtexprvalue="true"%>

<c:set var="nRegistres">${fn:length(registres)}</c:set>

<div id="contingut-missatges"></div>

<div id="registres-list" class="panel panel-default" role="tablist">
	<div class="panel-heading">
		<div class="row" role="button" data-toggle="collapse" data-parent="#registres-list" data-target="#registres-info" aria-expanded="true" aria-controls="registres-info">
			<div class="col-sm-3">
				<h3 class="panel-title">
					<span class="state-icon fa"></span>
					<span class="badge seleccioCount">${fn:length(registres)}</span> <spring:message code="registresSeleccionats.anotacions.seleccionades"></spring:message>
				</h3>
			</div>
			<div class="col-sm-7">
				<!-- Barra de progrés -->
				<div id="registres-progress" class="progress" style="height: 25px; margin-bottom: 0; display:none;">
					<div id="registres-progress-success" class="progress-bar progress-bar-success progress-bar-striped" style="width: 0%">
						<strong><span class="valor text-success"></span></strong>
					</div>
					<div id="registres-progress-error" class="progress-bar progress-bar-danger progress-bar-striped" style="width: 0%">
						<strong><span class="valor text-danger"></span></strong>
					</div>
				</div>
			</div>
			<div class="col-sm-2">
				<div class="row">
					<div class="col-sm-8">
						<button type="button" class="btn btn-warning pull-right" id="cancelarBtn" style="visibility:hidden;" title="<spring:message code="registre.user.controller.massiva.cancelar.title"/>">
							<span class="fa fa-stop"></span>&nbsp;
							<span id="cancelarBtnText"><spring:message code="comu.boto.cancelar"/></span>
						</button>
					</div>
					<div class="col-sm-4">
						<span class="fa fa-caret-down pull-right"></span>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="registres-info" class="panel-collapse collapse registre-info">

		<table class="table table-striped table-bordered dataTable">
			<thead>
				<tr>
					<th><spring:message code="registresSeleccionats.anotacio"></spring:message></th>
					<th><spring:message code="registresSeleccionats.estat"></spring:message></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="registre" items="${registres}">
					<tr id="tr_registre_${registre.id}" data-id="${registre.id}" class='registreTr'>
						<td>[<a href="<c:url value="/registreUser/registre/${registre.id}"/>" target="blank">${registre.numero}</a>] ${registre.extracte}</td>
						<td>
							<span class="estat esperant fa fa-clock-o" title="<spring:message code='registresSeleccionats.estat.esperant'></spring:message>"></span>
							<span class="estat processant fa fa-refresh fa-spin" title="<spring:message code='registresSeleccionats.estat.processant'></spring:message>" style="display:none"></span>
							<span class="estat success fa fa-check text-success" title="<spring:message code='registresSeleccionats.estat.success'></spring:message>" style="display:none"></span>
							<span class="estat error fa fa-exclamation-triangle text-danger" title="<spring:message code='registresSeleccionats.estat.error'></spring:message>" style="display:none"></span>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>    
	</div>
</div>


<script type="text/javascript">
// <![CDATA[

	var cancelar = false;
	var start = ${start == true};
	var nRegistres = ${nRegistres};

	$(document).ready( function() {	
		// Botó per cancel·lar les reindexacions d'expedients amb error
		$('#cancelarBtn').click(function(e) {
			$(this).attr('disabled', 'disabled');
			$('#cancelarBtnText').html("<spring:message code='comu.boto.cancelant'/>");
			cancelar = true;
			e.preventDefault();
			return false;
		})
				
		if (nRegistres == 0) {
			webutilMissatgeWarning('<spring:message code="registre.user.controller.massiva.cap"></spring:message>', '#contingut-missatges');
			$("${btnSubmit}").attr('disabled', true);
		} else if (start) {
			processaAnotacions();
		}
	});

	
	function processaAnotacions() {
		(async() => {
			processaAnotacionsAsync().then((data) => {
				// Deseleccionar elements en completar l'acció
				if ("${deselectUrl}" != "") {
					$.ajax({
						type: 'GET',
						url: '<c:url value="${deselectUrl}"></c:url>',
						async: false,
						success: function(data) {
							sessionStorage.setItem('selectedElements', data);
						} 
					});
				}
			});
		})();	
	}
	
	async function processaAnotacionsAsync() {
		
		$('${form}').webutilNetejarErrorsCamps();
		$spin = $('<span>&nbsp;<span class="fa fa-spinner fa-spin"></span></span>');
		$btnSubmit = $("${btnSubmit}", window.parent.document);
		$btnTancar = $("${btnTancar}", window.parent.document);
		$btnSubmit.attr('disabled', true).append($spin);
		
		// Obté la llista d'identificadors com un array
		var registresPendentsIds = [];
		$('.registreTr').not('.processat').each(function(){
			$('.estat', $(this)).hide();
			$('.esperant', $(this)).show();
			registresPendentsIds.push($(this).data('id'));
		});
	
		var pendents = registresPendentsIds.length;
		var total = $('.registreTr').length;
		var errors = 0;
		var correctes = $('.registreTr.processat').length;
	
		
		$('#registres-progress-success').css('width', 100 * correctes / total + '%');
		$('#registres-progress-error').css('width', 0 + '%');
		$('#registres-progress').show();
		$('.progress-bar').addClass('active');
		
		cancelar = false;
		$('#cancelarBtn').removeAttr("disabled").css('visibility', 'visible');
		$('#cancelarBtnText').html("<spring:message code='comu.boto.cancelar'/>");
	
		for (var i = 0; i < registresPendentsIds.length && !cancelar; ++i) {
	
			var resultat = await processarAnotacioAsync(registresPendentsIds[i]);
			if (resultat) {
				correctes++;
				$('#registres-progress-success').find('.valor').html(correctes);
			} else {
				errors++;
				$('#registres-progress-error').find('.valor').html(errors);
			}
			$('#registres-progress-success').css('width', 100 * correctes / total + '%');
			$('#registres-progress-error').css('width', 100 * errors / total + '%');
		}
		$('.progress-bar').removeClass('active');
		$btnSubmit.removeAttr('disabled');
		$('#cancelarBtn').css('visibility', 'hidden');
	
		$spin.remove();
		
// 		$('div.modal-backdrop',window.parent.document).remove();
// 		$('div.modal',window.parent.document).remove();		
		
		if (correctes == registresPendentsIds.length) {
			window.location = webutilModalTancarPath();
		}
	}
	
	function processarAnotacioAsync(registreId) {
		return new Promise(function (resolve, reject) {
			var $tr = $('#tr_registre_' + registreId);
			var ret = false;
			$('.estat', $tr).hide();
			$('.processant', $tr).show();
			// crida a la reindexació
			$.ajax({
				type: "POST",
				url: '<c:url value="${postUrl}"></c:url>' + registreId,
				data: $('${form}').serialize(),
				success : function(ajaxResponse) {
					var missatge = $('<div/>').html(ajaxResponse.missatge).text();
					if (ajaxResponse.estatOk) {
						ret = true;
						$('.success', $tr).show();
						$('.success', $tr).attr('title', missatge);
						$tr.addClass('processat');
					} else {
						if (ajaxResponse.errorsCamps) {
							$('${form}').webutilMostrarErrorsCamps(ajaxResponse.errorsCamps);
						}
						$('.error', $tr).show();
						$('.error', $tr).attr('title', missatge);
					}
				},
				error: function (request, status, error) {
					$('.error', $tr).show();
				},
				complete: function() {
					$('.processant', $tr).hide();
					resolve(ret);
				}
			});
		});
	}
	
// ]]>
</script>
