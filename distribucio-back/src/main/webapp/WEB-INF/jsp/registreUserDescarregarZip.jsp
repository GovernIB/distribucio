<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="multiple">${registres != null}</c:set>
<c:set var="nRegistres">${multiple ? fn:length(registres) : 0 }</c:set>
<c:set var="titol">
	<c:choose>
		<c:when test="${multiple}"><spring:message arguments="${nRegistres}" code="registre.annex.descarregar.zip.titol"/></c:when>
<%--		<c:otherwise><spring:message code="bustia.pendent.contingut.marcar.processat.titol"/></c:otherwise>--%>
	</c:choose>
</c:set>
<html>
<head>
    <title>${titol}</title>
    <link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
<%--    <c:if test="${requestLocale == 'en'}">--%>
<%--        <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>--%>
<%--    </c:if>--%>
<%--    <script src="<c:url value="/js/select2-locales/select2_${requestLocale}.min.js"/>"></script>--%>
    <link href="<c:url value="/css/jstree.min.css"/>" rel="stylesheet">
    <script src="<c:url value="/js/jstree.min.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <dis:modalHead/>

    <style>
        button.close-alertes {
            align-items: end;
            background: none repeat scroll 0 0 transparent;
            border: 0 none;
            cursor: pointer;
            padding: 0;
        }
    </style>
</head>
<body>

	<c:if test="${registres != null}">
        <dis:seleccioMultiple
            items="${registres}"
            itemId="id"
            itemUrl="/registreUser/registre"
            itemUrlParam1="id"
            itemKey="numero"
            itemText="extracte"
            missatgeHeader="registresSeleccionats.anotacions.seleccionades"/>
	</c:if>

	<form:form class="form-horizontal" modelAttribute="descarregarZipCommand">
        <c:forEach var="error" items="${errors}">
            <div class="alert alert-danger">${error}</div>
        </c:forEach>
        <c:if test="${midaMaxima != null}">
            <div class="alert alert-info"><spring:message code="registre.annex.descarregar.zip.size" arguments="${midaMaxima}"/></div>
        </c:if>

        <dis:inputCheckbox name="estructuraCarpetes" textKey="bustia.pendent.contingut.desacarregarZip.estructuraCarpetes"/>
        <dis:inputCheckbox name="versioImprimible" textKey="bustia.pendent.contingut.desacarregarZip.versioImprimible"/>

        <dis:inputSelect name="nomDocument" optionEnum="FileNameOption" textKey="bustia.pendent.contingut.desacarregarZip.nomDocument"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success <c:if test="${disabled}">disabled</c:if>" data-nosubmit="true"><span class="fa fa-check-circle-o"></span> <spring:message code="comu.boto.descarregar"/></button>
			<a href="<c:url value="/registreUser"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
