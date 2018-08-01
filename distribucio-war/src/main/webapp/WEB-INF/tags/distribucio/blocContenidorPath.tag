<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ attribute name="contingut" required="true" rtexprvalue="true" type="java.lang.Object"%>
<dis:blocIconaContingutNoms/>
<c:if test="${not contingut.escriptori}">
	<ol class="breadcrumb">
		<c:forEach var="contingutPath" items="${contingut.path}" varStatus="status">
			<li>
				<%--c:choose>
					<c:when test="${contingutPath.escriptori}">
						<c:choose>
							<c:when test="${contingut.codiPropietariEscriptoriPare == pageContext.request.userPrincipal.name}">
								<a href="${contingutPath.id}"><span class="fa fa-desktop"></span> <spring:message code="contingut.path.escriptori"/></a>
							</c:when>
							<c:otherwise>
								<span class="fa fa-desktop"></span> <spring:message code="contingut.path.escriptori.de"/> ${contingut.nomPropietariEscriptoriPare}
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise--%>
						<c:choose>
							<c:when test="${contingutPath.expedient}"><a href="<c:url value="/contingut/${contingutPath.id}"/>"><span class="fa ${iconaExpedientObert}" title="<spring:message code="contingut.icona.expedient"/>"></span> ${contingutPath.nom}</a></c:when>
							<c:when test="${contingutPath.document}"><a href="<c:url value="/contingut/${contingutPath.id}"/>"><span class="fa ${iconaDocument}" title="<spring:message code="contingut.icona.document"/>"></span> ${contingutPath.nom}</a></c:when>
							<c:when test="${contingutPath.carpeta}"><a href="<c:url value="/contingut/${contingutPath.id}"/>"><span class="fa ${iconaCarpeta}" title="<spring:message code="contingut.icona.carpeta"/>"></span> ${contingutPath.nom}</a></c:when>
							<c:when test="${contingutPath.arxiv}">
								<c:choose>
									<c:when test="${status.first}"><span class="fa fa-sitemap" title="<spring:message code="contingut.icona.unitat"/>"></span> ${contingutPath.nom}</c:when>
									<c:otherwise><span class="fa ${iconaArxiu}" title="<spring:message code="contingut.icona.arxiu"/>"></span> ${contingutPath.nom}</c:otherwise>
								</c:choose>
							</c:when>
							<c:when test="${contingutPath.bustia}">
								<c:choose>
									<c:when test="${status.first}"><span class="fa fa-sitemap" title="<spring:message code="contingut.icona.unitat"/>"></span> ${contingutPath.nom}</c:when>
									<c:otherwise><span class="fa ${iconaBustia}" title="<spring:message code="contingut.icona.bustia"/>"></span> ${contingutPath.nom}</c:otherwise>
								</c:choose>
							</c:when>
						</c:choose>
					<%--/c:otherwise>
				</c:choose--%>
			</li>
		</c:forEach>
		<li class="active">
			<c:choose>
				<c:when test="${contingut.expedient}"><span class="fa ${iconaExpedientObert}" title="<spring:message code="contingut.icona.expedient"/>"></span></c:when>
				<c:when test="${contingut.document}"><span class="fa ${iconaDocument}" title="<spring:message code="contingut.icona.document"/>"></span></c:when>
				<c:when test="${contingut.carpeta}"><span class="fa ${iconaCarpeta}" title="<spring:message code="contingut.icona.carpeta"/>"></span></c:when>
				<c:when test="${contingut.arxiv}"><span class="fa ${iconaArxiu}" title="<spring:message code="contingut.icona.arxiu"/>"></span></c:when>
				<c:when test="${contingut.bustia}"><span class="fa ${iconaBustia}" title="<spring:message code="contingut.icona.bustia"/>"></span></c:when>
			</c:choose>
			${contingut.nom}
		</li>
	</ol>
</c:if>
