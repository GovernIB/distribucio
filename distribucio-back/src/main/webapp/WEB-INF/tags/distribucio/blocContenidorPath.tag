<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ attribute name="contingut" required="true" rtexprvalue="true" type="java.lang.Object"%>
<dis:blocIconaContingutNoms/>
<ol class="breadcrumb">
	<c:forEach var="contingutPath" items="${contingut.path}" varStatus="status">
		<li>
			<c:choose>
				<c:when test="${contingutPath.bustia}">
					<c:choose>
						<c:when test="${status.first}"><span class="fa fa-sitemap" title="<spring:message code="contingut.icona.unitat"/>"></span> ${contingutPath.nom}</c:when>
						<c:otherwise><span class="fa ${iconaBustia}" title="<spring:message code="contingut.icona.bustia"/>"></span> ${contingutPath.nom}</c:otherwise>
					</c:choose>
				</c:when>
			</c:choose>
		</li>
	</c:forEach>
	<li class="active">
		<c:choose>
			<c:when test="${contingut.bustia}"><span class="fa ${iconaBustia}" title="<spring:message code="contingut.icona.bustia"/>"></span></c:when>
		</c:choose>
		${contingut.nom}
	</li>
</ol>
