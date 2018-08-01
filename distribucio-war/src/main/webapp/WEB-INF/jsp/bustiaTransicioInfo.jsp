<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>



<html>
<head>
	<title><spring:message code="bustia.list.unitatObsoleta"/></title>
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
				<spring:message code="bustia.list.unitatObsoleta.arguments" arguments="${bustiaDto.unitatOrganitzativa.denominacio} (${bustiaDto.unitatOrganitzativa.codi}),${bustiaDto.nom}"/>
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



</body>
</html>
