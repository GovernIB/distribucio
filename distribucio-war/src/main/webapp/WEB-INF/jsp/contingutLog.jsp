<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="titol"><spring:message code="contingut.log.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<dis:modalHead/>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script type="text/javascript">
var logTipusEnumText = [];
<c:forEach var="logTipus" items="${logTipusEnumOptions}">
logTipusEnumText["${logTipus.value}"] = "<spring:message code="${logTipus.text}"/>";
</c:forEach>
var logObjecteTipusEnumText = [];
<c:forEach var="logObjecteTipus" items="${logObjecteTipusEnumOptions}">
logObjecteTipusEnumText["${logObjecteTipus.value}"] = "<spring:message code="${logObjecteTipus.text}"/>";
</c:forEach>
$(document).ready(function() {
	var hash = window.location.hash;
    if (hash) {
        $('[data-toggle="tab"][href="' + hash + '"]').trigger('click');
    }
	$('button.log-detalls').click(function() {
		if ($('span', this).hasClass('fa-chevron-down')) {
			var $row = $(this).closest('tr');
			$.get(	'log/' + $(this).data('log-id') + '/detalls',
					function(data) {
						$row.after('<tr><td colspan="4"></td></tr>')
						$newTd = $('td', $row.next());
						$newTd.html($('#log-info-detall').html());
						
						if (data.params && data.params.length > 0) {
							for (var i = 0; i < data.params.length; i++) {
							    $('.table-params', $newTd).append("<tr><td class='log-info-param1-titol' width='20%'><b><spring:message code='contingut.log.detall.param'/> "+ (i + 1) +"</b></td><td class='log-info-param1-valor' width='30%'>" + data.params[i] + "</td></tr>");
							}
						} else {
							$('div.log-info-params', $newTd).remove();
						}
						
						if (data.pare != null) {
							$('td.log-info-accio-data', $newTd).text(data.pare.createdDateAmbFormat);
							$('td.log-info-accio-usuari', $newTd).text(data.pare.createdBy.nom);
							$('td.log-info-accio-tipus', $newTd).text(logTipusEnumText[data.pare.tipus]);
							$('td.log-info-accio-objecte', $newTd).text("[" + logObjecteTipusEnumText[data.objecteTipus] + "#" + data.objecteId + "] " + data.objecteNom);

							if (data.params !== null) {
								for (var i = 0; i < data.params.length; i++) {
								    $('.table-accio', $newTd).append("<tr><td class='log-info-param1-titol' width='20%'><b><spring:message code='contingut.log.detall.param'/></b></td><td class='log-info-param1-valor' width='30%'>" + data.params[i] + "</td></tr>");
								}
							} else {
								$('div.log-info-params', $newTd).remove();
							}
							
							if (data.param1 !== null || data.param2 !== null) {
								if (data.pare.param1 !== null)
									$('td.log-info-accio-param1-valor', $newTd).text(data.pare.param1);
								if (data.pare.param2 !== null)
									$('td.log-info-accio-param2-valor', $newTd).text(data.pare.param2);
							} else {
								$('td.log-info-accio-param2-valor', $newTd).closest('tr').remove();
							}
						} else {
							$('div.log-info-accio', $newTd).remove();
						}
						if (data.contingutMoviment != null) {
							if (data.contingutMoviment.origenId != null) {
								$('td.log-info-moviment-origen', $newTd).text("[#" + data.contingutMoviment.origenId + "] " + data.contingutMoviment.origenNom);
							}
							$('td.log-info-moviment-desti', $newTd).text("[#" + data.contingutMoviment.destiId + "] " + data.contingutMoviment.destiNom);
						} else {
							$('div.log-info-moviment', $newTd).remove();
						}
					});
			$('span', this).removeClass('fa-chevron-down');
			$('span', this).addClass('fa-chevron-up');
		} else {
			var $row = $(this).closest('tr').next().remove();
			$('span', this).removeClass('fa-chevron-up');
			$('span', this).addClass('fa-chevron-down');
		}
		webutilModalAdjustHeight();
	});
});
</script>
</head>
<body>

	<!------------------------------------ TABLIST --------------------------------------->
	<ul class="nav nav-tabs">
	<c:if test="${isPanelUser}">
		<li class="active">
			<a data-toggle="tab" href="#resum">
				<spring:message code="comu.boto.resum"/>
				<span class="badge">${fn:length(logsDetall)}</span>
			</a>
		</li>	
	</c:if>
		<li ${!isPanelUser ? "class=\"active\"" : ""}>
			<a data-toggle="tab" href="#accions">
				<spring:message code="comu.boto.accions"/>
				<span class="badge">${fn:length(logs)}</span>
			</a>
		</li>
		<li>
			<a data-toggle="tab" href="#moviments">
				<spring:message code="comu.boto.moviments"/>
				<span class="badge">${fn:length(moviments)}</span>
			</a>
		</li>
		<li>
			<a data-toggle="tab" href="#auditoria">
				<spring:message code="contingut.log.pipella.auditoria"/>
			</a>
		</li>
	</ul>
	<br/>
	<div class="tab-content">
	
		<!------------------------------------ TABPANEL RESUM --------------------------------------->
		<c:if test="${isPanelUser}">
			<div class="tab-pane active in" id="resum">
				<c:set var="isVistaMoviments" value="${cookie['vistaMoviments'].value}"/>
				<a href="<c:url value="/contingut/${contingut.id}/log/informe?isVistaMoviments=${isVistaMoviments}"/>" class="btn btn-primary pull-right" style="margin-bottom:5px">
					<i class="fa fa-file-text-o" aria-hidden="true"></i>
					<spring:message code="comu.boto.informe"/>
				</a>
				<table class="table table-striped table-bordered">
					<thead>
						<tr>
							<th width="15%"><spring:message code="contingut.log.columna.data"/></th>
							<th width="85%"><spring:message code="contingut.log.columna.resum"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="log" items="${logsResum}">
							<tr>
								<td><fmt:formatDate value="${log[0].createdDate}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
								<td>
									${log[1]}					
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</c:if>
	
	
		<!------------------------------------ TABPANEL ACCIONS --------------------------------------->
		<div class="tab-pane ${!isPanelUser ? "active in" : ""}" id="accions">
			<table class="table table-striped table-bordered">
				<thead>
					<tr>
						<th width="20%"><spring:message code="contingut.log.columna.data"/></th>
						<th width="15%"><spring:message code="contingut.log.columna.usuari"/></th>
						<th width="15%"><spring:message code="contingut.log.columna.accio"/></th>
						<th width="5%"></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="log" items="${logs}">
						<tr>
							<td><fmt:formatDate value="${log.createdDate}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
							<td>${log.createdBy.nom}</td>
							<td>
								<c:choose>
									<c:when test="${log.secundari}">
										<span class="fa fa-arrow-right" title="<spring:message code="contingut.log.title.secundari"/>"></span>
										<c:if test="${not empty log.objecteLogTipus}">
											<spring:message code="log.tipus.enum.${log.objecteLogTipus}"/>
										</c:if>
										<spring:message code="log.objecte.tipus.enum.${log.objecteTipus}"/>#${log.objecteId}
									</c:when>
									<c:otherwise>
										<spring:message code="log.tipus.enum.${log.tipus}"/>
									</c:otherwise>
								</c:choose>
							</td>
							<td><button class="btn btn-default btn-xs log-detalls" data-log-id="${log.id}"><span class="fa fa-chevron-down"></span></button></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<div id="log-info-detall" class="hidden">
				<div class="panel panel-default log-info-params" >
					<div class="panel-heading"><strong><spring:message code="contingut.log.detall.params"/></strong></div>
					<table class="table table-bordered table-params">

					</table>
				</div>
				<div class="panel panel-default log-info-accio">
					<div class="panel-heading"><strong><spring:message code="contingut.log.detall.accio"/></strong></div>
					<table class="table table-bordered table-accio">
						<tr>
							<td width="20%"><b><spring:message code="contingut.log.detall.accio.accio"/></b></td>
							<td class="log-info-accio-data"><spring:message code="contingut.log.detall.accio.data"/></td>
							<td class="log-info-accio-usuari"><spring:message code="contingut.log.detall.accio.usuari"/></td>
							<td class="log-info-accio-tipus"><spring:message code="contingut.log.detall.accio.tipus"/></td>
						</tr>
						<tr>
							<td width="20%"><b><spring:message code="contingut.log.detall.accio.objecte"/></b></td>
							<td class="log-info-accio-objecte" colspan="3"></td>
						</tr>
					</table>
				</div>
				<div class="panel panel-default log-info-moviment">
					<div class="panel-heading"><strong><spring:message code="contingut.log.detall.moviment"/></strong></div>
					<table class="table table-bordered">
						<tr>
							<td width="20%"><b><spring:message code="contingut.log.detall.moviment.origen"/></b></td>
							<td class="log-info-moviment-origen"></td>
							<td width="20%"><b><spring:message code="contingut.log.detall.moviment.desti"/></b></td>
							<td class="log-info-moviment-desti"></td>
						<tr>
					</table>
				</div>
			</div>
		</div>
		
		<!------------------------------------ TABPANEL MOVIMENTS --------------------------------------->		
		<div class="tab-pane" id="moviments">
			<c:if test="${not empty moviments}">
				<table class="table table-striped table-bordered">
					<thead>
						<tr>
							<th><spring:message code="contingut.log.columna.data"/></th>
							<th><spring:message code="contingut.log.columna.usuari"/></th>
							<th><spring:message code="contingut.log.columna.origen"/></th>
							<th><spring:message code="contingut.log.columna.desti"/></th>
							<th><spring:message code="contingut.log.columna.comentari"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="moviment" items="${moviments}">
							<tr>
								<td><fmt:formatDate value="${moviment.data}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
								<td>${moviment.remitent.nom}</td>
								<td>
									<c:if test="${not empty moviment.origenId}">
										${moviment.origenNom}
									</c:if>
								</td>
								<td>
									${moviment.destiNom}
								</td>
								<td>${moviment.comentari}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
		</div>
		
		<!------------------------------------ TABPANEL AUDITORIA --------------------------------------->	
		<div class="tab-pane" id="auditoria">
			<div class="row">
				<div class="col-sm-6">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title"><spring:message code="contingut.log.creacio"/></h3>
						</div>
		  				<div class="panel-body">
		    				<dl class="dl-horizontal">
								<dt><spring:message code="contingut.log.camp.usuari.creacio"/></dt>
								<dd>${contingut.createdBy.nom}</dd>
								<dt><spring:message code="contingut.log.camp.data.creacio"/></dt>
								<dd><fmt:formatDate value="${contingut.createdDate}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
							</dl>
		  				</div>
					</div>
				</div>
				<div class="col-sm-6">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title"><spring:message code="contingut.log.modificacio"/></h3>
						</div>
		  				<div class="panel-body">
		    				<dl class="dl-horizontal">
								<dt><spring:message code="contingut.log.camp.usuari.modificacio"/></dt>
								<dd>${contingut.lastModifiedBy.nom}</dd>
								<dt><spring:message code="contingut.log.camp.data.modificacio"/></dt>
								<dd><fmt:formatDate value="${contingut.lastModifiedDate}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
							</dl>
		  				</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="modal-botons" class="well">
		<a href="<c:url value="/contingut/${contingut.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>
