<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title><spring:message code="config.titol"/></title>
    <script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
    <script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
    <link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
    <link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<c:if test="${requestLocale == 'en'}">
		<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script> 
	</c:if>
	<script src="<c:url value="/js/select2-locales/select2_${requestLocale}.min.js"/>"></script>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
<%--     <script src="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.min.js"/>"></script> --%>
<%--     <link href="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.css"/>" rel="stylesheet"></link> --%>
    <link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
    <script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <script src="<c:url value="/js/webutil.datatable.js"/>"></script>
    <script src="<c:url value="/js/webutil.modal.js"/>"></script>
<%--     <script src="<c:url value="/js/jquery.fileDownload.js"/>"></script> --%>
    
<style>
.info-block .alert {
	padding: 2px 10px !important;
    margin-bottom: 10px;
    margin-top: -8px;
}
.info-block button.close-alertes {
    background: none repeat scroll 0 0 transparent;
    border: 0 none;
    cursor: pointer;
    padding: 0;
}
.info-block .close-alertes {
    color: #000000;
    float: right;
    font-weight: bold;
    opacity: 0.2;
    text-shadow: 0 1px 0 #FFFFFF;
}
</style> 

<script>


let getValueRadio = elem => {
    let inputs = $(elem).find("input");
    if (!inputs || (inputs && inputs.length !== 2)) {
        return null;
    }
    return $(inputs[0]).is(":checked") ? inputs[0].value : $(inputs[1]).is(":checked") ? inputs[1].value : null;
};
let removeValueRadio = elem => $(elem).find('input:radio').attr("checked", false);
let addSpinner = id => {
    let spinner;
    if (!document.getElementById(id + "_spinner")) {
        spinner = document.createElement("span");
        spinner.setAttribute("aria-hidden", true);
        spinner.className = "fa fa-circle-o-notch fa-spin fa-1x spinner-config";
        spinner.setAttribute("id", id + "_spinner");
        console.log(id);
        let elem = document.getElementById(id + "_key");
        elem.append(spinner);
    }
    return spinner;
};
let removeSpinner = spinner =>  {
    if (spinner) {
        spinner.remove();
    }
};
let addSeparador = tag => $(tag).closest(".form-group").addClass("separador");
let removeSeparador = tag => $(tag).closest(".form-group").removeClass("separador");
let mostrarMissatge = (id, data) => {
    let elem = document.getElementById(id);
    elem = !elem ? document.getElementById(id + "_1") : elem;
    let tagId = elem.getAttribute("id") + "_msg";
    let msg = document.getElementById(tagId);
    if (msg) {
        let el = document.getElementById(msg);
        if (el) {
            el.remove();
        }
    }
    let div = document.createElement("div");
    div.setAttribute("id", tagId);
    div.className = "flex-space-between alert-config " +  (data.status === 1 ?  "alert-config-ok" : "alert-config-error");
    div.append(data.message);
    let span = document.createElement("span");
    span.className = "fa fa-times alert-config-boto";
    div.append(span);
    elem.closest(".col-sm-8").append(div);
    span.addEventListener("click", () => div.remove());
    window.setTimeout(() => div ? div.remove() : "", data.status === 1 ? 2250 : 4250);
};
let getInputValue = elem =>  ($(elem).is(':checkbox') ? $(elem).is(":checked") : $(elem).is("div") ? getValueRadio(elem) : $(elem).val());
let guardarPropietat = (configKey, natejar) => {
    let configKeyReplaced = configKey.replaceAll("_",".");
    let spinner = addSpinner(configKey);
    let elem = $("#" + configKey);
    let value = !natejar ? getInputValue(elem) : null;
    let formData = new FormData();
    formData.append("key", configKeyReplaced);
    formData.append("value", value);
    $.ajax({
        url: "/distribucio/config/update",
        type: "post",
        processData: false,
        contentType: false,
        enctype: "multipart/form-data",
        data: formData,
        success: data => {
            removeSpinner(spinner);
            afegirCssSiValueNull(elem, value);
            mostrarMissatge(configKey + "_key", data);
        }
    });
};

$(document).ready(function() { 
	$("#header").append("<div style='float: right;'><a id='btn-sincronitzar' href='<c:url value='/config/synchronize'/>' class='btn btn-default'><span id='span-refresh-synchronize' class='fa fa-refresh'></span> <spring:message code='config.sync'/></a></div>");
	$("#header").append("<div style='float: right;'><a id='btn-reiniciarTasques' href='<c:url value='/config/reiniciarTasquesSegonPla'/>' class='btn btn-default'><span id='span-refresh-reiniciar-tasques' class='fa fa-refresh'></span> <spring:message code='config.reiniciar.tasques'/></a></div>");

	$('#btn-reiniciarTasques').click(function(e) {
		$("#btn-reiniciarTasques #span-refresh-reiniciar-tasques").addClass('fa-circle-o-notch');
		$("#btn-reiniciarTasques #span-refresh-reiniciar-tasques").addClass('fa-spin');
		$("#btn-reiniciarTasques").addClass('disabled');
		$("#btn-reiniciarTasques").css("pointer-events", "none");
	});
	$('#btn-sincronitzar').click(function(e) {
		$("#btn-sincronitzar #span-refresh-synchronize").addClass('fa-circle-o-notch');
		$("#btn-sincronitzar #span-refresh-synchronize").addClass('fa-spin');
		$("#btn-sincronitzar").addClass('disabled');
		$("#btn-sincronitzar").css("pointer-events", "none");
	});
	
	$(".form-update-config").submit(function(e) {
	    e.preventDefault();
	    let formData = new FormData(this);
	    let key = $('input[name=key]', this).val();
	
	    //$(this).find('button').find('i').removeClass();
	    //$(this).find('button').find('i').addClass('fa fa-circle-o-notch fa-spin');
	    $(this).find('.info-block').empty();
	    $(this).find('.form-group').removeClass('has-success');
	    $(this).find('.form-group').removeClass('has-error');

	    $.ajax({
	        url: '<c:url value="/config/update"/>',
	        type: 'post',
	        processData: false,
	        contentType: false,
	        enctype: 'multipart/form-data',
	        data: formData,
	        success: function(json) {
	            var $formElement = $('span:contains("' + key + '")').parent().parent();
	            if (json.error == true) {
		              $formElement.find('.info-block').append('<div class="alert alert-danger"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>' + json.errorMsg + '</div>');
		              $formElement.addClass('has-error');
				} else {
		              $formElement.find('.info-block').append('<div class="alert alert-success"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>' + "La propietat s'ha editat correctament" + '</div>');
		              $formElement.addClass('has-success');
				}
	        },
	        error: function(xhr, ajaxOptions, thrownError) {
	        	console.error("Error no controlat: " + xhr );
	            var $formElement = $('span:contains("' + key + '")').parent().parent();
	            $formElement.find('.info-block').append('<div class="alert alert-danger"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>Error: ' + xhr.status + ' ' + xhr.responseText + '</div>');
	            $formElement.addClass('has-error');
	        },
	        complete: function() {
	            var $formElement = $('span:contains("' + key + '")').parent().parent();
	            $formElement.find('button').find('i').removeClass();
	            $formElement.find('button').find('i').addClass('fa fa-edit');
	            $formElement.find('button').blur();
	        }
	    });
	});
});

<%
pageContext.setAttribute(
        "isRolActualAdministrador",
        es.caib.distribucio.back.helper.RolHelper.isRolActualAdministrador(request));
%>
</script>

</head>
<body>

	<div class="text-right" data-toggle="botons-titol">
		<c:if test="${isRolActualAdministrador}">
        <a class="btn btn-default" href="<c:url value="/entitat"/>" data-datatable-id="permisos"><span class="fa fa-reply"></span>&nbsp;<spring:message code="entitat.permis.list.boto.tornar"/></a>
    </c:if>
	</div>
	<div id="syncModal" class="modal fade" role="dialog">
	    <div class="modal-dialog">
	
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">Sincronitzant propietats</h4>
	            </div>
	            <div id="syncModal-body" class="modal-body">
	            </div>
	            <div class="modal-footer">
	                <button type="button" class="btn btn-default" data-dismiss="modal">Tanca</button>
	            </div>
	        </div>
	    </div>
	</div>



    <div class="row">
        <div class="col-md-3">
            <ul class="nav nav-pills nav-stacked">
                <c:forEach items="${config_groups}" var="group" varStatus="status_group">
                    <li role="presentation" class="${status_group.first ? 'active': ''}"><a data-toggle="tab" href="#group-${group.key}">${group.description}</a></li>
                </c:forEach>
            </ul>
        </div>
        <div class="col-md-9">
            <div class="tab-content">
            <c:forEach items="${config_groups}" var="group" varStatus="status_group">
                <c:set var="group" value="${group}" scope="request"/>
                <c:set var="level" value="0" scope="request"/>
                <div id="group-${group.key}" class="tab-pane fade ${status_group.first ? 'active in': ''}">
                    
                    <jsp:include page="configEntitatGroup.jsp"/>
                </div>
            </c:forEach>
            </div>
        </div>
    </div>
</body>
</html>
