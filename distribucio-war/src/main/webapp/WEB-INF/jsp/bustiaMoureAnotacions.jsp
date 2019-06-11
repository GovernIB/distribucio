<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="bustia.moure.anotacions.titol" arguments="${bustia.nom}"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/css/jstree.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jstree.min.js"/>"></script>
	<dis:modalHead/>
</head>
<body>
	<form:form action="" class="form-horizontal" commandName="moureAnotacionsCommand">
		<form:hidden path="origenId"/>
		<dis:inputArbre 
				name="destiId" 
				textKey="bustia.moure.anotacions.form.desti" 
				arbre="${arbreUnitatsOrganitzatives}" 
				required="true" 
				fulles="${busties}" 
				fullesAtributId="id" 
				fullesAtributNom="nom" 
				fullesAtributPare="unitatCodi" 
				fullesIcona="fa fa-inbox fa-lg" 
				isArbreSeleccionable="${false}" 
				isFullesSeleccionable="${true}"
				isSeleccioMultiple="${false}" 
				isOcultarCounts="${true}"/>
				
		<dis:inputTextarea name="comentari" textKey="bustia.moure.anotacions.form.comentari"/>
		
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-share"></span> <spring:message code="bustia.moure.anotacions.form.moure"/></button>
			<a href="<c:url value="/bustiaAdmin/${bustia.id}/moureAnotacions"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
