<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code='monitor.titol' /></title>
	<dis:modalHead/>
	<style type="text/css">	
		body.loading {
		    overflow: hidden;   
		}
		body.loading .wait {
		    display: block;
		}
		.table > tbody > tr > td {
			margin-bottom: 0px;
			margin-top: 0px !important;
			padding-bottom: 0px;
		    padding-top: 0px;
			border-bottom: 1px solid #cccccc; 
			overflow: auto; 
			padding: 2px 10px !important;
		}
		.table {
		    table-layout:fixed;
		    border-collapse: collapse;
		 }
		
		.table td {
		    text-overflow:ellipsis;
		    overflow:hidden;
		    white-space:nowrap;
		}
		
		.monitor_hilo {
			width: 650px;
		}
		
		.contingut-carregant {
			text-align: center;
			padding: 8px;
		}
		
		.min_width {
			width: 95px;
		}
		
		.top-buffer {
			margin-top: 10px;
		}
		
	</style>
	<script type="text/javascript">
		$(document).ready(function(){
			$("button[name=refrescar]").click(function() {
				carregaMonitor();
			});
			carregaMonitor();
		});
		
		function carregaMonitor() {				
		    $.ajax({
		        url: "monitor/all",
		        dataType: 'json',
		        async: false,
		        success: function(data){
		            var content = "";
		            content += '<ul class="nav nav-tabs" role="tablist">' +
		                '<li role="presentation" class="active"><a id="tab_sistema" href="#sistema" aria-controls="home" role="tab" data-toggle="tab"><spring:message code="monitor.sistema"/></a></li>' +
		                '<li role="presentation"><a id="tab_fils" href="#fils" aria-controls="profile" role="tab" data-toggle="tab"><spring:message code="monitor.fils"/></a></li>' +
		                '<li role="presentation"><a id="tab_tasques" href="#tasques" aria-controls="profile" role="tab" data-toggle="tab"><spring:message code="monitor.tasques"/></a></li>' +
		              '</ul>';
		            content += '<div class="tab-content">';
		            content += '<div role="tabpanel" class="tab-pane active" id="sistema">';
		            content += '<div class="top-buffer mesures_monitor_sistema">';
			            content += '<table class="table-monitor-titol table table-striped table-bordered dataTable">';
			            content += '<thead><tr>';
			            content += '</thead></tr>';
			            for (var i = 0; i < data.sistema.length; i++) {
			            	content += '<tr class="monitor_fila">';
			            	content += "<td>"+data.sistema[i]+"</td>";
			            	content += '</tr>';
			            }
			            content += '</table>';
		            content +=  '</div>';
		            content +=  '</div>';

		            content += '<div role="tabpanel" class="tab-pane" id="fils">';
		            content +=  '<div id="mesures_monitor" class="top-buffer">' +
			                        '<table class="table-monitor table table-striped table-bordered dataTable">' +
			                        '<thead><tr>' +
			                        '<th class="monitor_hilo"><spring:message code="monitor.hilo"/></th>' +
			                        '<th class="min_width"><spring:message code="monitor.cputime"/></th>' +
			                        '<th class="mid_width"><spring:message code="monitor.estado"/></th>' +
			                        '<th class="min_width"><spring:message code="monitor.espera"/></th>' +
			                        '<th class="min_width"><spring:message code="monitor.blockedtime"/></th>' +
			                        '</thead></tr>';
			            for (var i = 0; i < data.hilo.length; i++) {
			                content +=  '<tr class="monitor_fila">' +
			                            '<td class="monitor_hilo">' + data.hilo[i] + '</td>' +
			                            '<td class="min_width">' + data.cputime[i] + '</td>' +
			                            '<td>' + data.estado[i] + '</td>' +
			                            '<td>' + data.espera[i] + '</td>' +
			                            '<td>' + data.blockedtime[i] + '</td>' +
			                            '</tr>';
			            }
			            content +=  '</table>' +
		                        '</div>'+
		                        '</div>';
		                        
		                        
		                        
		                          content += '<div role="tabpanel" class="tab-pane" id="tasques">';
		       		            content += '<div id="mesures_monitor" class="top-buffer">' +
		       			                        '<table class="table-monitor table table-striped table-bordered dataTable">' +
		       			                        '<thead><tr>' +
		       			                        '<th class=""><spring:message code="monitor.tasques.tasca"/></th>' +
		       			                        '<th class=""><spring:message code="monitor.tasques.estat"/></th>' +
		       			                        '<th class=""><spring:message code="monitor.tasques.inici.execucio"/></th>' +
		       			                        '<th class=""><spring:message code="monitor.tasques.temps.execucio"/></th>' +
		       			                        //'<th class=""><spring:message code="monitor.tasques.fi.execucio"/></th>' +
		       			                        '<th class=""><spring:message code="monitor.tasques.propera.execucio"/></th>' +
		       			                        //'<th class=""><spring:message code="monitor.tasques.observacions"/></th>' +
		       			                        '</thead></tr>';
		       			             for (var i = 0; i < data.tasca.length; i++) {
		       			                content +=  '<tr class="monitor_fila">' +
		       			                             '<td class="">' + data.tasca[i].replace("Tasca: ", "") + '</td>' +
		       			                            '<td class="">' + data.estat[i].replace("Estat: ", "") + '</td>' +
		       			                            '<td>' + data.iniciExecucio[i].replace("Inici execució: ", "") + '</td>' +
		       			                            '<td>' + data.tempsExecucio[i].replace("Temps en execució: ", "") + '</td>' +
		       			                            //'<td>' + data.fiExecucio[i].replace("Fi execució: ", "") + '</td>' +
		       			                            '<td>' + data.properaExecucio[i].replace("Propera execució: ", "") + '</td>' +
		       			                            //'<td>' + data.observacions[i].replace("Observacions: ", "") + '</td>' + 
		       			                            '</tr>';
		       			            } 
		       			            content +=  '</table>' + 
				                        /* '<br><br><hr>' + 
				                        '<label class="ml-6" for="refrescarTasquesCadaSegon">' + 
				                        '<input onclick="refrescarTasquesCadaSegon()" type="checkbox" name="refrescarTasquesCadaSegon" id="refrescarTasquesCadaSegon">' + 
				                        ' Refrescar cada 1s</label>' + */
		       		                    '</div>'+  
		    		                    '</div>'+
		                        
		                        
		                        
		                        '</div>';
		                        
		                        //TODO: afegir una nova pipella i fer un mètode per carregar la taual
		                        // buildTaulaTasques(tasques): "<table> .... </table>"
		                        // aquesta funció JAVASCRIPT l'aprofitaràs en el refres
		                        
		           
		                $("#monitor_contens").html(content);
		        }
		    })
		    .fail(function( jqxhr, textStatus, error ) {
		         var err = textStatus + ', ' + error;
		         console.log( "Request Failed: " + err);
		    })
	        .always(function() {
	            $("body").removeClass("loading");
	        });
		}
		
		function refrescarTasquesCadaSegon() {
			setTimeout(function() {
				location.href = "/distribucio/monitor/tasques";
			}, 1000);
		}
	</script>
</head>
<body>
	<div id="monitor_contens">
		<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
	</div>
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></button>
		<button type="button" class="btn btn-primary" name="refrescar" value="refrescar"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.refrescar"/></button>
	</div>
</body>
</html>
