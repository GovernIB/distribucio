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
		var intervalCadaSegon;
		$(document).ready(function(){
			$("button[name=refrescar]").click(function() {
            	var checkValue = $("#chRefrescarTasques").is(":checked");
            	if (checkValue) {
            		clearInterval(intervalCadaSegon);
            	}
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
		       			                        '<th class="text-center"><spring:message code="monitor.tasques.estat"/></th>' +
		       			                        '<th class="text-center"><spring:message code="monitor.tasques.darrer.inici"/></th>' +
		       			                        '<th class="text-center"><spring:message code="monitor.tasques.temps.execucio"/></th>' +
		       			                        //'<th class=""><spring:message code="monitor.tasques.fi.execucio"/></th>' +
		       			                        '<th class="text-center"><spring:message code="monitor.tasques.propera.execucio"/></th></tr>' +
		       			                        //'<th class=""><spring:message code="monitor.tasques.observacions"/></th>' +
		       			                        '</thead><tbody id="tbody_monitor">';
		       			                        
		       			                        
		       			            content += getTasquesTBody(data.tasca, data.estat, data.iniciExecucio, data.tempsExecucio, data.properaExecucio, data.identificadors);
		       			            content +=  '</tbody></table>' + 
				                        '<br><br><hr>' + 
				                        	'<input class="ml-6" id="chRefrescarTasques" type="checkbox" name="refrescarTasquesCadaSegon">' +
					                        '<label class="ml-1" for="refrescarTasquesCadaSegon">' + 					                        					                        
					                        '<spring:message code="monitor.tasques.check.refresh"/></label>' +   
					                        '<span id="span-refresh" class="ml-2 fa fa-refresh" style="visibylity-hidden"></span>' + 
		       		                    '</div>'+  
		    		                    '</div>'+
		                        
		                        
		                        
		                        '</div>';
		                        
		           
		                $("#monitor_contens").html(content);
		                $("#chRefrescarTasques").click(function(){
		                	var checkValue = $("#chRefrescarTasques").is(":checked");
		                	if (checkValue) {
		                		refrescarTasquesCadaSegon();
		                	}else {
		                		clearInterval(intervalCadaSegon);
		                	}
		                	
		                });
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
		
		
		function carregaTasques() {
			$("#span-refresh").addClass('fa-circle-o-notch');
			$("#span-refresh").addClass('fa-spin');
		    $.ajax({
		        url: "monitor/tasques",
		        dataType: 'json',
		        async: false,
		        success: function(data){
		            $("#tbody_monitor").empty().html(getTasquesTBody(data.tasca, data.estat, data.iniciExecucio, data.tempsExecucio, data.properaExecucio, data.identificadors));
		        }
		    })
		    .fail(function( jqxhr, textStatus, error ) {
		         var err = textStatus + ', ' + error;
		         console.log( "Request Failed: " + err);
		    })
	        .always(function() {
	            $("body").removeClass("loading");
				$("#span-refresh").removeClass('fa-circle-o-notch');
				$("#span-refresh").removeClass('fa-spin')
	        });
		}
		
		function refrescarTasquesCadaSegon() {
			intervalCadaSegon = setInterval(function() {
				carregaTasques();
			}, 1000);
		}
		
		
		function getTasquesTBody(tasca, estat, iniciExecucio, tempsExecucio, properaExecucio, identificadors) {
			var content = '';
	            for (var i = 0; i < tasca.length; i++) {
	               content +=  '<tr class="monitor_fila">' +
	                            '<td id="tasca-' + identificadors[i] + '">' + tasca[i].replace("Tasca: ", "") + '</td>' +
	                           '<td class="text-center" id="estat-' + identificadors[i] + '">' + estat[i].replace("Estat: ", "") + '</td>' +
	                           '<td class="text-center" id="inici-execucio-' + identificadors[i] + '">' + iniciExecucio[i].replace("Darrer inici: ", "") + '</td>' +
	                           '<td class="text-right" id ="temps-execucio-' + identificadors[i] + '">' + tempsExecucio[i].replace("Temps en execució: ", "") + '</td>' +
	                           '<td class="text-center" id="propera-execucio-' + identificadors[i] + '">' + properaExecucio[i].replace("Propera execució: ", "") + '</td>' +
	                           '</tr>';
	           }  
	     	return content;
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
