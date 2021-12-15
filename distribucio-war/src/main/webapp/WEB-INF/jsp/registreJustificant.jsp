<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<table class="table table-bordered">
<tbody>
	<tr>
		<td style="width:30%"><strong><spring:message code="registre.annex.detalls.camp.eni.data.captura"/></strong></td>
		<td><c:if test="${not empty justificant.dataCaptura}"><fmt:formatDate value="${justificant.dataCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:if></td>
	</tr>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.eni.origen"/></strong></td>
		<td><c:if test="${not empty justificant.origenCiutadaAdmin}">${justificant.origenCiutadaAdmin}</c:if></td>
	</tr>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.eni.estat.elaboracio"/></strong></td>
		<td><c:if test="${not empty justificant.ntiElaboracioEstat}"><spring:message code="registre.annex.detalls.camp.ntiElaboracioEstat.${justificant.ntiElaboracioEstat}"/></c:if></td>
	</tr>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.eni.tipus.documental"/></strong></td>
		<td><c:if test="${not empty justificant.ntiTipusDocument}"><spring:message code="registre.annex.detalls.camp.ntiTipusDocument.${justificant.ntiTipusDocument}"/></c:if></td>
	</tr>
	<c:if test="${not empty justificant.sicresTipusDocument}">
		<tr>
			<td><strong><spring:message code="registre.annex.detalls.camp.sicres.tipus.document"/></strong></td>
			<td><spring:message code="registre.annex.detalls.camp.sicresTipusDocument.${justificant.sicresTipusDocument}"/></td>
		</tr>
	</c:if>
	<c:if test="${not empty justificant.localitzacio}">
		<tr>
			<td><strong><spring:message code="registre.annex.detalls.camp.localitzacio"/></strong></td>
			<td>${justificant.localitzacio}</td>
		</tr>
	</c:if>
	<c:if test="${not empty justificant.observacions}">
		<tr>
			<td><strong><spring:message code="registre.annex.detalls.camp.observacions"/></strong></td>
			<td>${justificant.observacions}</td>
		</tr>
	</c:if>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.fitxer"/></strong></td>
		<td>
			${justificant.fitxerNom} (${justificant.fitxerTamany} bytes)
			<a href="<c:url value="/modal/contingut/registre/${justificant.registreId}/justificant"/>" class="btn btn-default btn-sm pull-right">
				<span class="fa fa-download" title="<spring:message code="registre.annex.detalls.camp.fitxer.descarregar"/>"></span>
			</a>
		</td>
	</tr>
</tbody>
</table>