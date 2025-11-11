<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
    <c:when test="${empty limitCanviEstatCommand.id}"><c:set var="titol"><spring:message code="limit.canvi.estat.form.titol.crear"/></c:set></c:when>
    <c:otherwise><c:set var="titol"><spring:message code="limit.canvi.estat.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
    <title>${titol}</title>
    <link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
    <c:if test="${requestLocale == 'en'}">
        <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
    </c:if>
    <script src="<c:url value="/js/select2-locales/select2_${requestLocale}.min.js"/>"></script>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <script src="<c:url value="/js/webutil.modal.js"/>"></script>
    <dis:modalHead/>
</head>
<body>
<c:set var="formAction"><dis:modalUrl value="/limitCanviEstat/newOrModify"/></c:set>
<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="limitCanviEstatCommand" role="form">
    <form:hidden path="id"/>
    <dis:inputText name="usuariCodi" textKey="limit.canvi.estat.grid.usuariCodi" required="true"/>
    <dis:inputText name="descripcio" textKey="limit.canvi.estat.grid.descripcio" required="true"/>

    <dis:inputNumber name="limitMinutLaboral" textKey="limit.canvi.estat.grid.limitMinutLaboral" placeholder="${placeholderLimitMinutLaboral}"/>
    <dis:inputNumber name="limitMinutNoLaboral" textKey="limit.canvi.estat.grid.limitMinutNoLaboral" placeholder="${placeholderLimitMinutNoLaboral}"/>
    <dis:inputNumber name="limitDiaLaboral" textKey="limit.canvi.estat.grid.limitDiaLaboral" placeholder="${placeholderLimitDiaLaboral}"/>
    <dis:inputNumber name="limitDiaNoLaboral" textKey="limit.canvi.estat.grid.limitDiaNoLaboral" placeholder="${placeholderLimitDiaNoLaboral}"/>

    <div id="modal-botons">
        <button id="addBustiaButton" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
        <a href="<c:url value="/limitCanviEstat"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
    </div>
</form:form>
</body>
</html>
