<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="usuari.form.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<c:if test="${requestLocale == 'en'}">
		<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script> 
	</c:if>
	<script src="<c:url value="/js/select2-locales/select2_${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<dis:modalHead/>
<script type="text/javascript">
$(document).ready(function() {
	$("#rols").prop("disabled", true);

	var bustiaPerDefecte = '${bustiaPerDefecte}'
	var baseUrl = "<c:url value='/registreUser/bustiesPermeses'/>?mostrarInactives=false";
	$.get(baseUrl)
		.done(function(data) {
			var $bustiaPerDefecte = $('#bustiaPerDefecte');
			$bustiaPerDefecte.empty();
			$bustiaPerDefecte.append("<option value=\"\"><spring:message code="comu.empty.option"/></option>");
			for (var i = 0; i < data.length; i++) {
				if (data[i].id === parseInt(bustiaPerDefecte)) {  
					$('#bustiaPerDefecte').append('<option value="' + data[i].id + '" selected>' + data[i].nom + '</option>');
				} else {
					$('#bustiaPerDefecte').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
				}
			}
			var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
			$bustiaPerDefecte.select2("destroy");
			$bustiaPerDefecte.select2(select2Options);
		})
		.fail(function() {
			alert("<spring:message code="error.jquery.ajax"/>");
		});	
});
</script>
</head>
<body>
	<c:set var="formAction"><dis:modalUrl value="/usuari/configuracio"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="usuariCommand" role="form">
		<form:hidden path="codi"/>
		<dis:inputText name="nom" textKey="usuari.form.camp.nom" disabled="true"/>
		<dis:inputText name="nif" textKey="usuari.form.camp.nif" disabled="true"/>
		<dis:inputText name="email" textKey="usuari.form.camp.email" disabled="true"/>
		<dis:inputText name="emailAlternatiu" textKey="usuari.form.camp.email.alternatiu" />
		<dis:inputSelect name="rols" textKey="usuari.form.camp.rols" optionItems="${rolsPerMostrar}" multiple="true" disabled="true"/>
		<dis:inputCheckbox name="rebreEmailsBustia" textKey="usuari.form.camp.rebre.emails.bustia"/>
		<dis:inputCheckbox name="rebreEmailsAgrupats" textKey="usuari.form.camp.rebre.emails.agrupats"/>
		<dis:inputSelect name="idioma" optionItems="${idiomaEnumOptions}" textKey="usuari.form.camp.idioma" optionValueAttribute="value" optionTextKeyAttribute="text" disabled="false"/>
		<dis:inputSelect name="bustiaPerDefecte" optionItems="${replacedByJquery}" textKey="bustia.list.filtre.bustia"/>

        <dis:inputSelect name="entitatPerDefecteId" optionItems="${entitats}" optionMinimumResultsForSearch="1" optionValueAttribute="id" emptyOption="true" optionTextAttribute="nom" textKey="usuari.form.camp.entitatPerDefecte" placeholder="Selecciona..."/>

        <div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/usuari/configuracio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
