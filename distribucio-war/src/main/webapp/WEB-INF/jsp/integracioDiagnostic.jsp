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
			var integracions = $(".integracio");
			for(var i=0; i<integracions.length; i++) {
				let integracio = integracions.eq(i).data('codi');
				$("#span-refresh-" + integracio).addClass('fa-circle-o-notch');
				$("#span-refresh-" + integracio).addClass('fa-spin');
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
						    let h = document.createElement("p");
						    let t = document.createTextNode("    "+data.prova);
						    h.style.cssText = 'display:inline';
						    h.appendChild(t);
						    $("#span-refresh-" + integracio).append(h);
						}else {
							$("#span-refresh-" + integracio).addClass("fa-times text-danger");
						    let h = document.createElement("p");
						    let t = document.createTextNode("    "+data.errMsg);
						    h.style.cssText = 'display:inline';
						    h.appendChild(t);
						    $("#span-refresh-" + integracio).append(h);

						}						
			        }
			    });
			} 
		})
	</script>
	
</head>
<body>


	
	
	<ul class="nav nav-tabs" role="tablist">
		<c:forEach var="integracio" items="${integracions}">			
			<c:if test="${not empty integracio}">
				<dl class="dl-horizontal">
					<dt class="integracio" id="integracio_${integracio.codi}" data-codi="${integracio.codi}">${integracio.nom}</dt>
					<dd><span id="span-refresh-${integracio.codi}" class="ml-2 fa fa-refresh "></span></dd>
				</dl>
			</c:if>			
		</c:forEach>
	</ul>	
	<div id="modal-botons">
		<button name="btnIniciDiagnostic" type="button" id="inici-diagnostic" class="btn btn-success d-none"></button>
		<a href="<c:url value="/integracio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>

</body>