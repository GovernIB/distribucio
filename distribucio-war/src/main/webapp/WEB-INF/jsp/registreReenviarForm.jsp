<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="bustia.pendent.registre.reenviar.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<dis:modalHead titol="${titol}"/>
	<%--
	<link href="<c:url value="/css/jstree.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jstree.min.js"/>"></script>
	 --%>
	<link href="<c:url value="/webjars/jstree/3.2.1/dist/themes/default/style.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/webjars/jstree/3.2.1/dist/jstree.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/webjars/Sortable/1.4.2/Sortable.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<style type="text/css">
		.jstree-default a.jstree-search {
			color: #666666;
			background-color: #F8F8CF;
			font-weight: normal;
			font-style: normal;
		}
		
		.jstree-clicked-coneixement {
			background-color: #bee882 !important;
		}
		
		.jstree-default .jstree-clicked {
			background-color: #BEEBFF !important;
		}
		
		#jstree-search {
			float: right;
			border: 1px solid #ccc;
			border-radius: 4px;
		}
		
		.jstree-search-container {
			place-self: flex-end;
		}
		
		.bustia_container {
			display: flex;
		}
		
		.busties {
			width: 100%;
		}
		
		#arbreUnitats_destins .jstree-open > .jstree-anchor > .fa-square-o, 
		#arbreUnitats_destins .jstree-closed > .jstree-anchor > .fa-square-o {
			display:none;
		}
		
		i.fa.fa-check-square-o, i.fa.fa-square-o {
			font-size: 18px;
			margin-left: 2px;
			height: 24px;
			line-height: 24px;
		}

		.vakata-context {
			padding: 0px !important;
			box-shadow: 1px 1px 1px #999 !important;
		}
		
		.leyenda {
			width: 20%;
			padding: 1%;
			display: flex;
			flex-flow: column;
		}
		
		.leyenda_title {
			font-weight: bold;
		}
		
		.leyenda_container {
			height: auto;
			display: flex;
			flex-flow: column;
			border: 1px solid black;
			border-radius: 6px;
			padding: 5%;
			font-size: 12px;
		}
		
		.leyenda_container div {
			display: flex;
			align-items: center;
		}
		
		.leyenda_container > div > div:nth-child(1) {
			width: 10px;
			height: 10px;
		}
		
		.leyenda_processar > div {
			background-color: #BEEBFF;
		}
		
		.leyenda_coneixement > div {
			background-color: #bee882;
		}
		
		.leyenda_container > div > span {
			margin-left: 1%;
		}
		
		.favorits_container {
			width: 60%;
		}
		
		.capsalera {
			display: flex;
			justify-content: space-between;
		}
		
		.btn-tramitar, .btn-tramitar:hover {
			color: #fff !important;
			background-color: #beebff;
			border-color: #beebff;
		}
		
		.btn-coneixement, .btn-coneixement:hover {
			color: #fff !important;
			background-color: #bee882;
			border-color: #bee882;
		}
		
		.favorits_title {
			text-decoration: underline;
		}
		
		.star-parent {
			display: inline-flex;
			justify-content: center;
			align-items: center;
			height: 18px;
			width: 18px;
			border-radius: 50%;
			background-color: #f2c185;
			cursor: pointer;
			margin-left: 4px;
		}
		
		.star-parent.favorit {
			background-color: #ed8607 !important;
		}
		
		.star-parent i {
			color: #fff;
			font-size: 11px;	
		}
		
		.info-parent {
			display: inline-flex;
			justify-content: center;
			align-items: center;
			height: 18px;
			width: 18px;
			border-radius: 50%;
			background-color: #a6def4;
			cursor: pointer;
			margin-left: 5px;
		}
		
		.info-parent.coneixement {
			background-color: #00aaed !important;
		}
		
		.info-parent i {
			color: #fff;
			font-size: 11px;		
		}
		
		input#favorits {
			position: absolute;
			top: 0;
			left: 0;
			height: 15px;
			width: 20px;
		}
		
	</style>
	<script type="text/javascript">
		var idsBustiesFavorits = [];
		perConeixement = [];
		$(document).ready(function() {
			$.ajax({
	 			type: "GET",
	 			async: false,
				url: '<c:url value="/registreUser/favorits/list"/>',
				success: function (result) {
					idsBustiesFavorits = result;
				},
				error: function(e) {
					alert("hi ha hagut un recuperant la lista de ids de les bústies favorits");
				}
	 		});
			
			$('#jstree-search').on('input', function() {
				$("#arbreUnitats_destins").jstree("search", $(this).val());
			});
			
			//============= en seleccionar un node ===========
			$("#arbreUnitats_destins").on("select_node.jstree", function (e, data) {
				var nodeId = '#' + data.node.id;
				var nodeHrefId = '#' + data.node.a_attr.id;
				var selectedForConeixement = $(nodeHrefId).hasClass('jstree-clicked-coneixement');
	            var hasClassSquare = $(nodeHrefId).find('i.fa-square-o').length != 0;
	    		var hasClassClicked = $(nodeHrefId).parent().find('.jstree-clicked').length != 0;
	    		
	    		//============= canviar icona (checked/unchecked)===========
	    		if(hasClassSquare) {
	    			var currentCheckbox = $(nodeHrefId).find('i.fa-square-o');
	                currentCheckbox.removeClass('fa-square-o');
	                currentCheckbox.addClass('fa-check-square-o');
	    		} else if(!hasClassClicked) {
	    			var currentCheckbox = $(nodeHrefId).find('i.fa-check-square-o');
	                currentCheckbox.removeClass('fa-check-square-o');
	                currentCheckbox.addClass('fa-square-o');
	                //============ esborrar de coneixement si s'ha seleccionat prèviament i deseleccionar ================
	                if (selectedForConeixement) {
	                	removeFromConeixement(nodeId, nodeHrefId, false);
	                	$("#arbreUnitats_destins").jstree('deselect_node', nodeId);
	                }
	    		}
	    		//============ seleccionat de la taula esborrat de l'arbre ================
	    		if (selectedForConeixement) {
                	removeFromConeixement(data.node.id, nodeHrefId, false);
                }
			});
			
			//============= en deseleccionar un node ===========
			$("#arbreUnitats_destins").on("deselect_node.jstree", function (e, data) {
				var nodeId = '#' + data.node.id;
				var nodeHrefId = '#' + data.node.a_attr.id;
	    		var currentCheckbox = $(nodeHrefId).find('i.fa-check-square-o');
	            var selectedForConeixement = $(nodeHrefId).hasClass('jstree-clicked-coneixement');
	            
	            //============= deseleccionar ===========
	            currentCheckbox.removeClass('fa-check-square-o');
	            currentCheckbox.addClass('fa-square-o');
  		    	//============ esborrar de coneixement si s'ha seleccionat prèviament i actualitzar botó ================
	            if(selectedForConeixement) {
					removeFromConeixement(nodeId, nodeHrefId, true);
				}
			});
	
			$('#favorits').on('change', function() {
				var $arbre = $("#arbreUnitats_destins");
				if($(this).is(':checked')) {
					//mostrar favorits
					actualitzarArbreAmbFavorits($arbre);
				} else {
					//recarregar arbre
					$arbre.jstree(true).refresh();
				}
			});
		});
		
		function actualitzarArbreAmbFavorits($arbre) {
			$arbre.jstree('open_all');
			//amaga les que no són favorits
			$arbre.find('li').each(function (index,value) {
		        var node = $arbre.jstree().get_node(this.id);
		        var nodeId = node.id;
		    	var isBustia = node.icon.includes('inbox');
		    	if (isBustia) {
		    		if (idsBustiesFavorits.indexOf(parseInt(nodeId)) == -1)
		    			$arbre.jstree('hide_node', nodeId);
		    	}
			});

			$('.jstree-anchor')
				.find('i.jstree-checkbox')
				.removeClass('jstree-icon jstree-checkbox')
				.addClass('fa fa-square-o');
			
			//li afegeix l'icona de favorit
			$arbre.find('li').each(function (index,value) {
		        var node = $arbre.jstree().get_node(this.id);
		        var nodeId = node.id;
		    	var isBustia = node.icon.includes('inbox');
		    	var nodeAnchor = $('#' + node.a_attr.id);
		    	if (isBustia && nodeAnchor.next('span').length == 0) {
		    		nodeAnchor.after('<span id="' + nodeId + '" class="star-parent" title="<spring:message code="contingut.enviar.icona.afegir.favorits"/>"\
		    				onclick="toggleFavorits(this.id)"><i class="fa fa-star"/></span>');
		    		if (idsBustiesFavorits.indexOf(parseInt(nodeId)) != -1) 
		    			nodeAnchor.next().addClass('favorit');
		    		
		    		var nodeHrefId = '#' + node.a_attr.id;		            
		            var hasClassClicked = $(nodeHrefId).parent().find('.jstree-clicked').length != 0;
		            //============= canviar icona (checked/unchecked)===========
		    		if(hasClassClicked) {
		    			var currentCheckbox = $(nodeHrefId).find('i.fa-square-o');
		                currentCheckbox.removeClass('fa-square-o');
		                currentCheckbox.addClass('fa-check-square-o');
		            }	         
		    	}
		    	
		    	if (${isEnviarConeixementActiu} && isBustia && nodeAnchor.next().next('span').length == 0) {
		    		nodeAnchor.next().after('<span id="' + nodeId + '" class="info-parent" title="<spring:message code="contingut.enviar.icona.afegir.coneixement"/>"\
		    				onclick="toggleConeixement(this.id)"><i class="fa fa-info-circle"/></span>');
		    		if (perConeixement.indexOf(nodeId) != -1) {
		    			nodeAnchor.next().next().addClass('coneixement');
		    			addToConeixement(nodeId, nodeHrefId, false);
		    		}
		    	}
			});
			$arbre.find('li[data-jstree*="fa-folder"]').find('.jstree-anchor:first').find('.fa-square-o').hide();
		}
		
		function toggleFavorits(nodeId) {
			var $arbre = $("#arbreUnitats_destins");
			var node = $arbre.jstree().get_node(nodeId);
			var nodeHrefId = '#' + node.a_attr.id;
			var markedAsFavorit = $(nodeHrefId).next().hasClass('favorit');
			if (markedAsFavorit) {
				removeFromFavorits(nodeId);
				idsBustiesFavorits = $.grep(idsBustiesFavorits, function(value) {
				  return value != nodeId;
				});
				$('#favorits').trigger('change');
			} else {
				addToFavorits(nodeId);
				idsBustiesFavorits.push(parseInt(nodeId));
			}
		}
		
		function toggleConeixement(nodeId) {
			var $arbre = $("#arbreUnitats_destins");
			var node = $arbre.jstree().get_node(nodeId);
			var nodeHrefId = '#' + node.a_attr.id;
			var addToConeixmenet = $(nodeHrefId).next().next().hasClass('coneixement');
			if (addToConeixmenet) {
				removeFromConeixement(nodeId, nodeHrefId, false);
			} else {
				addToConeixement(nodeId, nodeHrefId, true);
			}
		}
		
		function addToConeixement(nodeId, nodeHrefId, select) {
			if (select)
				$("#arbreUnitats_destins").jstree('select_node', nodeId);
			/*$(nodeHrefId).removeClass('jstree-clicked');
	    	$(nodeHrefId).addClass('jstree-clicked-coneixement');*/
	    	perConeixement.push(nodeId);
          	$(nodeHrefId).next().next().addClass('coneixement');
		}
		
		function removeFromConeixement(nodeId, nodeHrefId, onlyConeixement) {
			if (!onlyConeixement)
				$(nodeHrefId).addClass('jstree-clicked');
	    	$(nodeHrefId).removeClass('jstree-clicked-coneixement');
          	perConeixement = $.grep(perConeixement, function(value) {
				 return value != nodeId;
			});
          	$(nodeHrefId).next().next().removeClass('coneixement');
		}
        
		//============ afegir a favorits ================
		//# - nodeId: id de la bústia
		function addToFavorits(nodeId) {
			var nodeHrefId = '#' + nodeId + '_anchor';
			var selected = $(nodeHrefId).hasClass('jstree-clicked');
			var selectedForConeixement = $(nodeHrefId).hasClass('jstree-clicked-coneixement');
	 		$.ajax({
	 			type: "GET",
				url: '<c:url value="/registreUser/favorits/add/"/>' + nodeId,
				success: function (result) {
					$('#taulaFavorits').DataTable().ajax.reload();
			 		$(nodeHrefId).next().addClass('favorit');
				},
				error: function(e) {
					alert("hi ha hagut un error actualitzant els favorits");
				}
	 		});
		}
		
		//============ esborrar de favorits ================
		//# - nodeId: id de la bústia
		function removeFromFavorits(nodeId) {
			var nodeHrefId = '#' + nodeId + '_anchor';
			$.ajax({
	 			type: "GET",
				url: '<c:url value="/registreUser/favorits/remove/"/>' + nodeId,
				success: function (result) {
					$('#taulaFavorits').DataTable().ajax.reload();
					$(nodeHrefId).next().removeClass('favorit');
				},
				error: function(e) {
					alert("hi ha hagut un error esborrant la bústia de favorits");
				}
	 		});
		}
		
		//============ actualitzar array amb destins (combinar per coneixement i per tramitar) ================
		//============ *al seleccionar per coneixement no s'està cridant l'event changed de jstree ============
		function updateConeixement() {
			var forConeixement = [];
			var r = ($('#destins').val().length > 0) ? $('#destins').val().split(',') : [];
			
			for(i = 0, j = perConeixement.length; i < j; i++) {
				var nodeIdNum;
				if (perConeixement[i].includes('#'))
					nodeIdNum = perConeixement[i].substring(1, perConeixement[i].length);
				else
					nodeIdNum = perConeixement[i];
				
				forConeixement.push(nodeIdNum);
			    r.push(nodeIdNum);
			}
         	//============= guardar node seleccionat manualment ===========
         	var uniqueBustiesId = [];
			$.each(r, function(i, el){
			    if($.inArray(el, uniqueBustiesId) === -1) uniqueBustiesId.push(el);
			});
          	$('#destins').val(uniqueBustiesId);
			$('#perConeixement').val(forConeixement);
		}
		
		function readyCallback(e,data) {
			var $arbre = $("#arbreUnitats_destins");
			$arbre.jstree('close_all');
			if (e.currentTarget.childNodes != undefined) {
				e.currentTarget.childNodes.forEach(function(parent) {
					openNodes(parent);
				});
			}
		}
		
		function openNodes(parent) {
			var maxLevel = ${maxLevel};
			var openNextLevel = true;
			
			parent.childNodes.forEach(function(child) {
				if (child.id != undefined && child.id != '') {
					$("#arbreUnitats_destins").jstree('open_node', '#' + child.id);
					var currentLevel = child.getAttribute('aria-level');
					if (currentLevel >= maxLevel) {
						openNextLevel = false;
					}
					if (openNextLevel) {
						openNodes(child.lastElementChild);
					}
				}
			});
		}
		
		function checkSelectedNodes() {
			var $arbre = $("#arbreUnitats_destins");
			//li afegeix l'icona de favorit
			$arbre.find('li').each(function (index,value) {
		        var node = $arbre.jstree().get_node(this.id);
		        var nodeId = node.id;
		        var nodeHrefId = '#' + node.a_attr.id;
		    	var isBustia = node.icon.includes('inbox');
		    	var nodeAnchor = $('#' + node.a_attr.id);
		    	
		    	if (isBustia) {
		    		var nodeHrefId = '#' + node.a_attr.id;		            
		            var hasClassClicked = $(nodeHrefId).parent().find('.jstree-clicked').length != 0;
		            //============= canviar icona (checked/unchecked)===========
		    		if(hasClassClicked) {
		    			var currentCheckbox = $(nodeHrefId).find('i.fa-square-o');
		                currentCheckbox.removeClass('fa-square-o');
		                currentCheckbox.addClass('fa-check-square-o');
		            }	         
		            
		    		if (perConeixement.indexOf(nodeId) != -1) {
		    			nodeAnchor.next().next().toggleClass('coneixement');
		    			addToConeixement(nodeId, nodeHrefId, false);
		    		}
		    	}
			});
		}
	</script>
</head>
<body>
	<form:form action="" class="form-horizontal" commandName="contingutReenviarCommand" onsubmit="updateConeixement()">
		<form:hidden path="params"/>
	    <c:choose>
	    	<c:when test="${isFavoritsPermes}">
	    		<div class="form-group">
		    		<div class="${isFavoritsPermes ? 'col-xs-offset-3 col-xs-9' : 'col-xs-offset-4 col-xs-8'}">
						<div class="jstree-search-container">
							<label><input id="favorits" type="checkbox">&nbsp;<span class="star-parent"><i class="fa fa-star favorit"></i></span>&nbsp;&nbsp;<spring:message code="contingut.enviar.info.filtre.favorits"/></label>
							<input id="jstree-search" placeholder="<spring:message code="contingut.enviar.info.cercar"/>"/>
						</div>
					</div>
				</div>
	    		<div class="bustia_container">
	    			
					<div class="busties">
						<dis:inputArbre name="destins" textKey="contingut.enviar.camp.desti" arbre="${arbreUnitatsOrganitzatives}" required="true" fulles="${busties}" 
						fullesAtributId="id" fullesAtributNom="nom" fullesAtributPare="unitatCodi"  fullesAtributInfo="perDefecte" fullesAtributInfoKey="contingut.enviar.info.bustia.defecte" 
						fullesIcona="fa fa-inbox fa-lg" isArbreSeleccionable="${false}" isFullesSeleccionable="${true}" isOcultarCounts="${true}" isSeleccioMultiple="${true}"
						readyCallback="readyCallback" isCheckBoxEnabled="${true}" isEnviarConeixementActiu="${isEnviarConeixementActiu}" isFavoritsPermes="${isFavoritsPermes}" labelSize="${isFavoritsPermes ? '2' : ''}"/>
					
						<form:hidden path="perConeixement"/>
						<dis:inputCheckbox name="deixarCopia" textKey="contingut.enviar.camp.deixar.copia" disabled="${disableDeixarCopia}" labelSize="${isFavoritsPermes ? '2' : ''}"/>
						<dis:inputTextarea name="comentariEnviar" textKey="contingut.enviar.camp.comentari" labelSize="${isFavoritsPermes ? '2' : ''}"/>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<div class="form-group">
					<div class="col-xs-offset-4 col-xs-8">
					  	<c:if test="${selectMultiple}"> 
						    <span class="fa fa-exclamation-triangle text-success" title=""></span>
						  	<spring:message code="contingut.enviar.info.seleccio.multiple"/>
					  	</c:if>
					    <input id="jstree-search" placeholder="<spring:message code="contingut.enviar.info.cercar"/>"/>
					</div>
				</div>
				<dis:inputArbre name="destins" textKey="contingut.enviar.camp.desti" arbre="${arbreUnitatsOrganitzatives}" required="true" fulles="${busties}" 
				fullesAtributId="id" fullesAtributNom="nom" fullesAtributPare="unitatCodi"  fullesAtributInfo="perDefecte" fullesAtributInfoKey="contingut.enviar.info.bustia.defecte" 
				fullesIcona="fa fa-inbox fa-lg" isArbreSeleccionable="${false}" isFullesSeleccionable="${true}" isOcultarCounts="${true}" isSeleccioMultiple="${true}"
				readyCallback="readyCallback" isCheckBoxEnabled="${false}" isEnviarConeixementActiu="${false}"/>
			
				<form:hidden path="perConeixement"/>
				<dis:inputCheckbox name="deixarCopia" textKey="contingut.enviar.camp.deixar.copia" disabled="${disableDeixarCopia}"/>
				<dis:inputTextarea name="comentariEnviar" textKey="contingut.enviar.camp.comentari"/>
			</c:otherwise>
		</c:choose>
	    
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-send"></span> <spring:message code="comu.boto.enviar"/></button>
			<a href="#" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>