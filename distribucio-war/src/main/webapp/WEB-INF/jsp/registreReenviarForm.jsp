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
	</style>
	<script type="text/javascript">
		perConeixement = [];
		$(document).ready(function() {
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

	  	          	//===== canviar text
	  		    	var nodeButton = $('a.btn-tramitar[value="' + data.node.id + '"]');
	  		    	updateButtonTramitacio(nodeButton, true);
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
	            
	          	//===== canviar text
  		    	var nodeButton = $('a.btn-tramitar[value="' + data.node.id + '"]');
  		    	updateButtonTramitacio(nodeButton, false);
  		    	
  		    	//============ esborrar de coneixement si s'ha seleccionat prèviament i actualitzar botó ================
	            if(selectedForConeixement) {
					removeFromConeixement(nodeId, nodeHrefId, true);
					//change text
			    	var nodeButton = $('a.btn-coneixement[value="' + nodeId + '"]');
			    	updateButtonConeixement(nodeButton, false);
				}
			});
			
			$('#taulaFavorits').on( 'draw.dt', function () {
				
				//============ en pitjar 'afegir/llevar per tramitar' ================
				$('.btn-tramitar').on('click', function() {
					var nodeId = $(this).attr('value');
					var nodeHrefId = '#' + nodeId + '_anchor';
					var isAfegir = $(this).find('span').hasClass('fa-plus');
		            var selectedForConeixement = $(nodeHrefId).hasClass('jstree-clicked-coneixement');
		            //============ actualitzar text/icona 'afegir per tramitar' ================
		            updateButtonTramitacio($(this), isAfegir);
		            
		            if (isAfegir) {
		           		$("#arbreUnitats_destins").jstree('select_node', nodeId);
		            } else {
		            	$("#arbreUnitats_destins").jstree('select_node', nodeId);
		            	$("#arbreUnitats_destins").jstree('deselect_node', nodeId);

				        //============ desactivar botó 'afegir per coneixement'================
			            var nodeButtonConeixement = $(this).closest('tr').find('a.btn-coneixement');
			            updateButtonConeixement(nodeButtonConeixement, false);
		            }
				});
				
				//============ en pitjar 'afegir/llevar per coneixement' ================
				$('.btn-coneixement').on('click', function() {
					var nodeId = '#' + $(this).attr('value');
					var nodeHrefId = nodeId + '_anchor';
					var isAfegir = $(this).find('span').hasClass('fa-plus');
		            var selectedForConeixement = $(nodeHrefId).hasClass('jstree-clicked-coneixement');
		           //============ actualitzar text/icona 'afegir per conexiement' ================
		          	updateButtonConeixement($(this), isAfegir);

		           //============ desactivar 'afegir per tramitar' ================
	          		var nodeButtonTramitacio = $(this).closest('tr').find('a.btn-tramitar');
		          	if (isAfegir) {
		          		nodeButtonTramitacio.addClass('disabled');
		          	} else {
		          		updateButtonTramitacio(nodeButtonTramitacio, true);
		          		nodeButtonTramitacio.removeClass('disabled');
		          	}
		          	

			        //============ actualitzar node ================
		            var hasClassSquare = $(nodeId).find('i.fa-square-o').length != 0;
		    		var hasClassClicked = $(nodeId).parent().find('.jstree-clicked').length != 0;
		    	
		    		if(hasClassSquare) {
		    			var currentCheckbox = $(nodeId).find('i.fa-square-o');
		                currentCheckbox.removeClass('fa-square-o');
		                currentCheckbox.addClass('fa-check-square-o');
		            //============ esborrar de coneixement si s'ha seleccionat prèviament ================
		    		} else if (selectedForConeixement) {
		    			removeFromConeixement(nodeId, nodeHrefId, false);
		    		}

		    	   	//============ canviar estil per coneixement ================
		            if(!selectedForConeixement) {
						addToConeixement(nodeId, nodeHrefId, false);
					} else {
						removeFromConeixement(nodeId, nodeHrefId, false);
					}
				});
				
				//============ esborrar de la taula favorits en pitjar botó taula ================
				$('.favorit-esborrar').on('click', function() {
					var bustiaId = $(this).attr('value');
					removeFromFavorits(bustiaId);
				});
			});
		});
		
		//============ afegir estil seleccionat per coneixement ================
		//# - nodeId: id node seleccionat
		//# - nodeHrefId: id href node seleccionat
		//# - selet: forçar la seva selecció
		function addToConeixement(nodeId, nodeHrefId, select) {
			if (select)
				$("#arbreUnitats_destins").jstree('select_node', nodeId);
			$(nodeHrefId).removeClass('jstree-clicked');
	    	$(nodeHrefId).addClass('jstree-clicked-coneixement');
	    	perConeixement.push(nodeId);
	    	
	    	var nodeButton = $('a.btn-coneixement[value="' + nodeId + '"]');
	    	updateButtonConeixement(nodeButton, true);
	    	
	    	// desactivar botó tramitar per no confundir
      		var nodeButtonTramitacio = nodeButton.closest('tr').find('a.btn-tramitar');
          	nodeButtonTramitacio.addClass('disabled');
		}
		
		//============ esborrar estil seleccionat per coneixement ================
		//# - nodeId: id node seleccionat
		//# - nodeHrefId: id href node seleccionat
		//# - onlyConeixement: forçar per esborrar únicament de coneixement (deixar seleccionat)
		function removeFromConeixement(nodeId, nodeHrefId, onlyConeixement) {
			if (!onlyConeixement)
				$(nodeHrefId).addClass('jstree-clicked');
	    	$(nodeHrefId).removeClass('jstree-clicked-coneixement');
	    	//var nodeIdIdx = perConeixement.indexOf(nodeId);
	    	//perConeixement.splice(nodeIdIdx, 1);
	    	//esborrat de l'arbre o taula
	    	var localNodeId = nodeId.includes('#') ? nodeId : '#' + nodeId;
	    	removeA(perConeixement, localNodeId);
	    	var nodeButton = $('a.btn-coneixement[value="' + nodeId + '"]');
	    	updateButtonConeixement(nodeButton, false);
	    	
	    	// activa botó tramitar
      		var nodeButtonTramitacio = nodeButton.closest('tr').find('a.btn-tramitar');
      		updateButtonTramitacio(nodeButtonTramitacio, true);
          	nodeButtonTramitacio.removeClass('disabled');
		}
        
		//============ esborrar de coneixement ================
		//# - element: icona node seleccionat
		//# - show: cheked/unchecked
		function toggleCheckClasses(element, show) {
	        if (show) {
	            element.removeClass('fa-square-o');
	            element.addClass('fa-check-square-o');
	        } else {
	            element.removeClass('fa-check-square-o');
	            element.addClass('fa-square-o');
	        }
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
				},
				error: function(e) {
					alert("hi ha hagut un error actualitzant els favorits");
				}
	 		});
	 		//=========== actualitzar taula si s'ha seleccionat abans d'afegir a favorits =====
	 		$('#taulaFavorits').on( 'draw.dt', function () {
				if (selectedForConeixement) {
					addToConeixement(nodeId, nodeHrefId, false);
				} else if (selected) {
					var nodeButton = $('a.btn-tramitar[value="' + nodeId + '"]');
	  		    	updateButtonTramitacio(nodeButton, true);
				}
	 		});
		}
		
		//============ esborrar de favorits ================
		//# - nodeId: id de la bústia
		function removeFromFavorits(nodeId) {
			$.ajax({
	 			type: "GET",
				url: '<c:url value="/registreUser/favorits/remove/"/>' + nodeId,
				success: function (result) {
					$('#taulaFavorits').DataTable().ajax.reload();
				},
				error: function(e) {
					alert("hi ha hagut un error esborrant la bústia de favorits");
				}
	 		});
		}
		
		//============ comprovar si és un favorit ================
		//# - nodeId: id de la bústia
		function existsInFavorits(nodeId) {
			var exists = false;
			$.ajax({
	 			type: "GET",
	 			async: false,
				url: '<c:url value="/registreUser/favorits/check/"/>' + nodeId,
				success: function (data) {
					exists = data;
				},
				error: function(e) {
					alert("hi ha hagut un error comprovant la existència del favorit");
				}
	 		});
			return exists;
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
		
		//============ canviar text/icona 'afegir per coneixement' ================
		//# - button: botó seleccionat
		//# - isAfegir: afegir or llevar
		function updateButtonConeixement(button, isAfegir) {
			if (isAfegir) {
				button.html('<span class="fa fa-minus"></span>&nbsp;&nbsp;<spring:message code="contingut.enviar.favorits.esborrar.coneixement"/>');
          	} else {
          		button.html('<span class="fa fa-plus"></span>&nbsp;&nbsp;<spring:message code="contingut.enviar.favorits.afegir.coneixement"/>');
          	}
		}
		
		//============ canviar text/icona 'afegir per tramitar' ================
		//# - button: botó seleccionat
		//# - isAfegir: afegir or llevar
		function updateButtonTramitacio(button, isAfegir) {
			if (isAfegir) {
          		button.html('<span class="fa fa-minus"></span>&nbsp;&nbsp;<spring:message code="contingut.enviar.favorits.esborrar.tramitacio"/>');
          	} else {
				button.html('<span class="fa fa-plus"></span>&nbsp;&nbsp;<spring:message code="contingut.enviar.favorits.afegir.tramitacio"/>');
          	}
		}
		
		function readyCallback(e,data) {
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
	</script>
</head>
<body>
	<form:form action="" class="form-horizontal" commandName="contingutReenviarCommand" onsubmit="updateConeixement()">
		<form:hidden path="params"/>
	    <c:choose>
	    	<c:when test="${isFavoritsPermes}">
	    		<div class="form-group">
		    		<div class="${isEnviarConeixementActiu ? 'capsalera' : ''} ${isFavoritsPermes ? 'col-xs-offset-6 col-xs-6' : 'col-xs-offset-4 col-xs-8'}">
		    			<c:if test="${isEnviarConeixementActiu}"> 
							<div class="leyenda">
								<div class="leyenda_title"><span><spring:message code="contingut.enviar.info.llegenda"/></span></div>
								<div class="leyenda_container">
									<div class="leyenda_processar">
										<div></div><span><spring:message code="contingut.enviar.info.llegenda.processar"/></span>
									</div>
									<div class="leyenda_coneixement">
										<div></div><span><spring:message code="contingut.enviar.info.llegenda.coneixement"/></span>
									</div>
								</div>
							</div>
						</c:if>
						<div class="jstree-search-container">
							<input id="jstree-search" placeholder="<spring:message code="contingut.enviar.info.cercar"/>"/>
						</div>
					</div>
				</div>
	    		<div class="bustia_container">
	    			<c:if test="${isFavoritsPermes}">
		    			<div class="favorits_container">
		    				<div class="favorits_title">
		    					<h4><spring:message code="contingut.enviar.info.favorits"/></h4>
		    				</div>
		    				<table 
								id="taulaFavorits" 
								class="table table-bordered table-striped"style="width:100%"
								data-toggle="datatable"
								data-url="<c:url value="/registreUser/favorits/datatable"/>"
								data-paging-enabled="false"
								data-default-order="1" 
								data-default-dir="desc"
								data-agrupar="5"
								class="table table-bordered table-striped"
								style="width:100%">
								<thead>
									<tr>
										<th data-col-name="id" data-visible="false"></th>
										<th data-col-name="createdDate" data-visible="false"></th>
										<th data-col-name="bustia.id" data-visible="false"></th>
										<th data-col-name="bustia.nom" data-orderable="false" width="25%"><spring:message code="contingut.enviar.camp.desti"/></th>
										
										<th data-col-name="id" data-orderable="false" data-template="#cellFavoritTramitarTemplate" width="10%">
											<script id="cellFavoritTramitarTemplate" type="text/x-jsrender">
											<a href="#" class="btn btn-tramitar" value="{{:bustia.id}}"><span class="fa fa-plus"></span>&nbsp;&nbsp;<spring:message code="contingut.enviar.favorits.afegir.tramitacio"/></a>
										</script>
										</th>
										<c:if test="${isEnviarConeixementActiu}">
											<th data-col-name="id" data-orderable="false" data-template="#cellFavoritConeixementTemplate" width="10%">
												<script id="cellFavoritConeixementTemplate" type="text/x-jsrender">
													<a href="#" class="btn btn-coneixement" value="{{:bustia.id}}"><span class="fa fa-plus"></span>&nbsp;&nbsp;<spring:message code="contingut.enviar.contextmenu.afegir.coneixement"/></a>
												</script>
											</th>
										</c:if>
										<th data-col-name="id" data-orderable="false" data-template="#cellFavoritEsborrarTemplate" width="10%">
											<script id="cellFavoritEsborrarTemplate" type="text/x-jsrender">
											<a href="#" class="btn btn-danger favorit-esborrar" value="{{:id}}"><span class="fa fa-trash"></span></a>
										</script>
										</th>
									</tr>
								</thead>
							</table>
		    			</div>
	    			</c:if>
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