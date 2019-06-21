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
	<dis:blocContenidorPath contingut="${contingut}"/>
	<c:if test="${contingut.registre && contingut.procesEstat == 'BACK_PENDENT' || contingut.procesEstat == 'ARXIU_PENDENT' || contingut.procesEstat == 'BACK_REBUDA' || contingut.procesEstat == 'BACK_PROCESSADA' || contingut.procesEstat == 'BACK_REBUTJADA' || contingut.procesEstat == 'BACK_ERROR'}">
	
		<!------------------------------ TABLIST ------------------------------------------------->
		<ul class="nav nav-tabs" role="tablist">
			<li class="active" role="presentation"><a href="#informacio" aria-controls="informacio" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.informacio"/></a>
			</li>
			<c:if test="${contingut.procesEstat == 'ARXIU_PENDENT' || contingut.procesEstat == 'REGLA_PENDENT'}">
				<li role="presentation">
					<a href="#proces" aria-controls="proces" role="tab" data-toggle="tab">
						<spring:message code="registre.detalls.pipella.proces"/>
						<c:if test="${contingut.procesError != null}"><span class="fa fa-warning text-danger"></span></c:if>
					</a>
				</li>
			</c:if>
			<c:if test="${contingut.procesEstat == 'BACK_PENDENT'}">
				<li role="presentation">
					<a href="#backPendent" aria-controls="proces" role="tab" data-toggle="tab">
						<spring:message code="registre.detalls.pipella.pendent.backoffice"/>
						<c:if test="${contingut.procesError != null}"><span class="fa fa-warning text-danger"></span></c:if>
					</a>
				</li>
			</c:if>	
<%-- 			<c:if test="${contingut.procesEstat == 'BACK_REBUTJADA'}"> --%>
<!-- 				<li role="presentation"> -->
<!-- 					<a href="#backRebutjada" aria-controls="proces" role="tab" data-toggle="tab"> -->
<%-- 						<spring:message code="registre.detalls.pipella.rebutjada.backoffice"/> --%>
<%-- 						<c:if test="${contingut.procesError != null}"><span class="fa fa-warning text-danger"></span></c:if> --%>
<!-- 					</a> -->
<!-- 				</li> -->
<%-- 			</c:if>			 --%>
<%-- 			<c:if test="${contingut.procesEstat == 'BACK_ERROR'}"> --%>
<!-- 				<li role="presentation"> -->
<!-- 					<a href="#backError" aria-controls="proces" role="tab" data-toggle="tab"> -->
<%-- 						<spring:message code="registre.detalls.pipella.error.backoffice"/> --%>
<%-- 						<c:if test="${contingut.procesError != null}"><span class="fa fa-warning text-danger"></span></c:if> --%>
<!-- 					</a> -->
<!-- 				</li> -->
<%-- 			</c:if>							 --%>
			<c:if test="${contingut.procesEstat == 'BACK_REBUDA' || contingut.procesEstat == 'BACK_PROCESSADA' || contingut.procesEstat == 'BACK_REBUTJADA' || contingut.procesEstat == 'BACK_ERROR'}">
				<li role="presentation">
					<a href="#backAction" aria-controls="proces" role="tab" data-toggle="tab">
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
			<dis:blocIconaContingut contingut="${contingut}"/>
			${contingut.nom}
		</dd>
		<c:if test="${contingut.registre}">
			<dt><spring:message code="registre.detalls.camp.tipus"/></dt><dd><spring:message code="registre.anotacio.tipus.enum.${contingut.registreTipus}"/></dd>
			<dt><spring:message code="contingut.admin.info.camp.estat"/></dt><dd>${contingut.procesEstat}</dd>
			<dt><spring:message code="registre.detalls.camp.numero"/></dt><dd>${contingut.identificador}</dd>
			<dt><spring:message code="registre.detalls.camp.data"/></dt><dd><fmt:formatDate value="${contingut.data}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
			<c:if test="${contingut.registreTipus == 'ENTRADA'}">
				<dt><spring:message code="registre.detalls.camp.desti"/></dt><dd>${contingut.unitatAdministrativa}</dd>				
			</c:if>
			<c:if test="${contingut.registreTipus == 'SORTIDA'}">
				<dt><spring:message code="registre.detalls.camp.origen"/></dt><dd>${contingut.unitatAdministrativa}</dd>
			</c:if>
			<dt><spring:message code="registre.detalls.camp.entitat"/></dt><dd>${contingut.entitatDescripcio} (${contingut.entitatCodi})</dd>
			<dt><spring:message code="registre.detalls.camp.oficina"/></dt><dd>${contingut.oficinaDescripcio} (${contingut.oficinaCodi})</dd>
			<dt><spring:message code="registre.detalls.camp.llibre"/></dt><dd>${contingut.llibreDescripcio} (${contingut.llibreCodi})</dd>
			<dt><spring:message code="registre.detalls.camp.extracte"/></dt><dd>${contingut.extracte}</dd>
			<dt><spring:message code="registre.detalls.camp.assumpte.tipus"/></dt><dd>${contingut.assumpteTipusDescripcio} (${contingut.assumpteTipusCodi})</dd>
			<dt><spring:message code="registre.detalls.camp.assumpte.codi"/></dt><dd>${contingut.assumpteDescripcio} (${contingut.assumpteCodi})</dd>
			<dt><spring:message code="registre.detalls.camp.procediment"/></dt><dd>${contingut.procedimentCodi}</dd>
			<dt><spring:message code="registre.detalls.camp.idioma"/></dt><dd>${contingut.idiomaDescripcio} (${contingut.idiomaCodi})</dd>
			<c:if test="${not empty contingut.transportTipusCodi}">
				<dt><spring:message code="registre.detalls.camp.transport.tipus"/></dt><dd>${contingut.transportTipusDescripcio} (${contingut.transportTipusCodi})</dd>
			</c:if>
			<c:if test="${not empty contingut.transportNumero}">
				<dt><spring:message code="registre.detalls.camp.transport.numero"/></dt><dd>${contingut.transportNumero}</dd>
			</c:if>
			<c:if test="${not empty contingut.usuariCodi}">
				<dt><spring:message code="registre.detalls.camp.usuari"/></dt><dd>${contingut.usuariNom} (${contingut.usuariCodi})</dd>
			</c:if>
			<c:if test="${not empty contingut.aplicacioCodi}">
				<dt><spring:message code="registre.detalls.camp.aplicacio"/></dt><dd>${contingut.aplicacioCodi} ${contingut.aplicacioVersio}</dd>
			</c:if>
			<c:if test="${not empty contingut.expedientNumero}">
				<dt><spring:message code="registre.detalls.camp.expedient"/></dt><dd>${contingut.expedientNumero}</dd>
			</c:if>
			<c:if test="${not empty contingut.observacions}">
				<dt><spring:message code="registre.detalls.camp.observacions"/></dt><dd>${contingut.observacions}</dd>
			</c:if>
			<dt><spring:message code="registre.detalls.camp.distribucio.alta"/></dt><dd><fmt:formatDate value="${contingut.createdDate}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
			<dt><spring:message code="registre.detalls.camp.distribucio.identificadorArxiu"/></dt><dd>${contingut.expedientArxiuUuid}</dd>
		</c:if>
	</dl>
	<c:if test="${contingut.registre && contingut.procesEstat == 'ARXIU_PENDENT' || contingut.procesEstat == 'REGLA_PENDENT'}">
			</div>
			
			<!------------------------------ TABPANEL PROCES ------------------------------------->
			<div class="tab-pane" id="proces" role="tabpanel">
				<c:if test="${contingut.procesError != null }">
					<div class="alert well-sm alert-danger alert-dismissable">
						<span class="fa fa-exclamation-triangle"></span>
						<spring:message code="registre.detalls.info.errors"/>
						<a href="../${contingut.pare.id}/registre/${contingut.id}/reintentar" class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentar"/></a>
					</div>
				</c:if>
				<dl class="dl-horizontal">
					<dt><spring:message code="registre.detalls.camp.proces.estat"/></dt>
					<dd>${contingut.procesEstat}</dd>
					<dt><spring:message code="registre.detalls.camp.proces.data"/></dt>
					<dd><fmt:formatDate value="${contingut.procesData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
					<dt><spring:message code="registre.detalls.camp.proces.intents"/></dt>
					<dd>${contingut.procesIntents}</dd>
				</dl>
				<c:if test="${contingut.procesEstat == 'ARXIU_PENDENT'}">
					<pre style="height:300px">${contingut.procesError}</pre>
				</c:if>
			</div>
		</div>
	</c:if>
	
	<c:if test="${contingut.registre && contingut.procesEstat == 'BACK_PENDENT'}">
			</div>
			<!------------------------------ TABPANEL BACK_PENDENT ------------------------------------->
			<div class="tab-pane" id="backPendent" role="tabpanel">
			
				    <c:if test="${contingut.procesError != null }">
						<div class="alert well-sm alert-danger alert-dismissable">
							<span class="fa fa-exclamation-triangle"></span>
							<spring:message code="registre.detalls.info.errors"/>
							<a href="../${contingut.pare.id}/registre/${contingut.id}/reintentarEnviamentBackoffice" class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentarEnviamentBackoffice"/></a>
						</div>
				    </c:if>	   
				    <c:if test="${contingut.procesError == null and contingut.procesIntents > 0}">
						<a href="../${contingut.pare.id}/registre/${contingut.id}/reintentarEnviamentBackoffice" class="btn btn-xs btn-default pull-right" style="margin-right: 10px;"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentarEnviamentBackoffice"/></a>
				    </c:if>	
					<dl class="dl-horizontal">
						<dt><spring:message code="registre.detalls.camp.proces.estat"/></dt>
						<dd>${contingut.procesEstat}</dd>
						<c:if test="${not empty contingut.procesData}">
							<dt><spring:message code="registre.detalls.camp.proces.data"/></dt>
							<dd><fmt:formatDate value="${contingut.procesData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>				
						</c:if>	
						<c:if test="${not empty contingut.backRetryEnviarData}">
							<dt><spring:message code="registre.detalls.camp.proces.data.back.proxima.intent"/></dt>
							<dd><fmt:formatDate value="${contingut.backRetryEnviarData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>				
						</c:if>													
						<dt><spring:message code="registre.detalls.camp.proces.intents"/></dt>
						<dd>${contingut.procesIntents}</dd>
					</dl>
				<c:if test="${not empty contingut.procesError}">
					<pre style="height:300px">${contingut.procesError}</pre>
				</c:if>
			</div>
		</div>
	</c:if>
	
<%-- 	<c:if test="${contingut.registre && ( contingut.procesEstat == 'BACK_REBUTJADA' || contingut.procesEstat == 'BACK_ERROR')}"> --%>
	
<!-- 		</div> -->
<!-- 		<div class="tab-pane" id="backAction" role="tabpanel"> -->
<%-- 			<a href="../${contingut.pare.id}/registre/${contingut.id}/reintentarEnviamentBackoffice" class="btn btn-xs btn-default pull-right" style="margin-right: 10px;"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentarEnviamentBackoffice"/></a> --%>

<!-- 			<dl class="dl-horizontal"> -->
<%-- 				<dt><spring:message code="registre.detalls.camp.proces.estat"/></dt> --%>
<%-- 				<dd>${contingut.procesEstat}</dd> --%>
<%-- 				<dt><spring:message code="registre.detalls.camp.proces.data.back.pendent"/></dt> --%>
<%-- 				<dd><fmt:formatDate value="${contingut.backPendentData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd> --%>
<%-- 				<dt><spring:message code="registre.detalls.camp.proces.data.back.rebuda"/></dt> --%>
<%-- 				<dd><fmt:formatDate value="${contingut.backRebudaData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd> --%>
<%-- 				<c:choose> --%>
<%-- 				   <c:when test = "${contingut.procesEstat == 'BACK_PROCESSADA'}"> --%>
<%-- 				      <dt><spring:message code="registre.detalls.camp.proces.data.back.processada"/></dt> --%>
<%-- 				      <dd><fmt:formatDate value="${contingut.backProcesRebutjErrorData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd> --%>
<%-- 				   </c:when> --%>
<%-- 				   <c:when test = "${contingut.procesEstat == 'BACK_REBUTJADA'}"> --%>
<%-- 				      <dt><spring:message code="registre.detalls.camp.proces.data.back.rebutjada"/></dt> --%>
<%-- 				      <dd><fmt:formatDate value="${contingut.backProcesRebutjErrorData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd> --%>
<%-- 				   </c:when> --%>
<%-- 				   <c:when test = "${contingut.procesEstat == 'BACK_ERROR'}"> --%>
<%-- 				      <dt><spring:message code="registre.detalls.camp.proces.data.back.error"/></dt> --%>
<%-- 				      <dd><fmt:formatDate value="${contingut.backProcesRebutjErrorData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd> --%>
<%-- 				   </c:when>          --%>
<%-- 				</c:choose>				 --%>
<!-- 			</dl> -->
<%-- 			<c:if test="${contingut.backObservacions != null}"> --%>
<%-- 				<pre style="height:300px">${contingut.backObservacions}</pre> --%>
<%-- 			</c:if> --%>
<!-- 		</div> -->
<!-- 	</div> -->
<%-- </c:if>	 --%>
	
	<c:if test="${contingut.registre && ( contingut.procesEstat == 'BACK_REBUDA' || contingut.procesEstat == 'BACK_PROCESSADA' || contingut.procesEstat == 'BACK_REBUTJADA' || contingut.procesEstat == 'BACK_ERROR')}">
		</div>
		
		<!------------------------------ TABPANEL BACK_ACTION ------------------------------------->
		<div class="tab-pane" id="backAction" role="tabpanel">
		
		    <c:if test="${contingut.procesEstat == 'BACK_REBUTJADA' || contingut.procesEstat == 'BACK_ERROR'}">
				<a href="../${contingut.pare.id}/registre/${contingut.id}/reintentarEnviamentBackoffice" class="btn btn-xs btn-default pull-right" style="margin-right: 10px;"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentarEnviamentBackoffice"/></a>
		    </c:if>	

			<dl class="dl-horizontal">
				<dt><spring:message code="registre.detalls.camp.proces.estat"/></dt>
				<dd>${contingut.procesEstat}</dd>
				<dt><spring:message code="registre.detalls.camp.proces.data.back.pendent"/></dt>
				<dd><fmt:formatDate value="${contingut.backPendentData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
				<dt><spring:message code="registre.detalls.camp.proces.data.back.rebuda"/></dt>
				<dd><fmt:formatDate value="${contingut.backRebudaData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
				<c:choose>
				   <c:when test = "${contingut.procesEstat == 'BACK_PROCESSADA'}">
				      <dt><spring:message code="registre.detalls.camp.proces.data.back.processada"/></dt>
				      <dd><fmt:formatDate value="${contingut.backProcesRebutjErrorData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
				   </c:when>
				   <c:when test = "${contingut.procesEstat == 'BACK_REBUTJADA'}">
				      <dt><spring:message code="registre.detalls.camp.proces.data.back.rebutjada"/></dt>
				      <dd><fmt:formatDate value="${contingut.backProcesRebutjErrorData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
				   </c:when>
				   <c:when test = "${contingut.procesEstat == 'BACK_ERROR'}">
				      <dt><spring:message code="registre.detalls.camp.proces.data.back.error"/></dt>
				      <dd><fmt:formatDate value="${contingut.backProcesRebutjErrorData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
				   </c:when>         
				</c:choose>				
			</dl>
				<c:choose>
				   <c:when test = "${not empty contingut.procesError}">
						<pre style="height:300px">${contingut.procesError}</pre>
				   </c:when>
				   <c:when test = "${contingut.backObservacions != null}">
						<pre style="height:300px">${contingut.backObservacions}</pre>
				   </c:when>
				</c:choose>			
		</div>
	</div>
	</c:if>
	
	
	
	<div id="modal-botons" class="well">
		<a href="<c:url value="/contingutAdmin"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>