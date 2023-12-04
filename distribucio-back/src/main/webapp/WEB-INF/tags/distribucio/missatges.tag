<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="commandName"/>
<c:forEach var="attributeName" items="${pageContext.request.attributeNames}">
	<c:if test="${not fn:contains(attributeName, '.') && fn:contains(attributeName, 'ommand')}">
		<c:set var="commandName" value="${attributeName}" />
	</c:if>
</c:forEach>

<c:if test="${not empty commandName}">
	<spring:hasBindErrors name="${commandName}">
		<c:if test="${not empty errors.globalErrors}">
			<c:forEach var="error" items="${errors.globalErrors}">
				<div class="alert alert-danger">
					<button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
					<spring:message message="${error}"/>
				</div>
			</c:forEach>
		</c:if>
	</spring:hasBindErrors>
</c:if>

<% request.setAttribute("sessionErrors", session.getAttribute(es.caib.distribucio.back.helper.MissatgesHelper.SESSION_ATTRIBUTE_ERROR)); %>
<% request.setAttribute("sessionWarnings", session.getAttribute(es.caib.distribucio.back.helper.MissatgesHelper.SESSION_ATTRIBUTE_WARNING)); %>
<% request.setAttribute("sessionSuccesses", session.getAttribute(es.caib.distribucio.back.helper.MissatgesHelper.SESSION_ATTRIBUTE_SUCCESS)); %>
<% request.setAttribute("sessionInfos", session.getAttribute(es.caib.distribucio.back.helper.MissatgesHelper.SESSION_ATTRIBUTE_INFO)); %>

<c:forEach var="text" items="${sessionErrors}">
	<div class="alert alert-danger">
		<button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
		${text}
	</div>
</c:forEach>
<c:forEach var="text" items="${sessionWarnings}">
	<div class="alert alert-warning">
		<button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
		${text}
	</div>
</c:forEach>
<c:forEach var="text" items="${sessionSuccesses}">
	<div class="alert alert-success">
		<button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
		${text}
	</div>
</c:forEach>
<c:forEach var="text" items="${sessionInfos}">
	<div class="alert alert-info">
		<button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
		${text}
	</div>
</c:forEach>

<% session.removeAttribute(es.caib.distribucio.back.helper.MissatgesHelper.SESSION_ATTRIBUTE_SUCCESS); %>
<% session.removeAttribute(es.caib.distribucio.back.helper.MissatgesHelper.SESSION_ATTRIBUTE_WARNING); %>
<% session.removeAttribute(es.caib.distribucio.back.helper.MissatgesHelper.SESSION_ATTRIBUTE_ERROR); %>
<% session.removeAttribute(es.caib.distribucio.back.helper.MissatgesHelper.SESSION_ATTRIBUTE_INFO); %>
