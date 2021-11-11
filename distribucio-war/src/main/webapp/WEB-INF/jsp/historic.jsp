<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="es.caib.distribucio.core.api.dto.historic.HistoricTipusEnumDto" %>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="decorator.menu.estadistiques"/></title>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.full.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/webjars/chartjs/2.9.4/Chart.min.js"/>"></script>
	<script src="<c:url value="/webjars/moment/2.15.1/min/moment.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>

	<style>
	.div-dades-carregant {
		min-width: 100%;
		min-height: 300px;
		background-color: #e8e8e8;
		height: 100%;
		left: 0;
		opacity: 0.5;
		position: absolute;
		top: 0;
		width: 100%;
		z-index: 99999;
	}
		
	.div-dades-carregant span {
	    left: 50%;
	    opacity: 0.85;
	    position: absolute;
	    top: 50%;
	}
	</style>
	
	<script type="text/javascript">
	
	var language = "${requestLocale}";
	if (language.startsWith("es")) {
		language = "es";
	} else {
		language = "ca";
	}
	
	var hiHaDadesUO = false;
	var hiHaDadesEstat = false;
	var hiHaDadesBustia = false;
	var hiHaDades = false;
	var graficoOTaulaValue = 'mostraGrafics';
	
	function formatSelectUnitatItem(select, item) {
		if (!item.id) {
		    return item.text;
		}
		valida = true;
		if (item.data) {
			valida = item.data.estat =="V";
		} else {
			if ($(select).val() == item.id) {
				// Consulta si no és válida per afegir la icona de incorrecta.
				$.ajax({
					url: $(select).data('urlInicial') +'/' + item.id,
					async: false,
					success: function(resposta) {
						valida = resposta.estat == "V";
					}
				});	
			}			
		}
		if (valida)
			return item.text;
		else
			return $("<span>" + item.text + " <span class='fa fa-exclamation-triangle text-warning' title=\"<spring:message code='unitat.filtre.avis.obsoleta'/>\"></span></span>");
	}
	
	function formatSelectUnitatSuperior(item) {
		return formatSelectUnitatItem($('#codiUnitatSuperior'), item);
	}

	function formatSelectUnitatFiltre(item) {
		return formatSelectUnitatItem($('#unitatIdFiltre'), item);
	}
	
	function formatDatepickers(){
		if ($('input[name="tipusAgrupament"]:checked').val() == 'MENSUAL') {
			$("#dataInici").datepicker({
				format: 'mm/yyyy',
				startView: "months", //TODO: da igual viewMode que startView que el dis:inputDate no le hace caso a nada de esto
				minViewMode: "months"
			});

			$("#dataFi").datepicker({
				format: 'mm/yyyy',
				startView: "months", 
				minViewMode: "months"
			});
		} else {
			$("#dataInici").datepicker({
				format: "dd/mm/yyyy",
			});
			$("#dataFi").datepicker({
			    format: "dd/mm/yyyy"
			});
		}
	}

    function getRandomColor() {
        var letters = '0123456789ABCDEF'.split('');
        var color = '#';
        for (var i = 0; i < 6; i++ ) {
            color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
    }

	var metricsDefinition = { // UO
			'ANOTACIONS_NOVES': {
				'attrname' : 'anotacions_noves',
				'text': "<spring:message code="historic.metriques.enum.ANOTACIONS_NOVES"/>"
			},
			'ANOTACIONS_TOTALS': {
				'attrname' : 'anotacions_totals',
				'text': "<spring:message code="historic.metriques.enum.ANOTACIONS_TOTALS"/>"
			},
			'ANOTACIONS_REENVIADES': {
				'attrname' : 'num_anotacions_reenviades',
				'text': "<spring:message code="historic.metriques.enum.ANOTACIONS_REENVIADES"/>"
			},
			'ANOTACIONS_EMAIL': {
				'attrname' : 'num_anotacions_email',
				'text': "<spring:message code="historic.metriques.enum.ANOTACIONS_EMAIL"/>"
			},
			'JUSTIFICANTS': {
				'attrname' : 'num_justificants',
				'text': "<spring:message code="historic.metriques.enum.JUSTIFICANTS"/>"
			},
			'ANNEXOS': {
				'attrname' : 'num_annexos',
				'text': "<spring:message code="historic.metriques.enum.ANNEXOS"/>"
			},
			'BUSTIES': {
				'attrname' : 'num_busties',
				'text': "<spring:message code="historic.metriques.enum.BUSTIES"/>"
			},
			'USUARIS': {
				'attrname' : 'num_usuaris',
				'text': "<spring:message code="historic.metriques.enum.USUARIS"/>"
			},  // ESTAT
			'CORRECTE': {
				'attrname' : 'correcte',
				'text': "<spring:message code="historic.metriques.enum.CORRECTE"/>"
			},
			'CORRECTE_TOTAL': {
				'attrname' : 'correcte_total',
				'text': "<spring:message code="historic.metriques.enum.CORRECTE_TOTAL"/>"
			},
			'ERROR': {
				'attrname' : 'error',
				'text': "<spring:message code="historic.metriques.enum.ERROR"/>"
			},
			'ERROR_TOTAL': {
				'attrname' : 'error_total',
				'text': "<spring:message code="historic.metriques.enum.ERROR_TOTAL"/>"
			},
			'TOTAL': {
				'attrname' : 'total',
				'text': "<spring:message code="historic.metriques.enum.TOTAL"/>"
			},
			'USUARIS_BUSTIA': { // BUSTIA
				'attrname' : 'usuaris',
				'text': "<spring:message code="historic.metriques.enum.USUARIS_BUSTIA"/>"
			},
			'USUARIS_PERMIS': {
				'attrname' : 'usuaris_permis',
				'text': "<spring:message code="historic.metriques.enum.USUARIS_PERMIS"/>"
			},
			'USUARIS_ROL': {
				'attrname' : 'usuaris_rol',
				'text': "<spring:message code="historic.metriques.enum.USUARIS_ROL"/>"
			}
			
		}
		
		function chartLine(canvas, labels, datasets, title) {
			return new Chart(canvas, {
			    type: 'line',
			    data: {
			        labels: labels,
			        datasets: datasets, 
			    },
			    options: {
			        scales: {
			            yAxes: [{
			                ticks: {
			                    beginAtZero: true
			                }
			            }]
			        },
		            title: {
						display: true,
						text: title
		            },
			        legend: {
			    	   position: 'chartArea'
			       }
			    }
			});
		}
		
		function totalitzar(datasetTotal, dataset) {
			for (var i = 0; i < datasetTotal.length; i++) {
				datasetTotal[i] += dataset[i];
			}
			return datasetTotal;
		}
		
		function getDate(date){
			if ($('input[name="tipusAgrupament"]:checked').val() == 'MENSUAL') {
				mes = date.substring(3, 5);
				mes = mes.replace(/^0+/, '');		
				if (mes == 1) return '<spring:message code="mes.1"/>';
				else if (mes == 2) return '<spring:message code="mes.2"/>';
				else if (mes == 3) return '<spring:message code="mes.3"/>';
				else if (mes == 4) return '<spring:message code="mes.4"/>';
				else if (mes == 5) return '<spring:message code="mes.5"/>';
				else if (mes == 6) return '<spring:message code="mes.6"/>';
				else if (mes == 7) return '<spring:message code="mes.7"/>';
				else if (mes == 8) return '<spring:message code="mes.8"/>';
				else if (mes == 9) return '<spring:message code="mes.9"/>';
				else if (mes == 10) return '<spring:message code="mes.10"/>';
				else if (mes == 11) return '<spring:message code="mes.11"/>';
				else if (mes == 12) return '<spring:message code="mes.12"/>';	
			} else { //DIARI
				return date;	
			}
		}

		
	$(document).ready(function() {
		
		$('#codiUnitatSuperior').change(function() {
			$('#unitatIdFiltre').attr('urlParamAddicional', $(this).val());
			$('#unitatIdFiltre').val(null).trigger('change.select2');
		});

		$('#codiUnitatSuperior').on('select2:clear', function (e) {
			$('#unitatIdFiltre').attr('urlParamAddicional', '');
			$('#unitatIdFiltre').val(null).trigger('change.select2');
		});
				
		$('input[name="graficsOTaula"]').change(function() {
		    if (this.value == 'mostraTaules') {
		    	if (hiHaDadesUO){
					$('#div-taula-uo').show();
					$('#div-canvas-uo').hide();
		    	}
		    	if (hiHaDadesEstat){
					$('#div-taula-estat').show();
					$('#div-canvas-estat').hide();
		    	}
		    	if (hiHaDadesBustia){
					$('#div-taula-bustia').show();
					$('#div-canvas-bustia').hide();
		    	}
				graficoOTaulaValue = 'mostraTaules';
		    }
		    else if (this.value == 'mostraGrafics') {
		    	if (hiHaDadesUO){
					$('#div-taula-uo').hide();
					$('#div-canvas-uo').show();
		    	}
		    	if (hiHaDadesEstat){
					$('#div-taula-estat').hide();
					$('#div-canvas-estat').show();
		    	}
		    	if (hiHaDadesBustia){
					$('#div-taula-bustia').hide();
					$('#div-canvas-bustia').show();
		    	}
				graficoOTaulaValue = 'mostraGrafics';
		    }
		});
		
		$('input[name="tipusAgrupament"]').on('change', function() {
			$('#hdActualitzar').val($('#rbTipusAgrupamentDiari').prop("checked"));
			$('#export-format-div').hide();
        	$('#btnExportar-div').hide();
			
			//TODO: NO funciona con el tag dis:inputDate, solo con <input type="text" name="dataInici" id="dataInici" /> dentro de un div con class="datepicker"
			formatDatepickers();
			
		});
				
		$("#dadesMostrar").on('change', function() {
			var select2Options = {
					theme: 'bootstrap', 
					width: 'auto', 
					minimumResultsForSearch: "0"};
			$('#export-format-div').hide();
        	$('#btnExportar-div').hide();
		});
		
		$('#dadesMostrar').trigger('change');
		
		$("#dataInici").on('change', function() {
			$('#export-format-div').hide();
        	$('#btnExportar-div').hide();
		});
		
		$("#dataFi").on('change', function() {
			$('#export-format-div').hide();
        	$('#btnExportar-div').hide();
		});
		
		$("#codiUnitatSuperior").on('change', function() {
			$('#export-format-div').hide();
        	$('#btnExportar-div').hide();
		});
		
		$("#unitatIdFiltre").on('change', function() {
			$('#export-format-div').hide();
        	$('#btnExportar-div').hide();
		});
		
        $('#btn-netejar-filtre').click(function() {
        	$('#codiUnitatSuperior').val(null);
    		$('#unitatIdFiltre').val(null);
    		$('#form_netejar_filtre').submit();
    		
        });

        $('#form_filtre').submit(function(e) {
        	
        	hiHaDades = false;
        	hiHaDadesUO = false;
        	hiHaDadesEstat = false;
        	hiHaDadesBustia = false;
        	
			$('.div-dades-carregant').show();
           	
           	$("#taulaUO").dataTable().fnDestroy();
           	$("#taulaEstat").dataTable().fnDestroy();
           	$("#taulaBustia").dataTable().fnDestroy();

			var content = '';
			$.ajax({
				type: 'POST',
		        url: '<c:url value="/historic/JsonDadesHistoric"/>',
				data: $(this).serialize(),
		        success: function(json) {	
		        	if (!jQuery.isEmptyObject(json)) {			        		
		        		if (!jQuery.isEmptyObject(json.json_dades_uo_map)){
			        		hiHaDades = true;
			        		hiHaDadesUO = true;
			        		tratarDadesUo(json.json_dades_uo_map);
			        	} else {
			        		$("#dadesUO").hide();
			        	}
 			        	if (!jQuery.isEmptyObject(json.json_dades_estat_map)){
							hiHaDades = true;						
							hiHaDadesEstat = true;						
 			        		tratarDadesEstat(json.json_dades_estat_map);
 			        	} else {
			        		$("#dadesEstat").hide();
			        	}
 			        	if (!jQuery.isEmptyObject(json.json_dades_bustia_map)){
							hiHaDades = true;
							hiHaDadesBustia = true;
 			        		tratarDadesBustia(json.json_dades_bustia_map);
 			        	} else {
			        		$("#dadesBustia").hide();
			        	}
		        	}
		        	if (!hiHaDades) {
		        		$('#div-sense-dades').show();
	           			$("#dadesUO").hide();
	           			$("#dadesEstat").hide();
	           			$("#dadesBustia").hide();
		        	} else {
		        		$('#div-sense-dades').hide();
		        		$('#export-format-div').show();
			        	$('#btnExportar-div').show();
		        	}
				        	
		      	},
		        error: function(e) {
		        	content += 'Hi ha hagut un error recuperant dades de historic ';
		        },
		      	complete: function() {
		      		$('.div-dades-carregant').hide();
		      	}
		    });

	        e.preventDefault();
        });
	                
		$('#btnExportar').click(function(event) {
			 event.preventDefault();
			 window.location.href = $(this).attr('href') + "?format=" + $('#exportFormat').val();
		});

	}); // FIN $(document).ready
	
	function tratarDadesUo(json) {
		$("#tBodyTaulaUO").empty();
		$.each(json, function(i, val) {
			$.each(val, function(j, dataUO) {
				let fila = '';
				fila += ('<tr>');
				fila += ('<td data-sort="' + moment(dataUO.fecha, 'dd/MM/yyyy') + '">' +  getDate(dataUO.fecha) + '</td>');
					fila += ('<td>' + dataUO.uo_codi + '</td>');
					fila += ('<td>' + dataUO.uo + '</td>');
					fila += ('<td>' + dataUO.anotacions_noves + '</td>');
					fila += ('<td>' + dataUO.anotacions_totals + '</td>');
					fila += ('<td>' + dataUO.num_anotacions_reenviades + '</td>');
					fila += ('<td>' + dataUO.num_anotacions_email + '</td>');
					fila += ('<td>' + dataUO.num_justificants + '</td>');
					fila += ('<td>' + dataUO.num_annexos + '</td>');
					fila += ('<td>' + dataUO.num_busties + '</td>');
					fila += ('<td>' + dataUO.num_usuaris + '</td>');
					fila += ('</tr>');
					$("#tBodyTaulaUO").append(fila);
			});
		});
		
		$("#taulaUO").dataTable( {
			language: {
				url: webutilContextPath() + '/js/datatables/i18n/datatables.' + language + '.json'
			},
			paging: true,
			pageLength: 10,
			order: [[ 0, "asc" ]],
			pagingStyle: 'page',
			lengthMenu: [10, 20, 50, 100, 250],
			dom: '<"row"<"col-md-6"i><"col-md-6"<"botons">>>' + 't<"row"<"col-md-3"l><"col-md-9"p>>',
			select: {
				style: 'multi',
				selector: 'td:first-child',
				info: false
			}
		} );
		
		seccioDadesUO(json);
		
		$("#dadesUO").show();
		
		if (graficoOTaulaValue == 'mostraTaules') {
			$('#div-taula-uo').show();
			$('#div-canvas-uo').hide();
			$('#div-taula-estat').show();
			$('#div-canvas-estat').hide();
			$('#div-taula-bustia').show();
			$('#div-canvas-bustia').hide();
		} else if (graficoOTaulaValue == 'mostraGrafics') {
			$('#div-taula-uo').hide();
			$('#div-canvas-uo').show();
			$('#div-taula-estat').hide();
			$('#div-canvas-estat').show();
			$('#div-taula-bustia').hide();
			$('#div-canvas-bustia').show();
		}

	}
	
	function seccioDadesUO(json) {

		var metriques = [
			'ANOTACIONS_NOVES',
			'ANOTACIONS_TOTALS',
			'ANOTACIONS_REENVIADES',
			'ANOTACIONS_EMAIL',
			'JUSTIFICANTS',
			'ANNEXOS',
			'BUSTIES',
			'USUARIS'
		];
		
		var selectorContainer = '#div-canvas-uo';
		var $container = $(selectorContainer);

		function viewHistoric(data) {
			$container.html("");
			
			var columns = Object.keys(data);
			
			if (columns.length > 1) {
				var colors = {};
				for (var column in data) {
					colors[column] = getRandomColor();
				}
				metriques.forEach(function(metric){
					var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
					var canvas = '<canvas id="chart-' + metric + '" width="400" height="100"></canvas>';
					$container.append(title + canvas);
					createChartMetric(data, metric, colors);
					
				});
			} else {
				var title = '<h2>' + '<spring:message code="historic.chart.titol.una.UO"/> '+ columns[0] + '</h2>'
				var canvas = '<canvas id="chart-' + columns[0] + '" width="400" height="100"></canvas>';
				$container.append(title + canvas);
				createChartAllMetriques(data, metriques);
			}
		}
		
		viewHistoric(json);

	}
	
	function tratarDadesEstat(json) {
		$("#tBodyTaulaEstat").empty();
		$.each(json, function(i, val) {
			$.each(val, function(j, dataUO) {
				let fila = '';
				fila += ('<tr>');
				fila += ('<td data-sort="' + moment(dataUO.fecha, 'dd/MM/yyyy') + '">' +  getDate(dataUO.fecha) + '</td>');
					fila += ('<td>' + dataUO.estat + '</td>');
					fila += ('<td>' + dataUO.correcte + '</td>');
					fila += ('<td>' + dataUO.correcte_total + '</td>');
					fila += ('<td>' + dataUO.error + '</td>');
					fila += ('<td>' + dataUO.error_total + '</td>');
					fila += ('<td>' + dataUO.total + '</td>');
					fila += ('</tr>');
					$("#tBodyTaulaEstat").append(fila);
			});
		});
			
		$("#taulaEstat").dataTable( {
			language: {
				url: webutilContextPath() + '/js/datatables/i18n/datatables.' + language + '.json'
			},
			paging: true,
			pageLength: 10,
			order: [[ 0, "asc" ]],
			pagingStyle: 'page',
			lengthMenu: [10, 20, 50, 100, 250],
			dom: '<"row"<"col-md-6"i><"col-md-6"<"botons">>>' + 't<"row"<"col-md-3"l><"col-md-9"p>>',
			select: {
				style: 'multi',
				selector: 'td:first-child',
				info: false
			}
		} );
		
		seccioDadesEstat(json);
		
		$("#dadesEstat").show();
		
		if (graficoOTaulaValue == 'mostraTaules') {
			$('#div-taula-estat').show();
			$('#div-canvas-estat').hide();
		} else if (graficoOTaulaValue == 'mostraGrafics') {
			$('#div-taula-estat').hide();
			$('#div-canvas-estat').show();
		}

	}
	
	function seccioDadesEstat(json) {

		var metriques = [
			'CORRECTE',
			'CORRECTE_TOTAL',
			'ERROR',
			'ERROR_TOTAL',
			'TOTAL'
		];
		
		var selectorContainer = '#div-canvas-estat';
		var $container = $(selectorContainer);

		function viewHistoric(data) {
			$container.html("");
			
			var columns = Object.keys(data);
			
			if (columns.length > 1) {
				var colors = {};
				for (var column in data) {
					colors[column] = getRandomColor();
				}
				metriques.forEach(function(metric){
					var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
					var canvas = '<canvas id="chart-' + metric + '" width="400" height="100"></canvas>';
					$container.append(title + canvas);
					createChartMetric(data, metric, colors);
					
				});
			} else {
				var title = '<h2>' + '<spring:message code="historic.chart.titol.un.estat"/> '+ columns[0] + '</h2>'
				var canvas = '<canvas id="chart-' + columns[0] + '" width="400" height="100"></canvas>';
				$container.append(title + canvas);
				createChartAllMetriques(data, metriques);
			}
		}
		
		viewHistoric(json);

	}
	
	function tratarDadesBustia(json) {
		$("#tBodyTaulaBustia").empty();
		$.each(json, function(i, val) {
			$.each(val, function(j, dataUO) {
				let fila = '';
				fila += ('<tr>');
				fila += ('<td data-sort="' + moment(dataUO.fecha, 'dd/MM/yyyy') + '">' +  getDate(dataUO.fecha) + '</td>');
					fila += ('<td>' + dataUO.uo_codi + '</td>');
					fila += ('<td>' + dataUO.uo + '</td>');
					fila += ('<td>' + dataUO.bustia_id + '</td>');
					fila += ('<td>' + dataUO.nom + '</td>');
					fila += ('<td>' + dataUO.usuaris + '</td>');
					fila += ('<td>' + dataUO.usuaris_permis + '</td>');
					fila += ('<td>' + dataUO.usuaris_rol + '</td>');
					fila += ('</tr>');
					$("#tBodyTaulaBustia").append(fila);
			});
		});
			
		$("#taulaBustia").dataTable( {
			language: {
				url: webutilContextPath() + '/js/datatables/i18n/datatables.' + language + '.json'
			},
			paging: true,
			pageLength: 10,
			order: [[ 0, "asc" ]],
			pagingStyle: 'page',
			lengthMenu: [10, 20, 50, 100, 250],
			dom: '<"row"<"col-md-6"i><"col-md-6"<"botons">>>' + 't<"row"<"col-md-3"l><"col-md-9"p>>',
			select: {
				style: 'multi',
				selector: 'td:first-child',
				info: false
			}
		} );
		
		seccioDadesBustia(json);
		
		$("#dadesBustia").show();
		
		if (graficoOTaulaValue == 'mostraTaules') {
			$('#div-taula-bustia').show();
			$('#div-canvas-bustia').hide();
		} else if (graficoOTaulaValue == 'mostraGrafics') {
			$('#div-taula-bustia').hide();
			$('#div-canvas-bustia').show();
		}

	}
	
	function seccioDadesBustia(json) {

		var metriques = [
			'USUARIS_BUSTIA',
			'USUARIS_PERMIS',
			'USUARIS_ROL'
		];
		
		var selectorContainer = '#div-canvas-bustia';
		var $container = $(selectorContainer);

		function viewHistoric(data) {
			$container.html("");
			
			var columns = Object.keys(data);
			
			if (columns.length > 1) {
				var colors = {};
				for (var column in data) {
					colors[column] = getRandomColor();
				}
				metriques.forEach(function(metric){
					var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
					var canvas = '<canvas id="chart-' + metric + '" width="400" height="100"></canvas>';
					$container.append(title + canvas);
					createChartMetric(data, metric, colors);
					
				});
			} else {
				var title = '<h2>' + '<spring:message code="historic.chart.titol.una.bustia"/> '+ columns[0] + '</h2>'
				var canvas = '<canvas id="chart-' + columns[0] + '" width="400" height="100"></canvas>';
				$container.append(title + canvas);
				createChartAllMetriques(data, metriques);
			}
		}
		
		viewHistoric(json);

	}

	function createChartMetric(data, metric, colors) {

		var columns = Object.keys(data);
		
		columns.forEach(function(c){
			data[c] = data[c].sort((a, b) => (moment(a.fecha,'DD-MM-YYYY') > moment(b.fecha,'DD-MM-YYYY')));
		});
		
		var dates = data[columns[0]].map(item => getDate(item.fecha));
		
		var datasets = [];
		var datasetTotal = new Array(dates.length).fill(0);
		columns.forEach(function(c){
			var attrname = metricsDefinition[metric]['attrname'];
			var dataset = data[c].map(item => item[attrname] != null ? item[attrname] : 0)
			datasetTotal = totalitzar(datasetTotal, dataset);
			var color = (colors == null || colors[c] == null) ? getRandomColor() : colors[c]
			datasets.push({
				'data': dataset,
				'label': c,
		        'lineTension': 0, //equivalent to the old bezierCurve: false
				'backgroundColor': "rgba(0,0,0,0.0)",
				'borderColor': color
			});	
		});			
		
		var color = '';
		do {
			color = getRandomColor();
		} while (color in colors);
		
		datasets.push({
			'data': datasetTotal,
			'label': 'Total',
	        'lineTension': 0,
			'backgroundColor': "rgba(0,0,0,0.0)",
			'borderColor': color
		});
		
		var ctx = 'chart-' + metric;
		var labels = dates;
		var chart = chartLine(ctx, labels, datasets, metricsDefinition[metric]["text"]);
		chart.update();
	}
	
	function createChartAllMetriques(data, metriques) {

		var columns = Object.keys(data);
		
		columns.forEach(function(c){
			data[c] = data[c].sort((a, b) => (moment(a.fecha,'DD-MM-YYYY') > moment(b.fecha,'DD-MM-YYYY')));
		});
		
		var dates = data[columns[0]].map(item => getDate(item.fecha));
		var colors = new Array(metriques.length).fill(getRandomColor());
		var datasets = [];
		
		var colors = {};
		for (var metric in metriques) {
			colors[metric] = getRandomColor();
		}
		
		metriques.forEach(function(metric){
			var nombre = metricsDefinition[metric]['text'];
			var attrname = metricsDefinition[metric]['attrname'];
			var dataset = data[columns[0]].map(item => item[attrname] != null ? item[attrname] : 0);
			var color = (colors == null || colors[metric] == null) ? getRandomColor() : colors[metric]
			datasets.push({
				'data': dataset,
				'label': nombre,
		        'lineTension': 0, //equivalent to the old bezierCurve: false
				'backgroundColor': "rgba(0,0,0,0.0)",
				'borderColor': color
			});	
		});
		
		var ctx = 'chart-' + columns[0];
		var labels = dates;
		var chart = chartLine(ctx, labels, datasets, columns[0]);
		chart.update();
	}
	
	</script>
</head>

<body>
	<div id="div-loading" class="div-dades-carregant" style="display:none;"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
	<form:form id="form_filtre" action="" method="post" cssClass="well" commandName="historicFiltreCommand">
		<div class="row">
			<div class="col-md-2">
				<dis:inputDate name="dataInici" inline="true" placeholderKey="historic.filtre.data.inici"/>
			</div>
			<div class="col-md-2">
				<dis:inputDate name="dataFi" inline="true" placeholderKey="historic.filtre.data.fi"/>
			</div>		
			<div class="col-md-4">
				<c:url value="/unitatajax/unitatSuperior" var="urlConsultaInicialUnitatSuperior"/>
				<c:url value="/unitatajax/unitatsSuperiors" var="urlConsultaLlistatUnitatsSuperiors"/>
				<dis:inputSuggest
					name="codiUnitatSuperior" 
					urlConsultaInicial="${urlConsultaInicialUnitatSuperior}" 
					urlConsultaLlistat="${urlConsultaLlistatUnitatsSuperiors}" 
					inline="true"
					placeholderKey="unitat.list.filtre.codiUnitatSuperior"
					suggestValue="codi"
					suggestText="codiAndNom"
					minimumInputLength="0"
					optionTemplateFunction="formatSelectUnitatSuperior"/>
			</div>
			<div class="col-md-4">
				<c:url value="/unitatajax/unitat" var="urlConsultaInicial"/>
				<c:url value="/unitatajax/unitats" var="urlConsultaLlistat"/>
				<dis:inputSuggest
					name="unitatIdFiltre" 
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					inline="true"
					placeholderKey="bustia.form.camp.unitat"
					suggestValue="id" 
					suggestText="codiAndNom"
					optionTemplateFunction="formatSelectUnitatFiltre"
					multiple="true"
					urlParamAddicional=""/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-4">
				<dis:inputSelect name="dadesMostrar" 
								 optionEnum="HistoricDadesMostrarEnumDto" 
								 placeholderKey="historic.filtre.dadesMostrar"
								 netejar="false"
								 emptyOption="false" 
								 inline="true"
								 required="true"
								 multiple="true"/>
			
			</div>
			<div class="col-md-4">
				<div class="btn-group" data-toggle="buttons">
					<label class="btn btn-default form-check-label <c:if test="${historicFiltreCommand.actualitzar == true}">active</c:if>"> 
						<form:radiobutton id="rbTipusAgrupamentDiari" path="tipusAgrupament" value="DIARI"/>
						<i class="fa fa-clock-o"></i> <spring:message code="historic.filtre.mostraDadesActual"/>
					</label> 
					<label class="btn btn-default form-check-label <c:if test="${historicFiltreCommand.tipusAgrupament == 'DIARI' && !historicFiltreCommand.actualitzar == true}">active</c:if>"> 
						<form:radiobutton path="tipusAgrupament" value="DIARI"/>
						<i class="fa fa-calendar"></i> <spring:message code="historic.filtre.mostraDadesPerDia"/>
					</label> 
					<label class="btn btn-default form-check-label <c:if test="${historicFiltreCommand.tipusAgrupament == 'MENSUAL'}">active</c:if>"> 
						<form:radiobutton path="tipusAgrupament" value="MENSUAL"/>						 
						<i class="fa fa-calendar-o"></i> <spring:message code="historic.filtre.mostraDadesPerMes"/>
					</label>
				</div>
				<form:hidden path="actualitzar" id="hdActualitzar" />			
			</div>		
			<div id="export-format-div" class="col-md-1" style="display: none;">
				<select id="exportFormat"  name="format" class="form-control" style="width:100%"
						data-minimumresults="-1"
						data-toggle="select2">
							<option value="JSON">JSON</option>
							<option value="XLSX">XLSX</option>
							<option value="ODT">ODT</option>
							<option value="XML">XML</option>
				</select>
			</div>
			<div id="btnExportar-div" class="col-md-1" style="display: none;">
				<div class="pull-right">
					<a id="btnExportar" href="<c:url value="/historic/exportar"/>" class="btn btn-success">
						<span class="fa fa-download"></span>&nbsp; <spring:message code="historic.exportacio.boto.exportar"/>
					</a>
				</div>
			</div>
			<div class="col-md-2 pull-right">
				<div class="pull-right">
					<button id="btn-netejar-filtre" type="button" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button id="btn-filtrar-filtre" type="submit" name="accio" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
		<div class="row form-filtre-visualitzacio">
			<div class="col-md-2">
				<div class="btn-group" data-toggle="buttons">
					<label class="btn btn-default form-check-label active"> 
						<input type="radio" value="mostraGrafics" name="graficsOTaula" checked="checked">
						<i class="fa fa-bar-chart" aria-hidden="true"></i> <spring:message code="historic.filtre.mostraGrafics"/>
					</label> 
					<label class="btn btn-default form-check-label"> 
						<input type="radio" value="mostraTaules" name="graficsOTaula">
						<i class="fa fa-table" aria-hidden="true"></i> <spring:message code="historic.filtre.mostraTaules"/>
					</label> 
				</div>
			</div>
		</div>
	</form:form>
	
	<div id="div-sense-dades" style="display: none;">
		<table id="taula-sense-Dades" class='table table-bordered table-striped table-hover' style='width:100%'>
			<tr class="odd">
				<td valign="top" align="center" class="dataTables_empty"><spring:message code="historic.titol.seccio.sense.dades"/></td>
			</tr>
		</table>
	</div>
	<div id="dadesUO" style="display: none;">
		<h1><spring:message code="historic.titol.seccio.dades.uo"/></h1>
		<div class="row">
			<div id="div-taula-uo" class="col-md-12" style="display: none;">
				<table id="taulaUO" class='table table-bordered table-striped table-hover' style='width:100%'>
					<thead>
						<tr>
<%-- 						<th data-col-name="data" data-type="date" data-converter="date" nowrap><spring:message code="historic.taula.header.data" /></th> --%>
							<th><spring:message code="historic.taula.header.data" /></th>
							<th><spring:message code="historic.taula.header.codi.uo" /></th>
							<th><spring:message code="historic.taula.header.uo" /></th>
							<th><spring:message code="historic.taula.header.anotacions.noves" /></th>
							<th><spring:message code="historic.taula.header.anotacions.totals" /></th>
							<th><spring:message code="historic.taula.header.anotacions.reenviades" /></th>
							<th><spring:message code="historic.taula.header.anotacions.perEmail" /></th>
							<th><spring:message code="historic.taula.header.anotacions.justificants" /></th>
							<th><spring:message code="historic.taula.header.anotacions.annexos" /></th>
							<th><spring:message code="historic.taula.header.anotacions.busties" /></th>
							<th><spring:message code="historic.taula.header.anotacions.usuaris" /></th>
						</tr>
					</thead>
					<tbody id="tBodyTaulaUO"></tbody>
				</table>
			</div>
			<div id="div-canvas-uo" class="col-md-12" style="display: none;"></div>
		</div>
	</div>

	<div id="dadesEstat" style="display: none;">
		<h1><spring:message code="historic.titol.seccio.dades.estat"/></h1>
		<div class="row">
			<div id="div-taula-estat" class="col-md-12" style="display: none;">
				<table id="taulaEstat" class='table table-bordered table-striped table-hover' style='width:100%'>
					<thead>
						<tr>
							<th><spring:message code="historic.taula.header.data" /></th>
							<th><spring:message code="historic.taula.header.estat" /></th>
							<th><spring:message code="historic.taula.header.estats.correcte" /></th>
							<th><spring:message code="historic.taula.header.estats.correcteTotal" /></th>
							<th><spring:message code="historic.taula.header.estats.error" /></th>
							<th><spring:message code="historic.taula.header.estats.errorTotal" /></th>
							<th><spring:message code="historic.taula.header.estats.total" /></th>
						</tr>
					</thead>
					<tbody id="tBodyTaulaEstat"></tbody>
				</table>
			</div>
			<div id="div-canvas-estat" class="col-md-12" style="display: none;"></div>
		</div>
	</div>

	<div id="dadesBustia" style="display: none;">
		<h1><spring:message code="historic.titol.seccio.usuaris.busties"/></h1>
		<div class="row">
			<div id="div-taula-bustia" class="col-md-12" style="display: none;">
				<table id="taulaBustia" class='table table-bordered table-striped table-hover' style='width:100%'>
					<thead>
						<tr>
							<th><spring:message code="historic.taula.header.data" /></th>
							<th><spring:message code="historic.taula.header.codi.uo" /></th>
							<th><spring:message code="historic.taula.header.uo" /></th>
							<th><spring:message code="historic.taula.header.bustia.id" /></th>
							<th><spring:message code="historic.taula.header.bustia.nom" /></th>
							<th><spring:message code="historic.taula.header.busties.usuaris" /></th>
							<th><spring:message code="historic.taula.header.busties.usuarisPermis" /></th>
							<th><spring:message code="historic.taula.header.busties.usuarisRol" /></th>
						</tr>
					</thead>
					<tbody id="tBodyTaulaBustia"></tbody>
				</table>
			</div>
			<div id="div-canvas-bustia" class="col-md-12" style="display: none;"></div>
		</div>
	</div>

	<form:form id="form_netejar_filtre" action="" method="post" cssClass="well" commandName="historicFiltreCommand" style="display: none;">
		<input id="accio" name="accio" type="hidden" value="netejar"/>
	</form:form>
	
</body>
</html>