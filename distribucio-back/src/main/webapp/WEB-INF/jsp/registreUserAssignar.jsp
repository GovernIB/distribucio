<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="titol"><spring:message code="bustia.pendent.assignar.titol"/></c:set>
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
	<dis:modalHead/>
<script>
$(document).ready(function() {
	if (${fn:length(usuarisAmbPermis)} > 0) {
		$('#accio-assignar').removeAttr('disabled');
	}
});
</script>
</head>
<body>
	<form:form action="" method="post" cssClass="form-horizontal" modelAttribute="registreAssignarCommand">
		<c:choose>
			<c:when test="${empty usuarisAmbPermis}">
				<dis:inputFixed name="usuariCodi" textKey="bustia.pendent.assignar.camp.usuari">
					<p class="text-danger">
						<spring:message code="bustia.pendent.assignar.no.usuaris"/>
					</p>
				</dis:inputFixed>
			</c:when>
			<c:otherwise>
				<dis:inputSelect 
					name="usuariCodi" 
					textKey="bustia.pendent.assignar.camp.usuari" 
					optionItems="${usuarisAmbPermis}" 
					optionValueAttribute="codi" 
					optionTextAttribute="nom" 
					emptyOption="true"
					required="true"
					placeholderKey="bustia.pendent.assignar.camp.usuari"
					optionMinimumResultsForSearch="0"/>
				<dis:inputTextarea name="comentari" textKey="bustia.pendent.assignar.camp.comentari"/>
			</c:otherwise>
		</c:choose>
		<div id="modal-botons" class="well">
			<button id="accio-assignar" type="submit" class="btn btn-success" disabled="disabled"><span class="fa fa-inbox"></span> <spring:message code="bustia.pendent.assignar.submit"/></button>
			<a href="#" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>