<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="contingut.admin.info.titol"/></title>
	<dis:modalHead/>
</head>
<body>
	<dis:blocContenidorPath contingut="${registre}"/>
	<c:if test="${registre.procesEstat != 'BUSTIA_PENDENT' && registre.procesEstat != 'BUSTIA_PROCESSADA'}">
	
		<!------------------------------ TABLIST ------------------------------------------------->
		<ul class="nav nav-tabs" role="tablist">
			<li class="active" role="presentation"><a href="#informacio" aria-controls="informacio" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.informacio"/></a>
			</li>
			<c:if test="${registre.procesEstat == 'ARXIU_PENDENT' || registre.procesEstat == 'REGLA_PENDENT' || registre.procesEstat == 'BACK_PENDENT'}">
				<li role="presentation">
					<a href="#processamentAutomatic"  role="tab" data-toggle="tab">
						<spring:message code="registre.detalls.pipella.proces"/>
						<c:if test="${registre.procesError != null}"><span class="fa fa-warning text-danger"></span></c:if>
					</a>
				</li>
			</c:if>
			<c:if test="${registre.procesEstat == 'BACK_REBUDA' || registre.procesEstat == 'BACK_PROCESSADA' || registre.procesEstat == 'BACK_REBUTJADA' || registre.procesEstat == 'BACK_ERROR'}">
				<li role="presentation">
					<a href="#processamentBackoffice"  role="tab" data-toggle="tab">
						<spring:message code="registre.detalls.pipella.proces.backoffice"/>
					</a>
				</li>
			</c:if>
		</ul>
		
		<br/>
		
		<div class="tab-content">
			<!------------------------------ TABPANEL INFORMACIO ------------------------------------->
			<div class="tab-pane active in" id="informacio" role="tabpanel">
	</c:if>
	<dl class="dl-horizontal">
		<dt><spring:message code="contingut.admin.info.camp.contingut"/></dt>
		<dd>
			<dis:blocIconaContingut contingut="${registre}"/>
			${registre.nom}
		</dd>
		<dt><spring:message code="registre.detalls.camp.tipus"/></dt><dd><spring:message code="registre.anotacio.tipus.enum.${registre.registreTipus}"/></dd>
		<dt><spring:message code="contingut.admin.info.camp.estat"/></dt><dd>${registre.procesEstat}</dd>
		<dt><spring:message code="registre.detalls.camp.numero"/></dt><dd>${registre.identificador}</dd>
		<dt><spring:message code="registre.detalls.camp.data"/></dt><dd><fmt:formatDate value="${registre.data}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
		<c:if test="${registre.registreTipus == 'ENTRADA'}">
			<dt><spring:message code="registre.detalls.camp.desti"/></dt><dd>${registre.unitatAdministrativa}</dd>				
		</c:if>
		<c:if test="${registre.registreTipus == 'SORTIDA'}">
			<dt><spring:message code="registre.detalls.camp.origen"/></dt><dd>${registre.unitatAdministrativa}</dd>
		</c:if>
		<dt><spring:message code="registre.detalls.camp.entitat"/></dt><dd>${registre.entitatDescripcio} (${registre.entitatCodi})</dd>
		<dt><spring:message code="registre.detalls.camp.oficina"/></dt><dd>${registre.oficinaDescripcio} (${registre.oficinaCodi})</dd>
		<dt><spring:message code="registre.detalls.camp.llibre"/></dt><dd>${registre.llibreDescripcio} (${registre.llibreCodi})</dd>
		<dt><spring:message code="registre.detalls.camp.extracte"/></dt><dd>${registre.extracte}</dd>
		<dt><spring:message code="registre.detalls.camp.assumpte.tipus"/></dt><dd>${registre.assumpteTipusDescripcio} (${registre.assumpteTipusCodi})</dd>
		<dt><spring:message code="registre.detalls.camp.assumpte.codi"/></dt><dd>${registre.assumpteDescripcio} (${registre.assumpteCodi})</dd>
		<dt><spring:message code="registre.detalls.camp.procediment"/></dt><dd>${registre.procedimentCodi}</dd>
		<dt><spring:message code="registre.detalls.camp.idioma"/></dt><dd>${registre.idiomaDescripcio} (${registre.idiomaCodi})</dd>
		<c:if test="${not empty registre.transportTipusCodi}">
			<dt><spring:message code="registre.detalls.camp.transport.tipus"/></dt><dd>${registre.transportTipusDescripcio} (${registre.transportTipusCodi})</dd>
		</c:if>
		<c:if test="${not empty registre.transportNumero}">
			<dt><spring:message code="registre.detalls.camp.transport.numero"/></dt><dd>${registre.transportNumero}</dd>
		</c:if>
		<c:if test="${not empty registre.usuariCodi}">
			<dt><spring:message code="registre.detalls.camp.usuari"/></dt><dd>${registre.usuariNom} (${registre.usuariCodi})</dd>
		</c:if>
		<c:if test="${not empty registre.aplicacioCodi}">
			<dt><spring:message code="registre.detalls.camp.aplicacio"/></dt><dd>${registre.aplicacioCodi} ${registre.aplicacioVersio}</dd>
		</c:if>
		<c:if test="${not empty registre.expedientNumero}">
			<dt><spring:message code="registre.detalls.camp.expedient"/></dt><dd>${registre.expedientNumero}</dd>
		</c:if>
		<c:if test="${not empty registre.observacions}">
			<dt><spring:message code="registre.detalls.camp.observacions"/></dt><dd>${registre.observacions}</dd>
		</c:if>
		<dt><spring:message code="registre.detalls.camp.distribucio.alta"/></dt><dd><fmt:formatDate value="${registre.createdDate}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
		<dt><spring:message code="registre.detalls.camp.distribucio.identificadorArxiu"/></dt><dd>${registre.expedientArxiuUuid}</dd>
	
	</dl>
	<c:if test="${registre.procesEstat == 'ARXIU_PENDENT' || registre.procesEstat == 'REGLA_PENDENT' || registre.procesEstat == 'BACK_PENDENT'}">
			</div>
			
			<!------------------------------ TABPANEL PROCESSAMENT_AUTOMATIC ------------------------------------->
			<div class="tab-pane" id="processamentAutomatic" role="tabpanel">
			
			
				<!------ REINTENTAR PROCESSAMENT ------>
				<c:if test="${registre.procesError != null }">
					<div class="alert well-sm alert-danger alert-dismissable">
						<span class="fa fa-exclamation-triangle"></span>
						<spring:message code="registre.detalls.info.errors"/>
						
						<c:if test="${registre.procesEstat == 'ARXIU_PENDENT' || registre.procesEstat == 'REGLA_PENDENT'}">
							<a href="../${registre.pare.id}/registre/${registre.id}/reintentar" class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentar"/></a>
						</c:if>
						<c:if test="${registre.procesEstat == 'BACK_PENDENT'}">						
							<a href="../${registre.pare.id}/registre/${registre.id}/reintentarEnviamentBackoffice" class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentarEnviamentBackoffice"/></a>					
						</c:if>
						
					</div>
				</c:if>   
			    <c:if test="${registre.procesEstat == 'BACK_PENDENT' && registre.procesError == null && registre.procesIntents > 0}">
					<a href="../${registre.pare.id}/registre/${registre.id}/reintentarEnviamentBackoffice" class="btn btn-xs btn-default pull-right" style="margin-right: 10px;"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentarEnviamentBackoffice"/></a>
			    </c:if>					
				
				<!------ PROCESSAMENT INFO ------>
				<dl class="dl-horizontal">
				
					<dt><spring:message code="registre.detalls.camp.proces.estat"/></dt>
					<dd>${registre.procesEstat}</dd>
					
					<c:if test="${not empty registre.procesData}">
						<dt><spring:message code="registre.detalls.camp.proces.data"/></dt>
						<dd><fmt:formatDate value="${registre.procesData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>				
					</c:if>	
					
					<c:if test="${registre.procesEstat == 'BACK_PENDENT' && not empty registre.backRetryEnviarData}">
						<dt><spring:message code="registre.detalls.camp.proces.data.back.proxima.intent"/></dt>
						<dd><fmt:formatDate value="${registre.backRetryEnviarData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>				
					</c:if>						
					
					<dt><spring:message code="registre.detalls.camp.proces.intents"/></dt>
					<dd>${registre.procesIntents}</dd>
					
				</dl>
				
				<c:if test="${not empty registre.procesError}">
					<pre style="height:300px">${registre.procesError}</pre>
				</c:if>
				
			</div>
		</div>
	</c:if>
	
	
	<c:if test="${registre.procesEstat == 'BACK_REBUDA' || registre.procesEstat == 'BACK_PROCESSADA' || registre.procesEstat == 'BACK_REBUTJADA' || registre.procesEstat == 'BACK_ERROR'}">
		</div>
		
		<!------------------------------ TABPANEL PROCESSAMENT_BACKOFFICE ------------------------------------->
		<div class="tab-pane" id="processamentBackoffice" role="tabpanel">
		
		    <c:if test="${registre.procesEstat == 'BACK_REBUTJADA' || registre.procesEstat == 'BACK_ERROR'}">
				<a href="../${registre.pare.id}/registre/${registre.id}/reintentarEnviamentBackoffice" class="btn btn-xs btn-default pull-right" style="margin-right: 10px;"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentarEnviamentBackoffice"/></a>
		    </c:if>	

			<dl class="dl-horizontal">
			
				<dt><spring:message code="registre.detalls.camp.proces.estat"/></dt>
				<dd>${registre.procesEstat}</dd>
				
				<dt><spring:message code="registre.detalls.camp.proces.data.back.pendent"/></dt>
				<dd><fmt:formatDate value="${registre.backPendentData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
				
				<dt><spring:message code="registre.detalls.camp.proces.data.back.rebuda"/></dt>
				<dd><fmt:formatDate value="${registre.backRebudaData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
				
				<c:choose>
				   <c:when test = "${registre.procesEstat == 'BACK_PROCESSADA'}">
				      <dt><spring:message code="registre.detalls.camp.proces.data.back.processada"/></dt>
				      <dd><fmt:formatDate value="${registre.backProcesRebutjErrorData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
				   </c:when>
				   
				   <c:when test = "${registre.procesEstat == 'BACK_REBUTJADA'}">
				      <dt><spring:message code="registre.detalls.camp.proces.data.back.rebutjada"/></dt>
				      <dd><fmt:formatDate value="${registre.backProcesRebutjErrorData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
				   </c:when>
				   
				   <c:when test = "${registre.procesEstat == 'BACK_ERROR'}">
				      <dt><spring:message code="registre.detalls.camp.proces.data.back.error"/></dt>
				      <dd><fmt:formatDate value="${registre.backProcesRebutjErrorData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
				   </c:when>         
				</c:choose>	
							
			</dl>
			
				<c:choose>
				   <c:when test = "${not empty registre.procesError}">
						<pre style="height:300px">${registre.procesError}</pre>
				   </c:when>
				   <c:when test = "${registre.backObservacions != null}">
						<pre style="height:300px">${registre.backObservacions}</pre>
				   </c:when>
				</c:choose>			
		</div>
	</div>
	</c:if>
	
	
	
	<div id="modal-botons" class="well">
		<a href="<c:url value="/contingutAdmin"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>