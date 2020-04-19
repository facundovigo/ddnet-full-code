
/**
 *  Variables globales
 */
	var dt = null;


	$(document).ready(function(){
		
		init_practice_selectmenu();
		init_practice_button();
		init_practice_table();
		ajuste_ancho();
		
		$('input.region-filter').on('keypress', function(){
			var tecla = event.which || event.keyCode;
			if(tecla < 48 || tecla > 57) return false;
			else return;
		}).tooltip();
		
		do_the_search();
	});
	
	window.addEventListener( 'resize', ajuste_ancho );
	
	
	
	
	
	
	
	
	/**
	 *  Ajustar elementos ante cambios de tamaño de ventana
	 */
	function ajuste_ancho(){
		
		var theDiv = $('div.practice-table');
		var theTable = $('div#practice-result_wrapper');
		var theActions = $('div.practice-actions');
		var theParent = $('div.actions-parent');
		
		if(theDiv.is(':visible')){
			theDiv.css('width', '99%');
			theTable.css('width', '90%');
			
			if(theTable.width() < 800){
				theDiv.css('width', '800px');
				theTable.css('width', '800px');
			}
			theParent.width(theDiv.width());
			theActions.width(theTable.width());
		
		} else{
			theParent.css('width', '99%');
			theActions.css('width', '80%');
			
			if(theActions.width() < 800){
				theParent.css('width', '800px');
				theActions.css('width', '800px');
			}
			theDiv.width(theParent.width());
			theTable.width(theActions.width());
		}
	}
	
	
	
	
	
	
	
	
	/**
	 *  Inicializar tabla con jquery datatable
	 */
	function init_practice_table(){
		
		var columns = [
						{ "title": "Prestación", "data": "prestacion", "class": "center", "width": "120px" },
						{ "title": "Región", "data": "region", "class": "center", "width": "80px" },
						{ "title": "Nombre", "data": "nombre", "class": "left" },
						{ "title": "Valor", "data": "valor", "class": "right", "width": "110px" },
						{ "title": "Valor Em.", "data": "emergencyValue", "class": "right", "width": "110px" },
						{ "title": "Modalidad", "data": "modalidad", "class": "center", "width": "100px" },
						{ "title": "Requiere Informe", "data": "requiereInforme", "class": "center", "width": "150px" },
						{ "title": "Región Informe", "data": "regionInforme", "class": "left", "width": "250px" }
					  ];
		var columnDefs= [
			 {	"targets": 3,
				"render": function( data, type, row ){
					return '<span style="color:#080">$ '+data+'</span>';
				}
			 },
			 {	"targets": 4,
				"render": function( data, type, row ){
					return '<span style="color:#800">$ '+data+'</span>';
				}
			 },
		     {	"targets": 6,
				"render": function( data, type, row ){
					return data ? "SI" : "NO";
				}
			 }
		];

		dt = $('table#practice-result').dataTable({
				
			"data": [],
			"lengthMenu": [ 5, 10, 20, 30 ],
			"pageLength": 10,
			"pagingType": "full_numbers",
			"order": [[ 0, "asc" ],[ 1, "asc"]],
			"dom": '<"p-cant">lrtTip',
			"tableTools": {
				"sRowSelect": "os",
				"aButtons": [],
				fnRowSelected: p_table_selection_changed,
				fnRowDeselected: p_table_selection_changed											
			},
			"columns": columns,
			"columnDefs": columnDefs,
			"language": {
	            "lengthMenu": "Mostrar _MENU_ prácticas por página",
	            "zeroRecords": "Sin prácticas para mostrar",
	            "info": "Mostrando página _PAGE_ de _PAGES_",
	            "infoEmpty": "Sin prácticas para mostrar",
	            "infoFiltered": "(filtrados de un total de _MAX_ prácticas)",
			    "emptyTable":     "Sin prácticas para mostrar",
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
		
		$('div.p-cant').html('Resultado: <b>0</b> prácticas');
	}
	
	
	/**
	 *  Pedido de búsqueda
	 */
	function do_the_search(){
		draw_table(dt);
		cancel_the_operation();
	}
	
	
	
	
	/**
	 *  Volcar los resultados de la búsqueda en la tabla
	 */
	function draw_table( dt ){
		
		$.ajax({
		method: 'GET',
		dataType: 'json',
		url: 'rest/practica?' + $('form#search-practice').serialize(),
		beforeSend:function(){$('div#ddap-loading-message').show()}
		
		}).done(function(data){
			dt.fnClearTable();
		  	if (data && data.length > 0) {
		  		dt.fnAddData(data);
		  		$('div.p-cant').html('Resultado: <b>' + data.length + '</b> prácticas');
		  		
		  	} else $('div.p-cant').html('Resultado: <b>0</b> prácticas');
		
		}).fail(function(){$alert('ERROR. Falló la carga de Prácticas');
		}).always(function(){$('div#ddap-loading-message').hide()});
	}
	
	
	
	/**
	 * 	Eventos ante selección / deselección de filas
	 */
	function p_table_selection_changed(){
		
		var selectedPract = get_selected_practices();
		var theModify = $('button#modify-practice');
		var theDelete = $('button#delete-practice');
		
		theModify.button('option', 'disabled', true);
		theDelete.button('option', 'disabled', true);
		
		if(selectedPract.length <= 0) return;
		
		else if(selectedPract.length == 1){
			theModify.button('option', 'disabled', false);
			theDelete.button('option', 'disabled', false);
			
		} else {
			theDelete.button('option', 'disabled', false);
		}
	}
	
	
	/**
	 * 	Retornar la data de filas seleccionadas
	 */
	function get_selected_practices() {
		var oTT = TableTools.fnGetInstance('practice-result');
	    return oTT.fnGetSelectedData();
	}
	
	
	
	
	
	
	
	/**
	 *  Inicializar button con jquery ui
	 */
	function init_practice_button(){
		
		$('button#search-practice').button({
			label: 'Buscar',
			icons: { 'primary': 'ui-icon-search'}
		
		}).on('click', do_the_search );
		
		$('button#new-practice').button({
			label: 'Nueva',
			icons: {'primary': 'ui-icon-circle-plus'}
		
		}).on('click', add_practice);
		
		$('button#modify-practice').button({
			label: 'Modificar',
			icons: {'primary': 'ui-icon-pencil'},
			disabled: true
		
		}).on('click', modify_the_practice );
		
		$('button#delete-practice').button({
			label: 'Eliminar',
			icons: {'primary': 'ui-icon-trash'},
			disabled: true
		
		}).on('click', delete_practices );
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	*	Inicializar select con jquery ui
	*/
	function init_practice_selectmenu(){
		
		$.getJSON('rest/practica/mods')
		.done(function(data){
			var select = $('select#practice-modalities');
			
			select.find('option').remove().end();
			select.append('<option value="" selected="selected" >Seleccione...</option>');
			
			if(data && data != null){
				$.each(data, function(i,mod){
					select.append('<option value="'+mod+'" >'+modality_translator(mod)+'</option>');
				});
			}
			select.selectmenu({ change: do_the_search });
			
		}).fail(function(){ $('select#practice-modalities').parent().html('ERROR cargando modalidades'); });
		
		$('select#need-report').selectmenu({ change: do_the_search });
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 *  Opción para crear nueva práctica
	 */
	function add_practice(){
		
		var theDiv = $('div.practice-table');
		var theParent = $('div.actions-parent');
		var theActions = $('div.practice-actions');
		var oTT = TableTools.fnGetInstance('practice-result');
		
		$('input#flag-is-new').val('1');
		
		if(theDiv.is(':visible')) {
			$('button#new-practice').button('option', 'disabled', true);
			oTT.fnSelectNone();
			theDiv.slideUp();
			theParent.show();
			theActions.load('add-practice.html');
		
		} else return;
	}
	
	
	
	/**
	 *  Confirmación de nueva práctica
	 */
	function create_the_practice(){
		
		if($('input#capability').val()==''){
			$alert('Ingrese la Prestación');
			return;
		}
		if($('input#name').val()==''){
			$alert('Ingrese el Nombre');
			return;
		}
		if($('select#modality').val()==''){
			$alert('Ingrese la Modalidad');
			return;
		}
		
		$.ajax({
			method: 'POST',
			url: 'rest/practica/add/true',
			data: $('form#new-practica').serialize(),
			beforeSend:function(){$('div#ddap-loading-message').show()}
			
		}).done(function(){
			$alert('Se ha creado la Práctica');
			cancel_the_operation();
			do_the_search();
		
		}).fail(function(obj,err,status){ 
			if(status=='No Aceptable') $alert('No se dió de alta la Práctica porque ya existe una con la prestación y región ingresadas.');
			else $alert('ERROR. No se creó la Práctica. Intente nuevamente.');
		}).always(function(){$('div#ddap-loading-message').hide()});
	}
	
	
	
	function clear_practice_form(){
		
		$('form#new-practica input').val('');
		$('form#new-practica input:checked').prop('checked',false);
		$('form#new-practica select#modality').val('').change();
		$('form#new-practica select#type').val('Práctica').change();
	}
	
	
	
	function cancel_the_operation(){
		
		var theDiv = $('div.practice-table');
		var theParent = $('div.actions-parent');
		var theActions = $('div.practice-actions');
		
		if(theDiv.is(':visible')) return ;
		else {
			theActions.children().remove();
			theParent.hide();
			theDiv.slideDown();
		}
		$('button#new-practice').button('option', 'disabled', false);
		$('button#modify-practice').button('option', 'disabled', true);
		$('button#delete-practice').button('option', 'disabled', true);
	}
	
	
	/**
	 *  Opción para modificar la Práctica seleccionada
	 */
	function modify_the_practice(){
		
		var selectedPract = get_selected_practices();
		if(selectedPract.length != 1) return;
		
		var id = 0;
		$.each(selectedPract, function(i,p){
			id = p.id;
		});
		
		var theDiv = $('div.practice-table');
		var theParent = $('div.actions-parent');
		var theActions = $('div.practice-actions');
		var oTT = TableTools.fnGetInstance('practice-result');
		
		$('input#flag-is-new').val('0');
		
		if(theDiv.is(':visible')) {
			$('button#new-practice').button('option', 'disabled', true);
			oTT.fnSelectNone();
			theDiv.slideUp();
			theParent.show();
			theActions.load('add-practice.html');
			$('input[name="practice-id"]').val(id);
		
		} else return;
	}
	
	/**
	 *  Obtener datos de la práctica a modificar
	 */
	function get_current_practice(){
		
		
		$.ajax({
			method: 'GET',
			dataType: 'json',
			url: 'rest/practica/' + $('input[name="practice-id"]').val(),
			beforeSend:function(){$('div#ddap-loading-message').show()}
			
		}).done(function(data){
			if(data && data != null){
				$('input#capability').val(data.prestacion);
				$('input#name').val(data.nombre);
				$('select#modality').val(data.modalidad);
				$('input#region').val(data.region);
				$('input#internal').val(data.interno);
				$('input#abbreviated').val(data.abreviado);
				$('input#service').val(data.servicio);
				$('input#speciality').val(data.especialidad);
				$('select#type').val(data.tipo);
				$('input#need-report').prop('checked', data.requiereInforme);
				$('input#report-region').val(data.regionInforme);
				$('input#practice-value').val(data.valor);
				$('input#practice-emergency-value').val(data.emergencyValue);
				
			} else $alert('ERROR. No se encontró la Práctica seleccionada.');
			
		}).fail(function(){$alert('ERROR. Falló la carga de Prácticas');
		}).always(function(){$('div#ddap-loading-message').hide()});
	}
	
	
	/**
	 * 	Confirmación de edición de la práctica
	 */
	function edit_the_practice(){
		if($('input#capability').val()==''){
			$alert('Ingrese la Prestación');
			return;
		}
		if($('input#name').val()==''){
			$alert('Ingrese el Nombre');
			return;
		}
		if($('select#modality').val()==''){
			$alert('Ingrese la Modalidad');
			return;
		}
		if($('input#practice-value').val()==''){
			$alert('Ingrese el Valor');
			return;
		}
		
		$.ajax({
			method: 'POST',
			url: 'rest/practica/add/false',
			data: $('form#new-practica').serialize()
			
		}).done(function(){
			$alert('Se ha modificado la Práctica');
			cancel_the_operation();
			do_the_search();
		
		}).fail(function(obj,err,status){
			if(status=='No Aceptable') $alert('No se modificó la Práctica porque ya existe una con la prestación y región ingresadas.');
			else $alert('ERROR. No se modificó la Práctica. Intente nuevamente.');
		});
	}
	
	
	
	
	/**
	 * 	Para eliminar prácticas seleccionadas
	 */
	function delete_practices(){
		
		var do_the_delete = function(){
			var selectedPract = get_selected_practices();
			if(selectedPract.length <= 0) return;
			
			var dto = { practiceIDs : [] };
			
			$.each(selectedPract, function(i, p){
				dto.practiceIDs.push(p.id);
			});
			
			$.ajax({
				method: 'POST',
				url: 'rest/practica/delete',
				data: dto
				
			}).done(function(){
				$alert('Se han eliminado las Prácticas seleccionadas');
				cancel_the_operation();
				do_the_search();
			
			}).fail(function(obj,err,status){ 
				$alert('ERROR. Falló la eliminación de Prácticas. Intente nuevamente.');
				do_the_search();
			});
		};
		
		$confirm('Una vez eliminadas las Prácticas no pueden recuperarse', do_the_delete);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
