<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<% 
	pageContext.setAttribute(
				"isRolActualAdministrador",
				es.caib.distribucio.war.helper.RolHelper.isRolActualAdministrador(request));
%>

<html>
<head>
	<script type="text/javascript">
	
	$(document).ready(function() {
		var vistaMovimentsCookie = getCookie("vistaMoviments");
	    var isVistaMoviments = (vistaMovimentsCookie == "" || !JSON.parse(vistaMovimentsCookie))? false : true;
	    $("#collapse-registre-firmes-<c:out value='${annex.id}'/>").on('show.bs.collapse', function(data){  	
		    if (!$(this).data("loaded")) {
		    	var registreId = $(this).parents(".collapse-annex").data("registreId"); 
		        $(this).append("<div style='text-align: center; margin-bottom: 60px; margin-top: 60px;''><span class='fa fa-circle-o-notch fa-spin fa-3x'/></div>");
		        $(this).load("<c:url value="/nodeco/registreUser/registreAnnexFirmes/"/>/" + registreId + "/" + ${annex.id} + "/false?isVistaMoviments=" + isVistaMoviments);
		        $(this).data("loaded", true);
		    }
	    });
	    
	    $('.btn-validarFirmes').click(function() {
	    	$(this).find(".fa").addClass('fa-spin');
	    });
	});

</script>


</head>

<body>

<table class="table table-bordered">
<tbody>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.eni.data.captura"/></strong></td>
		<td><c:if test="${not empty annex.dataCaptura}"><fmt:formatDate value="${annex.dataCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:if></td>
	</tr>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.eni.origen"/></strong></td>
		<td><c:if test="${not empty annex.origenCiutadaAdmin}">${annex.origenCiutadaAdmin}</c:if></td>
	</tr>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.eni.estat.elaboracio"/></strong></td>
		<td><c:if test="${not empty annex.ntiElaboracioEstat}"><spring:message code="registre.annex.detalls.camp.ntiElaboracioEstat.${annex.ntiElaboracioEstat}"/></c:if></td>
	</tr>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.eni.tipus.documental"/></strong></td>
		<td><c:if test="${not empty annex.ntiTipusDocument}"><spring:message code="registre.annex.detalls.camp.ntiTipusDocument.${annex.ntiTipusDocument}"/></c:if></td>
	</tr>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.sicres.tipus.document"/></strong></td>
		<td><c:if test="${not empty annex.sicresTipusDocument}"><spring:message code="registre.annex.detalls.camp.sicresTipusDocument.${annex.sicresTipusDocument}"/></c:if></td>
	</tr>
	<c:if test="${not empty annex.localitzacio}">
		<tr>
			<td><strong><spring:message code="registre.annex.detalls.camp.localitzacio"/></strong></td>
			<td>${annex.localitzacio}</td>
		</tr>
	</c:if>
	<c:if test="${not empty annex.observacions}">
		<tr>
			<td><strong><spring:message code="registre.annex.detalls.camp.observacions"/></strong></td>
			<td>${annex.observacions}</td>
		</tr>
	</c:if>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.arxiu.uuid"/></strong></td>
		<td>
			${annex.fitxerArxiuUuid}
			<c:if test="${annex.fitxerArxiuUuid == null }">
				<span class="fa fa-warning text-warning" title="<spring:message code="registre.annex.detalls.camp.arxiu.uuid.buit.avis"/>"></span>
			</c:if>
		</td>
	</tr>
	
	<c:if test="${not empty annex.firmaCsv}">
		<tr>
			<td><strong><spring:message code="registre.annex.detalls.camp.firmaCsv"/></strong></td>
			<td>
				${annex.firmaCsv}
				<c:if test="${not empty concsvBaseUrl}">
					<a href="${concsvBaseUrl}/view.xhtml?hash=${annex.firmaCsv}" target="_blank" title="<spring:message code="registre.annex.detalls.camp.firmaCsv.consv"/>"><span class="fa fa-external-link"></span></a>
				</c:if>			
			</td>
		</tr>
	</c:if>
	
	<c:forEach var="metaDada" items="${annex.metaDadesMap}">
		<tr>
			<td><strong>
				<c:choose>
					<c:when test="${metaDada.key=='eni:resolucion'}">
						<spring:message code="registre.annex.detalls.camp.metaData.resolucion"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:profundidad_color'}">
						<spring:message code="registre.annex.detalls.camp.metaData.profundidad_color"/>
					</c:when>
					<c:when test="${metaDada.key=='cm:title'}">
						<spring:message code="registre.annex.detalls.camp.metaData.titol"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:idioma'}">
						<spring:message code="registre.annex.detalls.camp.metaData.idioma"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:descripcion'}">
						<spring:message code="registre.annex.detalls.camp.metaData.descripcio"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:app_tramite_doc'}">
						<spring:message code="registre.annex.detalls.camp.metaData.appTramitDoc"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:organo'}">
						<spring:message code="registre.annex.detalls.camp.metaData.organ"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:origen'}">
						<spring:message code="registre.annex.detalls.camp.metaData.origen"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:estado_elaboracion'}">
						<spring:message code="registre.annex.detalls.camp.metaData.estatElaboracio"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:tipo_doc_ENI'}">
						<spring:message code="registre.annex.detalls.camp.metaData.tipusDocEni"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:cod_clasificacion'}">
						<spring:message code="registre.annex.detalls.camp.metaData.codiClassificacio"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:csv'}">
						<spring:message code="registre.annex.detalls.camp.metaData.csv"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:def_csv'}">
						<spring:message code="registre.annex.detalls.camp.metaData.defCsv"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:id'}">
						<spring:message code="registre.annex.detalls.camp.metaData.id"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:id_origen'}">
						<spring:message code="registre.annex.detalls.camp.metaData.idOrigen"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:fecha_inicio'}">
						<spring:message code="registre.annex.detalls.camp.metaData.dataInici"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:nombre_formato'}">
						<spring:message code="registre.annex.detalls.camp.metaData.nomFormat"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:extension_formato'}">
						<spring:message code="registre.annex.detalls.camp.metaData.extensioFormat"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:tamano_logico'}">
						<spring:message code="registre.annex.detalls.camp.metaData.midaLogica"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:termino_punto_acceso'}">
						<spring:message code="registre.annex.detalls.camp.metaData.termePuntAcces"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:id_punto_acceso'}">
						<spring:message code="registre.annex.detalls.camp.metaData.idPuntAcces"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:esquema_punto_acceso'}">
						<spring:message code="registre.annex.detalls.camp.metaData.esquemaPuntAcces"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:soporte'}">
						<spring:message code="registre.annex.detalls.camp.metaData.suport"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:loc_archivo_central'}">
						<spring:message code="registre.annex.detalls.camp.metaData.locArxiuCentral"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:loc_archivo_general'}">
						<spring:message code="registre.annex.detalls.camp.metaData.arxiuGeneral"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:unidades'}">
						<spring:message code="registre.annex.detalls.camp.metaData.unitats"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:subtipo_doc'}">
						<spring:message code="registre.annex.detalls.camp.metaData.subtipusDoc"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:tipo_asiento_registral'}">
						<spring:message code="registre.annex.detalls.camp.metaData.tipusAsientoRegistral"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:codigo_oficina_registro'}">
						<spring:message code="registre.annex.detalls.camp.metaData.codiOficinaRegistre"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:fecha_asiento_registral'}">
						<spring:message code="registre.annex.detalls.camp.metaData.dataAsientoRegistral"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:numero_asiento_registral'}">
						<spring:message code="registre.annex.detalls.camp.metaData.numAsientoRegistral"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:tipoFirma'}">
						<spring:message code="registre.annex.detalls.camp.metaData.tipusFirma"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:perfil_firma'}">
						<spring:message code="registre.annex.detalls.camp.metaData.perfilFirma"/>
					</c:when>
					<c:when test="${metaDada.key=='eni:fecha_sellado'}">
						<spring:message code="registre.annex.detalls.camp.metaData.dataSegellat"/>
					</c:when>
					<c:otherwise>
						${fn:toUpperCase(metaDada.key)}
					</c:otherwise>
				</c:choose>
			</strong></td>
			<td>
				${metaDada.value}
			</td>
		</tr>
	</c:forEach>
	
	
	
	
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.tipus.mime"/></strong></td>
		<td><c:if test="${not empty annex.fitxerTipusMime}">${annex.fitxerTipusMime}</c:if></td>
	</tr>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.validacio.firma"/></strong></td>
		<td>
			<!-- Informació de la validació de firmes -->
			<c:if test="${empty annex.validacioFirmaEstat || annex.validacioFirmaEstat == 'NO_VALIDAT'}">
				- <spring:message code="validacio.firma.enum.NO_VALIDAT"></spring:message>
			</c:if>
			<c:if test="${not empty annex.validacioFirmaEstat}">
				<c:choose>
					<c:when test="${annex.validacioFirmaEstat == 'SENSE_FIRMES'}"><span class="fa fa-ban"></span></c:when>
					<c:when test="${annex.validacioFirmaEstat == 'FIRMA_VALIDA'}"><span class="fa fa-pencil-square text-success"></span></c:when>
					<c:when test="${annex.validacioFirmaEstat == 'FIRMA_INVALIDA'}"><span class="fa fa-pencil-square text-danger"></span></c:when>
					<c:when test="${annex.validacioFirmaEstat == 'ERROR_VALIDANT'}"><span class="fa fa-exclamation-triangle text-danger"></span></c:when>
				</c:choose>
				<spring:message code="validacio.firma.enum.${annex.validacioFirmaEstat}"></spring:message>
			</c:if>
			
			<c:if test="${annex.validacioFirmaEstat == 'FIRMA_INVALIDA' || annex.validacioFirmaEstat == 'ERROR_VALIDANT'}">
				: ${annex.validacioFirmaError}
			</c:if>
			
			<c:if test="${isRolActualAdministrador}">
				<a href="<c:url value="/registreAdmin/registre/${registreId}/annex/${annex.id}/validarFirmes"/>"  class="btn-validarFirmes btn btn-xs btn-default pull-right processarBtn" title="<spring:message code="contingut.admin.controller.validar.firmes.title"/>"><span class="fa fa-refresh"></span></a>
			</c:if>
			
		</td>
	</tr>
	<tr>
		<td><strong><spring:message code="registre.annex.detalls.camp.fitxer"/></strong></td>
		<td>

			${annex.fitxerNom} (<span title="${annex.fitxerTamany} bytes">${annex.fitxerTamanyStr}</span>)
		
			<c:if test="${not empty concsvBaseUrl}">
				<a class="btn btn-default btn-sm pull-right arxiu-download"
					<c:choose>
					    <c:when test="${annex.fitxerArxiuUuid != null}">
					    	href="<c:url value="/modal/contingut/registre/${registreId}/annex/${annex.id}/arxiu/DOCUMENT"/>" 
						</c:when>    
					    <c:otherwise>
					        disabled="disabled" title="<spring:message code="registre.annex.descarregar.imprimible.no.disponible"/>"
					    </c:otherwise>
					</c:choose>
				>
					<spring:message code="registre.annex.descarregar.imprimible"/>
					<span class="fa fa-print" title="<spring:message code="registre.annex.detalls.camp.fitxer.descarregar"/>"></span>
				</a>
			</c:if>			
			<a href="<c:url value="/modal/contingut/registre/${registreId}/annex/${annex.id}/arxiu/DOCUMENT_ORIGINAL"/>" class="btn btn-default btn-sm pull-right arxiu-download">
				<spring:message code="registre.annex.descarregar.original"/>
				<span class="fa fa-download" title="<spring:message code="registre.annex.detalls.camp.fitxer.descarregar"/>"></span>
			</a>	

		</td>
	</tr>
	<c:if test="${annex.ambFirma}">
		<tr>
			<td colspan="2">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<span class="fa fa-certificate"></span>
							<spring:message code="registre.annex.detalls.camp.firmes"/>
							<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-registre-firmes-${annex.id}"><span class="fa fa-chevron-down"></span></button>
						</h3>
					</div>
					<div id="collapse-registre-firmes-${annex.id}" class="panel-collapse collapse collapse-annex collapse-registre-firmes" role="tabpanel"> 

					</div> 
				</div>
			</td>
		</tr>
	</c:if>
</table>

</body>