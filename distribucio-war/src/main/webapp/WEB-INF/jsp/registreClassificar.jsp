<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="bustia.pendent.classificar.form.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<dis:modalHead/>
<script>
	$(document).ready(function() {

	});
</script>
</head>
<body>
	<form:form action="" method="post" cssClass="form-horizontal" commandName="registreClassificarCommand">

		<form:hidden path="bustiaId"/>
		<form:hidden path="contingutId"/>
		
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-magic"></span> <spring:message code="bustia.pendent.accio.classificar"/></button>
			<a href="<c:url value="/contenidor/${expedientCommand.pareId}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
		
		<div class="form-group">
			<div class="col-xs-offset-4 col-xs-8">
			    <span class="fa fa-exclamation-triangle text-success" title=""></span>
			    <spring:message code="bustia.pendent.accio.enviarViaEmail.info"/>
			</div>
  		</div>
	</form:form>
</body>
</html>