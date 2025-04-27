<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ attribute name="items" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="itemId" required="true" rtexprvalue="true"%>
<%@ attribute name="itemUrl" required="true" rtexprvalue="true"%>
<%@ attribute name="itemUrlParam1" rtexprvalue="true"%>
<%@ attribute name="itemUrlParam2" rtexprvalue="true"%>
<%@ attribute name="itemKey" required="true" rtexprvalue="true"%>
<%@ attribute name="itemText" required="true" rtexprvalue="true"%>
<%@ attribute name="missatgeCap" required="true" rtexprvalue="true"%>
<%@ attribute name="missatgeHeader" required="true" rtexprvalue="true"%>
<%@ attribute name="missatgeColumn" required="true" rtexprvalue="true"%>
<%@ attribute name="start" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ attribute name="form" required="true" rtexprvalue="true"%>
<%@ attribute name="btnSubmit" required="true" rtexprvalue="true"%>
<%@ attribute name="btnTancar" required="false" rtexprvalue="true"%>
<%@ attribute name="postUrl" required="true" rtexprvalue="true"%>
<%@ attribute name="deselectUrl" required="false" rtexprvalue="true"%>

<c:set var="nItems">${fn:length(items)}</c:set>

<div id="contingut-missatges"></div>

<div id="items-list" class="panel panel-default" role="tablist">
	<div class="panel-heading">
		<div class="row" role="button" data-toggle="collapse" data-parent="#items-list" data-target="#items-info" aria-expanded="true" aria-controls="items-info">
			<div class="col-sm-3">
				<h3 class="panel-title">
					<span class="state-icon fa"></span>
					<span class="badge seleccioCount">${fn:length(items)}</span> <spring:message code="${missatgeHeader}"/>
				</h3>
			</div>
			<div class="col-sm-7">
				<!-- Barra de progrés -->
				<div id="items-progress" class="progress" style="height: 25px; margin-bottom: 0; display:none;">
					<div id="items-progress-success" class="progress-bar progress-bar-success progress-bar-striped" style="width: 0%">
						<strong><span class="valor text-success"></span></strong>
					</div>
					<div id="items-progress-error" class="progress-bar progress-bar-danger progress-bar-striped" style="width: 0%">
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
	<div id="items-info" class="panel-collapse collapse registre-info">

		<table class="table table-striped table-bordered dataTable">
			<thead>
				<tr>
					<th><spring:message code="${missatgeHeader}"></spring:message></th>
					<th><spring:message code="itemsSeleccionats.estat"></spring:message></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="item" items="${items}">
					<tr id="tr_item_${item[itemId]}" data-id="${item[itemId]}" class='itemTr'>
						<td>[<a href="<c:url value="${itemUrl}/${item[itemUrlParam1]}/${not empty itemUrlParam2 ? item[itemUrlParam2] : ''}"/>" target="blank">${item[itemKey]}</a>] ${item[itemText]}</td>
						<td>
							<span class="estat esperant fa fa-clock-o" title="<spring:message code='itemsSeleccionats.estat.esperant'></spring:message>"></span>
							<span class="estat processant fa fa-refresh fa-spin" title="<spring:message code='itemsSeleccionats.estat.processant'></spring:message>" style="display:none"></span>
							<span class="estat success fa fa-check text-success" title="<spring:message code='itemsSeleccionats.estat.success'></spring:message>" style="display:none"></span>
							<span class="estat error fa fa-exclamation-triangle text-danger" title="<spring:message code='itemsSeleccionats.estat.error'></spring:message>" style="display:none"></span>
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
	var nItems = ${nItems};

	$(document).ready( function() {	
		// Botó per cancel·lar les reindexacions d'expedients amb error
		$('#cancelarBtn').click(function(e) {
			$(this).attr('disabled', 'disabled');
			$('#cancelarBtnText').html("<spring:message code='comu.boto.cancelant'/>");
			cancelar = true;
			e.preventDefault();
			return false;
		})
				
		if (nItems == 0) {
			webutilMissatgeWarning('<spring:message code="${missatgeCap}"></spring:message>', '#contingut-missatges');
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
		var itemsPendentsIds = [];
		$('.itemTr').not('.processat').each(function(){
			$('.estat', $(this)).hide();
			$('.esperant', $(this)).show();
			itemsPendentsIds.push($(this).data('id'));
		});
	
		var pendents = itemsPendentsIds.length;
		var total = $('.itemTr').length;
		var errors = 0;
		var correctes = $('.itemTr.processat').length;
	
		
		$('#items-progress-success').css('width', 100 * correctes / total + '%');
		$('#items-progress-error').css('width', 0 + '%');
		$('#items-progress').show();
		$('.progress-bar').addClass('active');
		
		cancelar = false;
		$('#cancelarBtn').removeAttr("disabled").css('visibility', 'visible');
		$('#cancelarBtnText').html("<spring:message code='comu.boto.cancelar'/>");
	
		for (var i = 0; i < itemsPendentsIds.length && !cancelar; ++i) {
	
			var resultat = await processarAnotacioAsync(itemsPendentsIds[i]);
			if (resultat) {
				correctes++;
				$('#items-progress-success').find('.valor').html(correctes);
			} else {
				errors++;
				$('#items-progress-error').find('.valor').html(errors);
			}
			$('#items-progress-success').css('width', 100 * correctes / total + '%');
			$('#items-progress-error').css('width', 100 * errors / total + '%');
		}
		$('.progress-bar').removeClass('active');
		$btnSubmit.removeAttr('disabled');
		$('#cancelarBtn').css('visibility', 'hidden');
	
		$spin.remove();
		
// 		$('div.modal-backdrop',window.parent.document).remove();
// 		$('div.modal',window.parent.document).remove();		
		
		if (correctes == itemsPendentsIds.length) {
			window.location = webutilModalTancarPath();
		}
	}
	
	function processarAnotacioAsync(itemId) {
		return new Promise(function (resolve, reject) {
			var $tr = $('#tr_item_' + itemId);
			var ret = false;
			$('.estat', $tr).hide();
			$('.processant', $tr).show();
			// crida a la reindexació
			$.ajax({
				type: "POST",
				url: '<c:url value="${postUrl}"></c:url>' + itemId,
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
