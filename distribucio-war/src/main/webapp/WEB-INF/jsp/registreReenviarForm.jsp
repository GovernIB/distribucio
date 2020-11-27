<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="bustia.pendent.registre.reenviar.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<dis:modalHead titol="${titol}"/>
	<link href="<c:url value="/css/jstree.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jstree.min.js"/>"></script>
</head>
<body>
	<form:form action="" class="form-horizontal" commandName="contingutReenviarCommand">
		<form:hidden path="origenId"/>
		<div class="form-group">
			<div class="col-xs-offset-4 col-xs-8">
			    <span class="fa fa-exclamation-triangle text-success" title=""></span>
			    <spring:message code="contingut.enviar.info.seleccio.multiple"/>
			</div>
	    </div>
		<dis:inputArbre name="destins" textKey="contingut.enviar.camp.desti" arbre="${arbreUnitatsOrganitzatives}" required="true" fulles="${busties}" fullesAtributId="id" fullesAtributNom="nom" fullesAtributPare="unitatCodi"  fullesAtributInfo="perDefecte" fullesAtributInfoKey="contingut.enviar.info.bustia.defecte" fullesIcona="fa fa-inbox fa-lg" isArbreSeleccionable="${false}" isFullesSeleccionable="${true}" isOcultarCounts="${true}" isSeleccioMultiple="${true}"/>

		<dis:inputCheckbox name="deixarCopia" textKey="contingut.enviar.camp.deixar.copia" disabled="${disableDeixarCopia}"/>
	
		<dis:inputTextarea name="comentariEnviar" textKey="contingut.enviar.camp.comentari"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-send"></span> <spring:message code="comu.boto.enviar"/></button>
			<a href="<c:url value="/contenidor/${contenidorOrigen.pare.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>