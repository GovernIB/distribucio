<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ attribute name="registres" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="form" required="true" rtexprvalue="true"%>
<%@ attribute name="btnSubmit" required="true" rtexprvalue="true"%>
<%@ attribute name="postUrl" required="true" rtexprvalue="true"%>



<div id="registres-list" class="panel panel-default" role="tablist">
	<div class="panel-heading">
		<div class="row" role="button" data-toggle="collapse" data-parent="#registres-list" data-target="#registres-info" aria-expanded="false" aria-controls="registres-info">
			<div class="col-sm-3">
				<h3 class="panel-title">
					<span class="state-icon fa"></span>
					<span class="badge seleccioCount">${fn:length(registres)}</span> <spring:message code="registresSeleccionats.anotacions.seleccionades"></spring:message>
				</h3>
			</div>
			<div class="col-sm-8">
				<!-- Barra de progrés -->
				<div id="registres-progress" class="progress" style="height: 25px; margin-bottom: 0; display:none;">
					<div id="registres-progress-success" class="progress-bar progress-bar-success progress-bar-striped" style="width: 0%">
						<strong><span class="valor text-success">0</span></strong>
					</div>
					<div id="registres-progress-error" class="progress-bar progress-bar-danger progress-bar-striped" style="width: 0%">
						<strong><span class="valor text-danger">0</span></strong>
					</div>
				</div>
			</div>
			<div class="col-sm-1">
				<span class="fa fa-caret-down pull-right"></span>
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


<script>

	var cancelar = false;
	
	function processaAnotacions() {
		(async() => {
			processaAnotacionsAsync();
		})();	
	}
	
	async function processaAnotacionsAsync() {
				
		$('${form}').webutilNetejarErrorsCamps();
		$spin = $('<span class="fa fa-spinner fa-spin"></span>');
		$("${btnSubmit}").attr('disabled',true).prepend(" ").prepend($spin);
	
		// Obté la llista d'identificadors com un array
		var registresPendentsIds = [];
		$('.registreTr').not('.processat').each(function(){
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
		$("${btnSubmit}").removeAttr('disabled');
	
		$spin.remove();
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
		        	   if (ajaxResponse.estatOk) {
		        		   ret = true;
		        		   $('.success', $tr).show();
							$tr.addClass('processat');
		        	   } else {
		        		   if (ajaxResponse.errorsCamps) {
								$('${form}').webutilMostrarErrorsCamps(ajaxResponse.errorsCamps);
		        		   }
		        		   $('.error', $tr).show();
		        		   $('.error', $tr).attr('title', ajaxResponse.missatge);
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
	
</script>
