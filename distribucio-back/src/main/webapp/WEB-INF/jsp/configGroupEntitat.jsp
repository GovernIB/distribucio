<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h${level + 4}>${ group.description }</h${level + 4}>
    </div>
    <div class="panel-body">
        <c:forEach items="${ llistatPropietats }" var="config" varStatus="status_group">
          <c:if test="${group.key == config.groupCode }">
            <c:set var = "configKey" value = "${fn:replace(config.key,'.','_')}"/>
            <c:url var="urlEdit" value="/config/update"/>
            <form:form id="filtre"  method="post" cssClass="form-update-config form-horizontal" modelAttribute="config_${configKey}">
                <form:hidden path="key"/>
                <div class="form-group">
                    <label for="config_${config.key}" class="col-sm-3 control-label" style="word-wrap: break-word;">${ config.description }</label>
                    <div class="col-sm-8">
                        <c:choose>
                            <c:when test="${config.typeCode == 'INT'}">
                            	<c:choose>
	                              <c:when test="${fn:contains(config.value, '////') }">
	                                <form:input  id="config_${config.key}" cssClass="form-control" path="value" placeholder="${fn:replace(config.value, '////', '')} "
	                                             type="number" maxlength="2048" disabled="${config.jbossProperty}" />                                           
	                              </c:when>
	                              <c:otherwise>
	                              	<c:set var="entitat" value="${config.entitatCodi}." scope="session" />
	                              	<c:set var="placeholder" value="${fn:replace(config.key, entitat, '')}" scope="session" />
	                                <form:input  id="config_${config.key}" cssClass="form-control" path="value" placeholder="${valorsDefault[placeholder]}"
	                                             type="number" maxlength="2048" disabled="${config.jbossProperty}" value="${config.value}"/>
		                          </c:otherwise>
	                          	</c:choose>
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
                            	<c:choose>
	                            	<c:when test="${fn:contains(config.value, '////') }">
		                            	<c:set var="valorGeneric" value="${fn:replace(config.value, '////', '')}" scope="session" />
		                            	<c:choose>
		                            		<c:when test="${valorGeneric == 'true '}">
			                                	<label>
				                                	<form:checkbox path="booleanValue" id="config_${config.key}" cssClass="visualitzar"
				                                                   disabled="${config.jbossProperty}" checked="true"/>
				                                </label>
			                                </c:when>
			                                <c:otherwise>
			                                	<label>
				                                	<form:checkbox path="booleanValue" id="config_${config.key}" cssClass="visualitzar"
				                                                   disabled="${config.jbossProperty}"/>
				                                </label>
			                                </c:otherwise>
		                                </c:choose>
	                            	</c:when>
	                            	<c:otherwise>
		                            	<c:set var="valorGeneric" value="${config.value}" scope="session" />
		                            	<c:choose>
		                            		<c:when test="${valorGeneric == 'true '}">
			                                	<label>
				                                	<form:checkbox path="booleanValue" id="config_${config.key}" cssClass="visualitzar"
				                                                   disabled="${config.jbossProperty}" checked="true"/>
				                                </label>
			                                </c:when>
			                                <c:otherwise>
			                                	<label>
				                                	<form:checkbox path="booleanValue" id="config_${config.key}" cssClass="visualitzar"
				                                                   disabled="${config.jbossProperty}"/>
				                                </label>
			                                </c:otherwise>
		                                </c:choose>
	                                </c:otherwise>
                                </c:choose>
                            </div>
                            </c:when>
                            <c:when test="${config.validValues != null and fn:length(config.validValues) > 0}">
                                <form:select path="value" cssClass="form-control" id="config_${config.key}" disabled="${config.jbossProperty}" style="width:100%" data-toggle="select2"
                                             data-placeholder="${config.description}">
                                    <c:forEach var="opt" items="${config.validValues}">
                                        <form:option value="${opt}"/>
                                    </c:forEach>
                                </form:select>
                            </c:when>
                            <c:otherwise>
                            	<c:choose>
	                              <c:when test="${fn:contains(config.value, '////') }">
	                                <form:input  id="config_${config.key}" cssClass="form-control" path="value" placeholder="${fn:replace(config.value, '////', '')} "
	                                             type="text" maxlength="2048" disabled="${config.jbossProperty}" />                                           
	                              </c:when>
	                              <c:otherwise>
	                              	<c:set var="entitat" value="${config.entitatCodi}." scope="session" />
	                              	<c:set var="placeholder" value="${fn:replace(config.key, entitat, '')}" scope="session" />
	                                <form:input  id="config_${config.key}" cssClass="form-control" path="value" placeholder="${valorsDefault[placeholder]}"
	                                             type="text" maxlength="2048" disabled="${config.jbossProperty}" value="${config.value}"/>
		                          </c:otherwise>
	                          	</c:choose>
                            </c:otherwise>
                        </c:choose>
                        <span class="help-block">${config.key}</span>
                        <span class="info-block"></span>
                    </div>
                    <div class="/*col-sm-1*/ d-flex">
                        <c:set var="entitat" value="${config.entitatCodi}." scope="session" />
                        <c:set var="valorGeneric" value="${fn:replace(config.key, entitat, '')}" scope="session" />
                    	<c:choose>
                    		<c:when test="${fn:contains(config.value, '////') }">
		                        <button id="add_${config.key}" onclick="accioBoto('add_${config.key}', '${valorsDefault[valorGeneric]}', null)" class="btn btn-success"<c:if test="${config.jbossProperty}"> disabled</c:if>>
		                            <i id="addIconButton_${config.key}" class="fa fa-plus"></i>
		                        </button>
		                        <button id="del_${config.key}" onclick="accioBoto('del_${config.key}', '${valorsDefault[valorGeneric]}', 'otherAdd')" class="btn btn-danger d-none"<c:if test="${config.jbossProperty}"> disabled</c:if>>
		                            <i id="delIconButton_${config.key}" class="fa fa-trash-o"></i>
		                        </button>
                    		</c:when>
                    		<c:otherwise>
		                        <button id="mod_${config.key}" onclick="accioBoto('mod_${config.key}', '${valorsDefault[valorGeneric]}', null)" class="btn btn-success"<c:if test="${config.jbossProperty}"> disabled</c:if>>
		                            <i id="modIconButton_${config.key}" class="fa fa-edit"></i>
		                        </button>
		                        <button id="del_${config.key}" onclick="accioBoto('del_${config.key}', '${valorsDefault[valorGeneric]}', 'otherEdit')" class="btn btn-danger"<c:if test="${config.jbossProperty}"> disabled</c:if>>
		                            <i id="delIconButton_${config.key}" class="fa fa-trash-o"></i>
		                        </button>
	                        </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </form:form> 
          </c:if>    
        </c:forEach>

        <c:set var="level" value="${level + 1}" scope="request"/>
        <c:forEach items="${ group.innerConfigs }" var="group" varStatus="status_group">
            <c:set var="group" value="${group}" scope="request"/>
            <jsp:include page="configGroupEntitat.jsp"/>
            <c:set var="level" value="${level - 1}" scope="request"/>
        </c:forEach>
    </div>
</div>
