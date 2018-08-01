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
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<dis:modalHead/>
</head>
<body>


	<c:if test="${bustiaDto.unitatOrganitzativa.tipusTransicio != null}">

		<div class="panel panel-danger">
			<div class="panel-heading">
				<span class="fa fa-warning text-danger"></span>
				<spring:message code="bustia.list.unitatObsoleta"/> 
			</div>
			<div class="panel-body">
<!-- 				<div class="row"> -->
<%-- 					<label class="col-xs-4 text-right"><spring:message --%>
<%-- 							code="bustia.form.tipusTransicio" /></label> --%>
<!-- 					<div class="col-xs-8"> -->
<%-- 						<c:if --%>
<%-- 							test="${bustiaDto.unitatOrganitzativa.tipusTransicio == 'DIVISIO'}"> --%>
<%-- 							<spring:message code="unitat.tipusTransicio.DIVISIO" /> --%>
<%-- 						</c:if> --%>
<%-- 						<c:if --%>
<%-- 							test="${bustiaDto.unitatOrganitzativa.tipusTransicio == 'FUSIO'}"> --%>
<%-- 							<spring:message code="unitat.tipusTransicio.FUSIO" /> --%>
<%-- 						</c:if> --%>
<%-- 						<c:if --%>
<%-- 							test="${bustiaDto.unitatOrganitzativa.tipusTransicio == 'SUBSTITUCIO'}"> --%>
<%-- 							<spring:message code="unitat.tipusTransicio.SUBSTITUCIO" /> --%>
<%-- 						</c:if> --%>
<!-- 					</div> -->
<!-- 				</div> -->
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
<%-- 				<c:if test="${bustiaDto.unitatOrganitzativa.tipusTransicio == 'FUSIO'}"> --%>
<!-- 					<div class="row"> -->
<%-- 						<label class="col-xs-4 text-right"><spring:message --%>
<%-- 								code="unitat.transicioInfo.altresUnitatsFusionades" /></label> --%>
<!-- 						<div class="col-xs-8"> -->
<!-- 							<ul style="padding-left: 17px;"> -->
<%-- 								<c:forEach --%>
<%-- 									items="${bustiaDto.unitatOrganitzativa.altresUnitatsFusionades}" --%>
<%-- 									var="unitatMap" varStatus="loop"> --%>
<%-- 									<li>${unitatMap.value} (${unitatMap.key})</li> --%>
<%-- 								</c:forEach> --%>
<!-- 							</ul> -->
<!-- 						</div> -->
<!-- 					</div> -->
<%-- 				</c:if> --%>
			</div>
		</div>
	</c:if>
	<c:set var="formAction"><dis:modalUrl value="/bustiaAdmin/newOrModify"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="bustiaCommand" role="form">
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
			suggestText="nom" />
<%-- 		<dis:inputText name="unitatCodi" textKey="bustia.form.camp.unitat" required="true"/> --%>
		<dis:inputText name="nom" textKey="bustia.form.camp.nom" required="true"/>
		<div id="modal-botons">
			<button id="addBustiaButton" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/bustiaAdmin"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>		
	</form:form>



</body>
</html>