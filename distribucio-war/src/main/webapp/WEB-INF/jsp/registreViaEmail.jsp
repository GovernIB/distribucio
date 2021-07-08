<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="bustia.pendent.contingut.enviarViaEmail.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<dis:modalHead/>
<script>
	$(document).ready(function() {
		$("input:visible:enabled:not([readonly]),textarea:visible:enabled:not([readonly]),select:visible:enabled:not([readonly])").first().focus();
	});
</script>
</head>
<body>
	<form:form action="" method="post" cssClass="form-horizontal" commandName="registreEnviarViaEmailCommand">

		<form:hidden path="contingutId"/>
		
		<dis:inputTextarea name="addresses" textKey="bustia.pendent.contingut.enviarViaEmail.destinataris" required="true"/>
		<div class="form-group">
			<div class="col-xs-offset-4 col-xs-8">
			    <span class="fa fa-exclamation-triangle text-success" title=""></span>
			    <spring:message code="bustia.pendent.accio.enviarViaEmail.info"/>
			</div>
  		</div>		
		<dis:inputTextarea name="motiu" textKey="bustia.pendent.contingut.enviarViaEmail.motiu"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.enviar"/></button>
			<a href="#" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>

	</form:form>
</body>
</html>