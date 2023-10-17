<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty bustiaCommand.id}"><c:set var="titol"><spring:message code="bustia.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="bustia.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<c:if test="${requestLocale == 'en'}">
		<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script> 
	</c:if>
	<script src="<c:url value="/js/select2-locales/select2_${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<dis:modalHead/>
	
	
	
	<script type="text/javascript">

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

	function formatSelectUnitat(item) {
		return formatSelectUnitatItem($('#unitatId'), item);
	}
	
	$(document).ready(function() {
		$("input:visible:enabled:not([readonly]),textarea:visible:enabled:not([readonly]),select:visible:enabled:not([readonly])").first().focus();
	});	
	</script>
	
</head>
<body>


	<c:if test="${bustiaDto.unitatOrganitzativa.tipusTransicio != null}">

		<div class="panel panel-danger">
			<div class="panel-heading">
				<span class="fa fa-warning text-danger"></span>
				<spring:message code="bustia.list.unitatObsoleta"/> 
			</div>
			<div class="panel-body">
				<div class="row">
					<label class="col-xs-4 text-right"><spring:message
							code="bustia.form.novesUnitats" /></label>
					<div class="col-xs-8">
						<ul style="padding-left: 17px;">
							<c:forEach items="${bustiaDto.unitatOrganitzativa.lastHistoricosUnitats}"
								var="newUnitat" varStatus="loop">
								<li>${newUnitat.denominacio} (${newUnitat.codi})</li>
							</c:forEach>
						</ul>
					</div>
				</div>
				<c:if test="${!empty bustiesOfOldUnitatWithoutCurrent}">
					<div class="row">
						<label class="col-xs-4 text-right"><spring:message
 								code="bustia.form.altresBustiesAfectades" /></label> 
						<div class="col-xs-8">
 							<ul style="padding-left: 17px;"> 
								<c:forEach items="${bustiesOfOldUnitatWithoutCurrent}" 
 									var="bustia" varStatus="loop"> 
 									<li>${bustia.nom}</li> 
 								</c:forEach> 
 							</ul> 
 						</div> 
 					</div> 
 				</c:if> 
			</div>
		</div>
	</c:if>
	<c:set var="formAction"><dis:modalUrl value="/bustiaAdmin/newOrModify"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="bustiaCommand" role="form">
		<form:hidden path="id"/>
		<form:hidden path="pareId"/>
		<c:url value="/unitatajax/unitat" var="urlConsultaInicial"/>
		<c:url value="/unitatajax/unitats" var="urlConsultaLlistat"/>
		<input id="isOrganigrama" name="isOrganigrama" type="hidden" value="${isOrganigrama}"/>
<%-- 		<dis:inputHidden name="isOrganigrama"/> --%>
		<dis:inputSuggest 
			name="unitatId" 
			textKey="bustia.form.camp.unitat"
			urlConsultaInicial="${urlConsultaInicial}" 
			urlConsultaLlistat="${urlConsultaLlistat}" 
			inline="false" 
			placeholderKey="bustia.form.camp.unitat"
			suggestValue="id"
			suggestText="codiAndNom"
			required="true" 
			optionTemplateFunction="formatSelectUnitat"/>
<%-- 		<dis:inputText name="unitatCodi" textKey="bustia.form.camp.unitat" required="true"/> --%>
		<dis:inputText name="nom" textKey="bustia.form.camp.nom" required="true"/>
		<div id="modal-botons">
			<button id="addBustiaButton" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/bustiaAdmin"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>		
	</form:form>

	<c:if test="${!empty usuaris}">
		<table class="table table-striped table-bordered">
			<thead>
				<tr>
					<th colspan="4"><div align="center"><spring:message code="bustia.usuarisTable"/></div></th>
				</tr>
				<tr>
					<th width="15%"><spring:message code="bustia.usuarisTable.codi"/></th>
					<th width="15%"><spring:message code="bustia.usuarisTable.nom"/></th>
					<th width="5%"><spring:message code="bustia.usuarisTable.permisPerUsuari"/></th>
					<th width="15%"><spring:message code="bustia.usuarisTable.rols"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="usuari" items="${usuaris}">
					<tr>
						<td>${usuari.codi}</td>
						<td>${usuari.nom}</td>
						<td style="text-align:center">
							<c:choose>
								<c:when test="${usuari.hasUsuariPermission}">
									<span class="fa fa-check-square-o"></span>
								</c:when>
								<c:otherwise>
									<span class="fa fa-square-o"></span>
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:if test="${!empty usuari.rols}">
								${usuari.rols}
							</c:if>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>

</body>
</html>