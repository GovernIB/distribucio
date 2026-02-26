<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<c:set var="titol"><spring:message code="regla.list.accio.aplicar.manualment"/></c:set>

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

    <script type="text/javascript">
        $(function() {
            $('#aplicarReglaButton').on('click', function() {
                 window.location = '${pageContext.request.contextPath}/regla/${reglaId}/aplicar';
            });
        });
    </script>

</head>
    <body>
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th width="10%"><spring:message code="regla.list.accio.aplicar.columna.numero"/></th>
                    <th width="20%"><spring:message code="regla.list.accio.aplicar.columna.titol"/></th>
                    <th width="5%" data-converter="datetime"><spring:message code="regla.list.accio.aplicar.columna.data"/></th>
                    <th width="15%"><spring:message code="regla.list.accio.aplicar.columna.unitatOrganitzativa"/></th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                        <c:when test="${not empty registres}">
                            <c:forEach var="registre" items="${registres}">
                                <tr>
                                    <td>${registre.numero}</td>
                                    <td>${registre.nom}</td>
                                    <td><fmt:formatDate value="${registre.data}" pattern="dd-MM-yyyy HH:mm:ss" /></td>
                                    <td>${registre.unitatAdministrativaDescripcio}</td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="4" class="text-center text-muted">
                                    <spring:message code="regla.list.accio.aplicar.table.noDadaDisponible"/>
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
            </tbody>
        </table>
        <div id="modal-botons">
            <button id="aplicarReglaButton" class="btn btn-success" type="submit">
                <span class="fa fa-cog"></span>
                <spring:message code="regla.list.accio.aplicar.manualment"/>
            </button>
            <button class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
        </div>

    </body>
</html>