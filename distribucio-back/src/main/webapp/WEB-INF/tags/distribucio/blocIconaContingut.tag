<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ attribute name="contingut" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="tamanyDoble" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ attribute name="tamanyEnorme" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ attribute name="nomesIconaNom" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<dis:blocIconaContingutNoms/>
<c:if test="${tamanyDoble}"><c:set var="iconaTamany" value="fa-2x"/></c:if>
<c:if test="${tamanyEnorme}"><c:set var="iconaTamany" value="fa-5x"/></c:if>
<c:choose>
	<c:when test="${nomesIconaNom}">
		<c:choose>
			<c:when test="${contingut.bustia}">${iconaBustia} ${iconaTamany}</c:when>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${contingut.bustia}">
				<span class="fa ${iconaBustia} ${iconaTamany}" title="<spring:message code="contingut.icona.bustia"/>"></span>
			</c:when>
		</c:choose>
	</c:otherwise>
</c:choose>
