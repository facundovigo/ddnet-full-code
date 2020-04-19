
var studyId= null, ddConfig= null, study= null, userinfo= null, dtFiles= null, dtOrdMed= null, dtMP3= null, dtLog= null, afterConex= false;

window.addEventListener('resize',ajustes.studyWindow);
function $alert(mensaje){if(!mensaje||mensaje==null)return false;mensaje=mensaje.toString();var cuerpo='';cuerpo+='<div id="dialog-message" title="Mensaje"><br>';cuerpo+=mensaje.replace(/\n/g,'<br>');cuerpo+='</div>';$('body').append(cuerpo);$("#dialog-message").dialog({modal:true,buttons:{OK:function(){$(this).dialog("close");$(this).remove();}}});}
function $confirm(mensaje,callback){if(!mensaje||mensaje==null)return false;mensaje=mensaje.toString();var cuerpo='';cuerpo+='<div id="dialog-confirm" title="Confirmar"><br>';cuerpo+=mensaje.replace(/\n/g,'<br>');cuerpo+='</div>';$('body').append(cuerpo);$('div#dialog-confirm').dialog({resizable:false,modal:true,buttons:{'Confirmar':function(){$(this).dialog('close');$(this).remove();callback();},'Cancelar':function(){$(this).dialog('close');$(this).remove()}}})}
function set_as_datepicker(elem){var dayNames=["Domingo","Lunes","Martes","Mi&eacute;rcoles","Jueves","Viernes","S&aacute;bado"];var dayNamesMin=["DOM","LUN","MAR","MIE","JUE","VIE","SAB"];var monthNames=["Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Setiembre","Octubre","Noviembre","Diciembre"];var monthNamesShort=['Ene','Feb','Mar','Abr','May','Jun','Jul','Ago','Sep','Oct','Nov','Dic'];$(elem).datepicker({dateFormat:"dd/mm/yy",changeYear:true,yearRange:"-100:+0",changeMonth:true,dayNames:dayNames,dayNamesMin:dayNamesMin,monthNames:monthNames,monthNamesShort:monthNamesShort})}
function openDialog(url,title,height,width){var iframe='<div id="dynamicModalDialog"><iframe id="dynamicModalDialogIframe" src=""></iframe></div>';$('body').append(iframe);$('div#dynamicModalDialog').dialog({title:title,height:height,width:width,closeOnEscape:true,closeText:'Cerrar',clickOutside:false,hide:{effect:'fade',duration:500},modal:true,open:function(ev,ui){$('iframe#dynamicModalDialogIframe').attr('src',url)},close:function(ev,ui){$('iframe#dynamicModalDialogIframe').attr('src','about:blank');$('div#dynamicModalDialog').remove()}});}
function getConfig(name){var entry=$.grep(ddConfig,function(element,index){return element.name==name});if(entry&&entry.length>0)return entry[0].value;return null}
function open_conf_dialog(){var url='config.html',title='Configuración de Usuario';openDialog(url, title, 350, 350)}
function update_configuration_tip(userProp){if(userProp&&userProp!=null){var viewerMessage='',host=userProp.hostname,aet=userProp.aet,port=userProp.port;if(aet)viewerMessage+=aet+'@';if(host)viewerMessage+=host;if(port)viewerMessage+=(host?':'+port:'Puerto:'+port);if(viewerMessage.length>0)$("a#configurationLink").attr('title','Visualizador : '+viewerMessage)}else $("a#configurationLink").attr('title','')}


function get_header_info(){
	$.ajax({method:'GET',dataType:'json',url:'rest/studies/'+studyId+'/full',async:false})
	.done(function(data){
		study= data;
		if(data && data!=null){
			var span= $('div.header-info').find('span');
			$(span[0]).text(data.description!=null?data.description:'---');
			$(span[1]).text(data.patientName);
			$(span[2]).text(data.patientID);
			$(span[3]).text(data.age!=null?data.age:'---');
			$(span[4]).text(data.sex!=null?data.sex:'---');
			$(span[5]).text(data.accessionNumber!=null?data.accessionNumber:'---');
			$(span[6]).text(data.formattedStudyDate);
			$(span[7]).text(data.modality);
			$.get('rest/institutions/'+data.institutionID+'/logo/check')
			.done(function(flag){
				if(flag && flag==1) $('img#institutionLogo').attr('src','rest/institutions/'+data.institutionID+'/logo');
				else $('img#institutionLogo').attr('src','../images/logo.jpg');
			});
		}
	})
	.fail(function(){window.close()});
}



var studyActions= {
	studyVisualization: {
		preset_menu: function(){
			$('div.visual-menu').find('h4').text('Imágenes (#'+study.numImgs+') :');
			$('ul#visualMenu').menu({items:'> :not(.ui-widget-header)'});
			var li= $('ul#visualMenu').find('li');
			
			!userinfo.permissions.canSimpleView ? $(li[1]).addClass('ui-state-disabled') :
				$(li[1]).on('click',function(){
					var url= getConfig('actions.study.simple-view.url');
					url= url.replace('${HOST}',location.hostname).replace('${STUDYID}',studyId).replace('&seriesUID=${SERIEID}','');
					window.open(url,'Visualización simple para estudio: '+studyId,800,800);
				})
			!userinfo.permissions.canIntermedView ? $(li[2]).addClass('ui-state-disabled') :
				$(li[2]).on('click',function(){
					var url= getConfig('actions.study.dd-viewer.url');
					url= url.replace('${HOST}',location.hostname).replace('${STUDYID}',studyId);
					window.open(url,'Visualización simple para estudio: '+studyId,800,800);
				})
			!userinfo.permissions.canAdvancedView ? $(li[3]).addClass('ui-state-disabled') :
				$(li[3]).on('click',function(){
					var url= getConfig('actions.study.advanced-view.url').replace('${HOST}',location.hostname).replace('${STUDYID}',studyId);
					window.open(url,'Visualización avanzada para estudio: '+studyId,800,800);
				})
			!userinfo.permissions.canUseAgent ? $(li[5]).addClass('ui-state-disabled') :
				$(li[5]).on('click',function(){
					if(ddUserAgent.isUserAgentActive()) getStudyImagesFor(study,'lossy');
					else {
						ddUserAgent.startUserAgent(
							function(){getStudyImagesFor(study,'lossy')},
							function(){warnUserAgentNotStarted()}
						);
			    	}
				})
			!userinfo.permissions.canUseAgent ? $(li[6]).addClass('ui-state-disabled') :
				$(li[6]).on('click',function(){
					if(ddUserAgent.isUserAgentActive()) getStudyImagesFor(study,'loseless');
			    	else {
						ddUserAgent.startUserAgent(
							function(){getStudyImagesFor(study,'loseless')}, 
							function(){warnUserAgentNotStarted()}
						);
			    	}
				})
			!userinfo.permissions.canDownloadDicomdir ? $(li[7]).addClass('ui-state-disabled') :
				$(li[7]).on('click',function(){
					var url= 'rest/studies/'+studyId+'/media?quality=0&options=nonexec';
	        		$.fileDownload(url);
				})
				
			$(li[9]).on('click',function(){openDialog('study-series.html?s='+studyId,'Series del Estudio',520,750)})
		}
	},
	studyReport: {
		charge_templates: function(){
			$.getJSON('rest/reporting/templates/modalities/'+study.modality)
			.done(function(info) {
				var templatesSelector= $('select#templates');
				var label= $('div.report-center').find('label');
				templatesSelector.empty();
				templatesSelector.append('<option value="">Seleccione...</option>');
			    if(info && info.length>0){
				$.each(info, function(index, template) {
					templatesSelector.append('<option value="'+template+'">'+template+'</option>');
				});
				templatesSelector.combobox({
					select: function(ev,ui){
					if(ui.item.value&&ui.item.value!=''){
						$.getJSON("rest/reporting/templates/modalities/"+study.modality+"/"+ui.item.value)
						.done(function(data){$('textarea#studyReportBody').val(data.body)})
						.fail(function(){$alert('ERROR al obtener la Plantilla.\nPor favor, reintente la operación realizada.')});
					}}
				});
				$(label[1]).remove();
				} else{
					$(label[1]).text('No se encontraron plantillas para esta modalidad.');
				    $('select#templates').remove();
				}
			})
			.fail(function() {
				var label= $('div.report-center').find('label');
				$(label[1]).text('No se encontraron plantillas para esta modalidad.');
			    $('select#templates').remove();
			});
		},
		init_buttons: function(){
			$('div.report-right').find('input:checkbox').on('click',function(){
				$(this).prop('checked') ?
					$(this).closest('label').addClass('selected') :
					$(this).closest('label').removeClass('selected') ;
			});
			var buttons= $('div.report-right').find('button');
			$(buttons[0]).button({label:'GUARDAR',icons:{primary:'ui-icon-folder-open'},disabled:!userinfo.permissions.canSaveReports})
			.on('click',function(){
				if(!$('textarea#studyReportBody').val().trim()){
					toastr['warning']('No hay texto ingresado','NO VÁLIDO'); return
				}
				var save_report= function(){
					var dto={
						reportBody:$('textarea#studyReportBody').val(),
						finished:false,
					};
					$.ajax({type:'POST', url:'rest/studies/'+studyId+'/report/body', data:dto})
					.done(function(){
						toastr['success']('Se ha guardado el Preinforme','HECHO');
						location.href='study.html?s='+studyId;
					})
					.fail(function(){toastr['error']('Falló la generación del Preinforme','ERROR')});
				};
				$confirm('¿Confirma la grabación de los cambios introducidos?', save_report);
			});
			$(buttons[1]).button({label:'FIRMAR',icons:{primary:'ui-icon-folder-collapsed'},disabled:!userinfo.permissions.canFinishReport})
			.on('click',function(){
				if(!$('textarea#studyReportBody').val().trim()){
					toastr['warning']('No hay texto ingresado','NO VÁLIDO'); return
				}
				var finish_report= function(){
					var dto={
						reportBody:$('textarea#studyReportBody').val(),
						finished:true,
						needSecondRead:$('input#secondRead').prop('checked'),
						isTeachingFile:$('input#teachingFile').prop('checked'),
						isEmergency:$('input#clinicalEmergency').prop('checked')
					};
					$.ajax({type:'POST', url:'rest/studies/'+studyId+'/report/body', data:dto})
					.done(function(){
						toastr['success']('Se ha firmado el Informe','HECHO');
						location.href='study.html?s='+studyId;
					})
					.fail(function(){toastr['error']('Falló la generación del Informe','ERROR')});
				};
				$confirm('¿Confirma la finalización y cierre de este Informe?', finish_report);
			});
			$('div.report-right').tooltip();
			$(buttons[2]).button({
				label:'AUDIO',
				icons:{primary:'ui-icon-volume-on'},
				disabled:!userinfo.permissions.canRecordAudio
			}).on('click',function(){
				if (ddUserAgent.isUserAgentActive()) {
				
				ddUserAgent.sendUserAgentCommand('record-study-audio-report', 
					{
						'patientName': (study.pn || study.patientName || '---'),
						'studyDescription': (study.desc || study.description || '---'),
						'studyID': studyId,
						'host': location.host
					}
				);
				} else {
					$.fileDownload('rest/tools/download/audio')
					.done(function(){
						$alert(
							'Se ha descargado el grabador.\nEjecútelo y verifique que la señal ' +
							'se ponga en verde.\nLuego presione "Grabar" nuevamente.'
						);
					})
					.fail(function(){toastr['error']('Falló la descarga del Grabador','ERROR')});
					return false;
				}
			});
		},
		get: function(){
			
			/*if(!actionIsAllowed(userinfo, userinfo.institutions[0].institution.id, 'view-study-report') ||
			   !actionIsAllowed(userinfo,userinfo.institutions[0].institution.id,'edit-study-report')	){
				$('a[href="#studyMessages"]').click();
				$('a[href="#studyReport"]').closest('li').remove();
				$('div#studyReport').remove();
			}*/
			
			$.getJSON('rest/studies/'+studyId+'/report-info')
			.done(function(data){
				if(data && data!=null){
					$('textarea#studyReportBody').val(data.reportBody);
					if(data.rs==3){
						$('div.report-center').children().remove();
						var url= 'rest/reporting/reports/pdf/'+studyId;
						$('div.report-center').append(
							'<br><br>' +
							(actionIsAllowed(userinfo,userinfo.institutions[0].institution.id,'view-study-report') ?
									'<a href="'+url+'"><img src="../images/pdf.png"/></a>' :
									'<img src="../images/pdf.png"/>'
							) + '<h5>Informe firmado por usuario '+data.reportUser+'</h5>'
						).css('width','485px');
						if(data.secondReading) $('input#secondRead').click();
						if(data.teachingFile) $('input#teachingFile').click();
						if(data.emergency) $('input#clinicalEmergency').click();
						$('div.report-right').find('.ui-button').button('option','disabled',true);
						$('div.report-right').find(':not(.ui-button)').prop('disabled',true);
						$('div.report-right').find('label').css('color','#444');
					}
				}
			}).fail();
			if(study.audioReport && userinfo.permissions.canPlayAudio) 
				$('a#viewRecords').removeClass('no-records').addClass('with-records')
								  .attr('href','javascript:get_audio_reports();');
		}
	},
	Assignments: {
		get: function(){
			var buttons= $('div.study-assignments').find('button');
			
			$.getJSON('rest/study/assignments/'+studyId)
			.done(function(info){ 
				$('span#assignedTo').html('');
				if(info && info!=null){
					$('span#assignedTo').html( 
						'<span class="ui-icon black ui-icon-circle-check"></span> &nbsp;'	+
						'Este estudio se asignó a: <b>'+info.firstName+' '+info.lastName+'</b>' 
					);
					if(userinfo.permissions.canReassignStudy){
						$('label#selectUser').text('Reasignar a :');
						$(buttons[0]).button({
							label:'Reasignar y Avisar',
							icons:{primary:'ui-icon-person',secondary:'ui-icon-mail-closed'}
						}).on('click',function(){studyActions.Assignments.set(this,'study')})
					} else {
						$('label#selectUser').remove();
						$(buttons[0]).remove();
						$('select#usersList').remove();
					}
				} else {
					if(userinfo.permissions.canAssignStudy){
						$('span#assignedTo').html('<span class="ui-icon black ui-icon-circle-close"></span>&nbsp;Este estudio no está asignado')
						$('label#selectUser').text('Asignar a :');
						$(buttons[0]).button({
							label:'Asignar y Avisar',
							icons:{primary:'ui-icon-person',secondary:'ui-icon-mail-closed'}
						}).on('click',function(){studyActions.Assignments.set(this,'study')})
					} else {
						$('span#assignedTo').html('No puede Asignar usuario al estudio<br><img class="no-permission" src="../images/inhabilitado.png" />')
						$('label#selectUser').remove();
						$(buttons[0]).remove();
						$('select#usersList').remove();
					}
				}
			})
			.fail(function(){ 
				$('span#assignedTo').text('ERROR al cargar la Asignación');
				$('label#selectUser').remove();
				$('select#usersList').remove();
				$(buttons[0]).remove();
			});
			
			if(userinfo.permissions.canAssignPatient){
				$.get('rest/study/assignments/patient/'+studyId)
				.done(function(d1,d2,d3){
					if(d2=='nocontent'){
						$('span#assignedUser').html('<span class="ui-icon black ui-icon-circle-close"></span>&nbsp;Este paciente no tiene usuario');
						$(buttons[1]).button({
							label:'Asignar al Paciente',
							icons: {primary:'ui-icon-pin-s',secondary:'ui-icon-mail-closed'}
						}).on('click',function(){studyActions.Assignments.set(this,'patient')})
					} else{
						$('span#assignedUser').html('<span class="ui-icon black ui-icon-circle-check"></span>&nbsp;Este paciente ya tiene usuario');
						$('input#patientMail').closest('label').remove();
						$(buttons[1]).remove();
						$('div.study-assignments').find('h6').remove();
					}
				})
				.fail(function(){ 
					$('span#assignedUser').text('ERROR en la búsqueda de información');
					$('input#patientMail').closest('label').remove();
					$(buttons[1]).remove();
					$('div.study-assignments').find('h6').remove();
				});
			} else{
				$('span#assignedUser').text('No puede Asignar usuario al paciente');
				$('input#patientMail').closest('label').remove();
				$(buttons[1]).remove();
				$('div.study-assignments').find('h6').remove();
			}
		},
		set: function(button,option){
			if(option=='study'){
				var selectedPhysician= $("select#usersList").val();
				if(!selectedPhysician){toastr['warning']('Seleccione un médico para Asignar','NO VÁLIDO');return}
				var dto= {physicianCode:selectedPhysician, studiesIDs:[]};
				dto.studiesIDs.push(studyId);
				var label= $(button).button('option','label');
				if(label.startsWith('Asignar')){
					$.ajax({type:'POST', url:'rest/study/assignments', data:dto})
					.done(function(){ 
					toastr['success']('Estudio asignado al usuario '+selectedPhysician,'HECHO');
					location.href='study.html?s='+studyId;
					})
					.fail(function(){toastr['error']('Falló la Asignación del Estudio','ERROR')});
				} else if(label.startsWith('Reasignar')){
					$.ajax({type:'POST', url:'rest/study/assignments/reassign', data:dto})
					.done(function(){
					toastr['success']('Estudio reasignado al usuario '+selectedPhysician,'HECHO');
					location.href='study.html?s='+studyId;
					})
					.fail(function(){toastr['error']('Falló la Asignación del Estudio','ERROR')});
				} else return;
			} else if(option=='patient'){
				var mail= $('input#patientMail').val();
				if(!mail){toastr['warning']('Ingrese dirección de correo del Paciente','NO VÁLIDO');return}
				var dto= {studyId:studyId, email:mail};
				$.ajax({type:'POST', url:'rest/study/assignments/patient', data:dto})
				.done(function(){
				toastr['success']('Estudio asignado al Paciente','HECHO');
				location.href='study.html?s='+studyId;
				})
				.fail(function(){toastr['error']('Falló la Asignación del Paciente','ERROR')});
			} else return;
		},
		usersList: function(){
			$.getJSON('rest/user/medicos')
			.done(function(result) {
			    var physiciansSelect= $("select#usersList");
			    $(physiciansSelect).find('option').remove();			    		
		  		$(physiciansSelect).append('<option value="" selected="selected">Seleccione...</option>');
		  		if(result && result.length>0) {
		  		$.each(result,function(index, dr) {
			    	//if(dr.login!=login)
					    physiciansSelect.append('<option value="'+dr.login+'">'+dr.firstName+' '+dr.lastName+'</option>');
				    //
				})}
			})
			.fail(function(){
				$('span#assignedTo').text('ERROR al cargar la Asignación');
				$('label#selectUser').remove();
				$('select#usersList').remove();
				$($('div.study-assignments').find('button')[0]).remove();
			});
		}
	},
	ClinicalData: {
		get: function(){
			//if(actionIsAllowed(userinfo,userinfo.institutions[0].institution.id,'declare-clinical-data')){
			if(userinfo.permissions.canDeclareCD){
				$.getJSON('rest/study/datos-clinicos/'+studyId)
				.done(function(cld){
					var button= $('div.clinical-data').find('button');
					if(cld && cld!=null){
					$('select.cld-selector').val(cld.priority).change();
					$('textarea.cld-messages').val(cld.notes);
					$('div.clinical-data').find(':not(button)').prop('disabled',true);
					$('span.cld-info').html('Último cambio hecho por: <b>'+cld.user+'</b>');
						if(actionIsAllowed(userinfo,userinfo.institutions[0].institution.id,'modify-clinical-data')){
						$(button).button({
							label:'Modif.',
							icons:{primary: 'ui-icon-pencil'}
						}).on('click',function(){
							$('div.clinical-data').find(':not(button)').prop('disabled',false);
							$(button).button({
								label:'Guardar',
								icons:{primary: 'ui-icon-disk'}
							}).on('click',studyActions.ClinicalData.set);
						})} else $(button).remove();
					} else {
					$('span.cld-info').text('No hubo cambios aplicados en este Estudio.');
					$(button).button({
						label:'Guardar',
						icons:{primary: 'ui-icon-disk'}
					}).on('click',studyActions.ClinicalData.set)}
				})
				.fail(function(){
					$('div.clinical-data :not(h4)').remove();
					$('div.clinical-data').append('<label>ERROR en la búsqueda de Datos Clínicos</label>');
				});
				$('select.cld-selector').on('change',function(){
					var op= $(this).find(':selected');
					$(this).css('color',$(op).css('color'));
				});
			} else {
				$('div.clinical-data').find(':not(h4)').remove();
				$('div.clinical-data').append('<img class="no-permission" src="../images/inhabilitado.png" />');
				$('div.clinical-data').append('<h5>No puede declarar Datos Clínicos</h5>');
			}
		},
		set: function(){
			var dto= {cldUrgency:$('select.cld-selector').val(), cldNotes:$('textarea.cld-messages').val()};
			$.ajax({url:'rest/study/datos-clinicos/'+studyId, method:'POST', data:dto})
			.done(function(){ 
				toastr['success']('Datos Clínicos modificados','HECHO');
				location.href='study.html?s='+studyId;
			})
			.fail(function(){toastr['error']('Falló la petición de Datos Clínicos','ERROR')});
		}
	},
	Messages: {
		init_buttons: function(){
			var buttons= $('div.study-messages').find('button');
			var body= $('textarea.msg-body');
			var aux= $('input#msgMemory');
			$(buttons[0]).button({
				label:'Agregar Msj',
				icons:{primary:'ui-icon-pencil'}
			}).on('click',function(){
				var label= $(this).button('option','label');
				if($('input#isUrgency').is(':checked'))$('input#isUrgency').click();
				if(label.startsWith('Agregar')){
					$(this).button({label:'Cancelar',icons:{primary:'ui-icon-close'}});
					$(aux).val($(body).val());
					$(body).val('');
					$(body).removeAttr('readonly');
					$('div#selectUrgency').slideDown();
					$(buttons[1]).button('option','disabled',false);
				} else if(label.startsWith('Cancelar')){
					$(this).button({label:'Agregar Msj',icons:{primary:'ui-icon-pencil'}});
					$(body).val($(aux).val());
					$(aux).val('');
					$(body).attr('readonly','readonly');
					$('div#selectUrgency').slideUp();
					$(buttons[1]).button('option','disabled',true);
					$('input#urgencyFlag').val('0');
					$('input#urgencySolved').val('0');
				} else return;
			});
			$(buttons[1]).button({
				label:'Guardar Msj',
				icons:{primary:'ui-icon-disk'},
				disabled:true
			}).on('click',studyActions.Messages.set);
		},
		get: function(){
			if(userinfo.permissions.canWriteMessage){
				$.getJSON('rest/study/incidencias/'+studyId)
				.done(function(data){
					var body= $('textarea.msg-body');
					var buttons= $('div.study-messages').find('button');
					$(body).text('');
					if(data && data.length>0){
						$.each(data,function(i,inc){
						var fechaMsj= inc.fecha.substring(6,8)+'-'+inc.fecha.substring(4,6)+'-'+inc.fecha.substring(0,4);
						var horaMsj= inc.fecha.substring(9,14);
						var fechaInicio= inc.inicio==null ? "" :
							inc.inicio.substring(6,8)+"-"+inc.inicio.substring(4,6)+"-"+inc.inicio.substring(0,4)+'_'+inc.inicio.substring(9,14);
						$(body).append(
							(inc.incidencia==2? '*** El siguiente mensaje ha sido marcado como URGENCIA ***\n\n\t'
							:inc.incidencia==1? '*** El siguiente mensaje ha sido marcado como RESOLUCIÓN ***\n\n\t' :'')
							+'El día '+fechaMsj+' a las '+horaMsj+'hs. el usuario '+inc.usuario+' escribió:'+
							'\n\n\t\t'+inc.mensaje+'\n\n\n'
						);
						if(inc.incidencia==2 && inc.refInc==0){
							$('div#incidentsNotSolved').append(
								'<label><span style="font-size:1.2em;font-weight:bold;">·</span>&nbsp;' +
								'<span ref="'+fechaMsj+'_'+horaMsj+'" class="view-incident" onclick="studyActions.Messages.select_message(this);">URGENCIA</span>' +
								' del día '+inc.inicio.replace(/\//g,'-')+' por el usuario '+inc.usuario+', ' +
								'para resolver el día '+inc.resolucion.replace(/\//g,'-')+'</label>' +
								'&nbsp;&nbsp;<span id="'+inc.id+'" class="solve-incident" onclick="studyActions.Messages.solve_urgency(this);">RESOLVER</span><br><br>'
							).show()
						} else if(inc.incidencia==1){
							$('div#incidentsSolved').append(
								'<label><span style="font-size:1.2em;font-weight:bold;">·</span>&nbsp;' +
								'<span ref="'+fechaInicio+'" class="view-incident" onclick="studyActions.Messages.select_message(this);">URGENCIA</span>' +
								' resuelta por el usuario '+inc.usuario+' el día '+fechaMsj +
								', pedida para el día '+inc.resolucion.replace(/\//g,'-')+'</label>' +
								'&nbsp;&nbsp;<span ref="'+fechaMsj+'_'+horaMsj+'" class="solve-incident" onclick="studyActions.Messages.select_message(this);">VER</span><br><br>'
							).show()
						}
						})
						$(body).attr('readonly','readonly');
						$('div#selectUrgency').hide();
					} else {
						$(buttons[0]).hide();
						$(buttons[1]).button('option','disabled',false);
					}
				})
				.fail(function(){
					$('div.study-messages').find(':not(h4)').remove();
					$('div.study-messages').append('<label>ERROR al cargar los Mensajes</label>');
				});
				set_as_datepicker($('input.inc-date'));
				$('input.inc-date').prop('disabled',true);
				$('input#isUrgency').on('click',function(){
					var label= $(this).closest('label');
					$(this).prop('checked') ? 
						($(label).addClass('checked'),$('input.inc-date').prop('disabled',false),$('input#urgencyFlag').val('1')) :
						($(label).removeClass('checked'),$('input.inc-date').val('').prop('disabled',true),$('input#urgencyFlag').val('0')) ;
					$('input#urgencySolved').val('0');
				}); $('label.is-urgency').on('mousedown',function(){return false});
			} else {
				$('div.study-messages').find(':not(h4)').remove();
				$('div.study-messages').append('<img class="no-permission" src="../images/inhabilitado.png" />');
				$('div.study-messages').append('<h5>No puede ver los Mensajes</h5>');
			}
		},
		select_message: function(elem){
			var date= $(elem).attr('ref').split('_');
			var str= $("textarea#msgBody").val();
			var inicio= str.indexOf('El día '+date[0]+' a las '+date[1]+'hs.');
			var fin= inicio+32;
			var msgBody= document.getElementById('msgBody');
			
			if (msgBody.setSelectionRange) {
		        msgBody.focus();
		        msgBody.setSelectionRange(inicio,fin);
		    }
		    else if (areat.createTextRange) {
		        var range= msgBody.createTextRange();
		        range.collapse(true);
		        range.moveEnd('character',fin);
		        range.moveStart('character', inicio);
		        range.select();
		    }
			var substring= msgBody.value.substr(0,inicio); var array= substring.split('\n'); var pos= 0;
			for(var i=0; i<array.length-1; i++){ if(array[i]!='') pos+= 20; else pos+= 5;}
			msgBody.scrollTop= pos;
		},
		solve_urgency: function(elem){
			var buttons= $('div.study-messages').find('button');
			var body= $('textarea.msg-body');
			var aux= $('input#msgMemory');
			if($('input#isUrgency').is(':checked'))$('input#isUrgency').click();
			$('input#urgencyFlag').val('2');
			$('input#urgencySolved').val($(elem).attr('id'));
			$(buttons[0]).button({label:'Cancelar',icons:{primary:'ui-icon-close'}});
			if($(body).val()){
				$(aux).val($(body).val());
				$(body).val('');
			}
			$(body).removeAttr('readonly');
			$(buttons[1]).button('option','disabled',false);
			$('div#selectUrgency').slideUp();
		},
		set: function(){
			var body= $('textarea.msg-body');
			var dates= $('input.inc-date');
			if(!$(body).val()){toastr['warning']('Ingrese un Mensaje','NO VÁLIDO');return}
			var dto= {
				msgBody:$(body).val(),
				msgUrgencyType:parseInt($('input#urgencyFlag').val()),
				msgSolvedUrgency:$('input#urgencySolved').val(),
				msgDeclareDate:$(dates[0]).val(),
				msgToSolveDate:$(dates[1]).val()
			};
			$.ajax({url:'rest/study/incidencias/'+studyId, method:'POST', data:dto})
			.done(function(){
				toastr['success']('Mensaje guardado','HECHO');
				location.href='study.html?s='+studyId;
			})
			.fail(function(){toastr['error']('No se guardó el Mensaje','ERROR')});
		}
	},
	studyFiles:{
		set_files_table: function(){
			$.getJSON('rest/study/files/f/'+studyId)
		  	.done(function(result) {
		  	dtFiles.fnClearTable();
		  	if (result && result.length>0){
		  		dtFiles.fnAddData(result);
		  		$('div#filesListCant').html('Resultado : <b>'+result.length+'</b> Archivos');
		  	} else $('div#filesListCant').html('Resultado : <b>0</b> Archivos');
		  	});
			
		},
		set_om_table: function(){
			$.getJSON('rest/study/files/om/'+studyId)
		  	.done(function(result) {
		  	dtOrdMed.fnClearTable();
		  	if (result && result.length>0){
		  		dtOrdMed.fnAddData(result);
		  		$('div#medOrderListCant').html('Resultado : <b>'+result.length+'</b> Ord. Médicas');
		  	} else $('div#medOrderListCant').html('Resultado : <b>0</b> Ord. Médicas');
		  	});
			
		},
		set_mp3_table: function(){
			$.getJSON('rest/study/files/mp3/'+studyId)
		  	.done(function(result) {
		  	dtMP3.fnClearTable();
		  	if (result && result.length>0){
		  		dtMP3.fnAddData(result);
		  		$('div#mp3ListCant').html('Resultado : <b>'+result.length+'</b> Audios');
		  	} else $('div#mp3ListCant').html('Resultado : <b>0</b> Audios');
		  	});
			
		},
		set_uploader_div: function(uploader,code){
			if(actionIsAllowed(userinfo,userinfo.institutions[0].institution.id,'upload-study-file')){
				$(uploader).uploadFile({
					url:'rest/study/files/'+studyId+'/'+code,
					fileName:'study-file',
					autoSubmit:true,
					showDone:false,
					maxFileSize:52428800,
					acceptFiles:code=='f'?'*':'image/*',
				    dragDropStr:'<span><b>Arrastre Archivos aqu&iacute;</b></span>',
				    multiDragErrorStr:'No se permite arrastrar y soltar m&uacute;ltiples archivos. Realice la acci&oacute;n de a un archivo a la vez.',
				    extErrorStr:'no est&aacute; permitido este tipo de archivo. Extensiones de archivo permitidas: ',
				    sizeErrorStr:' super&oacute; el tama&ntilde;o m&aacute;ximo de archivo. Tama&ntilde;o m&aacute;ximo permitido: ',
				    uploadErrorStr:'No permitido',
				    showStatusAfterSuccess: false,
				    showStatusAfterError: false,
					afterUploadAll:function(){ toastr['success']('Archivos subidos','HECHO');location.href='study.html?s='+studyId },
					onError: function(files,status,errMsg){ 
						if(errMsg == 'No Aceptable') toastr['warning']('Formato de Archivo incorrecto','NO VÁLIDO')
						else toastr['error']('Falló la subida de Archivos','ERROR') 
					}
				});
			} else $(uploader).remove();
		},
		qr_button: function(){
			var button= $('div.files-list').find('button');
			if(userinfo.permissions.canViewQR){
				$(button).button({
					label:'Código QR'
				}).on('click',function(){
					$('div.files-list').append('<div id="QRcode" style="width:250px; height:250px; margin-top:15px;"></div>');
					var node= document.getElementById('QRcode');
					while(node.firstChild) node.removeChild(node.firstChild);
					var qrcode= new QRCode(document.getElementById('QRcode'),{width:300,height:300});
					var studyText= '';
					$.getJSON('rest/studies/'+studyId)
					.done(function(study){
						if(study && study!=null){
							studyText+= study.id + '|';
							studyText+= "{ 'id': " + study.pid + "," ;
							studyText+= " 'p': '" + study.pn + "'," ;
							studyText+= " 'e': '" + study.desc + "'," ;
							studyText+= " 'f': '" + study.qrdate.replace(' ','T') + "' }" ;
							qrcode.makeCode(studyText);
						}
					})
					.fail(function(){toastr['error']('Falló la creación del QR','ERROR');$("div#QRcode").dialog('close')});
					$("div#QRcode").dialog({
		       			height:380,
		       			width:330,
		       			closeOnEscape:true,
		       			closeText:'Cerrar',
		       			modal:true,
		       			title:'Código QR',
		       			hide:{effect:'fade',duration:400},
		       			clickOutside:false,
		       			open:function(ev,ui){$('#QRcode.ui-widget-content').css('background', 'white')},
		       			close:function(ev,ui){$(this).remove()}
		       		});
				});
			} else {
				$(button).remove();
				$('div.files-list').find('h5').remove();
			}
			
		},
		delete_file: function(filename, url){
			$.ajax({url:url, type:'DELETE'})
			.done(function(){ toastr['success']('Archivo eliminado','HECHO');location.href='study.html?s='+studyId })
			.fail(function(){ toastr['error']('Falló la eliminación del Archivo','ERROR')});
		}
	},
	studyLog: {
		init_buttons: function(){
			$('div#refreshLog').find('button').button({
				label:'Recargar',
				icons:{primary:'ui-icon-refresh'}
			}).on('click',studyActions.studyLog.charge);
		},
		charge: function(){
			$.getJSON('rest/studies/'+studyId+'/study-log')
			.done(function(data){
				dtLog.fnClearTable();
			  	if(data && data.length>0){
			  		dtLog.fnAddData(data);
			  		$('div#loggerListCant').html('Resultado : <b>'+data.length+'</b> datos');
			  	} else $('div#loggerListCant').html('Resultado : <b>0</b> datos');
			})
			.fail(function(){ 
				$('#tab_study-log').children().remove();
			  	$('#tab_study-log').append('ERROR al cargar el Logger'); 
			});
		}
	}
};

function agent_interval(){
	if(ddUserAgent.isUserAgentActive()){
		$('img.report-rec-status').attr('src','../images/punto_verde.png');
		afterConex= true;
		
	} else {
		$('img.report-rec-status').attr('src','../images/punto_rojo.png');
		if(afterConex){
			location.href='study.html?s='+studyId;
			afterConex= false;
		}
	}
}
function get_audio_reports(){
	openDialog('report-audios.html?s='+studyId, 'Audios de Informe', 400, 380);
	return false;
}


$(document).ready(function() {
	studyId= $.url().param('s');
	if(!studyId||studyId==null) window.close();

	$.ajax({type:'GET',dataType:'json',url:'rest/user/info',async:false}).done(function(user){userinfo= user;$('#userInfo').text(user.login+' ['+user.lastName+', '+user.firstName+']');update_configuration_tip(user.prop)}).fail(function(){window.location.href='../authentication-servlet/logout'})
	$.getJSON('rest/config/all').done(function(result){ddConfig=result}).fail(function(){window.location.href='../authentication-servlet/logout'});
	$('#tabs').tabs();
	$('span#ddnetVersion').text('Versión 2016.09.07');
	get_header_info();
	studyActions.studyVisualization.preset_menu();
	studyActions.studyReport.get();
	studyActions.studyReport.charge_templates();
	studyActions.studyReport.init_buttons();
	studyActions.Assignments.get();
	studyActions.Assignments.usersList();
	studyActions.ClinicalData.get();
	studyActions.Messages.init_buttons();
	studyActions.Messages.get();
	
	if(userinfo.permissions.canManageFile){
		dtFiles= jqDataTable.filesList(studyId);
		studyActions.studyFiles.set_files_table();
		studyActions.studyFiles.set_uploader_div($('div#fileUploader'),'f');
	
	} else {
		$('div.files-list').find(':not(h4)').remove();
		$('div.files-list').append('<img class="no-permission" src="../images/inhabilitado.png" />')
						   .append('<h5>No puede ver los Archivos</h5>');
	}
	if(userinfo.permissions.canManageOM){
		dtOrdMed= jqDataTable.medOrderList(studyId);
		studyActions.studyFiles.set_om_table();
		studyActions.studyFiles.set_uploader_div($('div#ordMedUploader'),'om');
	
	} else {
		$('div.med-order-list').find(':not(h4)').remove();
		$('div.med-order-list').append('<img class="no-permission" src="../images/inhabilitado.png" />')
							   .append('<h5>No puede ver las Ord.Médicas</h5>');
	}
	if(userinfo.permissions.canManageAudio){
		dtMP3= jqDataTable.mp3List(studyId);
		studyActions.studyFiles.set_mp3_table();
		studyActions.studyFiles.set_uploader_div($('div#mp3Uploader'),'mp3');
	
	} else {
		$('div.mp3-list').find(':not(h4)').remove();
		$('div.mp3-list').append('<img class="no-permission" src="../images/inhabilitado.png" />')
						 .append('<h5>No puede ver los MP3</h5>');
	}
	
	studyActions.studyFiles.qr_button();
	dtLog= jqDataTable.loggerList();
	studyActions.studyLog.init_buttons();
	studyActions.studyLog.charge();
	$('a#configurationLink').tooltip();
	ddUserAgent.initialize();
	setInterval(agent_interval,1000);
	ajustes.studyWindow();
	
	if(!userinfo.permissions.canDoLog){
		$('a[href="#studyLog"]').closest('li').remove();
		$('div#studyLog').remove();
	}
	if(!userinfo.permissions.canDoFiles){
		$('a[href="#studyFiles"]').closest('li').remove();
		$('div#studyFiles').remove();
	}
	if(!userinfo.permissions.canDoMessages){
		$('a[href="#studyMessages"]').closest('li').remove();
		$('div#studyMessages').remove();
	}
	if(!userinfo.permissions.canDoReports){
		$('a[href="#studyMessages"]').click();
		$('a[href="#studyReport"]').closest('li').remove();
		$('div#studyReport').remove();
	}
});