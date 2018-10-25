<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<table class="table table-striped table-bordered">
	<tbody>
		<tr>
			<td><strong><spring:message code="arxiu.metadades.nti.camp.identificador"/></strong></td>
			<td>${arxiuDetall.identificador}</td>
		</tr>
		<tr>
			<td><strong><spring:message code="arxiu.metadades.nti.camp.nom"/></strong></td>
			<td>${arxiuDetall.nom}</td>
		</tr>
		<c:if test="${not empty arxiuDetall.serieDocumental}">
			<tr>
				<td><strong><spring:message code="arxiu.metadades.nti.camp.serie.doc"/></strong></td>
				<td>${arxiuDetall.serieDocumental}</td>
			</tr>
		</c:if>
	</tbody>
</table>
<c:if test="${not empty arxiuDetall.contingutTipusMime or not empty arxiuDetall.contingutArxiuNom}">
	<div class="panel panel-default">
		<div class="panel-heading"><h4 style="margin:0"><strong><spring:message code="arxiu.metadades.nti.grup.contingut"/></strong></h4></div>
		<table class="table table-striped table-bordered">
		<tbody>
			<c:if test="${not empty arxiuDetall.contingutTipusMime}">
				<tr>
					<td><strong><spring:message code="arxiu.metadades.nti.camp.contingut.tipus.mime"/></strong></td>
					<td>${arxiuDetall.contingutTipusMime}</td>
				</tr>
			</c:if>
			<c:if test="${not empty arxiuDetall.contingutArxiuNom}">
				<tr>
					<td><strong><spring:message code="arxiu.metadades.nti.camp.arxiu.metadades.nti.nom"/></strong></td>
					<td>${arxiuDetall.contingutArxiuNom}</td>
				</tr>
			</c:if>
		</tbody>
		</table>
	</div>
</c:if>
<c:if test="${not empty arxiuDetall.eniIdentificador}">
	<div class="panel panel-default">
		<!--div class="panel-heading"><h4 style="margin:0"><strong><spring:message code="arxiu.metadades.nti.grup.metadades"/></strong></h4></div-->
		<table class="table table-striped table-bordered">
		<tbody>
			<tr>
				<td><strong><spring:message code="arxiu.metadades.nti.camp.eni.versio"/></strong></td>
				<td>${arxiuDetall.eniVersio}</td>
			</tr>
			<tr>
				<td><strong><spring:message code="arxiu.metadades.nti.camp.eni.identificador"/></strong></td>
				<td>${arxiuDetall.eniIdentificador}</td>
			</tr>
			<c:if test="${not empty arxiuDetall.eniOrgans}">
				<tr>
					<td><strong><spring:message code="arxiu.metadades.nti.camp.eni.organs"/></strong></td>
					<td>
						<c:forEach var="organ" items="${arxiuDetall.eniOrgans}" varStatus="status">
							${organ}<c:if test="${not status.last}">,</c:if>
						</c:forEach>
					</td>
				</tr>
			</c:if>
			<c:if test="${not empty arxiuDetall.eniDataObertura}">
				<tr>
					<td><strong><spring:message code="arxiu.metadades.nti.camp.eni.data.obertura"/></strong></td>
					<td><fmt:formatDate value="${arxiuDetall.eniDataObertura}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
				</tr>
			</c:if>
			<c:if test="${not empty arxiuDetall.eniClassificacio}">
				<tr>
					<td><strong><spring:message code="arxiu.metadades.nti.camp.eni.classificacio"/></strong></td>
					<td>${arxiuDetall.eniClassificacio}</td>
				</tr>
			</c:if>
			<c:if test="${not empty arxiuDetall.eniEstat}">
				<tr>
					<td><strong><spring:message code="arxiu.metadades.nti.camp.eni.estat"/></strong></td>
					<td><spring:message code="expedient.estat.enum.${arxiuDetall.eniEstat}"/></td>
				</tr>
			</c:if>
			<c:if test="${not empty arxiuDetall.eniDataCaptura}">
				<tr>
					<td><strong><spring:message code="arxiu.metadades.nti.camp.eni.data.captura"/></strong></td>
					<td><fmt:formatDate value="${arxiuDetall.eniDataCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
				</tr>
			</c:if>
			<c:if test="${not empty arxiuDetall.eniOrigen}">
				<tr>
					<td><strong><spring:message code="arxiu.metadades.nti.camp.eni.origen"/></strong></td>
					<td><spring:message code="document.nti.origen.enum.${arxiuDetall.eniOrigen}"/></td>
				</tr>
			</c:if>
			<c:if test="${not empty arxiuDetall.eniEstatElaboracio}">
				<tr>
					<td><strong><spring:message code="arxiu.metadades.nti.camp.eni.estat.elab"/></strong></td>
					<td><spring:message code="registre.annex.detalls.camp.ntiElaboracioEstat.${arxiuDetall.eniEstatElaboracio}"/></td>
				</tr>
			</c:if>
			<c:if test="${not empty arxiuDetall.eniTipusDocumental}">
				<tr>
					<td><strong><spring:message code="arxiu.metadades.nti.camp.eni.tipus.doc"/></strong></td>
					<td><spring:message code="registre.annex.detalls.camp.ntiTipusDocument.${arxiuDetall.eniTipusDocumental}"/></td>
				</tr>
			</c:if>
			<c:if test="${not empty arxiuDetall.eniFormat}">
				<tr>
					<td><strong><spring:message code="arxiu.metadades.nti.camp.eni.format.nom"/></strong></td>
					<td>${arxiuDetall.eniFormat}</td>
				</tr>
			</c:if>
			<c:if test="${not empty arxiuDetall.eniExtensio}">
				<tr>
					<td><strong><spring:message code="arxiu.metadades.nti.camp.eni.format.ext"/></strong></td>
					<td>${arxiuDetall.eniExtensio}</td>
				</tr>
			</c:if>
			<c:if test="${not empty arxiuDetall.eniInteressats}">
				<tr>
					<td><strong><spring:message code="arxiu.metadades.nti.camp.eni.interessats"/></strong></td>
					<td>
						<c:forEach var="interessat" items="${arxiuDetall.eniInteressats}" varStatus="status">
							${interessat}<c:if test="${not status.last}">,</c:if>
						</c:forEach>
					</td>
				</tr>
			</c:if>
			<c:if test="${not empty arxiuDetall.eniDocumentOrigenId}">
				<tr>
					<td><strong><spring:message code="arxiu.metadades.nti.camp.eni.doc.orig.id"/></strong></td>
					<td>${arxiuDetall.eniDocumentOrigenId}</td>
				</tr>
			</c:if>
		</tbody>
		</table>
	</div>
</c:if>