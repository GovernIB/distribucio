<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="firma.info.titol"/></title>
	<dis:modalHead/>
</head>
<body>
	<ul class="nav nav-tabs" role="tablist">
		<li class="active" role="presentation">
			<a href="#dades" aria-controls="dades" role="tab" data-toggle="tab"><spring:message code="firma.info.pipella.dades"/></a>
		</li>
		<li role="presentation">
			<a href="#annexos" aria-controls="annexos" role="tab" data-toggle="tab"><spring:message code="firma.info.pipella.annexos"/></a>
		</li>
		<c:if test="${portafirmes.enviamentError || portafirmes.processamentError}">
			<li role="presentation">
				<a href="#errors" class="text-danger" aria-controls="errors" role="tab" data-toggle="tab"><span class="fa fa-exclamation-triangle"></span> <spring:message code="firma.info.pipella.errors"/></a>
			</li>
		</c:if>
	</ul>
	<br/>
	<div class="tab-content">
		<div class="tab-pane active in" id="dades" role="tabpanel">
			<dl class="dl-horizontal">
				<dt><spring:message code="firma.info.camp.document"/></dt>
				<dd>${portafirmes.document.nom}</dd>
				<dt><spring:message code="firma.info.camp.assumpte"/></dt>
				<dd>${portafirmes.assumpte}</dd>
				<dt><spring:message code="firma.info.camp.data.enviament"/></dt>
				<dd><fmt:formatDate value="${portafirmes.dataEnviament}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
				<dt><spring:message code="firma.info.camp.estat"/></dt>
				<dd><spring:message code="portafirmes.estat.enum.${portafirmes.estat}"/></dd>
				<dt><spring:message code="firma.info.camp.prioritat"/></dt>
				<dd><spring:message code="portafirmes.prioritat.enum.${portafirmes.prioritat}"/></dd>
				<dt><spring:message code="firma.info.camp.data.cad"/></dt>
				<dd><fmt:formatDate value="${portafirmes.dataCaducitat}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
				<dt><spring:message code="firma.info.camp.document.tipus"/></dt>
				<dd>${portafirmes.documentTipus}</dd>
				<c:if test="${not empty portafirmes.destinatari}">
					<dt><spring:message code="firma.info.camp.responsables"/></dt>
					<dd>${portafirmes.destinatari}</dd>
				</c:if>
				<c:if test="${not empty portafirmes.fluxTipus}">
					<dt><spring:message code="firma.info.camp.flux.tipus"/></dt>
					<dd><spring:message code="metadocument.fluxtip.enum.${portafirmes.fluxTipus}"/></dd>
				</c:if>
				<c:if test="${not empty portafirmes.fluxId}">
					<dt><spring:message code="firma.info.camp.flux.id"/></dt>
					<dd>${portafirmes.fluxId}</dd>
				</c:if>
				<c:if test="${not empty portafirmes.portafirmesId}">
					<dt><spring:message code="firma.info.camp.portafirmes.id"/></dt>
					<dd>${portafirmes.portafirmesId}</dd>
				</c:if>
			</dl>
		</div>
		<div class="tab-pane" id="annexos" role="tabpanel">
		</div>
		<div class="tab-pane" id="errors" role="tabpanel">
			<c:if test="${portafirmes.enviamentError}">
				<div class="alert well-sm alert-danger alert-dismissable">
					<span class="fa fa-exclamation-triangle"></span>
					<spring:message code="firma.info.errors.enviament"/>
					<a href="../portafirmes/reintentar" class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span> <spring:message code="firma.info.errors.enviament.reintentar"/></a>
				</div>
				<div class="panel panel-default">
					<div class="panel-heading">
						<h4 class="panel-title"><spring:message code="firma.info.error.enviament"/></h4>
					</div>
					<div class="panel-body">
						<br/>
						<dl class="dl-horizontal">
							<dt><spring:message code="firma.info.camp.error.data.darrer"/></dt>
							<dd><fmt:formatDate value="${portafirmes.enviamentData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
							<dt><spring:message code="firma.info.camp.error.intents"/></dt>
							<dd>${portafirmes.enviamentCount}</dd>
						</dl>
						<pre style="height:300px; margin: 12px">${portafirmes.enviamentErrorDescripcio}</pre>
					</div>
				</div>
			</c:if>
			<c:if test="${portafirmes.processamentError}">
				<div class="alert well-sm alert-danger alert-dismissable">
					<span class="fa fa-exclamation-triangle"></span>
					<spring:message code="notificacio.info.errors.processament"/>
					<a href="../portafirmes/reintentar" class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span> <spring:message code="firma.info.errors.processament.reintentar"/></a>
				</div>
				<div class="panel panel-default">
					<div class="panel-heading">
						<h4 class="panel-title"><spring:message code="firma.info.error.processament"/></h4>
					</div>
					<div class="panel-body">
						<br/>
						<dl class="dl-horizontal">
							<dt><spring:message code="firma.info.camp.error.data.darrer"/></dt>
							<dd><fmt:formatDate value="${portafirmes.processamentData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
							<dt><spring:message code="firma.info.camp.error.intents"/></dt>
							<dd>${portafirmes.processamentCount}</dd>
						</dl>
						<pre style="height:300px; margin: 12px">${portafirmes.processamentErrorDescripcio}</pre>
					</div>
				</div>
			</c:if>
		</div>
	</div>
	<div id="modal-botons" class="well">
		<c:if test="${portafirmes.estat == 'ENVIAT_OK' or portafirmes.estat == 'ENVIAT_ERROR'}">
			<a href="<dis:modalUrl value="/document/${portafirmes.document.id}/portafirmes/cancel"/>" class="btn btn-default"><span class="fa fa-times"></span> <spring:message code="firma.info.accio.cancel"/></a>
		</c:if>
		<a href="<c:url value="/contenidor/${portafirmes.document.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>
