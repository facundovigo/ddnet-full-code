
/**
 * 	Variables Globales
 */
	var dt = null,
		theTableParent = null,
		theTable = null,
		theActionsParent = null,
		theActions = null,
		theHideElement = null,
		patientInfo = null;


	$(document).ready(function(){
		init_jqbuttons();
		init_jqselectmenu();
		init_inputs();
		init_dt_table();
		ancho_centrado_elementos();
	});
	
	window.addEventListener( 'resize', ancho_centrado_elementos );
	
	
	
	
	

	
	
	
	
	

	
/**
 * 	Eliminar Paciente indicado
 */	
	function delete_patient(patID){
		
		if( patID == null || patID == '') return;
		
		var do_the_delete = function(){
			
			$.ajax({
				method: 'POST',
				url: 'rest/patient/delete/' + patID,
				beforeSend: function(){window.parent.$('div#ddap-loading-message').show()}
				
			}).done(function(){
				$alert('El Paciente se ha eliminado correctamente');
				do_the_search();
			
			}).fail(function(obj,err,status){$alert('ERROR. No se eliminó el Paciente. Intente nuevamente.');
			}).always(function(){window.parent.$('div#ddap-loading-message').hide()});
		};
		
		$confirm('Una vez eliminado el Paciente NO se puede recuperar.'+
				 '\nTambién se eliminarán las Prácticas que se haya realizado.', do_the_delete);
	}
	
	
	
	
/**
 * 	Realizar la búsqueda de Pacientes
 */	
	function do_the_search(){
		
		draw_table(dt);
		cancel_the_action();
	}
	
	
	
/**
 * 	Limpiar formulario de Paciente
 */	
	function clear_patient_form(){
		
		if($('#is-new-flag').val() == '1') $('#frm_new-patient input').val('');
		else $('#frm_new-patient input:not(#txtDocNumber)').val('');
			
		$('#frm_new-patient textarea').val('');
		$('#frm_new-patient select').val('');
	}
	
	

/**
 * 	Editar el Paciente seleccionado
 */
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
		
		$.post('rest/patient/add/false', $('#frm_new-patient').serialize())
		.done(function(){
			$alert('Se ha modificado el paciente');
			do_the_search();
			
		}).fail(function(){$alert('Ocurrió un error al querer editar el Paciente');});
	}
	
	

/**
 * 	Formulario para modificar un Paciente
 */	
	function modify_the_patient( patientData ) {
		var oTT = TableTools.fnGetInstance('patient-table');
		theHideElement = $('div#body-to-hide');
		patientInfo = patientData;
		
		$('input#is-new-flag').val('0');
		
		if(theHideElement.is(':visible')) {
			oTT.fnSelectNone();
			theHideElement.slideUp();
			theActionsParent.show();
			theActions.load('add-patient.html');
		
		} else return;
		
	}
	

/**
 * 	Obtener la data del Paciente a modificar
 */	
	function get_current_patient(){
		$('#cmbDocType').val(patientInfo.docType);
		$('#txtDocNumber').val(patientInfo.docNumber);
		$('#txtLastName').val(patientInfo.lastName);
		$('#txtFirstName').val(patientInfo.firstName);
		$('input[value="'+patientInfo.patSex+'"]').prop('checked', true);
		$('#txtBirthDate').val(patientInfo.birthDate);
		$('#txtPhoneNumber').val(patientInfo.phoneNumber);
		$('#txtCellPhoneNumber').val(patientInfo.cellPhoneNumber);
		$('#txtMailAddress').val(patientInfo.mailAddress);
		$('#txtNationality').val(patientInfo.nationality);
		$('#txtAddress').val(patientInfo.address);
		$('#txtLocation').val(patientInfo.location);
		$('#txtZipCode').val(patientInfo.zipCode);
		$('#sel_province').val(patientInfo.province).change();
		$('#txtOccupation').val(patientInfo.occupation);
		$('#txtMedInsurance').val(patientInfo.medInsurance);
		$('#txtPlan').val(patientInfo.plan);
		$('#txtNumPlan').val(patientInfo.numPlan);
		$('#txtObservation').text(patientInfo.observation != null ? patientInfo.observation : '');
	}

	
	
/**
 * 	Dar de alta un nuevo Paciente
 */
	function create_the_patient() {
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
		
		$.post('rest/patient/add/true', $('#frm_new-patient').serialize())
		.done(function(){
			$alert(
				'Se ha dado de alta el paciente '+$('#txtFirstName').val()+' '+$('#txtLastName').val()
			);
			$('input#doc-number').val($('input#txtDocNumber').val());
			do_the_search();
			
		}).fail(function(obj,err,status){
			if(status=='No Aceptable') $alert('Este número de documento ya está registrado.');
			else $alert('ERROR. Falló el alta del nuevo Paciente. Intente nuevamente.');
		});
	}
	
	
/**
 * 	Añadir formulario para Nuevo Paciente
 */	
	function add_new_patient() {
		
		var oTT = TableTools.fnGetInstance('patient-table');
		theHideElement = $('div#body-to-hide');
		
		$('input#is-new-flag').val('1');
		
		if(theHideElement.is(':visible')) {
			oTT.fnSelectNone();
			theHideElement.slideUp();
			theActionsParent.show();
			theActions.load('add-patient.html');
		
		} else return;
	}
	
	
	
	
	
/**
 * 	Cancelar acción que se está ejecutando
 */	
	function cancel_the_action() {
		theHideElement = $('div#body-to-hide');
		
		if(theHideElement.is(':visible')) return ;
		else {
			theActions.children().remove();
			theActionsParent.hide();
			theHideElement.slideDown();
		}
	}
	
	
	
	
/**
 * 	Darle el estilo JQuery Datatable a la tabla
 */	
	function init_dt_table() {
		
		var columns = [
						{ "title": "Tipo Doc.", "data": "docType", "class": "center", "width": "80px" },
						{ "title": "Número Doc.", "data": "docNumber", "class": "center", "width": "200px" },
						{ "title": "Apellido", "data": "lastName", "class": "center" },
						{ "title": "Nombre", "data": "firstName", "class": "center" },
						{ "title": "Sexo", "data": "patSex", "class": "center", "width": "80px" },
						{ "title": "Edad", "data": "birthDate", "class": "center", "width": "25px" },
						{ "title": "Fecha de Alta", "data": "insertDate", "class": "center", "width": "120px",'defaultContent':'---'},
						{ "title": "Info", "class": "center icon", "width": "60px" },
						{ "title": "Mod.", "class": "center icon", "width": "60px" },
						{ "title": "Elim.", "class": "center icon", "width": "60px" }
					  ];
		var columnDefs = [{	"targets": 5,
							"render": function( data, type, row ){
								if(!data||data==null)return '---';
								var hoy= new Date();
								var Fecha= data.substring(3,5)+'/'+data.substring(0,2)+'/'+data.substring(6,10);
								var fecha= new Date(Fecha);
								var edad= parseInt((hoy -fecha)/365/24/60/60/1000);
								return edad;
							}
						  },{	"targets": 7,
							"render": function( data, type, row ){
								return '<a><img class="table-image" src="../images/info.png" ></a>';
							}
						  },{	"targets": 8,
							"render": function( data, type, row ){
								return '<a><img class="table-image" src="../images/pencil.png" ></a>';
							}
						  },{	"targets": 9,
							"render": function( data, type, row ){
								return '<a><img class="table-image" src="../images/trash.png" ></a>';
							}
						 }];

		dt = $('table#patient-table').dataTable({
			
			"data": [],
			"lengthMenu": [ 5, 10, 20, 30 ],
			"pageLength": 10,
			"pagingType": "full_numbers",
			"order": [[ 0, "asc" ],[ 1, "asc"]],
			"dom": '<"pat-cant">lrtTip',
			"tableTools": {
				"sRowSelect": "none",
				"aButtons": [],
				fnRowSelected: null,
				fnRowDeselected: null
			},
			"columns": columns,
			"columnDefs": columnDefs,
			"language": {
	            "lengthMenu": "Mostrar _MENU_ pacientes por página",
	            "zeroRecords": "Sin pacientes para mostrar",
	            "info": "Mostrando página _PAGE_ de _PAGES_",
	            "infoEmpty": "Sin pacientes para mostrar",
	            "infoFiltered": "(filtrados de un total de _MAX_ pacientes)",
			    "emptyTable":     "No se encotraron pacientes",
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
		$('div.pat-cant').html('Resultado: <b>0</b> pacientes');
		
		$("#patient-table").on("click", "td", function() {
			var studyPos = $("#patient-table").dataTable().fnGetPosition( this );
			var fila = studyPos[0];
			var columna = studyPos[1];
			if(columna > 6){
				$(this).parent().removeClass('selected');
				$(this).parent().removeClass('DTTT_selected');
				var patientData = $('table#patient-table').DataTable().row(fila).data();
				
				switch(columna){
					case 7: openDialog('patient-info.html', 'Información de Paciente ' +patientData.firstName+ ' ' +patientData.lastName, 670, 510);
							$('div#dynamicModalDialog').data('patientData', patientData);
							break;
					case 8: modify_the_patient( patientData );
							break;
					case 9: //delete_patient( patientData.docNumber );
							break;
				}
			} 
		}).on('mousedown', function(){ return false; });
		
		$("#patient-table").on("dblclick", "td", function() {
			var studyPos = $("#patient-table").dataTable().fnGetPosition( this );
			var fila = studyPos[0];
			var columna = studyPos[1];
			var patientData = $('table#patient-table').DataTable().row(fila).data();
			if(columna <= 5){
				openDialog('new-practice.html', 'Nueva Práctica para el Paciente', 600, 780);
				$('div#dynamicModalDialog').data('patientData', patientData);
			}
		});
	}
	
	
	
/**
 *  Volcar los resultados de la búsqueda en la tabla
 */
	function draw_table( dt ){
		
		$.ajax({
		method: 'GET',
		dataType: 'json',
		url: 'rest/patient?' + $('form#patient-search').serialize(),
		beforeSend:function(){$('div#ddap-loading-message').show()}
		
		}).done(function(data){
			dt.fnClearTable();
		  	if (data && data.length > 0) {
		  		dt.fnAddData(data);
		  		$('div.pat-cant').html('Resultado: <b>' + data.length + '</b> pacientes');
				$('#patient-table td:not(.icon)').attr('title', 'Haga doble click para una nueva Práctica').tooltip();
		  		
		  	} else $('div.pat-cant').html('Resultado: <b>0</b> pacientes');
		
		}).fail(function(){$alert('ERROR. Falló la búsqueda de Pacientes');
		}).always(function(){$('div#ddap-loading-message').hide()});
	}
	
	
	

	
	
/**
 * 	Darle el estilo JQuery UI a los botones
 */	
	function init_jqbuttons() {
		
		$('button#do-pat-search').button({
			label: "Buscar",
			icons: {'primary': 'ui-icon-search',
					'secondary': 'ui-icon-person'}
		
		}).on('click', do_the_search);
		
		$('button#add-new').button({
			label: "Nuevo",
			icons: {'primary': 'ui-icon-circle-plus',
					'secondary': 'ui-icon-person'}
		
		}).on('click', add_new_patient );
		
	}
	
	
	
	
/**
 * 	Darle el estilo JQuery UI a los elementos select
 */	
	function init_jqselectmenu() {
		
		$('select#document-type').selectmenu();
		$('select#pat-sex').selectmenu();
	}
	
	
	
	
	
	
	
/**
 * 	Ajuste del ancho y centrado según ancho de la ventana
 */	
	function ancho_centrado_elementos() {
		
		theTableParent = $('div.patient-table-parent');
		theTable = $('div#patient-table_wrapper');
		theActionsParent = $('div.patient-actions-parent');
		theActions = $('div.patient-actions');
		
		if(theTableParent.is(':visible')){
			theTableParent.css('width', '99%');
			theTable.css('width', '90%');
			
			if(theTable.width() < 800){
				theTableParent.css('width', '800px');
				theTable.css('width', '800px');
			}
			theActionsParent.width(theTableParent.width());
			theActions.width(theTable.width());
		
		} else{
			theActionsParent.css('width', '99%');
			theActions.css('width', '80%');
			
			if(theActions.width() < 800){
				theActionsParent.css('width', '800px');
				theActions.css('width', '800px');
			}
			theTableParent.width(theActionsParent.width());
			theTable.width(theActions.width());
		}
	}
	
	
	
	
	
	
	
/**
 * 	Acciones iniciales sobre los elementos inputs
 */	
	function init_inputs() {
		
		set_as_datepicker($('input#birth-date'));
		
		$('input[title="Solamente Números"]').on('keypress', function(){
			var tecla = event.which || event.keyCode;
			if(tecla < 48 || tecla > 57) return false;
			else return;
		}).tooltip();
		
		$('input#birth-date').on('change',function(){$(this).val()?$('span#clearBirthDate').show():$('span#clearBirthDate').hide()});
		$('span#clearBirthDate').on('click',function(){var input=$('input#birth-date');if($(input).val())$(input).datepicker('setDate','');$(this).hide()}).tooltip();
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
	
	
	

	
	
	