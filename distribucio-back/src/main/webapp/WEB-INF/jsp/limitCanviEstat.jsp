<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title><spring:message code="decorator.menu.limit.canvi.estat"/></title>
    <script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
    <script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
    <link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
    <link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
    <c:if test="${requestLocale == 'en'}">
        <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.full.min.js"/>"></script>
    </c:if>
    <script src="<c:url value="/js/select2-locales/select2_${requestLocale}.full.min.js"/>"></script>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
    <script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <script src="<c:url value="/js/webutil.datatable.js"/>"></script>
    <script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
<table id="limits" data-toggle="datatable" data-url="<c:url value="/limitCanviEstat/datatable"/>" data-default-order="0"
       data-default-dir="asc"
       class="table table-bordered table-striped"
       data-botons-template="#botonsTemplate">
    <thead>
    <tr>
        <th data-col-name="id" data-visible="false">#</th>
        <th data-col-name="usuariCodi"><spring:message code="limit.canvi.estat.grid.usuariCodi"/></th>
        <th data-col-name="descripcio"><spring:message code="limit.canvi.estat.grid.descripcio"/></th>
        <th data-col-name="limitMinutLaboral"><spring:message code="limit.canvi.estat.grid.limitMinutLaboral"/></th>
        <th data-col-name="limitMinutNoLaboral"><spring:message code="limit.canvi.estat.grid.limitMinutNoLaboral"/></th>
        <th data-col-name="limitDiaLaboral"><spring:message code="limit.canvi.estat.grid.limitDiaLaboral"/></th>
        <th data-col-name="limitDiaNoLaboral"><spring:message code="limit.canvi.estat.grid.limitDiaNoLaboral"/></th>

        <th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
            <script id="cellAccionsTemplate" type="text/x-jsrender">
                <div class="dropdown">
                    <button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
                    <ul class="dropdown-menu">
                        <li><a href="limitCanviEstat/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
                        <li><a href="limitCanviEstat/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="bustia.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
                    </ul>
                </div>
            </script>
        </th>
    </tr>
    </thead>
</table>
<script id="botonsTemplate" type="text/x-jsrender">
    <p style="text-align:right"><a id="bustia-boto-nova" class="btn btn-default" href="limitCanviEstat/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;
        <spring:message code="limit.canvi.estat.nou.limit"/>
    </a></p>
</script>
</body>
</html>
