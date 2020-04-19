	

var dt = null;


	$(document).ready(function(){
			
		set_as_datepicker($('input.choose-date'));
		$('input.choose-date').datepicker("setDate", new Date());
		init_log_table();
		fill_the_table();
		ajuste_ancho();
		
		$('button#refresh-log').button({
			label: 'Recargar',
			icons: {'primary' : 'ui-icon-refresh'}
		
		}).on('click', fill_the_table);
		
		
		$.getJSON('rest/user/all')
		.done(function(data){
			if(data && data.length > 0){
				var select = $('select#users');
				select.find('option').remove().end();
				select.append('<option value="" selected="selected" >Todos</option>');
				$.each(data, function(i, u){
					select.append('<option value="'+u.login+'" >'+u.login+'</option>');
				});
				
				select.selectmenu({ change: fill_the_table });
			}
		}).fail(function(){
			$('select#users').parent().text('ERROR cargando Usuarios');
		});
		
		$('select#log-date-type').selectmenu({
			change: function( e, data ) {
				
				var desde = $('input#log-between-date-from'),
					hasta = $('input#log-between-date-to');
				var value = parseInt(data.item.value);
				$('input.choose-date').prop('disabled', true);
				
				switch(value){
					case 1:
						$('input.choose-date').datepicker("setDate", new Date());
						break;
					
					case 2:
						var yesterday = new Date();
						yesterday.setDate(yesterday.getDate() - 1);
						$('input.choose-date').datepicker("setDate", yesterday);
						break;
					
					case 3: 
						var lweek = new Date();
						lweek.setDate(lweek.getDate() - 7);
						desde.datepicker("setDate", lweek);
						hasta.datepicker("setDate", new Date());
						break;
						
					case 4: 
						var lmonth = new Date();
						lmonth.setDate(lmonth.getDate() - 30);
						desde.datepicker("setDate", lmonth);
						hasta.datepicker("setDate", new Date());
						break;
						
					case 5: 
						$('input.choose-date').datepicker("setDate", "");
						break;
						
					case 6: 
						$('input.choose-date').prop('disabled', false);
						break;
				}
				if(value < 6) fill_the_table();
			}
		});
		
	});
	


	window.addEventListener( 'resize', ajuste_ancho );



	/**
	 *  Ajustar elementos ante cambios de tamaño de ventana
	 */
	function ajuste_ancho(){
		
		var theDiv = $('div.log-table');
		var theTable = $('div#log-data_wrapper');
		
		theDiv.css('width', '99%');
		theTable.css('width', '80%');
		
		if(theTable.width() < 800){
			theDiv.css('width', '800px');
			theTable.css('width', '800px');
		}
	}
	
	
	
	

	function fill_the_table(){
		
		$('input[name="log-date-from"]').val($('input#log-between-date-from').val());
		$('input[name="log-date-to"]').val($('input#log-between-date-to').val());
		
		$.ajax({
			method: 'GET',
			dataType: 'json',
			url: 'rest/log?' + $('form#log-search-form').serialize(),
			beforeSend:function(){$('div#ddap-loading-message').show()}
			
		}).done(function(data){
			dt.fnClearTable();
			if(data && data.length > 0){
				dt.fnAddData(data);
		  		$('div.l-cant').html('Resultado: <b>' + data.length + '</b> datos');
		  		
		  } else $('div.l-cant').html('Resultado: <b>0</b> datos');
			
		}).fail(function(){$alert('Ocurrió un error al momento de cargar el log');
		}).always(function(){$('div#ddap-loading-message').hide()});
		
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 *  Inicializar tabla con jquery datatable
	 */
	function init_log_table(){
		
		var columns = [
						{ "title": "FECHA", "data": "date", "class": "center", "width": "15%" },
						{ "title": "HORA", "data": "date", "class": "center", "width": "15%" },
						{ "title": "USUARIO", "data": "user", "class": "center", "width": "20%" },
						{ "title": "ACCIÓN", "data": "action", "class": "left", "width": "25%" },
						{ "title": "DETALLE", "data": "details", "class": "left", "width": "25%" }
					  ];
		var columnDefs = [{		"targets": 0,
								"render": function( data, type, row ){
									return data.substr(0,10);
								}
						 },
						 {		"targets": 1,
							 	"render": function( data, type, row){
							 		return data.substr(11,12);
							 	}
						 }];

		dt = $('table#log-data').dataTable({
				
			"data": [],
			"lengthMenu": [ 5, 10, 20, 30 ],
			"pageLength": 10,
			"pagingType": "full_numbers",
			"order": [[ 0, "desc" ],[ 1, "desc"]],
			"dom": '<"l-cant">lrtTip',
			"tableTools": {
				"aButtons": [],
				fnRowSelected: function(){},
				fnRowDeselected: function(){}											
			},
			"columns": columns,
			"columnDefs": columnDefs,
			"language": {
	            "lengthMenu": "Mostrar _MENU_ datos por página",
	            "zeroRecords": "Sin datos para mostrar",
	            "info": "Mostrando página _PAGE_ de _PAGES_",
	            "infoEmpty": "Sin datos para mostrar",
	            "infoFiltered": "(filtrados de un total de _MAX_ datos)",
			    "emptyTable":     "Sin datos para mostrar",
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
		
		$('div.l-cant').html('Resultado: <b>0</b> datos');
	}