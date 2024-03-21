<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="multiple">${registres != null}</c:set>
<c:set var="nRegistres">${multiple ? fn:length(registres) : 0 }</c:set>
<c:set var="titol">
	<c:choose>
		<c:when test="${multiple}"><spring:message arguments="${nRegistres}" code="bustia.pendent.contingut.enviarViaEmail.i.marcarProcessat.titol.multiple"/></c:when>
		<c:otherwise><spring:message code="bustia.pendent.contingut.enviarViaEmail.i.marcarProcessat.titol"/></c:otherwise>
	</c:choose>
</c:set>
<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<dis:modalHead/>

<script type="text/javascript">

	var multiple = ${registres != null};

	$(document).ready(function() {
		$("input:visible:enabled:not([readonly]),textarea:visible:enabled:not([readonly]),select:visible:enabled:not([readonly])").first().focus();
		
		$("button[name='btnEnviarIProcessarSubmit']").click(function(){
			if (multiple) {
				processaAnotacions();
			}
	    });		
	});
</script>
</head>
<body>

	<c:if test="${registres != null}">
		<dis:processamentMultiple 
			registres="${registres}"
			btnSubmit="button[name='btnEnviarIProcessarSubmit']"
			form="#registreEnviarIProcessarCommand"
			postUrl="/registreUser/enviarIProcessarAjax/"
			deselectUrl="/registreUser/deselect"></dis:processamentMultiple>
	</c:if>

	<form:form action="" method="post" cssClass="form-horizontal" modelAttribute="registreEnviarIProcessarCommand">

		<form:hidden path="contingutId"/>
		
		<dis:inputTextarea name="addresses" textKey="bustia.pendent.contingut.enviarViaEmail.destinataris" required="true"/>
		<div class="form-group">
			<div class="col-xs-offset-4 col-xs-8">
			    <span class="fa fa-exclamation-triangle text-success" title=""></span>
			    <spring:message code="bustia.pendent.accio.enviarViaEmail.info"/>
			</div>
  		</div>		
		<dis:inputTextarea required="true" name="motiu" textKey="bustia.pendent.contingut.camp.motiu"/>
		<div id="modal-botons" class="well">
			<button name="btnEnviarIProcessarSubmit" type="${multiple ? 'button' : 'submit' }" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="bustia.pendent.contingut.enviarViaEmail.i.marcarProcessat.boto"/></button>
			<a href="#" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>