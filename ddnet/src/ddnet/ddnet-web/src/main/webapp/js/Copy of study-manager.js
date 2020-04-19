			var mainLayout = null;
			var ddConfig = null;
			var storage = $.localStorage;
			var pageInfo = null;
		    var disconnectionNotified = true;
		    var singleConnectionAllowedNotified = false;
		    //var isLoad = false;
						
			$(document).ready(function() {			
				
				addTbFoot();
				
				ddUserAgent.initialize({
					"master": true,
					"onConnected": function () {
				    	disconnectionNotified = false;
				    	singleConnectionAllowedNotified = false;

				    	if (window.userinfo)
				    		ddUserAgent.announceUser();
				    	
				    	try {
				    		$('#dd-useragent-status-text').text('Conectado')
				    			.attr("href", "javascript:ddUserAgent.showUserAgent();");
				    	} catch(ignore) { }
				    	toastr["success"]("UserAgent conectado", "DDNET");
					},
					"onDisconnected": function (reason) {
				    	try { 
				    		$('#dd-useragent-status-text').text('Desconectado')
				    			.attr("href", "javascript: ddUserAgent.startUserAgent();");
				    	} catch(ignore) { }
				    	
				    	if (reason == 4500) {
				    		if (!singleConnectionAllowedNotified) {
				    			singleConnectionAllowedNotified = true;
				    			toastr["error"]("Sólo una instancia de la aplicación web DDNET puede conectarse con UserAgent", "DDNET");		
				    		}
				    	} else {	
				    		if (!disconnectionNotified) {
				    			toastr["warning"]("UserAgent desconectado", "DDNET");
				    			disconnectionNotified = true;
				    		}
				    	}				    	
					}
				});
				 
				// Operaciones al momento de realizar/analizar resultados de consultas AJAX. 
				
				/*
				$(document).on({
				     ajaxStart: function() { doBlockUI(); },
				     ajaxStop:  function() { doUnblockUI(); },    
				     ajaxError: function() { doUnblockUI(); },
				     ajaxComplete: function(event, jqXHR, ajaxOptions) 
				     {
				    	 var contentType = jqXHR.getResponseHeader("Content-Type");
				    	 if (!contentType || !contentType.match("^application/json")){
				    		 //window.location.href = '../authentication-servlet/logout';
				    	 }
				   	 }
				});
				*/
				
				readUserInfo();
				
				// Obtener los valores de configuración
			    $.getJSON('rest/config/all')
					  .done(function(result) {
						  	ddConfig = result;
					  })
					  .fail(function() {
						  window.location.href = '../authentication-servlet/logout';
					  });
				
			    $('#ddnet-version').text('Version 2016.02.03');
				
			    AjustePantalla1();
				
				// Botones del cuadro de búsqueda de estudios.
			    $('#study-simple-view').button
				({
				    icons: { primary: "ui-icon-zoomout" }, text: true
				});
				$('#study-advanced-view').button
				({
				    icons: { primary: "ui-icon-zoomin" }, text: true
				});
				$('#study-search-dosearch').button
					({
						icons: { primary: "ui-icon-search" }, text: true
					});
				$('#study-search-pendings').button
					({
						icons: { primary: "ui-icon-notice" }, text: true
					});
				$('#study-search-clean').button
					({
					    icons: { primary: "ui-icon-refresh" }, text: true
					});
				$('#study-get-images').button
					({
					    icons: { primary: "ui-icon-disk" }, text: true
					});
				
				// Botones de acción sobre los estudios encontrados.				
				$('#study-assign-to').button
				({
				    icons: { primary: "ui-icon-person" }, text: true
				});	
				
				
				$('#study-diagnostic-center').selectmenu({
					change: function( e, data ) {
				        clrTopOfTable();
						advancedSearch();
				}});
				$('#study-user').selectmenu({
					change: function( e, data ) {
				        clrTopOfTable();
						advancedSearch();
				}});
				$('#study-modality').selectmenu({
					change: function( e, data ) {
				        clrTopOfTable();
						advancedSearch();
				}});
				$('#aux-filter').selectmenu({
					change: function( e, data ) {
				        clrTopOfTable();
						advancedSearch();
				}});
				
				// Configuración de los calendarios para búsqueda de estudios.				
				var dayNames = ["Domingo", "Lunes", "Martes", "Mi&eacute;rcoles", 
					"Jueves", "Viernes", "S&aacute;bado"]; 
				var dayNamesMin = ["DOM", "LUN", "MAR", "MIE", "JUE", "VIE", "SAB"];
				var monthNames = ["Enero", "Febrero", "Marzo", "Abril", 
				                  "Mayo", "Junio", "Julio", "Agosto",
				                  "Setiembre", "Octubre", "Noviembre", "Diciembre"];
				$("#study-date-between-from").datepicker({ dateFormat: "dd/mm/yy", 
					changeYear: true, yearRange: "-100:+0", changeMonth: true,
					dayNames: dayNames, dayNamesMin: dayNamesMin, monthNames: monthNames });
				$("#study-date-between-to").datepicker({ dateFormat: "dd/mm/yy", 
					changeYear: true, yearRange: "-100:+0", changeMonth: true,
					dayNames: dayNames, dayNamesMin: dayNamesMin, monthNames: monthNames });
				$("#patient-data-dob").datepicker({ dateFormat: "dd/mm/yy", 
					changeYear: true, yearRange: "-100:+0", changeMonth: true, 
					dayNames: dayNames, dayNamesMin: dayNamesMin, monthNames: monthNames });
				$("#study-date-between-from").datepicker("setDate", new Date());
				$("#study-date-between-to").datepicker("setDate", new Date());
				
				
				
				
				
				// Agrego handler para realizar efectivamente la búsqueda de estudios.
				$('#study-search-form').submit(function() {
				   //$.getJSON('rest/studies?' + $(this).serialize())
					$.ajax({
						  type: "GET",
						  dataType: 'json',
						  url: 'rest/studies?' + $(this).serialize(),
						  beforeSend: function(){$('div#ddnet-loading').show();}
						  
					}).done(function(result) {
							var dt = $('#studies-found').dataTable();
						  	var table = $("#studies-found").DataTable();
						  	var aux = 0;
						  	dt.fnClearTable();
						  	$('#chkAll').prop('checked', false);
						  	if (result && result.length > 0){
						  		dt.fnAddData(result);
						  		//isLoad = true;
						  		document.getElementById("result").innerHTML = "Resultado: " +result.length+ " estudio(s)";
						  		$.each(result, function(index, std) {if(std.inc == 2) aux++;});
								document.getElementById("btnIncidence").innerHTML = aux + " URGENCIA(S)";
								pageInfo = table.page.info();
								$( '#tbInfo' ).html( 'Mostrando p&aacute;gina ' +(pageInfo.page+1)+ ' de ' + pageInfo.pages );
								CompletePages( pageInfo, 1 );
						  		}
						  	
						  	else {
						  		document.getElementById("result").innerHTML = "Resultado: 0 estudio(s)";
						  		document.getElementById("btnIncidence").innerHTML = "0 URGENCIA(S)";
						  		$( '#tbInfo' ).html( 'Sin estudios para mostrar' );
						  		CompletePages( null, 0 );
						  	}
						  	
					  })
					  .fail(function() {
					    	alert('Ocurrió un error con su consulta de estudios. ' + 
					    		'Por favor, reintente la operación realizada.');
					  })
					  .always(function() {
						  setPaginationButtonsTooltips();
						  $('div#ddnet-loading').hide(600);
					  });				    
				    return false;
				});				
								
				// Agregar comportamiento para habilitar/deshabilitar fechas 'desde/hasta'
				// en la búsqueda de estudios.
				$.each($('input:radio[name="study-date-type"]'), function(index, item) {
					if (item.id == 'study-date-type-between')
						$(item).on('click', function() {
							$("#study-date-between-from").prop('disabled', false);
							$("#study-date-between-to").prop('disabled', false);
							
							$("#study-date-between-from").datepicker("setDate", new Date());
							$("#study-date-between-to").datepicker("setDate", new Date());
						});
					else
						$(item).on('click', function() { 
							$("#study-date-between-from").prop('disabled', true);
							$("#study-date-between-to").prop('disabled', true);
						});
				});
				
				// Esto deshabilita los input de fecha desde/hasta :)
				$("#study-date-type-today").click();
				
				// Agregar eventos a los botones relacionados a buscar estudios.
				$("#study-search-dosearch").on('click', function(){clrTopOfTable();advancedSearch();});
				$("#study-search-pendings").on('click', showPendingStudies);				
				$("#study-search-clean").on('click', showAllStudies);				

				
				
				// Configurar la grilla de resultados.							
				$('#studies-found').dataTable({
					"data": [],
					"order": [[ 13, "desc" ]],
					"lengthMenu": [ 10, 15, 20, 25, 30, 50, 75, 100 ],
					"pageLength": 10,
					"pagingType": "full_numbers",
					"dom": 'rt<"bottom">T',
					"tableTools": {
						"aButtons": [],
						fnRowSelected: function(o) {
							studiesSelectionChanged();
						},
						fnRowDeselected: function(o) {
							studiesSelectionChanged();
						}											
					},
					"columns": [																	
																									
						{ "title": "<input type='checkbox' id='chkAll'>", 							
								"class": "left", "width": "25px" },										
																									
						{ "title": "*", "data": "inc",												
								"defaultContent": "", "class": "center", "width": "25px" },			
																									
						{ "title": "Inf", "data": "rs", 																						
								"defaultContent": "?", "class": "center", "width": "25px" },		
																									
						{ "title": "A", "data": "fc", 												
								"defaultContent": "?", "class": "center", "width": "25px" },
						
						{ "title": "OM", "data": "om", 												
								"defaultContent": "?", "class": "center", "width": "25px" },
						
						{ "title": "MP3", "data": "", 												
								"defaultContent": "NO", "class": "center", "width": "25px" },
																									
						{ "title": "Dr.", "data": "dr", 											
								"defaultContent": "", "class": "center study", "width": "50px" },			
																									
						{ "title": "PACIENTE", "data": "pn", 
								"defaultContent": "?", "class": "left study" },

						{ "title": "MD", "data": "mod", 
								"defaultContent": "?", "class": "center study", "width": "25px" },
								
						{ "title": "ESTUDIO", "data": "desc", 
								"defaultContent": "", "class": "left study" },
															
						{ "title": "FECHA",	"data": "date", 
								"defaultContent": "?", "class": "center study", "width": "90px" },
						
						{ "title": "CENTRO", "data": "c", 
								"defaultContent": "?", "class": "left study", "width": "150px" },
						
						{ "title": "SERIES", "data": "ti", 
								"defaultContent": "?", "class": "center study", "width": "40px" },
								
						{ "title": "URG", "data": "urg", 
								"defaultContent": "0", "class": "left study", "width": "25px" }
													
					],					
					"columnDefs": [
					               {															
					            	   "targets": 0,											
					            	   "bSortable": false,										
					            	   "render": function ( data, type, row ) {					
					            		   														
					            		   return "<input type='checkbox' class='checked'>";		
					            	   }														
					               },
					               {
					            	 	"targets": 1,
					            	 	"render": function ( data, type, row ) {
					            	 		
					            	 		if(data==0) return "";
					            	 		else if(data==1) return "<span id='incidencia'>*</span>";
					            	 		else if(data==2) return "<span id='incidencia' class='unresolved'>*</span>";
					            	 						 
					            	 	}
					               },
					               {
					            	   "targets": 2,											
					            	   "render": function ( data, type, row ) {
					            		   var url = 'rest/reporting/reports/pdf/' + row.id;
				            		   	
					                       return data==0 ? "" : 																				
					                    	      data==1 ? "<img src='../images/punto_verde.png' />" :
					                    		  data==2 ? "<img src='../images/aconfirmar.png' />" : 											
					                    	      data==3 ? //(actionIsAllowed(userinfo, row.iid, 'view-study-report') ? 
					                    		   				"<a href='" + url + "'><img src='../images/informado.png' /></a>" : 			
					                    		   				//"<img src='../images/informado.png' />") : 										
					                    			   "?";
					                   }					                   
					               },
					               {
					            	   "targets": 3,											
					            	   "render": function ( data, type, row ) {
					            		  if (!row.fc || row.fc <= 0) 
					            		   	  return "<span></span>";

					            		  var studyID = row.id;
					            		  var IID = row.iid;
					            		  var imgID = "clipimg-" + studyID;
					            		  return "<span><img id='" + imgID + "' " + 
					            		  			"src='../images/paperclip3_black.png' " + 
					            		  			"studyid='" + studyID +  "' " + 
					            		  			"iid='" + IID +  "' " + 
					            		  			"opcode='files' " + 
				            		      			"class='datable-row-icon' /></span>";
					                   }
					               },
					               {
					            	   "targets": 4,											
					            	   "render": function ( data, type, row ) {
					            		  if (!row.om || row.om <= 0) 
					            		   	  return "<span></span>";

					            		  var studyID = row.id;
					            		  var imgID = "omimg-" + studyID;
					            		  return "<span><img id='" + imgID + "' src='../images/om.jpg' " +
					            		  		 "class='study-om-icon' /></span>";
					                   }	                   
					               },
					               {
					            	   "targets": [6,7,8,10,11,12],
					            	   "render": function ( data, type, row) {
										
					            		   if(!row.cc)
											return 	row.urg == 1 ? "<span style='color:orange;'>"+data+"</span>" :
													row.urg == 2 ? "<span style='color:red;'>"+data+"</span>" :
													data ;
					            		   else 
				            			   return 	row.urg == 1 ? "<span style='color:orange; font-weight: bold;'>"+data+"</span>" :
													row.urg == 2 ? "<span style='color:red; font-weight: bold;'>"+data+"</span>" :
													"<b>" + data + "</b>" ;
					            	   }
					               },
					               {
					            	 "targets": 9,
					            	 "render": function ( data, type, row) {
					            		 
					            		 if(!row.cc){
					            			 if(row.tf && !row.em){
					            				 return row.urg == 1 ? "<span style='color:orange;'>"+data+"</span>" +
					            						 '&nbsp;&nbsp; <img src="../images/teachingfile.png" style="width: 16px; height: 16px;"/>'	:
														
					            						row.urg == 2 ? "<span style='color:red;'>"+data+"</span>" +
					            						 '&nbsp;&nbsp; <img src="../images/teachingfile.png" style="width: 16px; height: 16px;"/>'	:
														
					            						data + '&nbsp;&nbsp; <img src="../images/teachingfile.png" style="width: 16px; height: 16px;"/>';
					            			 }
					            			 else if(row.em && !row.tf){
					            				 return row.urg == 1 ? "<span style='color:orange;'>"+data+"</span>" +
					            						 '&nbsp;&nbsp; <img src="../images/emergenciamedica.png" style="width: 16px; height: 16px;"/>'	:
														
					            						row.urg == 2 ? "<span style='color:red;'>"+data+"</span>" +
					            						 '&nbsp;&nbsp; <img src="../images/emergenciamedica.png" style="width: 16px; height: 16px;"/>'	:
														
					            						data + '&nbsp;&nbsp; <img src="../images/emergenciamedica.png" style="width: 16px; height: 16px;"/>';
					            			 }
					            			 else if(row.em && row.tf){
					            				 return row.urg == 1 ? "<span style='color:orange;'>"+data+"</span>" +
					            						 '&nbsp;&nbsp; <img src="../images/teachingfile.png" style="width: 16px; height: 16px;"/>' +
					            						 '&nbsp;&nbsp; <img src="../images/emergenciamedica.png" style="width: 16px; height: 16px;"/>'	:
														
					            						row.urg == 2 ? "<span style='color:red;'>"+data+"</span>" +
					            						 '&nbsp;&nbsp; <img src="../images/teachingfile.png" style="width: 16px; height: 16px;"/>' +
					            						 '&nbsp;&nbsp; <img src="../images/emergenciamedica.png" style="width: 16px; height: 16px;"/>'	:
														
					            						data + '&nbsp;&nbsp; <img src="../images/teachingfile.png" style="width: 16px; height: 16px;"/>' +
					            							   '&nbsp;&nbsp; <img src="../images/emergenciamedica.png" style="width: 16px; height: 16px;"/>';
					            			 }
					            			 else{
					            				 return row.urg == 1 ? "<span style='color:orange;'>"+data+"</span>" :
														row.urg == 2 ? "<span style='color:red;'>"+data+"</span>" :
														data ;
					            			 }
					            			 
					            		 }
					            		 else{
					            			 if(row.tf && !row.em){
					            				 return row.urg == 1 ? "<span style='color:orange; font-weight: bold;'>"+data+"</span>" +
					            						 '&nbsp;&nbsp; <img src="../images/teachingfile.png" style="width: 16px; height: 16px;"/>'	:
														
					            						row.urg == 2 ? "<span style='color:red; font-weight: bold;'>"+data+"</span>" +
					            						 '&nbsp;&nbsp; <img src="../images/teachingfile.png" style="width: 16px; height: 16px;"/>'	:
														
					            						"<b>" + data + "</b>" + '&nbsp;&nbsp; <img src="../images/teachingfile.png" style="width: 16px; height: 16px;"/>';
					            			 }
					            			 else if(row.em && !row.tf){
					            				 return row.urg == 1 ? "<span style='color:orange; font-weight: bold;'>"+data+"</span>" +
					            						 '&nbsp;&nbsp; <img src="../images/emergenciamedica.png" style="width: 16px; height: 16px;"/>'	:
														
					            						row.urg == 2 ? "<span style='color:red; font-weight: bold;'>"+data+"</span>" +
					            						 '&nbsp;&nbsp; <img src="../images/emergenciamedica.png" style="width: 16px; height: 16px;"/>'	:
														
					            						"<b>" + data + "</b>" + '&nbsp;&nbsp; <img src="../images/emergenciamedica.png" style="width: 16px; height: 16px;"/>';
					            			 }
					            			 else if(row.em && row.tf){
					            				 return row.urg == 1 ? "<span style='color:orange; font-weight: bold;'>"+data+"</span>" +
					            						 '&nbsp;&nbsp; <img src="../images/teachingfile.png" style="width: 16px; height: 16px;"/>' +
					            						 '&nbsp;&nbsp; <img src="../images/emergenciamedica.png" style="width: 16px; height: 16px;"/>'	:
														
					            						row.urg == 2 ? "<span style='color:red; font-weight: bold;'>"+data+"</span>" +
					            						 '&nbsp;&nbsp; <img src="../images/teachingfile.png" style="width: 16px; height: 16px;"/>' +
					            						 '&nbsp;&nbsp; <img src="../images/emergenciamedica.png" style="width: 16px; height: 16px;"/>'	:
														
					            						"<b>" + data + "</b>" + '&nbsp;&nbsp; <img src="../images/teachingfile.png" style="width: 16px; height: 16px;"/>' +
					            							   '&nbsp;&nbsp; <img src="../images/emergenciamedica.png" style="width: 16px; height: 16px;"/>';
					            			 }
					            			 else{
					            				 return row.urg == 1 ? "<span style='color:orange; font-weight: bold;'>"+data+"</span>" :
														row.urg == 2 ? "<span style='color:red; font-weight: bold;'>"+data+"</span>" :
														"<b>" + data + "</b>" ;
					            			 }
					            		 }
					            	 }
					               },
					               {
					            	   "targets": 13,
										"visible": false
					               }
					               					               
					           ],
					"language": {
			            "lengthMenu": "Mostrar _MENU_ estudios por página",
			            "zeroRecords": "No se encontraron registros para mostrar con los criterios especificados.",
			            "info": "Mostrando página _PAGE_ de _PAGES_",
			            "infoEmpty": "Sin estudios para mostrar",
			            "infoFiltered": "(filtrados de un total de _MAX_ estudios)",
					    "emptyTable":     "Sin estudios para mostrar",
					    "infoPostFix":    "",
					    "thousands":      ".",
					    "loadingRecords": "Cargando...",
					    "processing":     "Procesando...",
					    "search":         "Buscar:",
					    "paginate": {
					        "first":      "Primera",
					        "last":       "Última",
					        "next":       "Siguiente",
					        "previous":   "Anterior"
					    },
					    "aria": {
					        "sortAscending":  ": Click para ordenar ascendentemente",
					        "sortDescending": ": Click para ordenar descendentemente"
					    }
					}
				});
			
				$("#studies-found").DataTable().on('length.dt', function (e, settings, len) {
					storage.set('ddnet1.studies-found.page-size', len);
				});				
				
				$("#studies-found").DataTable().on('order.dt', function (e, settings, len) {
					CompletePages( $("#studies-found").DataTable().page.info(), 1 );
				});	
				
				$("#studies-found").on('click', 'img.datable-row-icon', function() {
					if($(".ui-dialog").is(":visible"))
						return;
					
					var studyID = this.attributes["studyid"].value;
					var opcode = this.attributes["opcode"].value;
					var iid = this.attributes["iid"].value;							
					
					if (opcode == 'files' && actionIsAllowed(userinfo, iid, 'view-study-files'))
			        	openStudyFilesDialog(studyID, 'Archivos', false, false);
			    });
				
				$("#studies-found").on( 'change', 'input.checked', function(){							
																										
					var check = $(this).prop('checked') ? true : false;									
					var oTT = TableTools.fnGetInstance('studies-found');							
																										
					if(check) oTT.fnSelect($(this).closest('tr'));
					else oTT.fnDeselect($(this).closest('tr'));											
																										
				});																					
																										
				$("#chkAll").on( 'click', function(){													
																										
					var check = $(this).prop('checked') ? true : false;									
					var oTT = TableTools.fnGetInstance('studies-found');								
					var checkboxes = $("#studies-found").find(':checkbox');								
																										
					if(check) {	oTT.fnSelectAll();														
								checkboxes.prop('checked',true);	}									
					else {	oTT.fnSelectNone();															
							checkboxes.prop('checked',false);	}										
				});																							
				
				// Agregado de comportamiento para los botones que actuan sobre los estudios encontrados.
				disableAllStudyActions();				
				
				$('#study-simple-view').click(function() {
					simpleVisualization();
			    });
				
				$('#study-advanced-view').click(function() {
					advancedVisualization();
			    });	
				
				$('#study-assign-to').click(function() {
					assignStudiesTo();
			    });
				
				$("#study-date-type-today").on('click', function(){
						$("#study-date-between-from").datepicker("setDate", new Date());
						$("#study-date-between-to").datepicker("setDate", new Date());
						clrTopOfTable();
						advancedSearch();
				});
				
				$("#study-date-type-yesterday").on('click', function(){
						clrTopOfTable();
						advancedSearch();
						var yesterday = new Date();
						yesterday.setDate(yesterday.getDate() - 1);
						$("#study-date-between-from").datepicker("setDate", yesterday);
						$("#study-date-between-to").datepicker("setDate", yesterday);
					});
				
				$("#study-date-type-lastweek").on('click', function(){
						clrTopOfTable();
						advancedSearch();
						var lweek = new Date();
						lweek.setDate(lweek.getDate() - 7);
						$("#study-date-between-from").datepicker("setDate", lweek);
						$("#study-date-between-to").datepicker("setDate", new Date());
				});
				
				$("#study-date-type-lastmonth").on('click', function(){
						clrTopOfTable();
						advancedSearch();
						var lmonth = new Date();
						lmonth.setDate(lmonth.getDate() - 30);
						$("#study-date-between-from").datepicker("setDate", lmonth);
						$("#study-date-between-to").datepicker("setDate", new Date());
				});
				
				$("#study-date-type-any").on('click', function(){
					clrTopOfTable();
					advancedSearch();
					$("#study-date-between-from").datepicker("setDate", '');
					$("#study-date-between-to").datepicker("setDate", '');
			});
				
				$("#study-diagnostic-center").on( 'change', function(){clrTopOfTable();advancedSearch();});
				$("#study-modality").on( 'change', function(){clrTopOfTable();advancedSearch();});
				$("#study-user").on( 'change', function(){clrTopOfTable();advancedSearch();});
				$("#aux-filter").on( 'change', function(){clrTopOfTable();advancedSearch();});
				$( "#check1" ).on( 'click' , function(){
							clrTopOfTable();
							$( '#check3' ).prop( 'checked' , false );
							$( '#check5' ).prop( 'checked' , false );
							advancedSearch();
							});
				$( "#check2" ).on( 'click' , function(){
							clrTopOfTable();
							$( '#check4' ).prop( 'checked' , false );
							advancedSearch();
							});
				$( "#check3" ).on( 'click' , function(){
							clrTopOfTable();
							$( '#check1' ).prop( 'checked' , false );
							$( '#check5' ).prop( 'checked' , false );
							advancedSearch();
							});
				$( "#check4" ).on( 'click' , function(){
							clrTopOfTable();
							$( '#check2' ).prop( 'checked' , false );
							advancedSearch();
							});
				$( "#check5" ).on( 'click' , function(){
							clrTopOfTable();
							$( '#check3' ).prop( 'checked' , false );
							$( '#check1' ).prop( 'checked' , false );
							advancedSearch();
							});
				$( "#check6" ).on( 'click' , function(){clrTopOfTable();advancedSearch();});
				
				document.getElementById("top-of-table").innerHTML = "<div id='result'></div>" +
																	"<div id='incidences'><span id='btnIncidence'></span></div>" +
																	"<div id='newstd'></div>";
				document.getElementById("result").innerHTML = "Resultado: 0 estudio(s)";
				document.getElementById("btnIncidence").innerHTML = "0 URGENCIA(S)";
				document.getElementById("newstd").innerHTML = "<span id='btnNewStd'>Ver Nuevos Estudios</span>";
				
				$("#studies-found").on("click", "td", function() {
					
					var studyPos = $("#studies-found").dataTable().fnGetPosition( this );
					var fila = studyPos[0];
					var columna = studyPos[1];
					
					if(columna > 5){
					
						var studyData = $("#studies-found").dataTable().fnGetData(fila);
						var title = 'Estudio del paciente: ' + studyData.pn + ' - Edad: ' + studyData.age;
					
						manageStudy(studyData.id, title);
					}
						
				});

				$( '#btnIncidence' ).on( 'click' , function(){
					
					$( '#incidenciaH' ).val('1');
					$( '#new-study' ).val('0');
					advancedSearch();
				});
				
				$( '#btnNewStd' ).on( 'click' , function(){
					
					$( '#new-study' ).val('1');
					$( '#incidenciaH' ).val('0');
					$("#study-date-type-any").prop("checked", true);
					$("#study-date-between-from").datepicker("setDate", '');
					$("#study-date-between-to").datepicker("setDate", '');
					advancedSearch();
				});
				
				$('#study-get-images').click(function() {
					getStudyImages();
			    });					
				
				// Configurar la validación de los campos de los controles de búsqueda de estudios.
				$("#study-search-form").validate({
					rules: {
						"study-accessionnumber": {
							required: false,
							maxlength: 100
						},							
						"study-date-between-from": {
							required: "#study-date-type-between:checked",
							maxlength: 100
						},
						"study-date-between-to": {
							required: "#study-date-type-between:checked",
							maxlength: 100
						},							
						"patient-data-id": {
							required: false,
							maxlength: 100
						},							
						"patient-data-name": {
							required: false,
							maxlength: 100
						},							
						"patient-data-dob": {
							required: false,
							maxlength: 100
						}							
					},
					messages: {
						"study-date-between-from": "Ingrese una fecha inicial",
						"study-date-between-to": "Ingrese una fecha final"
					}
				});
				
				// Atajos de teclado.
				shortcut.add("ctrl+f7", advancedSearch);   
			    shortcut.add("ctrl+f8", showPendingStudies); 				
			    shortcut.add("ctrl+f9", showAllStudies);
			    shortcut.add("ctrl+f9", showAllStudies);
			    shortcut.add("ctrl+left", prevStudiesPage);
			    shortcut.add("ctrl+right", nextStudiesPage);

			    setPaginationButtonsTooltips();
			    applyUserPreferences();
			    
				$('#configuration-link').click(function() {
					openConfigurationDialog();
			    });
				
				
				ChangePs();
				
			});			
			
			function applyUserPreferences() {
			    if (storage.isSet('ddnet1.studies-found.page-size'))
			    	$('#studies-found').DataTable().page.len(storage.get('ddnet1.studies-found.page-size')).draw();				
			}
						
			function manageStudy(studyID, title) {
        		var url = 'study.html?s=' + studyID;
        		var studyWindow = window.open(url, "");
			}
			
			
			function simpleVisualization() {
				var selectedStudies = getSelectedStudies();
				
				if (selectedStudies && selectedStudies.length > 0) {
					
					$.each(selectedStudies, function(index, study) {
				    	var studyID = study.id;
						var url = getConfig('actions.study.simple-view.url').replace('${HOST}', location.hostname)
																			.replace('${STUDYID}', studyID)
																			.replace('&seriesUID=${SERIEID}', '') ;
						
						window.open(url, 'Visualización simple para estudio: ' + studyID, 800, 600);
				    });
					
		        } else alert('Por favor, seleccione un estudio previo a realizar esta acción.');
			}

			function advancedVisualization() {
				var selectedStudies = getSelectedStudies();
		        
				if (selectedStudies && selectedStudies.length > 0) {
		        	
					$.each(selectedStudies, function(index, study) {
						var studyID = study.id;
						var comando = getConfig('actions.study.advanced-view.url')
							.replace('${HOST}', location.hostname)
							.replace('${STUDYID}', studyID)
							.replace('&seriesUID=${SERIEID}', '');
						
						window.open(comando, 'Visualización avanzada para estudio: ' + studyID, 800, 600);	
					});
					
		        } else alert('Por favor, seleccione un estudio previo a realizar esta acción.');
		    }
			
			function getStudyImages() {
				var selectedStudies = getSelectedStudies();				
		        if (selectedStudies.length > 0) {
		        	if (ddUserAgent.isUserAgentActive()) {
		        		getSelectedStudyImages(false);
		        	} else {
	        			ddUserAgent.startUserAgent(
	        					function() { getSelectedStudyImages(false); }, 
	        					function() { warnUserAgentNotStarted(); });
		        	}
	        	} else {
		        	alert('Por favor, seleccione al menos un estudio previo a realizar esta acción.');
		        }				
			}
						
			
			function getSelectedStudyImages(showWaiting) {
				var selectedStudies = getSelectedStudies();				
		        if (selectedStudies.length <= 0)
		        	return;
		        
		        var quality = $('#study-request-images-quality').val();
				
				if (showWaiting) {
	        		window.setTimeout(function() { doUnblockUI(); }, 4000);
	        		doBlockUI();					
				}
				
	        	$.each(selectedStudies, function(index, study) {
		        	getStudyImagesFor(study, quality);				    	
			    });				
			}
			
			function advancedSearch() {				
				var oTT = TableTools.fnGetInstance('studies-found');
				oTT.fnSelectNone();
				
				$("#study-report-status").val('1'); 		// cualquier estado de informe
				$("#search-details-message").text("Búsqueda personalizada");
				filtroCheck();
				doGetStudies();
			};
			
			function showPendingStudies() {
				$('#study-search-form')[0].reset();
				$("#study-report-status").val('2'); 					// solo estudios sin informe cerrado
				$("#study-date-type-any").prop("checked", true);		// buscar 'historicamente'
				$("#search-details-message").text("Mostrando sólo estudios pendientes para: " + userinfo.login);
				limpioCheck();
				clrTopOfTable();
				doGetStudies();
			};
			
			function showAllStudies() {
				$('#study-search-form')[0].reset();
				$("#study-report-status").val('1'); 		// cualquier estado de informe
				$("#study-date-type-any").prop("checked", true);
				$("#search-details-message").text("Mostrando todos los estudios");
				limpioCheck();
				clrTopOfTable();
				doGetStudies();
			};				
			
			function doGetStudies() {
				$("#study-search-form").validate();
				if ($("#study-search-form").valid())
					$('#study-search-form').submit();				
			}

			function prevStudiesPage() {
				$('#studies-found').DataTable().page('previous').draw(false);				
			}

			function nextStudiesPage() {
				$('#studies-found').DataTable().page('next').draw(false);				
			}

			function setPaginationButtonsTooltips() {
			    $('.paginate_button.previous').attr('title', 'Atajo: CONTROL-IZQUIERDA');
			    $('.paginate_button.next').attr('title', 'Atajo: CONTROL-DERECHA');			    				
			}
			
			function getSelectedStudies() {
		    	var oTT = TableTools.fnGetInstance('studies-found');
			    return oTT.fnGetSelectedData();								
			}
			
			function studiesSelectionChanged() {
		    	disableAllStudyActions();
				var selectedStudies = getSelectedStudies();
			    if (selectedStudies.length <= 0) {
			    	return;
			    } else if (selectedStudies.length == 1) {
			    	onStudySelected(selectedStudies[0]);
			    } else {
			    	var getReportOK = true;
			    	var dicomdirOK = true;
			    	var assignToOK = true;
			    	//var sViewOK = true;
			    	//var aViewOK = true;
				    $.each(selectedStudies, function(index, study) {
				    	getReportOK = getReportOK && actionIsAllowed(userinfo, study.iid, 'view-study-report');
				    	dicomdirOK = dicomdirOK && actionIsAllowed(userinfo, study.iid, 'access-study-dicomdir');
				    	assignToOK = assignToOK && actionIsAllowed(userinfo, study.iid, 'assign-study-to');
				    	//sViewOK = sViewOK && actionIsAllowed(userinfo, study.iid, 'simple-study-visualization');
				    	//aViewOK = aViewOK && actionIsAllowed(userinfo, study.iid, 'advanced-study-visualization');
				    });
				    
					$('#study-view-report').button('option', 'disabled', !getReportOK);
					$('#study-get-images').button('option', 'disabled', !dicomdirOK);
					$('#study-request-images-quality').attr('disabled', !dicomdirOK);
					$('#study-assign-to').button('option', 'disabled', !assignToOK);
					$('#study-request-images-quality').attr('disabled', !dicomdirOK);
					$('#study-assign-target').attr('disabled', !assignToOK);
					//$('#study-simple-view').button('option', 'disabled', !sViewOK);
					//$('#study-advanced-view').button('option', 'disabled', !aViewOK);
			    }
			}			
			
			function onStudySelected(study) {
				$('#study-simple-view').button('option', 'disabled', !actionIsAllowed(userinfo, study.iid, 'simple-study-visualization'));				
				$('#study-advanced-view').button('option', 'disabled', !actionIsAllowed(userinfo, study.iid, 'advanced-study-visualization'));
				$('#study-get-images').button('option', 'disabled', !actionIsAllowed(window.userinfo, study.iid, 'access-study-dicomdir'));					
				$('#study-request-images-quality').attr('disabled', !actionIsAllowed(userinfo, study.iid, 'access-study-dicomdir'));
				$('#study-assign-to').button('option', 'disabled', !actionIsAllowed(userinfo, study.iid, 'assign-study-to'));
				$('#study-assign-target').attr('disabled', !actionIsAllowed(userinfo, study.iid, 'assign-study-to'));					
			}
			
			function disableAllStudyActions() {
				$('#study-simple-view').button('option', 'disabled', true);				
				$('#study-advanced-view').button('option', 'disabled', true);
				$('#study-get-images').button('option', 'disabled', true);
				$('#study-request-images-quality').attr('disabled', true);
				$('#study-assign-to').button('option', 'disabled', true);
				$('#study-assign-target').attr('disabled', true);
			}			
			
					
			function updateStudyData(updatedStudyID) {
				$.getJSON("rest/studies/" + updatedStudyID)
				  .done(function(info) {
					  var dt = $('#studies-found').DataTable();
					  
					  $.each(dt.data(), function (index, rowData) {
						if (rowData.id == updatedStudyID) {
							dt.row(index).data(info);
							dt.rows().invalidate().draw(); 
							return false;
						}
					  });
				  })
				  .fail(function() {
				    alert('Ocurrió un error intentando actualizar la información actualmente presentada. ' + 
				    	'Por favor, realize una nueva búsqueda de estudios.');
				  });
			} 
			
			function getConfig(name) {
				var entry = $.grep(ddConfig, function(element, index){
				      return element.name == name;
				});
				
				if (entry && entry.length > 0)
					return entry[0].value;
				return null;
			}
						
			function assignStudiesTo() {
				var selectedStudies = getSelectedStudies();
				var asignado = false;
				
		        if (selectedStudies.length <= 0) {
		        	alert('Por favor, seleccione uno o más estudios previo a realizar esta acción.');
		        } else { 
					var selectedPhysician = $('#study-assign-target').val();
					if (!selectedPhysician)	{
						alert('Debe seleccionar un médico al cual se le asignará el o los estudios seleccionados.');
						return;
					}

				    var dto = {
				    		physicianCode: selectedPhysician,
				    		studiesIDs: []
				    };				    
				    
				    $.each(selectedStudies, function(index, study) {
				    	
				    	if(!study.dr == '') asignado = true;
				    });
				   
				    if(asignado) alert('No se puede realizar la asignación porque\n'+
				    					'  al menos un estudio ya está asignado');
				    
				    else {$.each(selectedStudies, function(index, study) {
				    		dto.studiesIDs.push(study.id);
					    });

					    $.ajax({
					    	  type: "POST",
					    	  url: 'rest/studies/assign-studies',
					    	  data: dto,
					    	  success: function() { alert('estudios asignados al usuario: ' + selectedPhysician);
					    		  					clrTopOfTable();
					    	  						advancedSearch(); }
					    	});}
				    
		        }
			}
			
			function openStudyFilesDialog(studyID, title, modal, editMode) {			
        		var url = 'study-files.html?s=' + studyID + '&e='+(editMode ? 1:0);
        		//var dialogHeight = editMode ? 600 : 410;
        		var hideEffect = editMode ? null : { effect: "fade", duration: 400 };
        		var dialogWidth = 800;
        		
           		$("#dynamicModalDialog").dialog({
           			height: 500,
           			width: dialogWidth,
           			closeOnEscape: true,
           			closeText: "Cerrar",
           			modal: modal,
           			title: title,
           			hide: hideEffect,
           			clickOutside: editMode,
           			open: function(ev, ui) {					        				
           				$('#dynamicModalDialogIframe').attr('src', url);
           			}
           		});				        						        			
			}
			
			function closeModalDialog() {
				$('#dynamicModalDialogIframe').attr('src', 'about:blank');
				$("#dynamicModalDialog").dialog("close");
			}
			
			function readUserInfo() {
				$.getJSON("rest/user/info")
				  .done(function(info) {
					  	window.userinfo = info;
					  	
					  	// Info general del usuario.
				    	$('#userinfo').text(info.login + ' [' + info.lastName + ', ' + info.firstName + ']');
						setTimeout(ddUserAgent.announceUser, 100);
				    	
					  	// Config settings
					  	updateConfigurationTip();
						$('#configuration-link').bubbletip($('#config-tip'));
					  	
				    	// Cargamos las modalidades.
				    	var modalitiesSelect = $("#study-modality"); 
			    		modalitiesSelect.find('option').remove().end();			    		
				    	if (info.modalities && info.modalities.length > 0) {
					    	modalitiesSelect.append('<option value="">Todas</option>');				    						    			
					    	$.each(info.modalities, function(index, modality) {
					    		modalitiesSelect.append('<option value="' + modality.name.replace(/\\/g, '_') + '">' + modality.name + '</option>');
					        });					    	
					    	modalitiesSelect.val('');
				    	}
				    	
				    	// Cargamos las instituciones.
				    	var institutionsSelect = $("#study-diagnostic-center"); 
				    	institutionsSelect.find('option').remove().end();	    		
				    	if (info.institutions.length > 0) {
				    		institutionsSelect.append('<option value="-1">Todos</option>');				    						    			
					    	$.each(info.institutions, function(index, i) {
					    		institutionsSelect.append('<option value="' + i.institution.id + '">' + i.institution.name + '</option>');
					        });					    	
					    	institutionsSelect.val('-1');
				    	}
				    					    	   	
				    	$('#study-diagnostic-center').selectmenu('refresh');
				    	$("#study-modality").selectmenu('refresh');
				    	
				    	cargarMedicos(userinfo);
				  })
				  .fail(function() {
				    	window.location.href = '../authentication-servlet/logout';
				  });
			}
			
			function updateConfigurationTip(h,p,a) {
				var viewerMessage = '';
				var ancho = $('#config-tip').width();
				$('#config-tip').css('margin-left', '-'+ancho+'px');
				
				if(h && p && a){
					if (a) {viewerMessage += a + "@";}
					if (h) {viewerMessage += h;}
					if (p) {
							if (h) viewerMessage += ":" + p;
							else viewerMessage += "Puerto: " + p;
					}
				}else if(window.userinfo.prop != null){
				
					if (window.userinfo.prop.aet) {
						viewerMessage += window.userinfo.prop.aet + "@";
					}
					
					if (window.userinfo.prop.hostname) {
						viewerMessage += window.userinfo.prop.hostname;
					}
					
					if (window.userinfo.prop.port) {
						if (window.userinfo.prop.hostname)
							viewerMessage += ":" + window.userinfo.prop.port;
						else
							viewerMessage += "Puerto: " + window.userinfo.prop.port;
					}
				}
				
				if (viewerMessage.length > 0) $("#config-tip-text").text('Visualizador: ' + viewerMessage);
			}

			function cargarMedicos( user ) {
				
				$.getJSON('rest/user/medicos')
				  .done(function(result) {
				    	
					  	var medicoSelect = $("#study-user"); 
				    	medicoSelect.find('option').remove().end();			    		
			    		medicoSelect.append('<option value="" >Todos</option>');
			    		var physiciansSelect = $("#study-assign-target"); 
				    	physiciansSelect.find('option').remove().end();			    		
			    		physiciansSelect.append('<option value="" selected="selected">Seleccione...</option>');
				    	if (result && result.length > 0) {
				    		if(actionIsAllowed(user, user.institutions[0].institution.id, 'assign-study-to')){
				    		$.each(result, function(index, dr) {
					    		
				    			if(dr.perfil != null && dr.perfil.fancyName != null)
					    		medicoSelect.append('<option value="' + dr.perfil.fancyName + '">' + dr.login + " (" + dr.perfil.fancyName + ")" + '</option>');
				    			else
				    			medicoSelect.append('<option value="' + dr.login + '">' + dr.login + " (" + dr.firstName + " " + dr.lastName + ")" + '</option>');
				    			physiciansSelect.append('<option value="' + dr.login + '">' + dr.firstName + ' ' + dr.lastName + '</option>');
					        });		
				    		}			    	
				    	}						  	
				    	medicoSelect.val('');
				    	
				    	$("#study-user").selectmenu('refresh');
				  })
				  .fail(function() {
					  
					  alert("ERROR AL CARGAR USUARIOS");
				  });
			
			
			$('#study-user').attr('disabled', !actionIsAllowed(user, user.institutions[0].institution.id, 'assign-study-to'));
			//$("#study-user").selectmenu('refresh');
			
			if(!actionIsAllowed(user, user.institutions[0].institution.id, 'access-to-abm')) 
				{	$('label[for="ABM"]').remove();
					$("#ABM").remove();	}
			if(!actionIsAllowed(user, user.institutions[0].institution.id, 'access-studies-from-institution')) 
				{	$("#study-diagnostic-center").prop('disabled', true);	}
			
			/*$.getJSON('rest/medicos/deGuardia')
			  .done(function(result) {
			    	
				  	if (result && result.length > 0) {
				    	$.each(result, function(index, dr) {
				    		$('#lblGuardia').text('DE GUARDIA: Dr. ' + dr.ulname + ' (' + dr.name + ')');
				        });					    	
			    	}
			  });
			
			if(actionIsAllowed(user, user.institutions[0].institution.id, 'set-doctor-on-call')) 
				{	$("#lblGuardia").on('mousedown', function(){return false;})
									.on('dblclick', function(){setMedicoGuardia();});
				}*/
			
			}
			
			function openConfigurationDialog() {
        		var url = 'config.html';
        		var dialogHeight = 315;
        		var dialogWidth = 350;
        		var hideEffect = { effect: "fade", duration: 400 };
        		
           		$("#dynamicModalDialog").dialog({
           			height: dialogHeight,
           			width: dialogWidth,
           			closeOnEscape: true,
           			closeText: "Cerrar",
           			modal: true,
           			title: "Configuración de usuario",
           			hide: hideEffect,
           			clickOutside: false,
           			open: function(ev, ui) {					        				
           				$('#dynamicModalDialogIframe').attr('src', url);
           			}
           		});  						        			
			}
			
			function updateConfigurationProperties() {
				updateConfigurationTip();
			}
			
			function closeModalDialog() {
				$('#dynamicModalDialogIframe').attr('src', 'about:blank');
				$("#dynamicModalDialog").dialog("close");
			}	

			function filtroCheck(){
				
				var isRead = $('#check1').prop('checked') ? true : false;
				var isUrgent = $('#check2').prop('checked') ? true : false;
				var isPreinf = $('#check3').prop('checked') ? true : false;
				var isPreferent = $('#check4').prop('checked') ? true : false;
				var isSigned = $('#check5').prop('checked') ? true : false;
				var isAttached = $('#check6').prop('checked') ? true : false;
				var aux = $("input[type='hidden'][name='aux-filter']");
				
				if ( isRead ) $( '#no-leido' ).val( '1' );
				else $( '#no-leido' ).val( '0' );
				if ( isUrgent ) $( '#urgente' ).val( '1' );
				else $( '#urgente' ).val( '0' );
				if ( isPreinf ) $( '#preinformado' ).val( '1' );
				else $( '#preinformado' ).val( '0' );
				if ( isPreferent ) $( '#preferente' ).val( '1' );
				else $( '#preferente' ).val( '0' );
				if ( isSigned ) $( '#firmado' ).val( '1' );
				else $( '#firmado' ).val( '0' );
				if ( isAttached ) $( '#con-anexo' ).val( '1' );
				else $( '#con-anexo' ).val( '0' );
				aux.val($('#aux-filter').val());
			}
			
			function limpioCheck(){
				
				$( '#check1' ).prop( 'checked' , false );
				$( '#check2' ).prop( 'checked' , false );
				$( '#check3' ).prop( 'checked' , false );
				$( '#check4' ).prop( 'checked' , false );
				$( '#check5' ).prop( 'checked' , false );
				$( '#check6' ).prop( 'checked' , false );
				$( '#no-leido' ).val( '0' );
				$( '#urgente' ).val( '0' );
				$( '#preinformado' ).val( '0' );
				$( '#preferente' ).val( '0' );
				$( '#firmado' ).val( '0' );
				$( '#con-anexo' ).val( '0' );
				$('#aux-filter').val( '0' );
				$("input[type='hidden'][name='aux-filter']").val( '0' );
				
			}
			
			function clrTopOfTable(){
				
				$( '#incidenciaH' ).val('0');
				$( '#new-study' ).val('0');
			}
			
			function CompletePages( info, index ) {
				
				$( '#pgIndex' ).children().remove();
				$( '#pgIndex' ).html(function(i,h){return h.replace(/&nbsp;/g,'');});
				
				if (info != null){
				
				var totPages = info.pages;
				var subPages = parseInt( totPages / 10 );
				if(totPages % 10 > 0) subPages ++;
				
				if(totPages <= 10){
					for( var i = 1 ; i <= totPages ; i++ ){
					
						var page = "&nbsp;&nbsp;<a id='p"+i+"' onclick='changeP(this);' class='pageNum'>"+i+"</a>";
						$( '#pgIndex' ).append( page );
					}}
				
				else{
					for( var i = 1 + (10*(index-1)) ; i <= 10 * index ; i++ ){
						
						if(i <= totPages){
							
						if((10*(index-1)) > 0 && i % 10 == 1) $( '#pgIndex' ).append( "&nbsp;&nbsp;<a class='pageNum prev'>...</a>" );
						var page = "&nbsp;&nbsp;<a id='p"+i+"' onclick='changeP(this);' class='pageNum'>"+i+"</a>";
						$( '#pgIndex' ).append( page );
						if(i % 10 == 0 && index < subPages) $( '#pgIndex' ).append( "&nbsp;&nbsp;<a class='pageNum nxt'>...</a>" );
						
						}}}
				
				$('#pgIndex').children().removeClass('pageNumSelected');
				$('#p'+(info.page+1)).addClass('pageNumSelected');
				
				$('a.prev').on('click', function(){modIndex(subPages, index, false);});
				$('a.nxt').on('click', function(){modIndex(subPages, index, true);});
				
				}
			}
			
		function modIndex(subPages, index, asc){
			
			var table = $("#studies-found").DataTable();
			pageInfo = table.page.info();
			
			if(asc){
				index++;
				if(index <= subPages) CompletePages(pageInfo, index);
			}
			else{
				index--;
				if(index > 0) CompletePages(pageInfo, index);
			}
		}
		
		function AjustePantalla1(){
			
			//var AltoVentana = window.innerHeight;
			var AnchoVentana = document.body.clientWidth;	
			

			if(AnchoVentana < 1000) $('.top-screen-center').css('font-size', '0.7em');
			else if(AnchoVentana > 1200) $('.top-screen-center').css('font-size', '0.85em');
			else $('.top-screen-center').css('font-size', '0.8em');
			
			var margen = ( $('body').width() - $('.top-screen-center').width() ) / 2 ;
			$('.top-screen-center').css('margin-left', margen+'px');
			
			var h = ($('#divCuerpoPagina').height() - $('.ui-layout-north').height());
			$('.ui-layout-center').css('height', (h)+'px');
			$('div#studies-found_wrapper').css('height', (h)+'px');
		}
		
		window.addEventListener( "resize" , function() { AjustePantalla1(); } );
			
//# sourceURL=study-manager.js