	
	var dt = null;

	$(document).ready(function(){
		
		adjustment();
		init_eq_list();
		charge_modalities();
		init_eq_buttons();
		get_last_code();
		
		fill_list('all');
	});
	
	window.addEventListener('resize', adjustment);
	
	
	
	function del_the_eq(){
		
		var code = $('input[name="codigo"]').val();
		var do_the_delete = function(){
			$.ajax({
				method: 'POST',
				url: 'rest/equipo/delete/'+ code,
				data: $('form#eq-form').serialize(),
				beforeSend:function(){$('div#ddap-loading-message').show()}
				
			}).done(function(data){
				$stop_wait();
			  	$alert('Se ha eliminado el Equipo');
			  	$('form#eq-form input[type="text"]').val('');
				$('form#eq-form input[type="number"]').val('0');
				$('form#eq-form select').val('').selectmenu('refresh');
				get_last_code();
				fill_list('all');
				$('button#mod-eq').button('option','disabled',true);
				$('button#del-eq').button('option','disabled',true);
				$('button#new-eq').button('option','label','Agregar');
				
			}).fail(function(){$alert('ERROR al eliminar el Equipo');
			}).always(function(){$('div#ddap-loading-message').hide()});
		};
		
		$confirm('Una vez eliminado NO puede recuperarse', do_the_delete);
	}
	
	function mod_the_eq(){
		
		if($('form#eq-form input[type="text"]').val() == '') {
			$alert('Complete todos los campos');
			return;
		}
		if($('form#eq-form select').val() == '') {
			$alert('Complete todos los campos');
			return;
		}
		$.ajax({
			method: 'POST',
			url: 'rest/equipo/new/0',
			data: $('form#eq-form').serialize()
			
		}).done(function(data){
		  	$alert('Se ha modificado el Equipo');
		  	$('form#eq-form input[type="text"]').val('');
			$('form#eq-form input[type="number"]').val('0');
			$('form#eq-form select').val('').selectmenu('refresh');
			get_last_code();
			fill_list('all');
			$('button#mod-eq').button('option','disabled',true);
			$('button#del-eq').button('option','disabled',true);
			$('button#new-eq').button('option','label','Agregar');
			
		}).fail(function(){
			$alert('ERROR al modificar el Equipo');
		});
	}
	
	
	function add_new_eq() {
		
		var label = $('button#new-eq').button('option','label');
		if(label == 'Agregar'){
			if($('form#eq-form input[type="text"]').val() == '') {
				$alert('Complete todos los campos');
				return;
			}
			if($('form#eq-form select').val() == '') {
				$alert('Complete todos los campos');
				return;
			}
			$.ajax({
				method: 'POST',
				url: 'rest/equipo/new/1',
				data: $('form#eq-form').serialize()
				
			}).done(function(data){
			  	$alert('Se ha dado de alta el Equipo');
			  	$('form#eq-form input[type="text"]').val('');
				$('form#eq-form input[type="number"]').val('0');
				$('form#eq-form select').val('').selectmenu('refresh');
				get_last_code();
				fill_list('all');
				
			}).fail(function(){
				$alert('ERROR al dar de alta el Equipo');
			});
			
		} else if(label == 'Nuevo'){
			$('button#mod-eq').button('option','disabled',true);
			$('button#del-eq').button('option','disabled',true);
			$('button#new-eq').button('option','label','Agregar');
			$('form#eq-form input[type="text"]').val('');
			$('form#eq-form input[type="number"]').val('0');
			$('form#eq-form select').val('').selectmenu('refresh');
			get_last_code();
			
		} else return;
	}
	
	
	
	function init_eq_buttons() {
		
		$('button#new-eq').button({
			label: 'Agregar',
			icons: {'primary':'ui-icon-plus'}
		
		}).on('click', add_new_eq );
		
		$('button#mod-eq').button({
			label: 'Modificar',
			icons: {'primary':'ui-icon-pencil'},
			disabled: true
		
		}).on('click', mod_the_eq);
		
		$('button#del-eq').button({
			label: 'Eliminar',
			icons: {'primary':'ui-icon-trash'},
			disabled: true
		
		}).on('click', del_the_eq);
	}
	
	
	
	function init_eq_list() {
		var columns = [
			{ "title": "CÓDIGO", "data": "code", "class": "center" },
			{ "title": "MARCA", "data": "trademark", "class": "center" },
			{ "title": "MODELO", "data": "model", "class": "center" }
		];
		var columnDefs = [];
		dt = $('table#eq-list').dataTable({
			"data": [],
			"pageLength": 10,
			"order": [[ 0, "asc" ]],
			"dom": '<"eq-cant">rtTp',
			"tableTools": {
				"aButtons": [],
				fnRowSelected: function(){},
				fnRowDeselected: function(){}											
			},
			"columns": columns,
			"columnDefs": columnDefs,
			"language": {
	            "lengthMenu": "Mostrar _MENU_ equipos por página",
	            "zeroRecords": "Sin equipos para mostrar",
	            "info": "Mostrando página _PAGE_ de _PAGES_",
	            "infoEmpty": "Sin equipos para mostrar",
	            "infoFiltered": "(filtrados de un total de _MAX_ equipos)",
			    "emptyTable":     "Sin equipos para mostrar",
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
		$('div.eq-cant').html('<b>0</b> EQUIPOS');
		$("table#eq-list").on("dblclick", "td", function() {
			var studyPos = $("#eq-list").dataTable().fnGetPosition( this );
			var fila = studyPos[0];
			var eqData = $('table#eq-list').DataTable().row(fila).data();
			$('form#eq-form select').val(eqData.mod).selectmenu('refresh');
			$('form#eq-form input[name="codigo"]').val(eqData.code);
			$('form#eq-form input[name="marca"]').val(eqData.trademark);
			$('form#eq-form input[name="modelo"]').val(eqData.model);
			var time = eqData.time.split(':');
			$('form#eq-form input[name="horas"]').val(time[0]);
			$('form#eq-form input[name="minutos"]').val(time[1]);
			$('form#eq-form input[name="segundos"]').val(time[2]);
			$('button#new-eq').button('option','label','Nuevo');
			$('button#mod-eq').button('option','disabled',false);
			$('button#del-eq').button('option','disabled',false);
		});
	}
	
	function fill_the_table(){
		draw_table(dt);
	}
	

	function fill_list( mod ) {
		$.ajax({
			method: 'GET',
			dataType: 'json',
			url: 'rest/equipo/'+mod,
			beforeSend:function(){$('div#ddap-loading-message').show()}
			
		}).done(function(data){
			dt.fnClearTable();
			if(data && data.length > 0){
				dt.fnAddData(data);
		  		$('div.eq-cant').html('<b>' + data.length + '</b> EQUIPOS');
		  		
		  } else $('div.eq-cant').html('<b>0</b> EQUIPOS');
			
		}).fail(function(){$alert('ERROR en la búsqueda de Equipos');
		}).always(function(){$('div#ddap-loading-message').hide()});
	}
	
	
	function charge_modalities() {
		$.getJSON('rest/practica/mods')
		.done(function(data){
			var select = $('select#eq-modalities');
			select.find('option').remove().end();
			select.append('<option value="all" selected="selected" >Todas</option>');
			var select1 = $('select#new_modalities');
			select1.find('option').remove().end();
			select1.append('<option value="" selected="selected" >Seleccione...</option>');
			
			if(data && data != null){
				$.each(data, function(i,mod){
					select.append('<option value="'+mod+'" >'+modality_translator(mod)+'</option>');
					select1.append('<option value="'+mod+'" >'+modality_translator(mod)+'</option>');
				});
			}
			select.selectmenu({
				change: function(e, data){
					var value = data.item.value;
					fill_list(value);
				}
			});
			select1.selectmenu();
		})
		.fail(function(){
			$('select#eq-modalities').parent().text('ERROR al cargar las modalidades');
			$('select#new_modalities').parent().text('ERROR al cargar las modalidades');
		});
	}
	
	
	function adjustment() {
		
		$('div.main').css('width','90%');
		
		if($('div.main').width() < 800){
			$('div.main').css('width','800px');
			$('div.to-the-right').width(395);
		}
		$('div.to-the-left').width(
			$('div.main').width() / 2 -2.5
		);
		$('div.to-the-right').width(
			$('div.main').width() / 2 -2.5
		);
	}
	
	
	function get_last_code() {
		$.get('rest/equipo/last-code')
		.done(function(data){
			if(data && data!=null)
				$('input[name="codigo"]').val(data);
		});
	}