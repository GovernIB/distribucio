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
			border: 1px solid #ccc;
			border-radius: 4px;
			width: 100%;
			padding: 2px 10px;
		}
		
		.jstree-filter {
			display: flex;
			align-items: center;
			padding-bottom: 10px;
		}
		
		.jstree-filter label {
			margin-bottom: 0px;
		}
		
		.jstree-filter div:nth-child(1) {
			width: 15%;
		}
		
		.jstree-filter div:nth-child(2) {
			width: 35%;
		}
		
		.jstree-filter div:nth-child(3) {
			margin-left: 5%;
		}
		
		.jstree-filter div:nth-child(3) label {
			display: flex;
			align-items: center;
		}
		
		.jstree-filter div:nth-child(3) input {
			margin: 0;
			height: 15px;
			width: 20px;
		}
		
		.bustia_container {
			display: flex;
		}
		
		.arbre_container {
			width: 45%;
		}
		
		.taules_container {
			width: 55%;
			margin: 2% 0 0 1%;
		}
		
		.taules_container div:nth-child(2) {
			margin-bottom: 0;
		}
		
		.taules_container div:nth-child(3) {
			display: flex;
			align-items: center;
		}
		
		.taules_container div:nth-child(3) input[type=checkbox] {
			margin: 0;
		}
		
		.busties {
			width: 100%;
		}
		
		#arbreUnitats_destins .jstree-open > .jstree-anchor > .fa-square-o, 
		#arbreUnitats_destins .jstree-closed > .jstree-anchor > .fa-square-o,
		[data-jstree*="fa-home"] > .jstree-anchor > .fa-square-o {
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
		
	</style>
	<script type="text/javascript">
		var idsBustiesFavorits = [];
		var idsPerConeixement = [];
		$(document).ready(function() {
			$('#taula_coneixement').hide();
			var $arbre = $("#arbreUnitats_destins");
			$.ajax({
	 			type: "GET",
	 			async: false,
				url: '<c:url value="/registreUser/favorits/list"/>',
				success: function (resultat) {
					idsBustiesFavorits = resultat;
				},
				error: function(e) {
					alert("hi ha hagut un recuperant la lista de ids de les bústies favorits");
				}
	 		});
			
			$('#jstree-search').on('input', function() {
				$("#arbreUnitats_destins").jstree("search", $(this).val());
			});
			
			//============= en seleccionar un node ===========
			$arbre.on("select_node.jstree", function (e, data) {
				var idNode = data.node.id;
				var nodeHrefId = '#' + data.node.a_attr.id;
				//var selectedForConeixement = $(nodeHrefId).hasClass('jstree-clicked-coneixement');
				var isPerConeixement = $(nodeHrefId).next().hasClass('coneixement');
	            var hasClassSquare = $(nodeHrefId).find('i.fa-square-o').length != 0;
	    		var hasClassClicked = $(nodeHrefId).parent().find('.jstree-clicked').length != 0;
	    		
	    		//============= canviar icona (checked/unchecked)===========
	    		if(hasClassSquare) {
	    			var currentCheckbox = $(nodeHrefId).find('i.fa-square-o');
	                currentCheckbox.removeClass('fa-square-o');
	                currentCheckbox.addClass('fa-check-square-o');
	                if(isPerConeixement) {
	                	actualitzarTaulaConeixement(idNode, isPerConeixement, false);
	                } else {
	               		actualitzarTaulaTramitacio(idNode);
	                }
	    		} else if(!hasClassClicked) {
	    			var currentCheckbox = $(nodeHrefId).find('i.fa-check-square-o');
	                currentCheckbox.removeClass('fa-check-square-o');
	                currentCheckbox.addClass('fa-square-o');
	    		}
			});
			
			//============= en deseleccionar un node ===========
			$arbre.on("deselect_node.jstree", function (e, data) {
				var idNode = data.node.id;
				var nodeHrefId = '#' + data.node.a_attr.id;
	    		var currentCheckbox = $(nodeHrefId).find('i.fa-check-square-o');
	            //============= deseleccionar ===========
	            currentCheckbox.removeClass('fa-check-square-o');
	            currentCheckbox.addClass('fa-square-o');
				esborrarDeConeixement(idNode, true);
				var isPerConeixement = $(nodeHrefId).next().hasClass('coneixement');
				if(isPerConeixement) {
					actualitzarTaulaConeixement(idNode, false, false);
		          	$(nodeHrefId).next().removeClass('coneixement');
				} else {
					actualitzarTaulaTramitacio(idNode);
				}
			});

			var favoritsCheckbox = $('#favorits');
			var checked = sessionStorage.getItem('favorites_checked');
			checked = (checked == undefined || checked == null) ? "false" : checked
			favoritsCheckbox.attr('checked', $.parseJSON(checked.toLowerCase()));
			
			favoritsCheckbox.on('change', function() {
				if($(this).is(':checked')) {
					//mostrar favorits
					sessionStorage.setItem('favorites_checked', true);
					actualitzarArbreAmbFavorits($arbre);
				} else {
					//mostrar tots
					sessionStorage.setItem('favorites_checked', false);
					$arbre.jstree(true).refresh();
				}
			});
			favoritsCheckbox.trigger('change');
		});

		var unitats = [];
		function actualitzarArbreAmbFavorits($arbre) {
			$arbre.jstree('open_all');
			
			//recupera els nodes que són unitats
			$arbre.find('li').each(function (index,value) {
		        var node = $arbre.jstree().get_node(this.id);
		        var idNode = node.id;
		    	var isUnitat = node.icon.includes('folder');
		    	if (isUnitat)
	    			unitats.push(node.id);
			});
			
			//amaga les que no són favorits
			$arbre.find('li').each(function (index,value) {
		        var node = $arbre.jstree().get_node(this.id);
		        var idNode = node.id;
		    	var isBustia = node.icon.includes('inbox');
		    	if (isBustia) {
		    		if (idsBustiesFavorits.indexOf(parseInt(idNode)) == -1) {
		    			$arbre.jstree('hide_node', idNode);
		    		}
		    	}
			});
			
			//amaga unitats sense bústies favorits
			$(unitats).each(function(idx, unitatId) {
				var senseCapBustiaFavorit = true;
				var unitat = $arbre.jstree().get_node(unitatId);
				var fills = unitat.children;
				
				$(fills).each(function(idxB, fillId) {
					var fill = $arbre.jstree().get_node(fillId);
					var isBustia = fill.icon.includes('inbox');
					var isVisible = fill.state.hidden == undefined;
					if (isBustia && isVisible) {
						senseCapBustiaFavorit = false;
						//assegurar que es mostra l'unitat de la bústia favorita
						$(fill.parents).each(function(idxP, parentId) {
							var parent = $arbre.jstree().get_node(parentId);
							$arbre.jstree('show_node', parent);
						});
						return;
					}
				});
				
				if (senseCapBustiaFavorit)
					$arbre.jstree('hide_node', unitat);
			});
			
			$('.jstree-anchor')
				.find('i.jstree-checkbox')
				.removeClass('jstree-icon jstree-checkbox')
				.addClass('fa fa-square-o');
			
			//li afegeix l'icona de favorit
			$arbre.find('li').each(function (index,value) {
		        var node = $arbre.jstree().get_node(this.id);
		        var idNode = node.id;
		    	var isBustia = node.icon.includes('inbox');
	    		var nodeHrefId = '#' + node.a_attr.id;		
		    	var nodeAnchor = $(nodeHrefId);
	            var hasClassClicked = nodeAnchor.parent().find('.jstree-clicked').length != 0;
	            var noSeleccionatPerConeixement = nodeAnchor.next('span').length == 0;
	            
		    	if (${isEnviarConeixementActiu} && isBustia && noSeleccionatPerConeixement) {
		    		nodeAnchor.after('<span id="' + idNode + '" class="info-parent" title="<spring:message code="contingut.enviar.icona.afegir.coneixement"/>"\
		    				onclick="toggleConeixement(this.id)"><i class="fa fa-info-circle"/></span>');
		    		if (idsPerConeixement.indexOf(idNode) != -1) {
		    			nodeAnchor.next().addClass('coneixement');
		    		}
		    	} else if (isBustia && noSeleccionatPerConeixement){
			    	nodeAnchor.after('<span></span>');
		    	}
		    	
		    	if (isBustia && nodeAnchor.next().next('span').length == 0) {
		    		nodeAnchor.next().after('<span id="' + idNode + '" class="star-parent" title="<spring:message code="contingut.enviar.icona.afegir.favorits"/>"\
		    				onclick="toggleFavorits(this.id)"><i class="fa fa-star"/></span>');
		    		if (idsBustiesFavorits.indexOf(parseInt(idNode)) != -1) {
		    			nodeAnchor.next().next().addClass('favorit');
		    		}
		            //============= canviar icona (checked/unchecked)===========
		    		if(hasClassClicked) {
		    			var currentCheckbox = nodeAnchor.find('i.fa-square-o');
		                currentCheckbox.removeClass('fa-square-o');
		                currentCheckbox.addClass('fa-check-square-o');
		            }	         
		    	}
			});
			$arbre.find('li[data-jstree*="fa-folder"]').find('.jstree-anchor:first').find('.fa-square-o').hide();
		}
		
		function toggleFavorits(idNode) {
			var $arbre = $("#arbreUnitats_destins");
			var node = $arbre.jstree().get_node(idNode);
			var nodeHrefId = '#' + node.a_attr.id;
			var markedAsFavorit = $(nodeHrefId).next().next().hasClass('favorit');
			if (markedAsFavorit) {
				esborrarDeFavorits(idNode);
				idsBustiesFavorits = $.grep(idsBustiesFavorits, function(value) {
				  return value != idNode;
				});
				$('#favorits').trigger('change');
			} else {
				afegirPerFavorits(idNode);
				idsBustiesFavorits.push(parseInt(idNode));
			}
		}
		
		function toggleConeixement(idNode) {
			var $arbre = $("#arbreUnitats_destins");
			var node = $arbre.jstree().get_node(idNode);
			var nodeHrefId = '#' + node.a_attr.id;
			var isPerConeixement = $(nodeHrefId).next().hasClass('coneixement');
			if (isPerConeixement) {
				esborrarDeConeixement(idNode, true);
			} else {
				afegirPerConeixement(idNode, nodeHrefId, true);
			}
		}
		
		function afegirPerConeixement(idNode, nodeHrefId, updateTaula) {
			var $arbre = $("#arbreUnitats_destins");
	    	var markedForTramitacio = $(nodeHrefId).hasClass('jstree-clicked');
	    	if (markedForTramitacio && updateTaula) {
	    		actualitzarTaulaTramitacio(idNode);
	    		actualitzarTaulaConeixement(idNode, true, false);
	    	}
	    	idsPerConeixement.push(idNode);
          	$(nodeHrefId).next().addClass('coneixement');
          	$arbre.jstree('select_node', idNode);
		}
		
		function esborrarDeConeixement(idNode, onlyConeixement) {
			var $arbre = $("#arbreUnitats_destins");
          	idsPerConeixement = $.grep(idsPerConeixement, function(value) {
				 return value != idNode;
			});
          	$arbre.jstree('deselect_node', idNode);
		}
        
		//============ afegir a favorits ================
		//# - idNode: id de la bústia
		function afegirPerFavorits(idNode) {
			var nodeHrefId = '#' + idNode + '_anchor';
			var selected = $(nodeHrefId).hasClass('jstree-clicked');
	 		$.ajax({
	 			type: "GET",
				url: '<c:url value="/registreUser/favorits/add/"/>' + idNode,
				success: function (result) {
					$('#taulaFavorits').DataTable().ajax.reload();
			 		$(nodeHrefId).next().next().addClass('favorit');
				},
				error: function(e) {
					alert("hi ha hagut un error actualitzant els favorits");
				}
	 		});
		}
		
		//============ esborrar de favorits ================
		//# - idNode: id de la bústia
		function esborrarDeFavorits(idNode) {
			var nodeHrefId = '#' + idNode + '_anchor';
			$.ajax({
	 			type: "GET",
				url: '<c:url value="/registreUser/favorits/remove/"/>' + idNode,
				success: function (result) {
					$('#taulaFavorits').DataTable().ajax.reload();
					$(nodeHrefId).next().next().removeClass('favorit');
				},
				error: function(e) {
					alert("hi ha hagut un error esborrant la bústia de favorits");
				}
	 		});
		}
		
		//============ actualitzar array amb destins (combinar per coneixement i per tramitar) ================
		//============ *al seleccionar per coneixement no s'està cridant l'event changed de jstree ============
		function updateConeixement() {
			var idsPerConeixementFiltered = [];
			var r = ($('#destins').val().length > 0) ? $('#destins').val().split(',') : [];
			
			for(i = 0, j = idsPerConeixement.length; i < j; i++) {
				var idNodeNum;
				if (idsPerConeixement[i].includes('#'))
					idNodeNum = idsPerConeixement[i].substring(1, idsPerConeixement[i].length);
				else
					idNodeNum = idsPerConeixement[i];
				
				idsPerConeixementFiltered.push(idNodeNum);
			    r.push(idNodeNum);
			}
         	//============= guardar node seleccionat manualment ===========
         	var uniqueBustiesId = [];
			$.each(r, function(i, el){
			    if($.inArray(el, uniqueBustiesId) === -1) uniqueBustiesId.push(el);
			});
          	$('#destins').val(uniqueBustiesId);
			$('#perConeixement').val(idsPerConeixementFiltered);
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
		        var idNode = node.id;
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
		            
		    		if (idsPerConeixement.indexOf(idNode) != -1) {
		    			nodeAnchor.next().toggleClass('coneixement');
		    			afegirPerConeixement(idNode, nodeHrefId, false);
		    		}
		    	}
			});
		}
		
		function actualitzarTaulaTramitacio(idNode, deselect) {
			var $arbre = $("#arbreUnitats_destins");
			var $taulaTramitacio = $('#taula_tramitacio');
			var $tbody = $taulaTramitacio.find('tbody');
			var node = $arbre.jstree().get_node(idNode);
			var existsInTable = $taulaTramitacio.find('tr#' + idNode).length > 0;
			
			if (!existsInTable) {
				$tbody.append('<tr id="' + idNode + '"><td>' + node.text + '</td><td width="55px"><button type="button" id ="' + idNode + '"class="btn btn-danger"\
						onclick="actualitzarTaulaTramitacio(this.id, true)"><span class="fa fa-trash"/></button></td></tr>');
			} else {
				if (deselect) {
					$arbre.jstree('deselect_node', idNode);
				}
				$taulaTramitacio.find('tr#' + idNode).remove();
			}
			actualitzarFilaBuida($tbody, false);
		}
		
		function actualitzarTaulaConeixement(idNode, selected, deselect) {
			var $arbre = $("#arbreUnitats_destins");
			var $taulaConeixement = $('#taula_coneixement');
			var $tbody = $taulaConeixement.find('tbody');
			var node = $arbre.jstree().get_node(idNode);
			var existsInTable = $taulaConeixement.find('tr#' + idNode).length > 0;
			
			if (!existsInTable) {
				$tbody.append('<tr id="' + idNode + '"><td>' + node.text + '</td><td width="55px"><button type="button" id ="' + idNode + '"class="btn btn-danger"\
						onclick="actualitzarTaulaConeixement(this.id, false, true)"><span class="fa fa-trash"/></button></td></tr>');
			} else {
				if (deselect) {
					esborrarDeConeixement(idNode, true);
				}
				if (!selected)
					$taulaConeixement.find('tr#' + idNode).remove();
			}
			actualitzarFilaBuida($tbody, true);
		}
		
		function actualitzarFilaBuida($tbody, hide) {
			var rows = $tbody.find('tr').length;
			var isEmptyRow = $tbody.find('tr:first').hasClass('empty');
			if (rows == 2 && isEmptyRow) {
				$tbody.find('tr:first').remove();
				$tbody.parent().show();
			} else if (rows == 0) {
				$tbody.append('<tr class="empty"><td colspan="2" class="text-center"><spring:message code="contingut.enviar.seleccionats.sensedades"/></td></tr>');
				if (hide)
					$tbody.parent().hide();
			}
		}
		
	</script>
</head>
<body>
	<form:form action="" class="form-horizontal" commandName="contingutReenviarCommand" onsubmit="updateConeixement()">
		<form:hidden path="params"/>
	    <c:choose>
	    	<c:when test="${isFavoritsPermes}">
	    	
	    		<div class="bustia_container">
		    		<div class="arbre_container">
		    			<div class="jstree-filter">
		    				<div>
								<label><spring:message code="contingut.enviar.camp.desti"/></label>
							</div>
		    				<div>
		    					<input id="jstree-search" placeholder="<spring:message code="contingut.enviar.info.cercar"/>"/>
		    				</div>
		    				<div>
								<label><input id="favorits" type="checkbox">&nbsp;<span class="star-parent favorit"><i class="fa fa-star favorit"></i></span>&nbsp;&nbsp;<spring:message code="contingut.enviar.info.filtre.favorits"/></label>
							</div>
						</div>
						<div class="busties">
							<dis:inputArbre name="destins" inline="true" textKey="contingut.enviar.camp.desti" arbre="${arbreUnitatsOrganitzatives}" required="true" fulles="${busties}" 
							fullesAtributId="id" fullesAtributNom="nom" fullesAtributPare="unitatCodi"  fullesAtributInfo="perDefecte" fullesAtributInfoKey="contingut.enviar.info.bustia.defecte" 
							fullesIcona="fa fa-inbox fa-lg" isArbreSeleccionable="${false}" isFullesSeleccionable="${true}" isOcultarCounts="${true}" isSeleccioMultiple="${true}"
							readyCallback="readyCallback" isCheckBoxEnabled="${true}" isEnviarConeixementActiu="${isEnviarConeixementActiu}" isFavoritsPermes="${isFavoritsPermes}" labelSize="0"
							showLabel="false"/>

						</div>
					</div>
		    		<div class="taules_container">
			    		<div class="form-group">
			    			<div class="col-xs-12">
			    				<label><spring:message code="contingut.enviar.seleccionats"/></label>
			    			</div>
			    			
			    			<div class="col-xs-12">
			    				<table id="taula_tramitacio" class="table table-bordered">
			    					<thead>
			    						<tr>
			    							<th><spring:message code="contingut.enviar.seleccionats.tramitacio"/></th>
			    							<th width="55px"></th>
			    						</tr>
			    					</thead>
			    					<tbody>
			    						<tr class="empty"><td colspan="2" class="text-center"><spring:message code="contingut.enviar.seleccionats.sensedades"/></td></tr>
			    					</tbody>
			    				</table>
			    			</div>
			    			<div class="col-xs-12">
			    				<table id="taula_coneixement" class="table table-bordered">
			    					<thead>
			    						<tr>
			    							<th><spring:message code="contingut.enviar.seleccionats.coneixement"/></th>
			    							<th width="55px"></th>
			    						</tr>
			    					</thead>
			    					<tbody>
			    						<tr class="empty"><td colspan="2" class="text-center"><spring:message code="contingut.enviar.seleccionats.sensedades"/></td></tr>
			    					</tbody>
			    				</table>
			    			</div>
			    		</div>			
						<form:hidden path="perConeixement"/>
						<div class="form-group col-xs-12">
							<form:checkbox path="deixarCopia" cssClass="span12" id="deixarCopia" disabled="${disableDeixarCopia}"/>
							<label for="deixarCopia" style="padding: 7px 0 0 7px;"><spring:message code="contingut.enviar.camp.deixar.copia"/></label>
						</div>
						<%--<dis:inputCheckbox name="deixarCopia" custom="true" textKey="contingut.enviar.camp.deixar.copia" disabled="${disableDeixarCopia}"/> --%>
						<dis:inputTextarea name="comentariEnviar" inline="true" rows="16" textKey="contingut.enviar.camp.comentari" labelSize="0"/>
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