	
	var dialog = null,
		iframe = null,
		patientData = null;

	$(document).ready(function(){
		
		dialog = window.parent.$("#dynamicModalDialog");
		iframe = window.parent.$("#dynamicModalDialogIframe");
		patientData = dialog.data('patientData');
		
		$('#btn_confirm-practice').button({
			label: 'Confirmar',
			icons: {'primary': 'ui-icon-circle-check'}
		
		}).on('click', create_the_practice );
		
		$('#btn_clear-form').button({
			label: 'Limpiar',
			icons: {'primary': 'ui-icon-refresh'}
		
		}).on('click', clear_practice_form );
		
		$('#btn_cancel-op').button({
			label: 'Cancelar',
			icons: {'primary': 'ui-icon-circle-close'}
		
		}).on('click',cancel_new_practice );
		
		$('div#addMed button').first().button({
			icons:{primary:'ui-icon-circle-check'}
		}).on('click',function(){
			if(!$('input#refPhysician').val()){window.parent.$alert('Ingrese un nombre, apellido, alias, etc.');return}
			var name= $('input#refPhysician').val(), 
			licType= $('input[name="med-license-type"]:checked').val(),
			licNumber= $('input#licNumber').val();
			var dto={name:name, licType:licType, licNumber:licNumber};
			$.ajax({url:'rest/medicos/new', method:'POST', data:dto})
			.done(function(){
				get_medicos();
				$('input#refPhysician').val('');
				$('input#licNumber').val('');
				//$('input[name="ref-phys-lic-number"]').val('');
				$('div#addMed').hide();
				$('div#medChooser').show();
				var combobox= $('div#medChooser').find('.custom-combobox');
				var input= $(combobox).find('input.custom-combobox-input');
				$(input).val('');
				$('span#matrNumber').text('');
				//$('select[name="ref-physician"]').val(name);
			})
			.fail(function(){window.parent.$alert('ERROR. No se dió de alta el Médico')});
		}).tooltip();
		$('div#addMed button').last().button({
			icons:{primary:'ui-icon-circle-close'}
		}).on('click',function(){
			$('div#addMed').hide();
			$('div#medChooser').show();
			var combobox= $('div#medChooser').find('.custom-combobox');
			var input= $(combobox).find('input.custom-combobox-input');
			$(input).val('');
			$('span#matrNumber').text('');
		}).tooltip();
		
		$('span#patient-name').html('<b>'+patientData.firstName+' '+patientData.lastName+'</b>');
		$('span#patient-sex').html('<b>'+patientData.patSex+'</b>');
		$('span#patient-id').html('<b>'+patientData.docType+' '+patientData.docNumber+'</b>');
		$('span#patient-bdate').html('<b>'+patientData.birthDate+'</b>');
		
		$.getJSON('rest/practica/mods')
		.done(function(data){
			var select= $('select#modalities');
			select.find('option').remove().end();
			select.append('<option value="" selected="selected">Seleccione...</option>');
			
			if(data && data != null){
				$.each(data, function(i,mod){
					select.append('<option value="'+mod+'" >'+modality_translator(mod)+'</option>');
				});
			}
			var modalitySelected= function(){
				if(!$(this).val()){
					$('div.by-modality').html(
						'<label>Seleccione Modalidad para cargar las Prácticas correspondientes</label> <br><br>'+
						'<label>Seleccione Modalidad para cargar los Equipos correspondientes</label>'
					);
				}else{
					$('div.by-modality').html(
						'<label id="pr">Tipo de Práctica :</label> <br>' +
						'<select id="practices" name="practice" style="width:95%;font-weight:bolder;"></select> <br><br>' +
						'<label id="eq">Equipo donde se realizará la práctica :</label> &nbsp;&nbsp;' +
						'<select id="equipments" name="equipment"></select>'
					);
					cargarEquipos($(this).val());
					cargarPracticas($(this).val());
				}
			};
			
			$(select).selectmenu().on('selectmenuchange',modalitySelected);
			
		}).fail(function(){	$('select#modalities').append('<option>Ocurrió un error al momento de cargar las Modalidades</option>');});
		
		get_medicos();
		
		$('input[title="Solamente Números"]').on('keypress', function(){
			var tecla = event.which || event.keyCode;
			if(tecla < 48 || tecla > 57) return false;
			else return;
		}).tooltip();
		
		$('span#emergency-value-from-practice').hide();
		$('input#isEmergency').on('click',function(){
			$('span#emergency-value-from-practice').toggle();
			$('span#value-from-practice').toggle();
		});
	});
	
	
	/**
	 * 	Cerrar ventana Nueva Práctica
	 */	
		function cancel_new_practice() {
			iframe.attr('src','about:blank');
			dialog.dialog("close");
		}
		
	/**
	 * 	Borrar datos ingresados en formulario de Prácticas
	 */	
		function clear_practice_form() {
			
			$('form#new-practice input').val('');
			$('form#new-practice textarea').val('');
			$('form#new-practice select').val('').change();
			$('select#modalities').val('').selectmenu('refresh').trigger('selectmenuchange');
			uploader.cancelAll();
			$('span#value-from-practice').text('');
		}
		
		
	/**
	 * 	Alta de Práctica
	 * 	Envío a la Worklist
	 */	
	function create_the_practice() {
		if(!$('input#AccessionNumber').val()){alert('Ingrese el Accession Number');return;}
		if(!$('select#practices').val()){alert('Seleccione una Práctica');return;}

		var send_to_worklist= function(){
			var form= $('form#new-practice').serialize();
			form+= '&patID='+ patientData.docNumber;
			form+= '&firstName='+ patientData.firstName;
			form+= '&lastName='+ patientData.lastName;
			form+= '&patSex='+ patientData.patSex;
			form+= '&birthDate='+ patientData.birthDate;
			
			var remuneracion= $('span#value-from-practice').is(':visible') ?
				$('span#value-from-practice').text().replace('$ ','') :
				$('span#emergency-value-from-practice').text().replace('$ ','');
			
			form+= '&value='+ remuneracion;
			var url= 'rest/worklist/new?'+form;

			window.parent.$.fileDownload(url)
			.done(function(){
				 uploader.startUpload();
				 window.parent.$alert('Se ha dado de alta la Práctica.\nVerifique si se generó el comprobante.');
				 cancel_new_practice();
			})
			.fail(function(obj,url){
				if(obj.toString().toLowerCase().indexOf('no aceptable')!=-1) window.parent.$alert('Ya existe una Práctica con este Accession Number.\nPor favor, ingrese uno distinto');
				else {
				window.parent.$alert(
					'No se dió de alta la Práctica.\n'	+
					'Vuelva a intentarlo o comuníquese con el Administrador.\n\n'	+
					'Ocurrió un error al momento de enviar los datos a la WorkList.'
				);
				cancel_new_practice();
				}
			});
			return false;

		}; window.parent.$confirm('¿Confirma la Práctica para el Paciente '+patientData.docNumber+'?',send_to_worklist);
	}


	function cargarEquipos(modality){
		
		$.getJSON('rest/equipo/'+modality)
		.done(function(data){
			
			var sel = $('select#equipments');
			sel.find('option').remove().end();			    		
			sel.append('<option value="" selected="selected" >Seleccione...</option>');
			
			if(data){
				$.each(data, function(i,eq){
					sel.append('<option value="'+eq.model+'">'+eq.model+'</option>');
				});
			}

			sel.css('min-width','200px');
			sel.selectmenu();
			
		}).fail(function(){	$('label#eq').text('Ocurrió un error al momento de cargar los Equipos');
							$('select#equipments').hide();});
	}

	function cargarPracticas(modality){
		
		$.getJSON('rest/practica/modality/'+modality)
		.done(function(data){
			var sel= $('select#practices');
			sel.find('option').remove().end();		    		
			sel.append('<option value="" selected="selected" >Seleccione...</option>');
			var name= '';
			if(data){
				$.each(data, function(i,p){
					name = p.abrev != null ? p.abrev : p.interno != null ? p.interno : p.nombre;
					sel.append('<option value="'+ p.id + '_' + p.nombre +'" remuneracion="'+p.valor+'" emergencia="'+p.emergencyValue+'">'+p.prestacion+' - '+name+'</option>');
				});
			}
			sel.combobox({
				select: function(ev,ui){
					if(ui.item.value&&ui.item.value!=''){
						var op= $(ui.item);
						var value= $(op).attr('remuneracion');
						var emergencyValue= $(op).attr('emergencia');
						$('span#value-from-practice').text('$ '+value);
						$('span#emergency-value-from-practice').text('$ '+emergencyValue);
					} else {
						$('span#value-from-practice').text('');
						$('span#emergency-value-from-practice').text('');
					}
				}
			});
		}).fail(function(){	$('label#pr').text('Ocurrió un error al momento de cargar las Prácticas');
							$('select#practices').hide();});
	}
	
	
	function get_medicos(){
		$.getJSON('rest/medicos')
		.done(function(medicos){
			var select= $('select#medicos');
			$(select).find('option').remove();
			$(select).append('<option value="">Seleccione...</option>');
			if(medicos && medicos.length>0){
			$.each(medicos,function(i,med){
				$(select).append('<option value="'+med.name+'" matr="'+med.licenseType+' '+med.licenseNumber+'">'+med.name+'</option>');
			})}
			$(select).append('<option value="new" class="add-new-med"> + + Agregar Nuevo + + </option>');
			$(select).combobox({
				select: function(ev,ui){
					if(ui.item.value&&ui.item.value!=''&&ui.item.value!='new'){
						var op= $(ui.item);
						var matr= $(op).attr('matr');
						$('span#matrNumber').text(matr);
					} else if(ui.item.value=='new'){
						$('div#medChooser').hide();
						$('div#addMed').show();
					}
				}
			});
		})
		.fail(function(){$('select#medicos').append('<option value=""></option><option value="new" class="add-new-med">Agregar Nuevo</option>')});
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
	