<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<script type="text/javascript">
	
	$(document).ready(function() {

	    $("#collapse-registre-firmes-<c:out value='${annex.id}'/>").on('show.bs.collapse', function(data){  	
		    if (!$(this).data("loaded")) {
		    	var registreId = $(this).parents(".collapse-annex").data("registreId"); 
		        $(this).append("<div style='text-align: center; margin-bottom: 60px; margin-top: 60px;''><span class='fa fa-circle-o-notch fa-spin fa-3x'/></div>");
		        $(this).load("<c:url value="/nodeco/registreUser/registreAnnexFirmes/"/>/" + registreId + "/" + ${annex.id} + "/false");
		        $(this).data("loaded", true);
		    }
	    });
	    

	    
	});

</script>


</head>

<body>

<table class="table table-bordered">
<tbody>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.eni.data.captura"/></strong></td>
		<td><c:if test="${not empty annex.dataCaptura}"><fmt:formatDate value="${annex.dataCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:if></td>
	</tr>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.eni.origen"/></strong></td>
		<td><c:if test="${not empty annex.origenCiutadaAdmin}">${annex.origenCiutadaAdmin}</c:if></td>
	</tr>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.eni.estat.elaboracio"/></strong></td>
		<td><c:if test="${not empty annex.ntiElaboracioEstat}"><spring:message code="registre.annex.detalls.camp.ntiElaboracioEstat.${annex.ntiElaboracioEstat}"/></c:if></td>
	</tr>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.eni.tipus.documental"/></strong></td>
		<td><c:if test="${not empty annex.ntiTipusDocument}"><spring:message code="registre.annex.detalls.camp.ntiTipusDocument.${annex.ntiTipusDocument}"/></c:if></td>
	</tr>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.sicres.tipus.document"/></strong></td>
		<td><c:if test="${not empty annex.sicresTipusDocument}"><spring:message code="registre.annex.detalls.camp.sicresTipusDocument.${annex.sicresTipusDocument}"/></c:if></td>
	</tr>
	<c:if test="${not empty annex.localitzacio}">
		<tr>
			<td><strong><spring:message code="registre.annex.detalls.camp.localitzacio"/></strong></td>
			<td>${annex.localitzacio}</td>
		</tr>
	</c:if>
	<c:if test="${not empty annex.observacions}">
		<tr>
			<td><strong><spring:message code="registre.annex.detalls.camp.observacions"/></strong></td>
			<td>${annex.observacions}</td>
		</tr>
	</c:if>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.arxiu.uuid"/></strong></td>
		<td>
			${annex.fitxerArxiuUuid}
			<c:if test="${annex.fitxerArxiuUuid == null }">
				<span class="fa fa-warning text-warning" title="<spring:message code="registre.annex.detalls.camp.arxiu.uuid.buit.avis"/>"></span>
			</c:if>
		</td>
	</tr>
	
	
	<c:forEach var="metaDada" items="${annex.metaDadesMap}">
		<tr>
			<td><strong>
				<c:choose>
					<c:when test="${metaDada.key=='eni:resolucion'}">
						<spring:message code="registre.annex.detalls.camp.metaData.resolucion"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:profundidad_color'}">
						<spring:message code="registre.annex.detalls.camp.metaData.profundidad_color"/>
					</c:when>
					<c:otherwise>
						${fn:toUpperCase(metaDada.key)}
					</c:otherwise>
				</c:choose>
			</strong></td>
			<td>
				${metaDada.value}
			</td>
		</tr>
	</c:forEach>
	
	
	
	
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.fitxer"/></strong></td>
		<td>
		
			<a href="<c:url value="/modal/contingut/registre/${registreId}/annex/${annex.id}/arxiu/DOCUMENT"/>" class="btn btn-default btn-sm pull-right arxiu-download">
				<span class="fa fa-download" title="<spring:message code="registre.annex.detalls.camp.fitxer.descarregar"/>"></span>
			</a>
		</td>
	</tr>
	<c:if test="${annex.ambFirma}">
		<tr>
			<td colspan="2">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<span class="fa fa-certificate"></span>
							<spring:message code="registre.annex.detalls.camp.firmes"/>
							<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-registre-firmes-${annex.id}"><span class="fa fa-chevron-down"></span></button>
						</h3>
					</div>
					<div id="collapse-registre-firmes-${annex.id}" class="panel-collapse collapse collapse-annex collapse-registre-firmes" role="tabpanel"> 

					</div> 
				</div>
			</td>
		</tr>
	</c:if>
</table>

</body>