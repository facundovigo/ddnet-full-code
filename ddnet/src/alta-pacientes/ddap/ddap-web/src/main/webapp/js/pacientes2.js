
	$(document).ready(function(){
		
		init_jqbuttons();
		posic_elems();
		input_events();

		
	});
	
	


	window.addEventListener('resize', function(){
		
		posic_elems();
		$('table.title').css('margin-left', $('table.body').css('margin-left'));
		if(navigator.userAgent.indexOf("Firefox") != -1 ){
			var body = document.getElementById('pat-tb-body');
			var pos1 = body.getBoundingClientRect();
			var title = document.getElementById('pat-tb-title');
			var pos2 = title.getBoundingClientRect();
			 
			$('table.title').css('margin-left', (pos1.left-pos2.left)+'px');
			
		}
			
	});
	
	
	
	
	
	
	
	
	
	function init_jqbuttons(){
		
		$('#btn_search').button({
			label: 'Buscar',
			icons: {'primary':'ui-icon-search'}
		
		}).on('click', search_asked_patient );
		
		$('#btn_new-pat').button({
			label: 'Nuevo',
			icons: {'primary':'ui-icon-circle-plus',
					'secondary':'ui-icon-contact'}
		
		}).on('click', add_new_patient );
		
		$('#btn_retry').button({
			label: 'Reintentar',
			icons: {'primary':'ui-icon-refresh'}
		
		}).on('click', retry_the_request );
		
		$('#btn_new-practice').button({
			label: 'Alta de Práctica',
			icons: {'primary' : 'ui-icon-circle-plus',
					'secondary' : 'ui-icon-note'}
		
		}).on('click', generate_new_practice );
		
		$('#btn_modify').button({
			label: 'Modificar',
			icons: {'primary' : 'ui-icon-pencil',
					'secondary' : 'ui-icon-person'}
		
		}).on('click', modify_the_patient );
		
		$('#btn_delete').button({
			label: 'Eliminar',
			icons: {'primary' : 'ui-icon-trash',
					'secondary' : 'ui-icon-person'}
		
		}).on('click', delete_patient );
	}
	
	
	
	
/**
 * 	Eventos para los diferentes objetos
 * 	(click, tecla, etc.)
 */	
	function input_events(){
		
		$('#txt_search-pat').on('keypress', function(){
			
			var tecla = event.which || event.keyCode;
			if(tecla == 13) search_asked_patient();
			else return;
		});
		
	}
	
	
	
	
	
	
	function search_asked_patient(){
		
		if($('#txt_search-pat').val() == ''){
				$alert('Ingrese un dato para buscar');
				return;
		}
		
		$('td.data').text('');
		
		$.getJSON('rest/patient/search/'+$('#txt_search-pat').val())
		.done(function(data){
			
			if(data && data.length == 1){
				$('label.search-pat').html('Paciente: <input type="text" id="txt_search-pat" placeholder="Nombre ó ID">');
				input_events();
				$('table').css('background','white').css('color','black');
				$('tr.even').css('background','#fefefe');
				$('tr.odd').css('background','#ededed');
				$('#btn_search').css('display','inline');
				$('#btn_retry').css('display', 'none');
			
				$.each(data, function(i, pat){
					$('#txt_search-pat').val(pat.docNumber);
					
					$('td[name="documento"]').text(pat.docType+' '+pat.docNumber);
					$('td[name="apellido"]').text(pat.lastName);
					$('td[name="nombre"]').text(pat.firstName);
					$('td[name="apellidoCas"]').text(pat.lastNameLaw == null ? '---' : pat.lastNameLaw);
					$('td[name="sexo"]').text(pat.patSex == null ? '---' : pat.patSex );
					$('td[name="fechaNac"]').text(pat.birthDate == null ? '---' : pat.birthDate);
					$('td[name="tel"]').text(pat.phoneNumber == null ? '---' : pat.phoneNumber);
					$('td[name="cel"]').text(pat.cellPhoneNumber == null ? '---' : pat.cellPhoneNumber);
					$('td[name="correo"]').text(pat.mailAddress == null ? '---' : pat.mailAddress);
					$('td[name="ocup"]').text(pat.occupation == null ? '---' : pat.occupation);
					$('td[name="direcc"]').text(pat.address == null ? '---' : pat.address);
					$('td[name="loc"]').text(pat.location == null ? '---' : pat.location);
					$('td[name="cp"]').text(pat.zipCode == null ? '---' : pat.zipCode);
					$('td[name="prov"]').text(pat.province == null ? '---' : pat.province);
					$('td[name="nac"]').text(pat.nationality == null ? '---' : pat.nationality);
					$('td[name="obraSocial"]').text(pat.medInsurance == null ? '---' : pat.medInsurance);
					$('td[name="osPlan"]').text(pat.plan == null ? '---' : pat.plan);
					$('td[name="osNumber"]').text(pat.numPlan == null ? '---' : pat.numPlan);
					$('td[name="observ"]').text(pat.observation == null ? '---' : pat.observation);
				});
				
				$('div.buttons-top').css('display', 'inline');
				$('div.buttons-bottom').css('display', 'inline');
			
			} else if(data && data.length > 1){
				var value = $('#txt_search-pat').val();
				
				$('label.search-pat').html('Paciente: <input type="text" id="txt_search-pat" placeholder="Nombre ó ID">');
				input_events();
				$('table').css('background','white').css('color','black');
				$('tr.even').css('background','#fefefe');
				$('tr.odd').css('background','#ededed');
				$('#btn_search').css('display','inline');
				$('#btn_retry').css('display', 'none');
				$('div.buttons-top').css('display', 'none');
				$('div.buttons-bottom').css('display', 'none');
				
				openDialog('pat-search.html?val='+value, 'BÚSQUEDA POR NOMBRE DE PACIENTE', 650, 800);
				
			} else{
				$('label.search-pat').text('No se ha encontrado el paciente');
				$('table').css('background','#aaa').css('color','#666');
				$('#btn_search').css('display','none');
				$('#btn_retry').css('display','inline');
				$('div.buttons-top').css('display', 'none');
				$('div.buttons-bottom').css('display', 'none');
			}
				
		}).fail( function(){ $alert('Ocurrió un error en la consulta del paciente.\nIntente nuevamente.'); 
							 $('#txt_search-pat').val(''); });
	}
	
	
	
	
	function get_current_patient(){
		
		$.getJSON('rest/patient/search/'+$('#txt_search-pat').val())
		.done(function(data){
			if(data && data != null){
				$.each(data, function(i, pat){
					$('#cmbDocType').val(pat.docType);
					$('#txtDocNumber').val(pat.docNumber);
					$('#txtLastName').val(pat.lastName);
					$('#txtFirstName').val(pat.firstName);
					$('#txtLastNameLaw').val(pat.lastNameLaw);
					$('input[value="'+pat.patSex+'"]').prop('checked', true);
					$('#txtBirthDate').val(pat.birthDate);
					$('#txtPhoneNumber').val(pat.phoneNumber);
					$('#txtCellPhoneNumber').val(pat.cellPhoneNumber);
					$('#txtMailAddress').val(pat.mailAddress);
					$('#txtNationality').val(pat.nationality);
					$('#txtAddress').val(pat.address);
					$('#txtLocation').val(pat.location);
					$('#txtZipCode').val(pat.zipCode);
					$('#sel_province').val(pat.province);
					$('#txtOccupation').val(pat.occupation);
					$('#txtMedInsurance').val(pat.medInsurance);
					$('#txtPlan').val(pat.plan);
					$('#txtNumPlan').val(pat.numPlan);
					$('#txtObservation').text(pat.observation != null ? pat.observation : '');
				});
			}
			
		}).fail(function(){
			$alert('Ocurrió un error al cargar el paciente.\nIntente nuevamente.');
			cancel_the_operation();
		});
	}
	
	
	
	
	
	
	
	
	
	
	function retry_the_request(){
		
		$('label.search-pat').html('Paciente: <input type="text" id="txt_search-pat" placeholder="Nombre ó ID">');
		input_events();
		$('table').css('background','white').css('color','black');
		$('tr.even').css('background','#fefefe');
		$('tr.odd').css('background','#ededed');
		$('#btn_search').css('display','inline');
		$('#btn_retry').css('display', 'none');
	}
	
	
	
	
	
	
	
	
	function add_new_patient(){
		
		$('div.the-right').children().remove();
		$('#is-new-flag').val('1');
		$('div.the-right').load('add-patient.html');
	
		$('label.search-pat').html('Paciente: <input type="text" id="txt_search-pat" placeholder="Nombre ó ID">');
		input_events();
		$('table').css('background','white').css('color','black');
		$('tr.even').css('background','#fefefe');
		$('tr.odd').css('background','#ededed');
		$('td.data').text('');
		$('div.buttons-top').css('display', 'none');
		$('div.buttons-bottom').css('display', 'none');
		$('#btn_search').css('display','inline');
		$('#btn_retry').css('display', 'none');
	}
	
	
	
	function create_the_patient(){
		
		if($('#txtLastName').val() == ''){
			$alert('Ingrese el APELLIDO del paciente');
			return;
		}
		if($('#txtFirstName').val() == ''){
			$alert('Ingrese el NOMBRE del paciente');
			return;
		}
		if($('#cmbDocType').val() == ''){
			$alert('Ingrese el TIPO DE DOCUMENTO del paciente');
			return;
		}
		if($('#txtDocNumber').val() == ''){
			$alert('Ingrese el NÚMERO DE DOCUMENTO del paciente');
			return;
		}
		
		$.post('rest/patient/add/true', $('#frm_new-patient').serialize() )
		.done(function(){
			$alert('Se ha dado de alta el paciente');
			$('#txt_search-pat').val($('#txtDocNumber').val());
			$('#btn_search').click();
			cancel_the_operation();
			
		}).fail(function(obj,err,status){
			if(status=='No Aceptable') $alert('Este número de documento ya está registrado.');
			else $alert('ERROR. Falló el alta del nuevo Paciente. Intente nuevamente.');
		});
	}
	
	
	
	function modify_the_patient(){
		
		$('div.the-right').children().remove();
		$('#is-new-flag').val('0');
		$('div.the-right').load('add-patient.html');
		$('#txt_search-pat').attr('readonly', 'readonly');
		
	}
	
	
	function edit_the_patient(){
		
		if($('#txtLastName').val() == ''){
			$alert('Ingrese el APELLIDO del paciente');
			return;
		}
		if($('#txtFirstName').val() == ''){
			$alert('Ingrese el NOMBRE del paciente');
			return;
		}
		if($('#cmbDocType').val() == ''){
			$alert('Ingrese el TIPO DE DOCUMENTO del paciente');
			return;
		}
		if($('#txtDocNumber').val() == ''){
			$alert('Ingrese el NÚMERO DE DOCUMENTO del paciente');
			return;
		}
		
		$.post('rest/patient/add/false', $('#frm_new-patient').serialize() )
		.done(function(){
			$alert('Se ha modificado el paciente');
			$('#txt_search-pat').val($('#txtDocNumber').val());
			$('#btn_search').click();
			cancel_the_operation();
			
		}).fail(function(){$alert('Ocurrió un error al querer editar el paciente');});
	}
	
	
	
	
	
	
	
	function clear_patient_form(){
		
		if($('#is-new-flag').val() == '1') $('#frm_new-patient input').val('');
		else $('#frm_new-patient input:not(#txtDocNumber)').val('');
			
		$('#frm_new-patient textarea').val('');
		$('#frm_new-patient select').val('');
	}
	
	
	function clear_practice_form(){
		
		$('form#new-practice input').val('');
		$('form#new-practice textarea').val('');
		$('form#new-practice select').val('').change();
		uploader.cancelAll();
	}
	
	
	
	function cancel_the_operation(){
		
		$('div.the-right').children().remove();
		$('#is-new-flag').val('0');
		$('div.the-right').append('Seleccione una tarea a realizar.');
		$('#txt_search-pat').removeAttr('readonly');
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	function posic_elems(){
		
		var leftSide = $('div.the-left');
		var rightSide = $('div.the-right');
		
		leftSide.css('width', '49%');
		rightSide.css('margin-left', '49.5%');
		
		if(leftSide.width() < 500) {
			leftSide.css('width', '500px');
			rightSide.css('margin-left', '510px');
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
/**
 * 	Opción dar de Alta una Práctica
 */	
	function generate_new_practice(){
		
		$('div.the-right').children().remove();
		$('#is-new-flag').val('0');
		$('div.the-right').load('new-practice.html');
	
	}
	
	
	
	
/**
 * 	Alta de Práctica
 * 	Envío a la Worklist
 */	
	function create_the_practice(){
		
		var send_to_worklist = null;
		var value = $('select#practices').val();
		
		if(!value || value == ''){ 
			$alert('Seleccione una Práctica');
			return;
		}
		
		send_to_worklist = function(){
		
			var form = $('form#new-practice').serialize();
			form += '&patID=' + $('#txt_search-pat').val();
			form += '&firstName=' + $('td[name="nombre"]').text();
			form += '&lastName=' + $('td[name="apellido"]').text();
			form += '&patSex=' + $('td[name="sexo"]').text();
			form += '&birthDate=' + $('td[name="fechaNac"]').text();
			
			var url = 'rest/worklist/new?' + form;
			
			$.fileDownload(url)
			 .done(function () {
				 uploader.startUpload();
				 $alert('Se ha dado de alta la Práctica.\nVerifique si se generó el comprobante.');
				 cancel_the_operation();
				 $('#txt_search-pat').val('');
				 $('td.data').text('');
				 
			 }).fail(function () {
				$alert(
					'No se dió de alta la Práctica.\n'	+
					'Vuelva a intentarlo o comuníquese con el Administrador.\n\n'	+
					'Ocurrió un error al momento de enviar los datos a la WorkList.'
				);
			 });
			
			return false;
		};
		
		$confirm('confirma?', send_to_worklist);
	}
	
	
	
	
/**
 * 	Eliminar el paciente encontrado
 */	
	function delete_patient(){
		
		var patID = $('#txt_search-pat').val();
		
		if( patID == null || patID == '') return;
		
		var do_the_delete = function(){
			
			$.ajax({
				method: 'POST',
				url: 'rest/patient/delete/' + patID,
				beforeSend: window.parent.$wait
				
			}).done(function(){
				window.parent.$stop_wait();
				$alert('El Paciente se ha eliminado correctamente');
				cancel_the_operation();
				$('#txt_search-pat').val('');
				$('td.data').text('');
				$('div.buttons-top').css('display', 'none');
				$('div.buttons-bottom').css('display', 'none');
			
			}).fail(function(obj,err,status){ 
				window.parent.$stop_wait();
				$alert('ERROR. No se eliminó el Paciente. Intente nuevamente.');
			});
		};
		
		$confirm('Una vez eliminado el Paciente NO se puede recuperar.'+
				 '\nTambién se eliminarán las Prácticas que se haya realizado.', do_the_delete);
	}
	
	
	
	
	
	
/**
 * 	Retorna el nombre completo según la modalidad
 */
	function modality_translator( mod ){
		
		if(!mod || mod == null) return;
		
		switch( mod ){
			case 'BI': return 'BI - Imagen Biomagnética'; break;
			case 'CR': return 'CR - Radiografía Computarizada'; break;
			case 'CT': return 'CT - Tomografía Computarizada'; break;
			case 'DX': return 'DX - Radiografía Digital'; break;
			case 'DXA': return 'DXA - Densitometría Ósea'; break;
			case 'ECG': return 'ECG - Electrocardiograma'; break;
			case 'ES': return 'ES - Endoscopía'; break;
			case 'IO': return 'IO - Radiografía Intraoral'; break;
			case 'MG': return 'MG - Mamografía'; break;
			case 'MR': return 'MR - Resonancia Magnética'; break;
			case 'NM': return 'NM - Medicina Nuclear'; break;
			case 'OT': return 'OT - Otra'; break;
			case 'PX': return 'PX - Radigrafía Panorámica'; break;
			case 'RF': return 'RF - Radio Fluoroscopia'; break;
			case 'RT': return 'RT - Radioterapia'; break;
			case 'US': return 'US - Ultrasonido'; break;
			case 'XA': return 'XA - Hemodinamia'; break;
		}
	}
	
	
	