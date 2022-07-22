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
	pageContext.setAttribute(
			"isRolActualAdminLectura",
			es.caib.distribucio.war.helper.RolHelper.isRolActualAdminLectura(request));
%>

<c:set var="titol"><spring:message code="contingut.comentaris.titol"/></c:set>
<html>
<head>
	<title>${titol}: ${contingut.nom}</title>
	<dis:modalHead/>
	
	<style>
		.enviar-comentari {
			width: 84%
		}
		.comentari-bocata {
			width: 85%;
		}
		.comentari-autor {
			color: #999;
		}
		div.comentari-autor {
			margin-bottom: 6px;
		}
		.comentari-propi {
			background-color: #dfeecf;
		}
		#comentaris_content {
		 	overflow: hidden;
		    height: 500px;
		    max-height: 500px;
		    overflow-y: scroll;
		    margin-bottom: 15px;
		}
		.comentari-propi p {
			white-space: pre-line;
		}
		.comentari_destins {
			position: absolute;
			width: 55%;
			height: auto;
			border: 1px solid #dcdcdc;
			border-radius: 5px;
			background-color: #ffffff; 
			color: #676666;
			bottom: 100%;
			margin-bottom: 1px;
		}
		.comentari_destins ul {
			list-style: none;
			padding: 1%;
			margin-bottom: 0;
		}
		.comentari_destins li {
			padding: 1%;
			cursor: pointer;
		}
		.comentari_destins hr {
			margin-top: 0 !important;
			margin-bottom: 0 !important;
		}
		.comentari-error {
			background-color: #fff3cd;
			color: #856404;
			border: 1px solid transparent;
			border-radius: 5px;
			border-color: #ffeeba;
			padding: 6px;
			
		}
		.comentari-error p {
			margin: 0;
		}
		.codi_usuari {
			color: #c30000;
		}
	</style>
	
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script type="text/javascript">
		var filtre;
		$(document).ready(function() {
			var searchEnabled = false;
			var searchBox = $('.comentari_destins');
			searchBox.hide();
			
			$('button.enviar-comentari').click(function() {
				var text = $("#comentari_text").val();
				if (text != undefined && text != "") {
					enviarLlistarComentaris(text);
				}
			});
			
			enviarLlistarComentaris("");
			
			var inputComentari = $('#comentari_text');
			inputComentari.on('keyup', function(event) {
				var currentPosition = document.getElementById('comentari_text').selectionStart;
				var field = $(this);
				//if @, disable if 'space'
				if (event.which == 50 || searchEnabled) {
					$(document).trigger('cercar', [field]);
					searchEnabled = true;
					
					if (event.which == 32)
						searchEnabled = false;
				}
				//per modificació: if not 'space' and already loaded (first search)
				if (event.which != 32 && !searchBox.is(':visible')) {
					var valueCurrentPosition = inputComentari.val().charAt(--currentPosition);
					while(valueCurrentPosition != '' && valueCurrentPosition != ' ') {
						valueCurrentPosition = inputComentari.val().charAt(--currentPosition);
						if (valueCurrentPosition == '@') {
							$(document).trigger('cercar', [field,currentPosition]);
						}
					}
				}
				if (event.which == 32)
					searchBox.hide();
				
				scrollFinal();
			});
			
			$(document).on('cercar', function(event, field, index) {
				var valueToSearch;
				//========= índex codi usuari
				if (index == undefined)
					index =  field.val().lastIndexOf('@');
				//========= Si conté espais cercar fins espai si no cercar fins final
				var lastIndex = field.val().indexOf(' ', index) != -1 ? field.val().indexOf(' ', index) : field.val().length;
				valueToSearch = field.val().substring(index + 1, lastIndex);
				filtre = valueToSearch;
				var urlInicial = "<c:url value="/userajax/usuaris/"/>" + valueToSearch;
				$.ajax({
					url: urlInicial,
					async: false,
					success: function(resposta) {
						if (resposta.length > 0) {
							searchBox.find('ul').empty();
							resposta.forEach(function(user, idx) {
								searchBox.show();
								var text = user.codi + ' (' + user.nom + (user.nif != null ? ' - ' + user.nif : '') + ')';
								searchBox.find('ul').append('<li onclick="seleccionarUsuari(this.id)" id="' + user.codi + '">' + text + '</li>' + (idx === resposta.length - 1 ? '' : '<hr>'));
							});
						} else {
							console.log('No s&#39;ha trobat cap usuari amb el filtre especificat [filtre=' + valueToSearch + ']'); 
							searchBox.hide();
						}
						if (resposta.length > 11) {
							searchBox.find('ul').css('height', '300px');
							searchBox.find('ul').css('overflow-y', 'scroll');
						} else {
							searchBox.find('ul').removeAttr('style');
						}
					},
					error: function(xhr, ajaxOptions, thrownError) {
						searchBox.hide();
						console.log('Hi ha hagut un error cercant els usuaris per filtre [filtre=' + valueToSearch + ']'); 
					}
				});
			});
		});
		
		function seleccionarUsuari(codi) {
			var searchBox = $('.comentari_destins');
			var currentFiltre = '@' + filtre;
			var currentUserCodi = '@' + codi;
			var currentInputValue = $('#comentari_text').val();
			var newInputValue = currentInputValue.replace(currentFiltre, currentUserCodi);
			$('#comentari_text').val(newInputValue);
			searchBox.hide();
		}
		
		function enviarLlistarComentaris(text) {
			$.ajax({
				type: 'POST',
				url: "<c:url value="/contingut/${contingut.id}/comentaris/publicar"/>",
				data: {
					text: text
				},
				success: function(data) {
					pintarComentaris(data);
					$("#comentari_text").val("");
					webutilModalAdjustHeight();
				},
				error: function() {
					console.log("error enviant comentari..");
				}
			});
		}
		
		function pintarComentaris(resposta) {
			var comentaris = resposta.comentaris;
			$("#comentaris_content").empty();
			for (var comentariIndex in comentaris) {
				var comentari = comentaris[comentariIndex];
				
				var fons = "";
				var pull = "left";
				var propi = comentari.createdBy.codi == "${usuariActual.codi}"; 
				if (propi) {
					pull = "right"
					fons = "comentari-propi";
				}
					
				var comentariHtml = '<div class="well comentari-bocata pull-' + pull + ' ' + fons + '">';
				if (!propi)
					comentariHtml += '<div class="comentari-autor"><strong>' + comentari.createdBy.nom + '</strong></div>';
				
				comentariHtml +='<p>' + comentari.text + '</p>' +
				'<small class="pull-right comentari-autor">' + comentari.createdDateAmbFormat + '</small>' +
				'</div>';
				$("#comentaris_content").append(comentariHtml);
			}
			if (resposta.error) {
				var errors = resposta.errorsDescripcio;
				var comentariHtml = '<div class="comentari-error pull-left">';
				for (var errorIndex in errors) {
					var error = errors[errorIndex];
					comentariHtml +='<p><span class="fa fa-exclamation-triangle"></span>&nbsp;&nbsp;' + error + '</p>';
				}
				comentariHtml += '</div>';
				$("#comentaris_content").append(comentariHtml);
			}
			scrollFinal();
		}
		
		function scrollFinal() {
			var contenidor = $('#comentaris_content');
			var height = contenidor[0].scrollHeight;
			contenidor.scrollTop(height);
		}
	</script>
	
</head>
<body>

	<div id="comentaris_content" class="col-xs-12">
	</div>
	<div class="col-xs-10">
		<div class="comentari_destins"><ul></ul></div>
		<input id="comentari_text" class="form-control" placeholder="<spring:message code="contingut.comentaris.text.placeholder"/>" maxlength="1024" ${hasPermisBustia ? '' : 'disabled'}/>
	</div>
	<div class="col-xs-2">
		<button class="btn btn-success enviar-comentari ${hasPermisBustia ? '' : 'disabled'}"><span class="fa fa-paper-plane-o"></span>&nbsp;<spring:message code="comu.boto.enviar"/></button>
	</div>
	<div class="col-xs-12" style="height:10px">
	</div>

	<div id="modal-botons" class="well">
		<a href="<c:url value="/registreUser"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>
