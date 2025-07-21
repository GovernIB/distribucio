<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="titol"><spring:message code="bustia.seleccio.perdefecte.titol"/></c:set>
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
		$(document).ready(function() {
			adjustModal();
		});
		
		function adjustModal() {
			var $iframe = $(window.frameElement);
			
			$iframe.on("load", function () {
				$iframe.css('height', '240px');
				$iframe.parent().css('height',  '240px');
			});
		}
	</script>
</head>
<body>
	<c:set var="formAction"><dis:modalUrl value="/bustiaAdmin/${bustiaId}/default/disable"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="bustiaPerDefecteCommand" role="form">
		
		<dis:inputSelect 
			name="bustiaId" 
			textKey="bustia.seleccio.perdefecte.form.camp.bustia" 
			placeholderKey="bustia.seleccio.perdefecte.form.camp.bustia"
			optionItems="${bustiesUnitat}" 
			optionTextAttribute="nom" 
			optionValueAttribute="id"
			optionMinimumResultsForSearch="0" 
			emptyOption="true"
			required="false" />
		
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/bustiaAdmin"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>		
	</form:form>
</body>
</html>