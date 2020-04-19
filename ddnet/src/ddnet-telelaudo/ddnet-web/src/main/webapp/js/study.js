/**
 * 
 * 	Funciones de la ventana "study.html"
 * 
 */

var study = null;
var attachmentsTable = $('#attachments-table').dataTable();
var isIncidence = null;
var aux = null;
var button = null;
var AltoVentana = window.innerHeight;
var AnchoVentana;
var Interval;
//var studyUID = null;

$(document).ready(function() {
    
	//studyUID = $.url().param('s');
	
	$("#TemplatesMenu").hide();
	AnchoVentana = document.body.clientWidth;
	if(AnchoVentana <= 1024) {	$('body').css("padding-left","3%").css("padding-right","3%");
								$('#btn-comprobar-caso').css('margin-right','25%');}
	else if(AnchoVentana > 1024 && AnchoVentana <= 1295) { 	$('body').css("padding-left","10%").css("padding-right","10%");
															$('#btn-comprobar-caso').css('margin-right','30%');}
});

$(window).load(function() {
    
	getTemplates();
	$('input[type="button"]').button();
	var interv = window.setInterval(function(){
		if($('#study-report-multiple').val()==null)
			$('#study-report-multiple').prop('selectedIndex','0');
		clearInterval(interv);
	},2000);
});

window.addEventListener( "resize" , function(){

		AnchoVentana = document.body.clientWidth;
		if(AnchoVentana <= 1024) { 	$('body').css("padding-left","3%").css("padding-right","3%");
									$('#btn-comprobar-caso').css('margin-right','25%');}
		else if(AnchoVentana > 1024 && AnchoVentana <= 1295) { 	$('body').css("padding-left","10%").css("padding-right","10%");
																$('#btn-comprobar-caso').css('margin-right','30%');}
		else {	$('body').css("padding-left","20%").css("padding-right","20%");
				$('#btn-comprobar-caso').css('margin-right','33%');}
		$('#study-report-topleft').css('margin-left', ($('#study-report-container').width()/1.5) + 'px');
});

/************************************************** DATOS CLINICOS *******************************************************/

function setClinicalData( studyID ) {
	
	var isUrgente = $( "#study-clinical-data-urgent" ).prop( 'checked' ) ? true : false ;
	var isPreferente = $( "#study-clinical-data-preferential" ).prop( 'checked' ) ? true : false ;
	var isOral = $( "#study-clinical-data-contrast-oral" ).prop( 'checked' ) ? true : false ;
	var isEv = $( "#study-clinical-data-contrast-ev" ).prop( 'checked' ) ? true : false ;
	var form = $('form[name="study-clinical-data-form"]');
	
	if(isUrgente) $('#study-clinical-data-urgency-h').val('2');
	else if(isPreferente) $('#study-clinical-data-urgency-h').val('1');
	if (isOral) $( "#cte_oral" ).val('1');
	else $( "#cte_oral" ).val('0');
	if (isEv) $( "#cte_ev" ).val('1');
	else $( "#cte_ev" ).val('0');
		
	$.post("rest/studies/" + studyID + "/datosclinicos" , form.serialize())
	.done( function() { location.reload(); } )
	.fail( function() { alert('ocurrió un error al momento de setear los datos clínicos'); } );
	
	
}

function getClinicalData( studyID ) {
	
	$.getJSON("rest/studies/" + studyID + '/datosclinicos')
	.done( function(data) {
		
		if(data && data.length>0){
			$.each(data, function(index, dc) {
				
				$("#study-clinical-data-other").val(dc.notes);
				if (dc.oral) $("#study-clinical-data-contrast-oral").prop( 'checked', 'checked' );
				if (dc.ev) $("#study-clinical-data-contrast-ev").prop( 'checked', 'checked' );
				
				if (dc.priority == 2) $("#study-clinical-data-urgent").prop( 'checked', 'checked' );
				else if (dc.priority == 1) $("#study-clinical-data-preferential").prop( 'checked', 'checked' );
				
				$.getJSON("rest/user/info").done(function(info) {
					
					var inst = userinfo.institutions[0].institution.id;
					
					$('#study-clinical-data :input').attr( 'disabled' , !actionIsAllowed(info, inst, 'modify-clinical-data') );
				});
				
	        });
		}
	});
	
}

/************************************************** ADJUNTAR ARCHIVOS *******************************************************/

function doAttachment( studyID ) {
	
	var iframe = '<div id="dynamicModalDialog">' +
					'<iframe id="dynamicModalDialogIframe" src="" ></iframe>' +		
				 '</div>';
	
	$('body').append(iframe);
	
	openStudyFilesDialogs(studyID, "Archivos", true, true); 
	
}

function openStudyFilesDialogs(studyID, title, modal, editMode) {			
	
	var url = 'study-files.html?s=' + studyID + '&e='+(editMode ? 1:0);
	var hideEffect = editMode ? null : { effect: "fade", duration: 400 };
	$('#dynamicModalDialogIframe').css('width', '99.7%');
	$('#dynamicModalDialogIframe').css('height', '99%');
	
		$("#dynamicModalDialog").dialog({
			height: 500,
			width: 800,
			closeOnEscape: true,
			closeText: "Cerrar",
			modal: modal,
			title: title,
			hide: hideEffect,
			clickOutside: !editMode,
			open: function(ev, ui) {					        				
				$('#dynamicModalDialogIframe').attr('src', url);
			},
			close: function(ev, ui) {
				$("#dynamicModalDialog").remove();
				location.reload();
			}
		});				        						        			
}

function setAttachment( hasFiles , studyID ) {
	
	$.post("rest/studies/" + studyID + "/attachments/" + hasFiles )
	.fail( function() { alert('ocurrió un error al momento de adjuntar archivos'); } );
}

/************************************************* ASIGNACIÓN *********************************************************/

function assignStudies( elem , studyID ) {
	
	var Abutton = $(elem);
	var label = Abutton.val();
	
	var selectedPhysician = $('#cmb-study-assignment').val();
	if (!selectedPhysician)	{
			alert('Debe seleccionar un médico al cual se le asignará el o los estudios seleccionados.');
			return;
		}

	var dto = {
    		physicianCode: selectedPhysician,
    		studiesIDs: []
    };				    
    
    dto.studiesIDs.push( studyID );

	if(label.startsWith('Asignar')) {    

	    $.ajax({
	    	  type: "POST",
	    	  url: 'rest/studies/assign-studies',
	    	  data: dto,
	    	  success: function() { alert("El estudio fue asignado al usuario "+selectedPhysician);
	    	  						location.reload();}
	    	});
	}
	
	else if(label.startsWith('Reasignar')) {
		
		$.ajax({
	    	  type: "POST",
	    	  url: 'rest/studies/reassign-studies',
	    	  data: dto,
	    	  success: function() { alert("El estudio fue reasignado al usuario "+selectedPhysician);
									location.reload(); }
	    	});
	}
}

function getAssignments ( studyID ){
	
	$.getJSON("rest/studies/" + studyID + "/assign-studies")
	.done( function(info) { 
		
		if(info != null){
			
				$("#lbl-assignments").text( 'Este estudio se asignó a: ' + info.firstName + ' ' + info.lastName );
				
				$("#lbl-assignments").append( '&nbsp;&nbsp;||&nbsp;&nbsp; Reasignar a:' );
				
				$("#btn-assign-study").val( 'Reasignar y Avisar' );
				
				$("#cmb-study-assignment option[value='" + info.login + "']").remove();
		}
			
	})
	.fail( function() { alert("ERROR al cargar la asignación"); } ); 
}


/************************************************* INCIDENCIAS ********************************************************/

function setIncidence( studyID ) {
	
	var date = $("#study-message-incident-resolution-date").val();
	var msg = $("#study-message-or-incident").val();
	var form = $('form[name="study-incidences"]');
	var isInc = $( "#study-message-is-incident" ).prop( 'checked' ) ? true : false ;
	
	if( msg == '' ) alert( "deje un mensaje que describa la incidencia" );
	else{
			
		if(date == '')alert( "indique la feche de la incidencia" );
		else{
			if (isInc) $( "#has-incidence" ).val('1');
			else $( "#has-incidence" ).val('0'); 
			 
			var time = new Date();
			
			var año = date.substring(6,10);
			var mes = date.substring(3,5);
			var dia = date.substring(0,2);
			var fulldate = año+mes+dia+" "+time.toLocaleTimeString().substring(0, 5);
			
			$("#study-message-incident-resolution-date").val(fulldate);
			
			form.valid();
			
			$.post("rest/studies/" + studyID + "/newincidence" , form.serialize())
			.done( function() { location.reload(); } )
			.fail( function() { alert('ocurrió un error al momento de generar la incidencia'); } );
			
			$("#study-message-incident-resolution-date").val(date);
			
		}}
}

function getIncidences( studyID ) {
	
		
	$.getJSON("rest/studies/" + studyID + '/incidences')
	.done( function(data) {
		
		var mensaje = '';
		
		if(data && data.length>0){
			$.each(data, function(index, i) {

				var Fmsj = i.mdate.substring(6,8) + "-" + i.mdate.substring(4,6) + "-" + i.mdate.substring(0,4) ;
				var Hmsj = i.mdate.substring(9,14) ;
				
				if(i.state == 2) {$( "#study-message-is-incident" ).attr( 'checked' , 'checked' );
								  $(".study-message-incident-resolution-date-label").text("Fecha de Resolución");}
				
				mensaje += "[Mensaje escrito día "+Fmsj+" a las "+Hmsj+" hs por el usuario: "+i.user+"]\n\n\t" + i.msj + "\n\n";
				
				
			});
			
			$("#study-message-or-incident").val(mensaje);
			$("#study-message-or-incident").attr( "readonly" , "readonly" );
			$("#btn-add-message").show();
			$("#btn-save-message").prop('disabled', true);
		}
		
		else {
			
			$("#btn-add-message").hide();
			$("#btn-save-message").prop('disabled', false);
			
		}
		
		isIncidence = $( "#study-message-is-incident" ).prop( 'checked' ) ? true : false ;
		aux = $("#study-message-or-incident").val();
		$("#btn-save-incidence").prop('disabled', true);
		$("#study-message-incident-resolution-date").prop('disabled', true);
		button = $("#btn-save-message").prop('disabled') ? true : false ;
	});
}

function addMessage() {
	
	var label = $( "#btn-add-message" ).val();
	
	if(label == 'Agregar Mensaje'){
		
		$("#study-message-or-incident").val('');
		$("#study-message-or-incident").removeAttr( "readonly" );
		$("#btn-save-message").prop('disabled', false);
		$("#btn-save-incidence").prop('disabled', true);
		$("#study-message-incident-resolution-date").prop('disabled', true);
		$( "#btn-add-message" ).val('Cancelar');
	}
	else if(label == 'Cancelar'){
		$("#study-message-or-incident").val(aux);
		$("#study-message-or-incident").attr( "readonly" , "readonly" );
		$("#btn-save-message").prop('disabled', true);
		$("#btn-save-incidence").prop('disabled', true);
		$("#study-message-incident-resolution-date").prop('disabled', true);
		$( "#btn-add-message" ).val('Agregar Mensaje');
	}
}

function addIncidence() {
	
	var checked = $( "#study-message-is-incident" ).prop( 'checked' ) ? true : false ;
	
	if(isIncidence == checked){
		$("#study-message-or-incident").val(aux);
		$("#study-message-or-incident").attr( "readonly" , "readonly" );
		if (button) $("#btn-save-message").prop('disabled', true);
		else $("#btn-save-message").prop('disabled', false);
		$("#btn-save-incidence").prop('disabled', true);
		$("#study-message-incident-resolution-date").prop('disabled', true);
		$("#btn-add-message").prop('disabled', false);
	}
	else{
	$("#study-message-or-incident").val('');
	$("#study-message-or-incident").removeAttr( "readonly" );
	$("#btn-save-message").prop('disabled', true);
	$("#btn-save-incidence").prop('disabled', false);
	$("#study-message-incident-resolution-date").prop('disabled', false);
	$("#btn-add-message").prop('disabled', true);
	
	}
	
}

function saveMessage( studyID ) {
	
	var msg = $("#study-message-or-incident").val();
	var form = $('form[name="study-incidences"]');
	var date = '';
	
	if( msg == '' ) alert( "escriba un mensaje" );
	else {
		var time = new Date();
		
		var dia = time.getDate();
		if(dia<10) dia = "0"+dia;
		var mes = time.getMonth()+1;
		if(mes<10) mes = "0"+mes;
		var año = time.getFullYear();
		
		date = año+"-"+mes+"-"+dia+" "+time.toLocaleTimeString().substring(0, 5);
		
		$("#message-date").val(date.replace( '-' , '' ).replace('-', ''));
		
		form.valid();
		
		$.post("rest/studies/" + studyID + "/newmessage" , form.serialize())
		.done( function() { location.reload(); } )
		.fail( function() { alert('ocurrió un error al momento de guardar el mensaje'); } );
		
	}
	
}
/************************************************ INFORMES *********************************************************/

function getTemplates() {
	
	var std = $.url().param('s');
	//var lista = [];
	
	$.getJSON("rest/reporting/templates/methods")
		.done( function(data) { 
		
		if(data && data.length>0) {
			
		$("#TemplatesMenu").children().remove();
			
		$.each(data, function(i, name) {
			
			$("#TemplatesMenu").append( "<li id='"+name+"'><a>"+name+"</a></li>" );
			$("#"+name).append( "<ul id='ul_"+name+"' class='sub1'></ul>" );
		
	$.getJSON("rest/reporting/templates/methods/"+name)
		.done( function(data1) { 
					
		if(data1 && data1.length>0){
						
		$("#"+name).append( "<ul id='ul_"+name+"' class='sub1'></ul>" );
					
		$.each(data1, function(i1, name1){
					
			$("#ul_"+name).append( "<li id='"+name+"_"+name1+"'><a>"+name1+"</a></li>" );
					
	$.getJSON("rest/reporting/templates/methods/"+name+"/"+name1)
		.done( function(data2) {
						
		if(data2 && data2.length>0){
							
		$("#"+name+"_"+name1).append( "<ul id='ul_"+name+"_"+name1+"' class='sub2'></ul>" );
							
		$.each(data2, function(i2, name2){
								
			if(name2.endsWith('.txt')) {
								
			$("#ul_"+name+"_"+name1).append( "<li id='"+name+"_spc_"+name1+"_spc_"+name2+"' onclick='setTemplate(this,\""+ std +"\");'><a>"
																				+name2.replace(/_/g , " ").replace('.txt','')+"</a></li>" );
			var ul = $("#ul_"+name+"_"+name1);
			ul.children().children().css("font-weight","bold");
			}
			else $("#ul_"+name+"_"+name1).append( "<li id='"+name+"_"+name1+"_"+name2+"'><a>"+name2.replace(/_/g , " ")+"</a></li>" );
								
	$.getJSON("rest/reporting/templates/methods/"+name+"/"+name1+"/"+name2)
		.done( function(data3) {
									
		if(data3 && data3.length>0){
										
		$("#"+name+"_"+name1+"_"+name2).append( "<ul id='ul_"+name+"_"+name1+"_"+name2+"' class='sub3'></ul>" );
										
		$.each(data3, function(i3, name3) {
											
			if(name3.endsWith('.txt')) {
			
				
			$("#ul_"+name+"_"+name1+"_"+name2).append( "<li id='"+name+"_spc_"+name1+"_spc_"+name2+"_spc_"+name3+"' onclick='setTemplate1(this,\""+ std +"\");'><a>"
																										+name3.replace(/_/g , " ").replace('.txt','')+"</a></li>" );
			
			
			var ul1 = $("#ul_"+name+"_"+name1+"_"+name2);
			ul1.children().children().css("font-weight","bold");
			}
			else $("#ul_"+name+"_"+name1+"_"+name2).append( "<li id='"+name+"_"+name1+"_"+name2+"_"+name3+"'><a>"+name3.replace(/_/g , " ")+"</a></li>" );
		
		});}
		
		Interval = window.setInterval(setMenu, 3000);
									
		}).fail( function() { alert("ERROR3"); } ); 
								
		});
		}
		}).fail( function() { alert("ERROR2"); } ); 
					
		});}	
					
		}).fail( function() { alert("ERROR1"); } );
				
		});}	
		
		}).fail( function() { alert("ERROR"); } );
	
}

function setMenu(){

	var cuerpoWidth = $('#study-report').width();
	var templ = $('#TemplatesContainer');
	var menuWidth = 0;
	
	templ.css('width', cuerpoWidth-30 + 'px');
	
	$('#TemplatesMenu li').each(function(i, elem){
		
		menuWidth += $(elem).width() + 21;
	});
	
	$('#jqxMenu').css('width', menuWidth + 'px');
	
if(!getF()){
	
	$('#jqxMenu').jqxMenu();
	
	$('#TemplatesMenu').show();
	
	clearInterval(Interval);
}
else{
	
	$('#jqxMenu').jqxMenu();
	
	$('#TemplatesMenu li').each(function(i, elem){
		
		$('#jqxMenu').jqxMenu('disable',elem.id,true);
	});
	
	$('#TemplatesMenu').show();
	
	clearInterval(Interval);
}
	
}

function setTemplate(elem , studyID) {
	
	var templID = elem.id;
	
	var f1 = templID.substring(0,templID.indexOf('_spc_'));
	
	var aux = templID.substring(f1.length+5,templID.length);
	
	var f2 = aux.substring(0,aux.indexOf('_spc_'));
	
	var f3 = aux.substring(f2.length+5,aux.length);
	
	if ($('#study-report-multiple').val() == 1 && $('#study-report-body').val().trim())
		if (!confirm('ATENCIÓN: Ya existe texto ingresado.\nSi continúa, el mismo será sobreescrito.\n' +
					 ' ¿Confirma la utilización del protocolo base seleccionado?'))
			return;
	
	$.getJSON("rest/reporting/templates/methods/"+studyID+"/"+f1+"/"+f2+"/"+f3)
	  .done(function(info) {
		  
		  if($('#study-report-multiple').val() != 1) $('#study-report-body').val($('#study-report-body').val() + '\n\n' + info.body);
		  else $('#study-report-body').val(info.body);
		  $('#study-report-body').css( 'height' , '140px' );
		  $('#study-report-body').css( 'height' , document.getElementById('study-report-body').scrollHeight + 'px' ); 

	  })
	  .fail(function() {
	    alert('Ocurrió un error en la comunicación con el servidor. ' + 
	    	'Por favor, reintente la operación realizada.');
	  });
}

function setTemplate1(elem , studyID) {
	
	var templID = elem.id;
	
	var f1 = templID.substring(0,templID.indexOf('_spc_'));
	
	var aux = templID.substring(f1.length+5,templID.length);
	
	var f2 = aux.substring(0,aux.indexOf('_spc_'));
	
	aux = aux.substring(f2.length+5,aux.length);
	
	var f3 = aux.substring(0,aux.indexOf('_spc_'));
	
	var f4 = aux.substring(f3.length+5,aux.length);
	
	if ($('#study-report-multiple').val() == 1 && $('#study-report-body').val().trim())
		if (!confirm('ATENCIÓN: Ya existe texto ingresado.\nSi continúa, el mismo será sobreescrito.\n' +
					 ' ¿Confirma la utilización del protocolo base seleccionado?'))
			return;
	
	
	$.getJSON("rest/reporting/templates/methods/"+studyID+"/"+f1+"/"+f2+"/"+f3+"/"+f4)
	  .done(function(info) {
		  
		  	if($('#study-report-multiple').val() != 1) $('#study-report-body').val($('#study-report-body').val() + '\n' + info.body);
		  	else $('#study-report-body').val(info.body);
		  	$('#study-report-body').css( 'height' , '140px' );
		  	$('#study-report-body').css( 'height' , document.getElementById('study-report-body').scrollHeight + 'px' ); 
		  
	  })
	  .fail(function() {
		        		alert('Ocurrió un error en la comunicación con el servidor. ' + 
		        		'Por favor, reintente la operación realizada.');
		        		});
}

function saveReport( studyID ) {
	
	var mensaje = '¿Confirma la grabación de los cambios introducidos?\nEl informe quedará marcado como "Preinformado",\npara permitir posteriores modificaciones.';
	
	if (!studyID)
		return;
	
	if (!$('#study-report-body').val().trim()) alert('No hay texto ingresado');
	
	else{
		
	if($('#study-report-is-teaching-file').prop('checked')) mensaje += '\n\nATENCIÓN: ha marcado el estudio como TEACHING FILE.\n' +
																		'Esta opción se confirma únicamente al FIRMAR el informe.\n';
	if($('#study-report-is-emergency').prop('checked')) mensaje += '\n\nATENCIÓN: ha marcado el estudio como EMERGENCIA MÉDICA.\n' +
																	'Esta opción se confirma únicamente al FIRMAR el informe.\n';
	
	if (!confirm(mensaje))
		return;
	
	$('#finished-flag').val('false');
	var form = $('form[name="report-data"]');
	
	$.post("rest/studies/" + studyID + "/report/body", form.serialize())
	  .done(function(info) {
		  location.reload();						  
	  })
	  .fail(function() {
	    alert('Error al guardar el informe');
	  });
	}
}

function finishReport ( studyID ) {
	
	if($('#study-report-multiple').val()==null)
		$('#study-report-multiple').prop('selectedIndex','0');
	
	if (!studyID)
		return;
	
	if (!$('#study-report-body').val().trim()) alert('No hay texto ingresado');
	
	else{
	
	if (!confirm('¿Confirma la finalización y cierre de este informe? No podrá volver a modificarlo una vez cerrado.'))
		return;
	
	$('#finished-flag').val('true');
	var form = $('form[name="report-data"]');
	
	$.post("rest/studies/" + studyID + "/report/body", form.serialize())
	  .done(function(info) {
		  location.reload();
	  })
	  .fail(function() {
	    alert('Ocurrió un error en la comunicación con el servidor. ' + 
	    	'Por favor, reintente la operación realizada.');
	  });
	}
}

/************************************************ LOG *********************************************************/


function openStudyLog( studyID ) {
	
	var iframe = '<div id="dynamicModalDialog">' +
						'<iframe id="dynamicModalDialogIframe" src="" ></iframe>' +		
				 '</div>';
		
	$('body').append(iframe);
	
	var url = 'study-log.html?s=' + studyID ;
	var height = AltoVentana * (2/3);
	var width = AnchoVentana / 1.2;
		
	$('#dynamicModalDialogIframe').css('width', '99.7%');
	$('#dynamicModalDialogIframe').css('height', '98.5%');
	
		$("#dynamicModalDialog").dialog({
			height: height,
			width: width,
			closeOnEscape: true,
			closeText: "Cerrar",
			modal: true,
			title: 'LOG DE TRAZABILIDAD - Estudio ' + studyID,
			open: function(ev, ui) {					        	
					$('#dynamicModalDialogIframe').attr('src', url);
				},
				close: function(ev, ui) {
					$('#dynamicModalDialogIframe').attr('src', 'about:blank');
					$("#dynamicModalDialog").remove();
				}
			});		
}

/************************************************ SEGUNDA LECTURA *********************************************************/


function openSegundaLectura( studyID, discrep ) {
	
	var iframe = '<div id="dynamicModalDialog">' +
						'<iframe id="dynamicModalDialogIframe" src="" ></iframe>' +		
				 '</div>';
		
	$('body').append(iframe);
	
	var url = 'study-segunda_lectura.html?s=' + studyID + '&disc=' + discrep;
	var height = AltoVentana / 2;
	var width = AnchoVentana / 2;
		
	$('#dynamicModalDialogIframe').css('width', '99.9%');
	$('#dynamicModalDialogIframe').css('height', '98.5%');
	
		$("#dynamicModalDialog").dialog({
			height: height,
			width: width,
			closeOnEscape: true,
			closeText: "Cerrar",
			modal: true,
			title: 'SEGUNDA LECTURA DE INFORME - Estudio ' + studyID,
			open: function(ev, ui) {					        	
					$('#dynamicModalDialogIframe').attr('src', url);
				},
				close: function(ev, ui) {
					$('#dynamicModalDialogIframe').attr('src', 'about:blank');
					$("#dynamicModalDialog").remove();
					//location.reload();
				}
			});		
}

/************************************************ COMPROBAR CASO *********************************************************/

function openComprobarCaso( studyID, state ) {
	
	var iframe = '<div id="dynamicModalDialog">' +
						'<iframe id="dynamicModalDialogIframe" src="" ></iframe>' +		
				 '</div>';
		
	$('body').append(iframe);
	
	var url = 'study-comprobar_caso.html?s=' + studyID + '&state=' + state;
	var height = AltoVentana / 1.8;
	var width = AnchoVentana / 2;
		
	$('#dynamicModalDialogIframe').css('width', '99.9%');
	$('#dynamicModalDialogIframe').css('height', '98.5%');
	
		$("#dynamicModalDialog").dialog({
			height: height,
			width: width,
			closeOnEscape: true,
			closeText: "Cerrar",
			modal: true,
			title: 'COMPROBAR CASO - Estudio ' + studyID,
			open: function(ev, ui) {					        	
					$('#dynamicModalDialogIframe').attr('src', url);
				},
				close: function(ev, ui) {
					$('#dynamicModalDialogIframe').attr('src', 'about:blank');
					$("#dynamicModalDialog").remove();
					//location.reload();
				}
			});		
}

function getPrevReports(studyID){
	
	var iframe = '<div id="dynamicModalDialog">' +
						'<iframe id="dynamicModalDialogIframe" src="" ></iframe>' +		
				 '</div>';

	$('body').append(iframe);

	var url = 'study-previous-reports.html?s=' + studyID ;
	var height = AltoVentana / 2;
	var width = height;

	$('#dynamicModalDialogIframe').css('width', '99.9%');
	$('#dynamicModalDialogIframe').css('height', '98.5%');
	
	$("#dynamicModalDialog").dialog({
		height: height,
		width: width,
		closeOnEscape: true,
		closeText: "Cerrar",
		modal: true,
		title: 'INFORMES PREVIOS - Estudio ' + studyID,
		open: function(ev, ui) {					        	
			$('#dynamicModalDialogIframe').attr('src', url);
		},
		close: function(ev, ui) {
			$('#dynamicModalDialogIframe').attr('src', 'about:blank');
			$("#dynamicModalDialog").remove();
		}
	});	
}













