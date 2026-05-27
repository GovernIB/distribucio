<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%
	pageContext.setAttribute(
			"isRolActualAdministrador",
			es.caib.distribucio.back.helper.RolHelper.isRolActualAdministrador(request));
	pageContext.setAttribute(
			"isRolActualAdminLectura",
			es.caib.distribucio.back.helper.RolHelper.isRolActualAdminLectura(request));
%>
<html>
<head>
	<title><spring:message code="backoffice.titol"/></title>
    <script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
    <script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
    <link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
    <c:if test="${requestLocale == 'en'}">
        <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
    </c:if>
    <script src="<c:url value="/js/select2-locales/select2_${requestLocale}.min.js"/>"></script>
    <script src="<c:url value="/js/select2-locales/select2_locale_ca.js"/>"></script>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
    <link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
    <script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
    <script src="<c:url value="/webjars/Sortable/1.4.2/Sortable.min.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <script src="<c:url value="/js/webutil.datatable.js"/>"></script>
    <script src="<c:url value="/js/webutil.modal.js"/>"></script>

    <script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
    <link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"/>

    <script type="text/javascript">
		$(document).ready(function() {
            $(document).on('hidden.bs.modal', function () {
                var data = sessionStorage.getItem('selectedElements');
                if (data != null) {
                    // Deseleccionar elements si s'ha realitzat una acció múltiple i les anotacions s'han mogut
                    $(".seleccioCount").html(data);
                    $('#backoffice').webutilDatatable('refresh');

                    sessionStorage.removeItem('selectedElements');
                }
            });

            $('form').submit(function() {
                $.get(
                    "backoffice/deselect",
                    function(data) {
                        $("#seleccioCount").html(data);
                        $('#backoffice').webutilDatatable('select-none');
                    }
                );
                return true;
            });

            let selectButtonsInitialized = false;
            $('#backoffice').on( 'draw.dt', function () {
                if (!selectButtonsInitialized) {
                    selectButtonsInitialized = true;

                    $('#seleccioAll').on('click', function() {
                        $.get(
                            "backoffice/select",
                            function(data) {
                                console.log(data);
                                $("#seleccioCount").html(data);
                                $('#backoffice').webutilDatatable('refresh');
                            }
                        );
                        return false;
                    });

                    $('#seleccioNone').on('click', function() {
                        $.get(
                            "backoffice/deselect",
                            function(data) {
                                $("#seleccioCount").html(data);
                                $('#backoffice').webutilDatatable('select-none');
                            }
                        );
                        return false;
                    });
                }
            }).on('selectionchange.dataTable', function (e, accio, ids) {
                $.get(
                    "backoffice/" + accio,
                    {ids: ids},
                    function(data) {
                        $("#seleccioCount").html(data);
                    }
                );
            });
        });

        function deselectItems() {
            $.get(
                "backoffice/deselect",
                function(data) {
                    $("#seleccioCount").html(data);
                    $('#backoffice').webutilDatatable('select-none');
                }
            );
        }

        function backofficeAccioMassiu(accio) {
            let doAction = false;

            if (accio === 'provar') {
                doAction = true
            } else if (accio === 'eliminar') {
                doAction = confirm('<spring:message code="backoffice.list.accio.massiva.eliminar"/>')
            }

            if (doAction) {
                $.get(
                    "backoffice/accioMassiva",
                    {accio: accio},
                    function() {
                        window.location.reload();
                    }
                );
            }
        }
	</script>
</head>
<body>

<form:form action="" method="post" cssClass="well" modelAttribute="backofficeFiltreCommand">
    <div class="row">
        <div class="col-md-3">
            <dis:inputText name="codi" inline="true" placeholderKey="backoffice.list.columna.codi"/>
        </div>
        <div class="col-md-3">
            <dis:inputText name="nom" inline="true" placeholderKey="backoffice.list.columna.nom"/>
        </div>
        <div class="col-md-3">
            <dis:inputText name="url" inline="true" placeholderKey="backoffice.list.columna.url"/>
        </div>
        <div class="col-md-3">
            <dis:inputSelect
                    name="tipus"
                    optionEnum="BackofficeTipusEnumDto"
                    emptyOption="true"
                    placeholderKey="backoffice.list.columna.tipus"
                    inline="true"/>
        </div>
    </div>
    <div class="row">
        <div class="col-md-3 d-flex pull-right justify-content-end">
            <button style="display:none" type="submit" name="accio" value="filtrar" ><span class="fa fa-filter"></span></button>
            <button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
            <button type="submit" name="accio" value="filtrar" class="ml-2 btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
        </div>
    </div>
</form:form>

<script id="botonsTemplate" type="text/x-jsrender">
    <div class="text-right">
        <c:if test="${isRolActualAdministrador}">
            <a class="btn btn-default" href="backoffice/new" data-toggle="modal" data-datatable-id="backoffice"><span class="fa fa-plus"></span>&nbsp;<spring:message code="backoffice.boto.nou"/></a>
        </c:if>

        <div class="btn-group">
            <button id="seleccioAll" title="<spring:message code="bustia.pendent.contingut.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></button>
            <button id="seleccioNone" title="<spring:message code="bustia.pendent.contingut.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></button>

            <button class="btn btn-default" data-toggle="dropdown"><span id="seleccioCount" class="badge">${fn:length(seleccio)}</span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
            <ul class="dropdown-menu dropdown-left-medium">
                <li><a href="JavaScript:backofficeAccioMassiu('provar');" data-refresh-pagina="true">
                    <span class="fa fa-cog"></span> <spring:message code="comu.boto.provar"/>
                </a></li>
                <li><a href="JavaScript:backofficeAccioMassiu('eliminar');" data-refresh-pagina="true">
                    <span class="fa fa-trash"></span> <spring:message code="comu.boto.esborrar"/>
                </a></li>
            </ul>
        </div>
    </div>
</script>

	<table
		id="backoffice"
		data-toggle="datatable"
		data-url="<c:url value="/backoffice/datatable"/>"
<%--		data-info-type="search" --%>
		class="table table-striped table-bordered"
        data-botons-template="#botonsTemplate"
        data-selection-enabled="true"
        data-rowhref-toggle="modal"
        data-refresh-tancar="true"
		data-state-save="true"
		data-state-duration="-1">
		<thead>
			<tr>
				<th data-col-name="codi" data-orderable="true"><spring:message code="backoffice.list.columna.codi"/></th>
				<th data-col-name="nom" data-orderable="false"><spring:message code="backoffice.list.columna.nom"/></th>
				<th data-col-name="url" data-orderable="false"><spring:message code="backoffice.list.columna.url"/></th>
				<th data-col-name="tipus" data-orderable="true"><spring:message code="backoffice.list.columna.tipus"/></th>
  				<c:if test="${isRolActualAdministrador}">
					<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<span class="hidden_dis"><spring:message code="comu.boto.accions"/></span>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="backoffice/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="backoffice/{{:id}}/provar"><span class="fa fa-cog"></span>&nbsp;&nbsp;<spring:message code="comu.boto.provar"/></a></li>
								<li><a href="backoffice/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="backoffice.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
					</th>
				</c:if>
			</tr>
		</thead>
	</table>
</body>