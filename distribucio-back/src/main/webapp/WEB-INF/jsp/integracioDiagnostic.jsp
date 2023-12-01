<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:set var="titol"><spring:message code="integracio.diagnostic.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<dis:modalHead/>
	
	<script>
		$(document).ready(function() {
			diagnostic();
			$('button[name=btnRefrescarDiagnostic]').click(function () {
				diagnostic();
				return false;
			});
		})
		
		function diagnostic() {
			var integracions = $(".integracio");
			for(var i=0; i<integracions.length; i++) {
				let integracio = integracions.eq(i).data('codi');
				$("#span-refresh-" + integracio).empty().addClass('fa-circle-o-notch');
				$("#span-refresh-" + integracio).addClass('fa-spin');
				$("#integracio_" + integracio + "_info").empty();
				$.ajax({
					method: "GET",
			        url: "<c:url value='/integracio/diagnosticAjax'/>/" + integracio, 
			        async: true,
			        success: function(data){
						$("#span-refresh-" + integracio).removeClass('fa-circle-o-notch');
						$("#span-refresh-" + integracio).removeClass('fa-spin');
						$("#span-refresh-" + integracio).removeClass('fa-refresh');
						if (data.correcte == true) {
							$("#span-refresh-" + integracio).addClass("fa-check text-success");
						    let t = document.createTextNode("    " + data.prova);
							$('#integracio_' + integracio + '_info').append(t);
							debugger;
						}else {
							$("#span-refresh-" + integracio).addClass("fa-times text-danger");
						    let t = document.createTextNode("    "+data.errMsg);
						    $('#integracio_' + integracio + '_info').append(t);
							debugger;
						}						
			        }
			    });
			} 
		}
	</script>
	
</head>
<body>


	
	
	<ul class="nav nav-tabs" role="tablist">
		<c:forEach var="integracio" items="${integracions}">			
			<c:if test="${not empty integracio}">
				<dl class="dl-horizontal">
					<dt class="integracio" id="integracio_${integracio.codi}" data-codi="${integracio.codi}">${integracio.nom}</dt>
					<dd><span id="span-refresh-${integracio.codi}" class="ml-2 fa fa-refresh "></span>
						<p id="integracio_${integracio.codi}_info" style="display:inline;"></p></dd>
				</dl>
			</c:if>			
		</c:forEach>
	</ul>	
	<div id="modal-botons">
		<button name="btnRefrescarDiagnostic" type="button" id="btnRefrescarDiagnostic" class="btn btn-success"> <span class="fa fa-refresh"></span> <spring:message code="comu.boto.refrescar"/> </button>
		<a href="<c:url value="/integracio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>

</body>