<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="registre.detalls.titol" arguments="${registre.numero}"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<dis:modalHead/>
<style>
body {
	min-height: 400px;
}
.tab-content {
    margin-top: 0.8em;
}
.icona-doc {
	color: #666666
}
.file-dt {
	margin-top: 9px;
}
.file-dd {
	margin-top: 3px;
}
tr.odd {
	background-color: #f9f9f9;
}
tr.detall {
/* 	background-color: cornsilk; */
}
tr.clicable {
	cursor: pointer;
}
</style>
<script type="text/javascript">
	$(document).ready(function() {
		$(".desplegable").click(function(){
			$(this).find("span").toggleClass("fa-caret-up");
			$(this).find("span").toggleClass("fa-caret-down");
		});
	    $("#collapse-justificant").on('show.bs.collapse', function(data){    
		    if (!$(this).data("loaded")) {
		        var registreId = $(this).data("registreId"); 
		        var bustiaId = $(this).data("bustiaId"); 
		        $("#collapse-justificant").append("<div style='text-align: center; margin-bottom: 60px; margin-top: 60px;''><span class='fa fa-circle-o-notch fa-spin fa-3x'/></div>");
		        $("#collapse-justificant").load("<c:url value="/nodeco/contingut/"/>" + bustiaId + "/registre/" + registreId + "/registreJustificant");
		        $(this).data("loaded", true);
		    }
	    });
	    $(".collapse-annex").on('show.bs.collapse', function(data){  
		    if (!$(this).data("loaded")) {	
		    	var registreId = $(this).data("registreId"); 
		        var bustiaId = $(this).data("bustiaId"); 
		        var annexId = $(this).data("annexId");
		        $(this).append("<div style='text-align: center; margin-bottom: 60px; margin-top: 60px;''><span class='fa fa-circle-o-notch fa-spin fa-3x'/></div>");
		        $(this).load("<c:url value="/nodeco/registreUser/registreAnnex/"/>" + bustiaId + "/" + registreId + "/" + annexId);
		        $(this).data("loaded", true);
		    }
	    });
		$('.arxiuInfoTab').on('shown.bs.tab', function(data){
			if (!$(this).data("loaded")) {	
		    	var registreId = $(this).data("registreId"); 
		        var bustiaId = $(this).data("bustiaId"); 
		        $('#arxiuInfo').load("<c:url value="/nodeco/contingut/"/>" + bustiaId + "/registre/" + registreId + "/arxiuInfo");
		        $(this).data("loaded", true);
		    }
		});		    
	});
</script>
</head>
<body>

	<!--------------------------------------------------- TABLIST ------------------------------------------------------>
	<ul class="nav nav-tabs" role="tablist">
		<li class="active" role="presentation"><a href="#informacio" aria-controls="informacio" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.informacio"/></a>
		</li>
		<li role="presentation">
			<a href="#interessats" aria-controls="interessats" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.interessats"/>&nbsp;<span class="badge">${fn:length(registre.interessats)}</span></a>
		</li>
		<li role="presentation">
			<a href="#annexos" aria-controls="annexos" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.annexos"/>&nbsp;<span class="badge">${fn:length(registre.annexos)}</span></a>
		</li>
		<c:if test="${not empty registre.expedientArxiuUuid}">
			<li role="presentation">
				<a href="#arxiuInfo" class="arxiuInfoTab" aria-controls="arxiuInfo" role="tab" data-toggle="tab" data-registre-id="${registre.id}" data-bustia-id="${bustiaId}"><spring:message code="registre.detalls.pipella.arxiu.info"/></a>
			</li>
		</c:if>
		
		
		
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
	<div class="tab-content">
	
		<!------------------------------------------- TABPANEL INFORMACIO --------------------------------------------->
		<div class="tab-pane active in" id="informacio" role="tabpanel">
			<table class="table table-bordered">
			<tbody>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.tipus"/></strong></td>
					<td><spring:message code="registre.anotacio.tipus.enum.${registre.registreTipus}"/></td>
				</tr>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.numero"/></strong></td>
					<td>${registre.numero}</td>
				</tr>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.data"/></strong></td>
					<td><fmt:formatDate value="${registre.data}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
				</tr>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.proces.estat"/></strong></td>
					<td>${registre.procesEstat}</td>
				</tr>		
			</tbody>
			</table>
			<div class="row">
				<div class="col-sm-6">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title"><spring:message code="registre.detalls.titol.obligatories"/></h3>
						</div>
						<table class="table table-bordered">
							<tbody>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.oficina"/></strong></td>
									<td>${registre.oficinaDescripcio} (${registre.oficinaCodi})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.llibre"/></strong></td>
									<td>${registre.llibreDescripcio} (${registre.llibreCodi})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.extracte"/></strong></td>
									<td>${registre.extracte}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.docfis"/></strong></td>
									<td>${registre.documentacioFisicaCodi} - ${registre.documentacioFisicaDescripcio}</td>
								</tr>
								<tr>
									<td><strong>
										<c:if test="${registre.registreTipus == 'ENTRADA'}"><spring:message code="registre.detalls.camp.desti"/></c:if>
										<c:if test="${registre.registreTipus == 'SORTIDA'}"><spring:message code="registre.detalls.camp.origen"/></c:if>
									</strong></td>
									<td>${registre.unitatAdministrativaDescripcio} (${registre.unitatAdministrativa})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.assumpte.tipus"/></strong></td>
									<td>${registre.assumpteTipusDescripcio} (${registre.assumpteTipusCodi})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.idioma"/></strong></td>
									<td>${registre.idiomaDescripcio} (${registre.idiomaCodi})</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
				<div class="col-sm-6">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title"><spring:message code="registre.detalls.titol.opcionals"/></h3>
						</div>
						<table class="table table-bordered">
							<tbody>
								<tr>
									<td colspan="2"><strong><spring:message code="registre.detalls.camp.assumpte.codi"/></strong></td>
									<td colspan="2">(${registre.assumpteCodi})</td>
								</tr>
								<tr>
									<td colspan="2"><strong><spring:message code="registre.detalls.camp.procediment"/></strong></td>
									<td colspan="2">${registre.procedimentCodi}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.refext"/></strong></td>
									<td>${registre.referencia}</td>
									<td><strong><spring:message code="registre.detalls.camp.numexp"/></strong></td>
									<td>${registre.expedientNumero}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.transport.tipus"/></strong></td>
									<td>${registre.transportTipusDescripcio} ${registre.transportTipusCodi!=null?'(':''}${registre.transportTipusCodi}${registre.transportTipusCodi!=null?')':''}</td>
									<td><strong><spring:message code="registre.detalls.camp.transport.num"/></strong></td>
									<td>${registre.transportNumero}</td>
								</tr>
								<tr>
									<td colspan="2"><strong><spring:message code="registre.detalls.camp.origen.oficina"/></strong></td>
									<td colspan="2">${registre.oficinaOrigenDescripcio} ${registre.oficinaOrigenCodi!=null?'(':''}${registre.oficinaOrigenCodi}${registre.oficinaOrigenCodi!=null?')':''}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.origen.num"/></strong></td>
									<td>${registre.numeroOrigen}</td>
									<td><strong><spring:message code="registre.detalls.camp.origen.data"/></strong></td>
									<td><fmt:formatDate value="${registre.dataOrigen}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
								</tr>
								<tr>
									<td colspan="2"><strong><spring:message code="registre.detalls.camp.observacions"/></strong></td>
									<td colspan="2">${registre.observacions}</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">
						<spring:message code="registre.detalls.titol.seguiment"/>
						<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-seguiment"><span class="fa fa-chevron-down"></span></button>
					</h3>
				</div>
				<div id="collapse-seguiment" class="panel-collapse collapse" role="tabpanel" aria-labelledby="dadesSeguiment">
					<table class="table table-bordered">
					<tbody>
						<c:if test="${not empty registre.entitatCodi}">
							<tr>
								<td><strong><spring:message code="registre.detalls.camp.entitat"/></strong></td>
								<td>${registre.entitatDescripcio} (${registre.entitatCodi})</td>
							</tr>
						</c:if>
						<c:if test="${not empty registre.aplicacioCodi}">
							<tr>
								<td><strong><spring:message code="registre.detalls.camp.aplicacio"/></strong></td>
								<td>${registre.aplicacioCodi} ${registre.aplicacioVersio}</td>
							</tr>
						</c:if>
						<c:if test="${not empty registre.usuariCodi}">
							<tr>
								<td><strong><spring:message code="registre.detalls.camp.usuari"/></strong></td>
								<td>${registre.usuariNom} (${registre.usuariCodi})</td>
							</tr>
						</c:if>
						<tr>
							<td><strong><spring:message code="registre.detalls.camp.distribucio.alta"/></strong></td>
							<td><fmt:formatDate value="${registre.createdDate}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
						</tr>
					</tbody>
					</table>
				</div>
			</div>
<!-- 			JUSTIFICANT -->
			<c:if test="${not empty registre.justificantArxiuUuid}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<spring:message code="registre.detalls.titol.justificant"/>
							<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-justificant"><span class="fa fa-chevron-down"></span></button>
						</h3>
					</div>
					<div id="collapse-justificant" class="panel-collapse collapse" role="tabpanel" aria-labelledby="justificant" data-registre-id="${registre.id}" data-bustia-id="${bustiaId}">

					</div>
				</div>
			</c:if>
<!-- 			FI JUSTIFICANT -->
		</div>
		
		<!------------------------------------------- TABPANEL INTERESSATS --------------------------------------------->
		<div class="tab-pane" id="interessats" role="tabpanel">
			<c:choose>
				<c:when test="${not empty registre.interessats}">
					<table class="table table-bordered">
						<thead>
							<tr>
								<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.tipus"/></th>
								<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.document"/></th>
								<th><spring:message code="registre.detalls.camp.interessat.nom"/></th>
								<th style="width: 50px;"></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="interessat" items="${registre.interessats}" varStatus="status">
								<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if>>
									<td>
										<spring:message code="registre.interessat.tipus.enum.${interessat.tipus}"/>
									</td>
									<td>${interessat.documentTipus}: ${interessat.documentNum}</td>
									<c:choose>
										<c:when test="${interessat.tipus == 'PERSONA_FIS'}">
											<td>${interessat.nom} ${interessat.llinatge1} ${interessat.llinatge2}</td>
										</c:when>
										<c:otherwise>
											<td>${interessat.raoSocial}</td>
										</c:otherwise>
									</c:choose>
									<td>
										<c:if test="${interessat.tipus != 'ADMINISTRACIO'}">
											<button type="button" class="btn btn-default desplegable" href="#detalls_${status.index}" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_${status.index}">
												<span class="fa fa-caret-down"></span>
											</button>
										</c:if>
									</td>
								</tr>
								<tr class="collapse detall" id="detalls_${status.index}">
									<td colspan="4">
										<div class="row">
											<div class="col-xs-6">
												<dl class="dl-horizontal">
													<dt><spring:message code="interessat.form.camp.pais"/></dt><dd>${interessat.pais} <c:if test="${not empty interessat.paisCodi}">(${interessat.paisCodi})</c:if></dd>
													<dt><spring:message code="interessat.form.camp.provincia"/></dt><dd>${interessat.provincia} <c:if test="${not empty interessat.provinciaCodi}">(${interessat.provinciaCodi})</c:if></dd>											
													<dt><spring:message code="interessat.form.camp.municipi"/></dt><dd>${interessat.municipi} <c:if test="${not empty interessat.municipiCodi}">(${interessat.municipiCodi})</c:if></dd>
													<dt><spring:message code="interessat.form.camp.adresa"/></dt><dd>${interessat.adresa}</dd>
													<dt><spring:message code="interessat.form.camp.codiPostal"/></dt><dd>${interessat.codiPostal}</dd>
												</dl>
											</div>
											<div class="col-xs-6">
												<dl class="dl-horizontal">
													<dt><spring:message code="interessat.form.camp.email"/></dt><dd>${interessat.email}</dd>
													<dt><spring:message code="interessat.form.camp.telefon"/></dt><dd>${interessat.telefon}</dd>
													<dt><spring:message code="registre.interessat.detalls.camp.emailHabilitat"/></dt><dd>${interessat.emailHabilitat}</dd>
													<dt><spring:message code="registre.interessat.detalls.camp.canalPreferent"/></dt><dd><c:if test="${not empty interessat.canalPreferent}"><spring:message code="registre.interessat.detalls.camp.canalPreferent.${interessat.canalPreferent}"/></c:if></dd>
													<dt><spring:message code="interessat.form.camp.observacions"/></dt><dd>${interessat.observacions}</dd>
												</dl>
											</div>
											<c:if test="${not empty interessat.representant}">
												<c:set var="representant" value="${interessat.representant}"/>
												<div class="col-xs-12">
													<table class="table table-bordered">
														<thead>
															<tr><th colspan="4"><spring:message code="registre.interessat.detalls.camp.representant"/></th></tr>
															<tr>
																<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.tipus"/></th>
																<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.document"/></th>
																<th><spring:message code="registre.detalls.camp.interessat.nom"/></th>
																<th style="width: 50px;"></th>
															</tr>
														</thead>
														<tbody>
															<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if>>
																<td>
																	<spring:message code="registre.interessat.tipus.enum.${representant.tipus}"/>
																</td>
																<td>${representant.documentTipus}: ${representant.documentNum}</td>
																<c:choose>
																	<c:when test="${representant.tipus == 'PERSONA_FIS'}">
																		<td>${representant.nom} ${representant.llinatge1} ${representant.llinatge2}</td>
																	</c:when>
																	<c:otherwise>
																		<td>${representant.raoSocial}</td>
																	</c:otherwise>
																</c:choose>
																<td>
																	<c:if test="${representant.tipus != 'ADMINISTRACIO'}">
																		<button type="button" class="btn btn-default desplegable" href="#detalls_${status.index}_rep" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_${status.index}_rep">
																			<span class="fa fa-caret-down"></span>
																		</button>
																	</c:if>
																</td>
															</tr>
															<tr class="collapse detall" id="detalls_${status.index}_rep">
																<td colspan="4">
																	<div class="row">
																		<div class="col-xs-6">
																			<dl class="dl-horizontal">
																				<dt><spring:message code="interessat.form.camp.pais"/></dt><dd>${representant.pais} <c:if test="${not empty representant.paisCodi}">(${representant.paisCodi})</c:if></dd>
																				<dt><spring:message code="interessat.form.camp.provincia"/></dt><dd>${representant.provincia} <c:if test="${not empty representant.provinciaCodi}">(${representant.provinciaCodi})</c:if></dd>											
																				<dt><spring:message code="interessat.form.camp.municipi"/></dt><dd>${representant.municipi} <c:if test="${not empty representant.municipiCodi}">(${representant.municipiCodi})</c:if></dd>
																				<dt><spring:message code="interessat.form.camp.adresa"/></dt><dd>${representant.adresa}</dd>
																				<dt><spring:message code="interessat.form.camp.codiPostal"/></dt><dd>${representant.codiPostal}</dd>
																			</dl>
																		</div>
																		<div class="col-xs-6">
																			<dl class="dl-horizontal">
																				<dt><spring:message code="interessat.form.camp.email"/></dt><dd>${representant.email}</dd>
																				<dt><spring:message code="interessat.form.camp.telefon"/></dt><dd>${representant.telefon}</dd>
																				<dt><spring:message code="registre.interessat.detalls.camp.emailHabilitat"/></dt><dd>${representant.emailHabilitat}</dd>
																				<dt><spring:message code="registre.interessat.detalls.camp.canalPreferent"/></dt><dd><c:if test="${not empty representant.canalPreferent}"><spring:message code="registre.interessat.detalls.camp.canalPreferent.${representant.canalPreferent}"/></c:if></dd>
																				<dt><spring:message code="interessat.form.camp.observacions"/></dt><dd>${representant.observacions}</dd>
																			</dl>
																		</div>
																	</div>
																</td>						
															</tr>
														</tbody>
													</table>
												</div>
											</c:if>
											<!-- ------------------------ -->
										</div>
									</td>						
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:when>
				<c:otherwise>
					<div class="row col-xs-12">
						<div class="well">
							<spring:message code="registre.interessat.buit"/>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
		
		<!------------------------------------------- TABPANEL ANNEXOS --------------------------------------------->
		<div class="tab-pane" id="annexos" role="tabpanel">
			<c:choose>
				<c:when test="${not empty registre.annexos}">
					<c:forEach var="annex" items="${registre.annexos}" varStatus="status">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h3 class="panel-title">
									<span class="fa fa-file"></span>
									${annex.titol}
									<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-annex-${status.index}"><span class="fa fa-chevron-down"></span></button>
								</h3>
							</div>
 							<div id="collapse-annex-${status.index}" class="panel-collapse collapse collapse-annex" role="tabpanel" aria-labelledby="dadesAnnex${status.index}" data-registre-id="${registre.id}" data-bustia-id="${bustiaId}" data-annex-id="${annex.id}"> 

 							</div> 
						</div>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${not empty annexosErrorMsg}">
							<div class="row col-xs-12">
								<div class="alert alert-danger">
									${annexosErrorMsg}
								</div>
							</div>						
						</c:when>
						<c:otherwise>
							<div class="row col-xs-12">
								<div class="well">
									<spring:message code="registre.annex.buit"/>
								</div>
							</div>
						</c:otherwise>
					</c:choose>				
				</c:otherwise>
			</c:choose>
		</div>
		
		
		<c:if test="${not empty registre.expedientArxiuUuid}">
			<!------------------------------------------- TABPANEL ARXIU INFO --------------------------------------------->
			<div class="tab-pane" id="arxiuInfo" role="tabpanel" data-loaded=false>
				<div style='text-align: center; margin-bottom: 60px; margin-top: 60px;''><span class='fa fa-circle-o-notch fa-spin fa-3x'/></div>
			</div>
		</c:if>
	
	
	
	
		
		<c:if test="${registre.procesEstat == 'ARXIU_PENDENT' || registre.procesEstat == 'REGLA_PENDENT' || registre.procesEstat == 'BACK_PENDENT'}">
	
			<!------------------------------ TABPANEL PROCESSAMENT_AUTOMATIC ------------------------------------->
			<div class="tab-pane" id="processamentAutomatic" role="tabpanel">
			
			
				<!------ REINTENTAR PROCESSAMENT ------>
				<c:if test="${registre.procesError != null }">
					<div class="alert well-sm alert-danger alert-dismissable">
						<span class="fa fa-exclamation-triangle"></span>
						<spring:message code="registre.detalls.info.errors"/>
						
						<c:if test="${registre.procesEstat == 'ARXIU_PENDENT' || registre.procesEstat == 'REGLA_PENDENT'}">
<%-- 							<a href="../${registre.pare.id}/registre/${registre.id}/reintentar" class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentar"/></a> --%>
						</c:if>
						<c:if test="${registre.procesEstat == 'BACK_PENDENT'}">						
<%-- 							<a href="../${registre.pare.id}/registre/${registre.id}/reintentarEnviamentBackoffice" class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentarEnviamentBackoffice"/></a>					 --%>
						</c:if>
						
					</div>
				</c:if>   
			    <c:if test="${registre.procesEstat == 'BACK_PENDENT' && registre.procesError == null && registre.procesIntents > 0}">
<%-- 					<a href="../${registre.pare.id}/registre/${registre.id}/reintentarEnviamentBackoffice" class="btn btn-xs btn-default pull-right" style="margin-right: 10px;"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentarEnviamentBackoffice"/></a> --%>
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
		</c:if>
		
		
		<c:if test="${registre.procesEstat == 'BACK_REBUDA' || registre.procesEstat == 'BACK_PROCESSADA' || registre.procesEstat == 'BACK_REBUTJADA' || registre.procesEstat == 'BACK_ERROR'}">
			
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
		</c:if>	
	
	
	
	
	
	</div>
	
	<div id="modal-botons" class="well">
		<a href="<c:url value="/registreUser"/>" class="btn btn-default modal-tancar" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>