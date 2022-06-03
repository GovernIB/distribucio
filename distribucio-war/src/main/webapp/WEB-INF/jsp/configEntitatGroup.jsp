<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<script type="text/javascript">
    $(document).ready(() => {
        $(".entitats").unbind("click").click(e => {
            e.stopPropagation();
            let div = $(e.currentTarget).parent().parent().next();
            div.empty();
            let span = $(e.currentTarget).find("span");
            if ($(div).is(":visible")) {
                span.removeClass("fa-caret-up");
                span.addClass("fa-caret-down");
                addSeparador(e.target);
                div.toggle();
                return;
            }
            $.ajax({
                type: "GET",
                url: "config/entitat/" + e.currentTarget.id.replace(/\./g,"-"),
                success: entitats => {
                    if (!entitats) {
                        return;
                    }
                    removeSeparador(e.currentTarget);
                    div.toggle();
                    if ($(div).is(":visible")) {
                        span.removeClass("fa-caret-down");
                        span.addClass("fa-caret-up");
                    }
                    for (let entitat of entitats) {
                        let keyReplaced = entitat.key.replaceAll('.', '_');
                        let string = '<div>';                        
                        let disabled = false;//entitat.jbossProperty || !entitat.value ? 'disabled' : '';
                        let textGray = disabled ? "text-gray" : "";
                        string += '<label id="' + keyReplaced+ '_codi_entitat" for="entitat_config_' + keyReplaced + '" class="col-sm-3 control-label margin-bottom ' + textGray + '" style="word-wrap: break-word;">- ' + entitat.entitatCodi + '</label>';
                        string += '<div class="col-sm-8 margin-bottom">';
                        let placeHolder = "placeholder=" + entitat.key;
                        if (entitat.typeCode === "INT") {
                            string += '<input id="' + keyReplaced + '" class="form-control entitat-input" type="number" maxlength="2048" value="' + entitat.value + '"' + disabled + ' ' + placeHolder + '>';
                        } else if(entitat.typeCode === "FLOAT") {
                            string += '<input id="' + keyReplaced + '" class="form-control entitat-input" type="number" step="0.01" maxlength="2048" value="' + entitat.value + '"' + disabled + ' ' + placeHolder + '>';
                        } else if(entitat.typeCode === "CREDENTIALS") {
                            string += '<input id="' + keyReplaced + '" class="form-control entitat-input" type="password" maxlength="2048" value="' + entitat.value + '"' + disabled + ' ' + placeHolder + '>';
                        } else if(entitat.typeCode === "BOOL") {
                            let checked = entitat.value === "true" ? 'checked' : '';
                            string += '<input id="' + keyReplaced + '" name="booleanValue" class="visualitzar entitat-input" type="checkbox" ' + disabled + ' ' + checked + '>';
                        } else if (entitat.validValues && entitat.validValues.length > 2) {
                            string += '<select id="' + keyReplaced + '" class="form-control ' + disabled + '>';
                            let selected = "";
                            string += '<option value=""></option>';
                            entitat.validValues.map(x => {
                                selected = x === entitat.value ? "selected" : "";
                                string += '<option value="' + x + '"' + ' ' + selected + '>' + x + '</option>';
                            });
                            string += '<select>';
                        } else if (entitat.validValues && entitat.validValues.length === 2) {
                            let checked = entitat.validValues[0] === entitat.value ? 'checked="checked"' : "";
                            let checked2 = entitat.validValues[1] === entitat.value ? 'checked="checked"' : "";
                            string += '<div id="' + keyReplaced + '" class="visualitzar entitat-input radio-div">'
                            	+ '<label id="' + keyReplaced+ '_radio_1" for="' + keyReplaced + '_1" class="radio-inline ' + textGray + '">'
                                + '<input id="' + keyReplaced + '_1" name="' + keyReplaced + '" type=radio value="' + entitat.validValues[0] + '"' + ' ' + checked + ' ' + disabled + '>'
                                + entitat.validValues[0]
                                + '</label>'
                                + '<label id="' + keyReplaced+ '_radio_2" for="' + keyReplaced+ '_2" class="radio-inline ' + textGray + '">'
                                + '<input id="' + keyReplaced + '_2" name="' + keyReplaced + '" type=radio value="' + entitat.validValues[1] + '"' + ' ' + checked2 + ' ' + disabled + '>'
                                + entitat.validValues[1]
                                + '</label></div>';
                        } else {
                        	string += '<input id="' + keyReplaced + '" class="form-control" type="text" maxlength="2048" value="'
                                + (entitat.value ? entitat.value : "" )+ '"' + disabled + ' ' + placeHolder + '>';
                        }
                        string +='<div><div id="'+ keyReplaced + '_key" class="display-inline"><span id="' + keyReplaced+ '_key_entitat" class="help-block display-inline ' + textGray + '"> ' + entitat.key + '</span></div>';
                        string += '<span id="info_block_' + keyReplaced + '" class="info-block display-inline ' + textGray + '"></span></div></div>'
                        string += '<div class="col-sm-1 margin-bottom flex-space-between d-flex">';
                        if (!entitat.jbossProperty) {
                        	let saveDelete = entitat.value ? "" : "no-display";
                            let config = !entitat.value ? "" : "no-display";
                            string += '<button onclick="accioBoto(' + keyReplaced + ', this.name)" id="' + keyReplaced + '_button_save" name="save" type="button" class="btn btn-success entitat-save ' + saveDelete + '"><i class="fa fa-save"></i></button>';
                            string += '<button onclick="accioBoto(' + keyReplaced + ', this.name)" id="' + keyReplaced + '_button_trash" name="trash" type="button" class="btn btn-danger entitat-trash ml-1 ' + saveDelete + '"><i class="fa fa-trash"></i></button>';
                            string += '<button onclick="accioBoto(' + keyReplaced + ', this.name)" id="' + keyReplaced + '_button_config" name="edit" type="button" class="btn btn-success entitat-config ' + config + '"><i class="fa fa-pencil"></i></button>';
                        }
                        string += '</div></div>';
                        div.append(string);
                    }
                    $("select", div).select2({
                        theme: "bootstrap",
                        allowClear: true,
                        minimumResultsForSearch: -1,
                        placeholder: ""
                    });
                    /*$(".entitat-save").unbind("click").click(e =>  {
                        let configKey = e.currentTarget.id.replace("_button_save", "");
                        guardarPropietat(configKey);
                    });
                    $(".entitat-trash").unbind("click").click(e => {
                        let configKey = e.currentTarget.id.replace("_button_trash", "");
                        let tag = $("#" + configKey);
                        if ($(tag).hasClass("radio-div")) {
                            $("#" + configKey + "_1").attr("disabled", true);
                            $("#" + configKey + "_2").attr("disabled", true);
                            $("#" + configKey + "_radio_1").addClass("text-gray");
                            $("#" + configKey + "_radio_2").addClass("text-gray");
                        }
                        $("#" + configKey + "_button_trash").addClass("no-display");
                        $("#" + configKey + "_button_save").addClass("no-display");
                        $("#" + configKey + "_button_config").removeClass("no-display");
                        $("#" + configKey + "_key_entitat").addClass("text-gray");
                        $("#" + configKey + "_codi_entitat").addClass("text-gray");
                        let elem = $("#" + configKey);
                        if (elem.is(':checkbox')) {
                            $(elem).prop("checked", false);
                        } else if ($(elem).is("div") ) {
                            removeValueRadio(elem);
                        } else if ($(elem).is("select")) {
                        	$(elem).val("").trigger("change");
                        } else {
                            elem.val("");
                        }
                        if (!$(elem).hasClass("radio-div")) {
                            $(elem).attr("disabled", true);
                        }
                        guardarPropietat(configKey, true);
                    });
                    
                    $(".entitat-config").unbind("click").click(e => {
                        let current = e.currentTarget;
                        let tag = $("#" + current.id.replace("_button_config", ""));
                        if ($(tag).hasClass("radio-div")) {
                            $("#" + current.id.replace("_button_config", "_1")).first().removeAttr("disabled");
                            $("#" + current.id.replace("_button_config", "_2")).first().removeAttr("disabled");
                            $("#" + current.id.replace("_button_config", "_radio_1")).removeClass("text-gray");
                            $("#" + current.id.replace("_button_config", "_radio_2")).removeClass("text-gray");
                        } else {
                            $("#" + current.id.replace("_button_config", "")).removeAttr("disabled");
                        }
                        $(current).addClass("no-display");
                        $("#" + current.id.replace("_button_config", "_button_save")).removeClass("no-display");
                        $("#" + current.id.replace("_button_config", "_button_trash")).removeClass("no-display");
                        $("#" + current.id.replace("_button_config", "_key_entitat")).removeClass("text-gray");
                        $("#" + current.id.replace("_button_config", "_codi_entitat")).removeClass("text-gray");
                    });*/
                }
            });
        });
    });
    
    function accioBoto(keyReplaced, nameEvent) {
    	$('.close-alertes').click();
		var iconSave = '#' + keyReplaced.id + '_button_save i';
		$(iconSave).addClass('fa-circle-o-notch');
		$(iconSave).addClass('fa-spin');
		var iconConfig = '#' + keyReplaced.id + '_button_config i';
		$(iconConfig).addClass('fa-circle-o-notch');
		$(iconConfig).addClass('fa-spin');
    	var configKey = keyReplaced.id.replaceAll('_', '.');
    	var inputEvent = document.getElementById(keyReplaced.id);
    	var buttonTrash = document.getElementById(keyReplaced.id + '_button_trash');
    	var buttonSave = document.getElementById(keyReplaced.id + '_button_save');
    	var buttonEdit = document.getElementById(keyReplaced.id + '_button_config');
		var stringClassInfo = 'info_block_' + keyReplaced.id;
    	if (nameEvent == 'trash') {
    		if (inputEvent.type == 'text' 
    		|| inputEvent.type == 'password' 
    		|| inputEvent.type == 'number') {
    		inputEvent.value = "";
	    	}else if (inputEvent.type == 'checkbox') {
	    		inputEvent.checked = null;
	    	}
    		buttonTrash.classList.remove('d-block');
    		buttonTrash.classList.add('d-none');
    	}
        let formData = new FormData();
        formData.append("key", configKey);
        if (inputEvent.type == 'checkbox'){
        	formData.append("value", inputEvent.checked);
        }else {
        	formData.append("value", inputEvent.value);
    	}
    	$.ajax({
	        url: '<c:url value="/config/update"/>',
	        type: 'post',
	        processData: false,
	        contentType: false,
	        enctype: 'multipart/form-data',
	        data: formData,
	        success: function(json) {
	            var $formElement = $('span:contains("' + key + '")').parent().parent();
	            var info_block = document.getElementById(stringClassInfo);
	            if (json.error == true) {
		            info_block.innerHTML = '<div class="alert alert-danger"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>' + json.errorMsg + '</div>';
		      		$(iconSave).removeClass('fa-circle-o-notch');
		    		$(iconSave).removeClass('fa-spin');
		    		$(iconConfig).removeClass('fa-circle-o-notch');
		    		$(iconConfig).removeClass('fa-spin');
				} else {
		            info_block.innerHTML = '<div class="alert alert-success"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>' + "La propietat s'ha editat correctament" + '</div>';
		      		$(iconSave).removeClass('fa-circle-o-notch');
		    		$(iconSave).removeClass('fa-spin');
		    		$(iconConfig).removeClass('fa-circle-o-notch');
		    		$(iconConfig).removeClass('fa-spin');
		    		if (nameEvent == 'trash') {
		        		$(iconConfig).removeClass('fa-save');
		        		$(iconConfig).addClass('fa-pencil');
		        		$(iconSave).removeClass('fa-save');
		        		$(iconSave).addClass('fa-pencil');
		    		} else if (nameEvent == 'save'){
		        		$(iconSave).removeClass('fa-pencil');
		        		$(iconSave).addClass('fa-save');
		        		buttonTrash.classList.remove('d-none');
		        		buttonTrash.classList.add('d-block');
		        	} else if (nameEvent == 'edit') {
		        		$(iconConfig).removeClass('fa-pencil');
		        		$(iconConfig).addClass('fa-save');
		        		buttonTrash.classList.remove('d-none');
		        		buttonTrash.classList.add('d-block');
		        	}
				}
	        },
	        error: function(xhr, ajaxOptions, thrownError) {
	        	console.error("Error no controlat: " + xhr );
	            var info_block = document.getElementById(stringClassInfo);
	            var $formElement = $('span:contains("' + key + '")').parent().parent();
	            info_block.innerHTML = '<div class="alert alert-danger"><button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>Error: ' + xhr.status + ' ' + xhr.responseText + '</div>';
	        }/*,
	        complete: function() {
	            var $formElement = $('span:contains("' + key + '")').parent().parent();
	            $formElement.find('button').find('i').removeClass();
	            $formElement.find('button').find('i').addClass('fa fa-edit');
	            $formElement.find('button').blur();
	        }*/
	    });
    }
</script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h${level + 4}>${ group.description }</h${level + 4}>
    </div>
    <div class="panel-body">
        <c:forEach items="${ group.configs }" var="config" varStatus="status_group">
            <c:set var = "configKey" value = "${fn:replace(config.key,'.','_')}"/>            
            <form:form id="filtre"  method="post" cssClass="form-update-config form-horizontal" action="config/update" commandName="config_${configKey}">
                <form:hidden path="key"/>
                <c:if test="${config.entitatCodi == null}">
                <div class="form-group separador padding-bottom-4">
                    <label for="config_${config.key}" class="col-sm-3 control-label" style="word-wrap: break-word;">${ config.description }</label>
                    <div class="col-sm-8">
                        <c:choose>
                            <c:when test="${config.typeCode == 'INT'}">
                                <form:input  id="config_${config.key}" cssClass="form-control" path="value" placeholder="${config.key}"
                                             type="number" maxlength="2048" disabled="${config.jbossProperty}"/>
                            </c:when>
                            <c:when test="${config.typeCode == 'FLOAT'}">
                                <form:input  id="config_${config.key}" cssClass="form-control" path="value" placeholder="${config.key}"
                                             type="number" step="0.01" maxlength="2048" disabled="${config.jbossProperty}"/>
                            </c:when>
                            <c:when test="${config.typeCode == 'CREDENTIALS'}">
                                <form:input  id="config_${config.key}" cssClass="form-control" path="value" placeholder="${config.key}"
                                             type="password" maxlength="2048" disabled="${config.jbossProperty}"/>
                            </c:when>
                            <c:when test="${config.typeCode == 'BOOL'}">
                            <div class="checkbox checkbox-primary">
                                <label>
                                <form:checkbox path="booleanValue" id="config_${config.key}" cssClass="visualitzar"
                                                   disabled="${config.jbossProperty}"/>
                                </label>
                            </div>
                            </c:when>
                            <c:when test="${config.validValues != null and fn:length(config.validValues) > 2}">
                                <form:select path="value" cssClass="form-control" id="config_${config.key}" disabled="${config.jbossProperty}" style="width:100%" data-toggle="select2"
                                             data-placeholder="${config.description}">
                                    <c:forEach var="opt" items="${config.validValues}">
                                        <form:option value="${opt}"/>
                                    </c:forEach>
                                </form:select>
                            </c:when>
                            <c:when test="${config.validValues != null and fn:length(config.validValues) == 2}">
                                <label id="config_${config.key}_1" class="radio-inline">
                                    <form:radiobutton path="value" value="${config.validValues[0]}"/> ${config.validValues[0]}
                                </label>
                                <label id="config_${config.key}_2" class="radio-inline">
                                    <form:radiobutton path="value" value="${config.validValues[1]}"/> ${config.validValues[1]}
                                </label>
                            </c:when>
                            <c:otherwise>
                                <form:input  id="config_${config.key}" cssClass="form-control" path="value" placeholder="${config.key}"
                                             type="text" maxlength="2048" disabled="${config.jbossProperty}"/>
                            </c:otherwise>
                        </c:choose>
                        <div id="config_${config.key}_key">
                        	<span class="help-block display-inline">${config.key}</span>
                        	<span class="info-block_${config.key}"></span>
                        </div>
                    </div>
                    <div class="/*col-sm-1*/ d-flex">
                        <c:if test="${not config.jbossProperty}">
                            <button class="btn btn-success"><i class="fa fa-save"></i></button>
                        </c:if>
                        <c:if test="${config.configurable}">
                            <div class="btn btn-default btn-sm btn-rowInfo entitats" id="${config.key}"><span class="fa fa-caret-down"></span></div>
                        </c:if>
                    </div>
                </div>
                <div class="form-group entitats-config separador"></div>
            	</c:if>
            </form:form>
        </c:forEach>

        <c:set var="level" value="${level + 1}" scope="request"/>
        <c:forEach items="${ group.innerConfigs }" var="group" varStatus="status_group">
            <c:set var="group" value="${group}" scope="request"/>
            <jsp:include page="configEntitatGroup.jsp"/>
            <c:set var="level" value="${level - 1}" scope="request"/>
        </c:forEach>
    </div>
</div>
