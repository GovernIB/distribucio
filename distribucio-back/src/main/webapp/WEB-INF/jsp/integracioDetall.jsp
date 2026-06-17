<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:set var="titol"><spring:message code="integracio.detall.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<dis:modalHead/>

    <style>
        .pre-wrapper { position: relative; }

        .btn-copiar-pre {
            position: absolute;
            top: -35px;
            right: 0px;
            background: #f8f9fa;
            border: 1px solid rgba(0,0,0,0.15);
            border-radius: 4px;
            padding: 4px 8px;
            cursor: pointer;
            opacity: 0.9;
            transition: all 0.2s;
            z-index: 10;
        }
        .btn-copiar-pre:hover { opacity: 1; background: #e9ecef; }
        .btn-copiar-pre.copiado {
            border-color: #28a745;
            color: #28a745;
            background: #f0fff4;
        }
    </style>

    <script>
        $(document).ready(function() {
            $('.btn-copiar-pre').on('click', function() {
                var $btn = $(this);
                var $pre = $($btn.data('target'));
                var texto = $pre.text();

                if (navigator.clipboard && navigator.clipboard.writeText) {
                    navigator.clipboard.writeText(texto).then(function() {
                        mostrarExito($btn);
                    }).catch(function() {
                        fallbackCopy(texto, $btn);
                    });
                } else {
                    fallbackCopy(texto, $btn);
                }
            });
        });

        function mostrarExito($btn) {
            var $icon = $btn.find('span');
            $icon.removeClass('fa-copy').addClass('fa-check');
            $btn.addClass('copiado').attr('title', '');

            setTimeout(function() {
                $icon.removeClass('fa-check').addClass('fa-copy');
                $btn.removeClass('copiado').attr('title', '<spring:message code="integracio.detall.camp.excepcio.copyBtn.label"/>');
            }, 2000);
        }

        function fallbackCopy(text, $btn) {
            var $temp = $('<textarea>').css({ position: 'fixed', opacity: 0 });
            $('body').append($temp);
            $temp.val(text).select();
            try {
                document.execCommand('copy');
                mostrarExito($btn);
            } catch (err) {
                console.error('No se pudo copiar:', err);
                alert('<spring:message code="integracio.detall.camp.excepcio.copyBtn.error"/>');
            }
            $temp.remove();
        }
    </script>
</head>
<body>
	<c:if test="${not empty integracio}">
		<dl class="dl-horizontal">
			<dt><spring:message code="integracio.detall.camp.data"/></dt>
			<dd><fmt:formatDate value="${integracio.data}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
			<dt><spring:message code="integracio.detall.camp.descripcio"/></dt>
			<dd>${integracio.descripcio}</dd>
			<dt><spring:message code="integracio.detall.camp.tipus"/></dt>
			<dd>${integracio.tipus}</dd>
			<dt><spring:message code="integracio.detall.camp.usuari"/></dt>
			<dd>${integracio.codiUsuari}</dd>
			<dt><spring:message code="integracio.detall.camp.entitat"/></dt>
			<dd>${integracio.codiEntitat}</dd>
			<dt><spring:message code="integracio.detall.camp.estat"/></dt>
			<dd>${integracio.estat}</dd>
			<c:if test="${not empty integracio.parametres}">
				<dt><spring:message code="integracio.detall.camp.params"/></dt>
				<dd>
					<ul>
						<c:forEach var="parametre" items="${integracio.parametres}">
							<li><strong>${parametre.nom}:</strong> ${parametre.descripcio}</li>
						</c:forEach>
					</ul>
				</dd>
			</c:if>			
			<c:if test="${integracio.estat == 'ERROR'}">
				<dt><spring:message code="integracio.detall.camp.error.desc"/></dt>
				<dd>
					${fn:escapeXml(integracio.errorDescripcio)}
				</dd>
				<dt><spring:message code="integracio.detall.camp.excepcio.missatge"/></dt>
				<dd>${fn:escapeXml(integracio.excepcioMessage)}</dd>
			</c:if>
		</dl>
		<c:if test="${integracio.estat == 'ERROR' && not empty integracio.excepcioMessage}">
            <div class="pre-wrapper">
                <pre  id="codigoEjemplo" style="height:300px">${fn:escapeXml(integracio.excepcioStacktrace)}</pre>
                <button type="button" class="btn-copiar-pre" data-target="#codigoEjemplo" title="<spring:message code="integracio.detall.camp.excepcio.copyBtn.label"/>">
                    <spring:message code="integracio.detall.camp.excepcio.copyBtn.label"/><span class="ml-1 fa fa-copy"></span>
                </button>
            </div>
		</c:if>
	</c:if>
	<div id="modal-botons">
		<a href="<c:url value="/integracio/${codiActual}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>

</body>