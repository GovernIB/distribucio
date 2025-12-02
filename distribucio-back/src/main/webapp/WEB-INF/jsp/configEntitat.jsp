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
.highlight {
    background-color: yellow;
    font-weight: bold;
}
.d-none {
	display: none;
}
</style> 

<script>
    const reloadTabs = () => {
        $('.panel-primary').each(function () {
            const $panel = $(this);
            if (hasFilter($panel)) {
                $panel.show();
            } else {
                $panel.hide();
            }
        });

        $('.tab-pane').each(function () {
            const $panel = $(this);
            const key = $panel.attr('id').replace("group-", 'tab-')
            if (hasFilter($panel)) {
                $('#' + key).show();
            } else {
                $('#' + key).hide();
            }
        });
    }

    const hasFilter = ($element, springFilter) => {
        if ($element.hasClass('panel-primary')) {
            return $element.find('.form-update-config')
                .filter(function () { return $(this).css('display') !== 'none' }).length > 0;
        }
        if ($element.hasClass('tab-pane')) {
            return $element.find('.panel-primary')
                .filter(function () { return $(this).css('display') !== 'none' }).length > 0;
        }
        if ($element.hasClass('form-update-config')) {
            if (springFilter === "") {
                return true
            }
            const $label = $element.find('label.control-label')?.first();

            const key = $element.find('input[name="key"]').val()?.toLowerCase();
            const description = $label.text().trim()?.toLowerCase();
            const value = $element.find('input[name="value"]').val()?.toLowerCase();

            const $entitats = $element.find('.entitat-prop');
            if ($entitats != null && $entitats.length > 0) {
                if ($entitats.filter(() => hasFilter($(this), springFilter)).length > 0) {return true}
            }

            const res = key?.includes(springFilter) || description?.includes(springFilter) || value?.includes(springFilter);
            return !!res;
        }
        if ($element.hasClass('entitat-prop')) {
            if (springFilter === "") {
                return true
            }
            const $label = $element.find('label.control-label')?.first();

            const key = $element.find('span.help-block').text().trim()?.toLowerCase();
            const description = $label.text().trim()?.toLowerCase();
            const value = $element.find('input[name="value"]').val()?.toLowerCase();

            const res = key?.includes?.(springFilter) || description?.includes?.(springFilter) || value?.includes?.(springFilter);
            return !!res;
        }
    }

    const marckSearch = ($element, springFilter) => {
        if ($element.hasClass('form-update-config')) {
            const $label = $element.find('label.control-label')?.first();

            const $keyElement = $element.find('.help-block')?.first();
            const key = $keyElement.text().trim()?.toLowerCase();

            if (springFilter === "") {
                $element.show();
                $label.html($label.text());
                $keyElement.html(key);
                return;
            }

            if (hasFilter($element, springFilter)) {
                $element.show();

                const regex = new RegExp('(' + springFilter + ')', 'gi');
                const text = $label.text(); // solo texto plano
                $label.html(text.replace(regex, '<span class="highlight">$1</span>'));
                $keyElement.html(key.replace(regex, '<span class="highlight">$1</span>'));
            } else {
                $element.hide()
            }
        }
        if ($element.hasClass('entitat-prop')) {
            const $label = $element.find('label.control-label')?.first();

            const $keyElement = $element.find('.help-block')?.first();
            const key = $keyElement.text().trim()?.toLowerCase();

            if (springFilter === "" || !hasFilter($element, springFilter)) {
                $label.html($label.text());
                $keyElement.html(key);
            } else {
                const regex = new RegExp('(' + springFilter + ')', 'gi');
                const text = $label.text(); // solo texto plano
                $label.html(text.replace(regex, '<span class="highlight">$1</span>'));
                $keyElement.html(key.replace(regex, '<span class="highlight">$1</span>'));
            }
        }
    }

$(document).ready(function() {
	$("#header").append("<div style='float: right;'><a id='btn-sincronitzar' href='<c:url value='/config/synchronize'/>' class='btn btn-default'><span id='span-refresh-synchronize' class='fa fa-refresh'></span> <spring:message code='config.sync'/></a></div>");
	$("#header").append("<div style='float: right;'><a id='btn-reiniciarTasques' href='<c:url value='/config/reiniciarTasquesSegonPla'/>' class='btn btn-default'><span id='span-refresh-reiniciar-tasques' class='fa fa-refresh'></span> <spring:message code='config.reiniciar.tasques'/></a></div>");
    $("#header").append("<div style='float: right;'><input id='btn-search' class='form-control' title='<spring:message code='comu.boto.filtrar'/>...' placeholder='<spring:message code='comu.boto.filtrar'/>...' type='text'/></div>");
    $("#header h2").append(" - ${entitatDto.nom}");

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
    $('#btn-search').on('input', function() {
        const springFilter = $(this).val().trim().toLowerCase();
        $('.form-update-config').each(function () {
            const $form = $(this);
            marckSearch($form, springFilter)

            $form.find('.entitat-prop').each(function () {
                marckSearch($(this), springFilter)
            })
        });

        reloadTabs();
    })
	
	$(".form-update-config").submit(function(e) {
	    e.preventDefault();
	    let formData = new FormData(this);
	    let key = $('input[name=key]', this).val();
	
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
	            /*var $formElement = $('span:contains("' + key + '")').parent().parent();
	            $formElement.find('button').find('i').removeClass();
	            $formElement.find('button').find('i').addClass('fa fa-edit');
	            $formElement.find('button').blur();*/
	        }
	    });
	});

    reloadTabs();
});

function guardarAnotacionsPendents() {
	var location = window.location.href;
	const locationSplit = location.split(webutilContextPath());
	window.location.href = '<c:url value="/config/reiniciarTasquesSegonPla?currentPage=' + locationSplit[1] + '"/>';		
}


function accioBoto(id, valorGeneric, otherButton) {
	const idSplit = id.split("_");
	var idElementDel = 'del_' + idSplit[1];
	var idElementMod = 'mod_' + idSplit[1];
	var idElementAdd = 'add_' + idSplit[1];
	var configElement = 'config_' + idSplit[1];
	var modIconButton = 'modIconButton_' + idSplit[1];
	var addIconButton = 'addIconButton_' + idSplit[1];
	var modIcon = document.getElementById(modIconButton);
	var addIcon = document.getElementById(addIconButton);
	var inputValor = document.getElementById(configElement).value;
	if (inputValor == '') {	
		modIcon.classList.add('fa-plus');
		document.getElementById(id).classList.add('d-none');
	}else{
		if (idSplit[0] == 'add') {
		addIcon.classList.remove('fa-plus');
		addIcon.classList.add('fa-edit');
		document.getElementById(idElementDel).classList.remove('d-none');
		}else if (idSplit[0] == 'del') {
			var inputType = document.getElementById(configElement).type;
			var inputField = document.getElementById(configElement);
			if (inputType == 'number' || inputType == 'text') {
				inputField.value='';
			}else if (inputType == 'checkbox'){
				if (valorGeneric == 'true'){
					inputField.checked = valorGeneric;					
				}else {
					inputField.checked = null;
				}
			}
			document.getElementById(id).classList.add('d-none');
			if(otherButton == 'otherEdit'){
				modIcon.classList.remove('fa-edit');
				modIcon.classList.add('fa-plus');
			}else if(otherButton == 'otherAdd') {
				addIcon.classList.remove('fa-edit');
				addIcon.classList.add('fa-plus');
			}
			document.getElementById(idElementMod).id = idElementAdd;
			document.getElementById(modIconButton).id = addIconButton;
		}else if (idSplit[0] == 'mod') {
			document.getElementById(idElementDel).classList.remove('d-none');
			addIcon.classList.remove('fa-plus');
			addIcon.classList.add('fa-edit');
			document.getElementById(idElementAdd).id = idElementMod;
			document.getElementById(addIconButton).id = modIconButton;
		}
	}
}
</script>

</head>
<body>

    <div class="row">
        <div class="col-md-3">
            <ul class="nav nav-pills nav-stacked">
                <c:forEach items="${config_groups}" var="group" varStatus="status_group">
                    <li id="tab-${group.key}" role="presentation" class="${status_group.first ? 'active': ''}"><a data-toggle="tab" href="#group-${group.key}">${group.description}</a></li>
                </c:forEach>
            </ul>
        </div>
        <div class="col-md-9">
            <div class="tab-content">
            <c:forEach items="${config_groups}" var="group" varStatus="status_group">
                <c:set var="group" value="${group}" scope="request"/>
                <c:set var="level" value="0" scope="request"/>
                <div id="group-${group.key}" class="tab-pane fade ${status_group.first ? 'active in': ''}">
                    <jsp:include page="configGroupEntitat.jsp"/>
                </div>
            </c:forEach>
            </div>
        </div>
    </div>
</body>
</html>
