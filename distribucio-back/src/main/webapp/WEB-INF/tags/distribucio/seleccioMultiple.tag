<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ attribute name="items" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="itemId" required="true" rtexprvalue="true"%>
<%@ attribute name="itemUrl" required="true" rtexprvalue="true"%>
<%@ attribute name="itemUrlParam1" rtexprvalue="true"%>
<%@ attribute name="itemUrlParam2" rtexprvalue="true"%>
<%@ attribute name="itemKey" required="true" rtexprvalue="true"%>
<%@ attribute name="itemText" required="true" rtexprvalue="true"%>
<%@ attribute name="missatgeHeader" required="true" rtexprvalue="true"%>

<c:set var="nItems">${fn:length(items)}</c:set>

<div id="contingut-missatges"></div>

<div id="items-list" class="panel panel-default" role="tablist">
	<div class="panel-heading">
		<div class="row" role="button" data-toggle="collapse" data-parent="#items-list" data-target="#items-info" aria-expanded="true" aria-controls="items-info">
			<div class="col-sm-3">
				<h3 class="panel-title">
					<span class="state-icon fa"></span>
					<span class="badge seleccioCount">${fn:length(items)}</span> <spring:message code="${missatgeHeader}"/>
				</h3>
			</div>
			<div class="col-sm-7">
				<!-- Barra de progrés -->
				<div id="items-progress" class="progress" style="height: 25px; margin-bottom: 0; display:none;">
					<div id="items-progress-success" class="progress-bar progress-bar-success progress-bar-striped" style="width: 0%">
						<strong><span class="valor text-success"></span></strong>
					</div>
					<div id="items-progress-error" class="progress-bar progress-bar-danger progress-bar-striped" style="width: 0%">
						<strong><span class="valor text-danger"></span></strong>
					</div>
				</div>
			</div>
			<div class="col-sm-2">
				<div class="row">
					<div class="col-sm-8">
						<button type="button" class="btn btn-warning pull-right" id="cancelarBtn" style="visibility:hidden;" title="<spring:message code="registre.user.controller.massiva.cancelar.title"/>">
							<span class="fa fa-stop"></span>&nbsp;
							<span id="cancelarBtnText"><spring:message code="comu.boto.cancelar"/></span>
						</button>
					</div>
					<div class="col-sm-4">
						<span class="fa fa-caret-down pull-right"></span>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="items-info" class="panel-collapse collapse registre-info">

		<table class="table table-striped table-bordered dataTable">
			<thead>
				<tr>
					<th><spring:message code="${missatgeHeader}"></spring:message></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="item" items="${items}">
					<tr id="tr_item_${item[itemId]}" data-id="${item[itemId]}" class='itemTr'>
						<td>[<a href="<c:url value="${itemUrl}/${item[itemUrlParam1]}/${not empty itemUrlParam2 ? item[itemUrlParam2] : ''}"/>" target="blank">${item[itemKey]}</a>] ${item[itemText]}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>    
	</div>
</div>