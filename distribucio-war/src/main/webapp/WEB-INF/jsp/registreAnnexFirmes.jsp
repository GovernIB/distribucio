<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>



<table class="table teble-striped table-bordered">
<thead>
	<tr>
		<th><spring:message code="registre.annex.detalls.camp.firma"/></th>
		
		<th><spring:message code="registre.annex.detalls.camp.firmaDetalls.nom"/></th>
		<th><spring:message code="registre.annex.detalls.camp.firmaDetalls.nif"/></th>
		<th><spring:message code="registre.annex.detalls.camp.firmaDetalls.data"/></th>
		<c:if test="${!isResum}">
			<th><spring:message code="registre.annex.detalls.camp.firmaDetalls.emissor"/></th>
		</c:if>
		<c:if test="${firma.tipus != 'PADES' and firma.tipus != 'CADES_ATT' and firma.tipus != 'XADES_ENV'}">
			<th><strong><spring:message code="registre.annex.detalls.camp.fitxer"/></strong></th>
		</c:if>
		<c:if test="${not empty firma.csvRegulacio}">
			<th><strong><spring:message code="registre.annex.detalls.camp.firmaCsvRegulacio"/></strong></th>
		</c:if>	
		<c:if test="${isUsuariActualAdministration && !isResum}">
			<th><strong><spring:message code="registre.annex.detalls.camp.firmaTipus"/></strong></th>
		</c:if>	
		<c:if test="${isUsuariActualAdministration && !isResum}">
			<th><strong><spring:message code="registre.annex.detalls.camp.firmaPerfil"/></strong></th>
		</c:if>			
		
	</tr>
<tbody>

	<c:set var="index">0</c:set>
	<c:forEach var="firma" items="${annex.firmes}" varStatus="statusFirma">
		<c:forEach var="detall" items="${firma.detalls}" varStatus="statusDetall">	
			<c:set var="index">${index + 1}</c:set>
			<tr>
				<td>
					<spring:message code="registre.annex.detalls.camp.firma"/> ${index}
					<c:if test="${firma.autofirma}">
						(<spring:message code="registre.annex.detalls.camp.firma.autoFirma"/> <span class="fa fa-info-circle" title="<spring:message code="registre.annex.detalls.camp.firma.autoFirma.info" />"></span>)
					</c:if>
				</td>
				<td>${detall.responsableNom}</td>
				<td>${detall.responsableNif}</td>
				<td>
					<c:if test="${not empty detall.data}"><fmt:formatDate value="${detall.data}" pattern="dd/MM/yyyy HH:mm:ss"/></c:if>
					<c:if test="${empty detall.data}"><spring:message code="registre.annex.detalls.camp.firmaDetalls.data.nd"/></c:if>
				</td>				
				<c:if test="${!isResum}">
					<td>${detall.emissorCertificat}</td>
				</c:if>
				<c:if test="${firma.tipus != 'PADES' and firma.tipus != 'CADES_ATT' and firma.tipus != 'XADES_ENV'}">
					<td>
						${firma.fitxerNom}
						<a href="<c:url value="/modal/contingut/${bustiaId}/registre/${registreId}/annex/${annex.id}/firma/${statusFirma.index}"/>" class="btn btn-default btn-sm pull-right">
							<span class="fa fa-download"  title="<spring:message code="registre.annex.detalls.camp.fitxer.descarregar"/>"></span>
						</a>
					</td>
				</c:if>	
				
				<c:if test="${not empty firma.csvRegulacio}">
					<td>${firma.csvRegulacio}</td>
				</c:if>			
										
				<c:if test="${isUsuariActualAdministration && !isResum}">
					<td><spring:message code="document.nti.tipfir.enum.${firma.tipus}"/></td>
				</c:if>			
				<c:if test="${isUsuariActualAdministration && !isResum}">
					<td>${firma.perfil}</td>
				</c:if>							
			</tr>
		</c:forEach>
	</c:forEach>

	
</tbody>
</table>






