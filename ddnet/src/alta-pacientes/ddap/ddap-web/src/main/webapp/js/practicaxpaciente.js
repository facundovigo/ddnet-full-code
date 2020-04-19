
	
	var dt = null;



	$(document).ready(function(){
		
		$('button#do-search').button({
			label: 'Buscar',
			icons: {'primary':'ui-icon-search'}
		
		}).on('click', do_the_search);
		
		init_table();
		init_selectmenu();
		ajuste_ancho();
		set_as_datepicker($('input.choose-date'));
		$('input.choose-date').datepicker("setDate", new Date());
		$('form input').on('keypress', function(){	
			var tecla = event.which || event.keyCode;
			if(tecla == 13) do_the_search();
			else return;
		});
		$('input[title="Solamente números"]').on('keypress',function(){
			var tecla= event.which||event.keyCode;
			if(tecla<48 || tecla>57) return false;
			else return;
		}).tooltip();
		
		do_the_search();
	});
	
	
	window.addEventListener( 'resize', ajuste_ancho );
	
	
	
	
	
	

	function init_selectmenu() {
		$('select#date-type').selectmenu({
			change: function( e, data ) {
				
				var desde = $('input#between-date-from'),
					hasta = $('input#between-date-to');
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
				if(value < 6) do_the_search();
			}
		});
		
		$('select#practice-state').selectmenu({
			change: do_the_search
		});
		$('select#pat-sex').selectmenu({
			change: do_the_search
		});
	}
	
	
	
	
	function ajuste_ancho(){
		
		var theDiv = $('div.for-the-table');
		var theTable = $('div#pp-data-list_wrapper');
		
		theDiv.css('width', '99%');
		theTable.css('width', '90%');
		
		if(theTable.width() < 800) {
			theDiv.css('width', '800px');
			theTable.css('width', '800px');
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

	function init_table(){
		
		var columns = [
		               	{ "title": "Acc.Number", "data": "code", "class": "center", "width": "120px" },
						{ "title": "Fecha", "data": "date", "class": "center", "width": "80px" },
						{ "title": "Hora", "data": "time", "class": "center", "width": "80px" },
						{ "title": "PatientID", "data": "patID", "class": "center", "width": "100px" },
						{ "title": "Apellidos", "data": "lastName", "class": "right", "width": "190px" },
						{ "title": "Nombres", "data": "firstName", "class": "right", "width": "190px" },
						{ "title": "Sexo", "data": "sex", "class": "center", "width": "60px" },
						{ "title": "Edad", "data": "birthDate", "class": "center", "width": "50px" },
						{ "title": "Práctica", "data": "pract", "class": "left" },
						{ "title": "Med. Derivante", "data": "medDerivante", "class": "center", "width": "100px", 'defaultContent':'---' },
						{ "title": "Estado", "data": "state", "class": "center", "width": "80px" },
						{ "title": "PDF", "class": "center", "width": "45px" }
					  ];
		var columnDefs = [	{'targets':7,
							 'render':function(data,type,row){
								if(!data||data==null)return '---';
								var hoy= new Date();
								var Fecha= data.substring(3,5)+'/'+data.substring(0,2)+'/'+data.substring(6,10);
								var fecha= new Date(Fecha);
								var edad= parseInt((hoy -fecha)/365/24/60/60/1000);
								return edad;
							}},
							{	"targets": 10,
								"render": function( data, type, row ){
									return data == 'En Espera' ? "<span class='status en-espera' " 	+
																 "code='" + row.code + "' " 		+
																 "status='1' >"						+
																 data + "</span>"
																 
										:  data == 'Ingresó' ? "<span class='status ingreso' " 	+
															   "code='" + row.code + "' " 		+
															   "status='2' >"					+
															   data + "</span>"
												
										:  data == 'Terminó' ? "<span class='status termino' " 	+
																"code='" + row.code + "' " 		+
																"status='3' >" 					+
																data + "</span>"
									
										:	"<span class='status cancelado' " 	+
											"code='" + row.code + "' " 			+
											"status='4' >" 						+
											data + "</span>";
								}
							},
							{	"targets": 11,
								"render": function( data, type, row ){
									var url = 'rest/practicaxpaciente/pdf/' + row.code;
									return "<a href='" + url + "'><img class='table-icon' src='../images/informado.png' /></a>";
								}
							}
		                 ];

		dt = $('table#pp-data-list').dataTable({
				
			"data": [],
			"lengthMenu": [ 5, 10, 20, 30 ],
			"pageLength": 10,
			"order": [[ 1, "desc" ],[ 2, "desc" ]],
			"dom": '<"pp-cant">lrtTip',
			"tableTools": {
				"aButtons": [],
				fnRowSelected: function(o) {  },
				fnRowDeselected: function(o) {  }											
			},
			"columns": columns,
			"columnDefs": columnDefs,
			"language": {
	            "lengthMenu": "Mostrar _MENU_ prácticas/paciente por página",
	            "zeroRecords": "Sin prácticas/paciente para mostrar",
	            "info": "Mostrando página _PAGE_ de _PAGES_",
	            "infoEmpty": "Sin prácticas/paciente para mostrar",
	            "infoFiltered": "(filtrados de un total de _MAX_ prácticas/paciente)",
			    "emptyTable":     "Sin prácticas/paciente para mostrar",
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
		
		$('div.pp-cant').html('Resultado: <b>0</b> prácticas/paciente');
		
		$('table#pp-data-list').on('click', 'img.table-icon.multiple', function() {
			var code = this.attributes["code"].value;
			openDialog('orden-medica.html?code='+code, 'Orden Médica', 550, 850);
		});
		
		$('table#pp-data-list').on('dblclick', 'span.status', function() {
			var code = this.attributes["code"].value;
			var status = this.attributes["status"].value;
			openDialog('pp-status.html?code='+code+'&status='+status, 'Estado de la práctica '+code, 300, 550);
		
		}).on('mousedown', 'span.status', function(){return false;});
	}


	function draw_table( dt ){
		$.ajax({
		method: 'GET',
		dataType: 'json',
		url: 'rest/practicaxpaciente?'+ $('form#search-practice-by-patient').serialize(),
		beforeSend:function(){$('div#ddap-loading-message').show()}

		}).done(function(data){
			dt.fnClearTable();
		  	if (data && data.length > 0) {
		  		dt.fnAddData(data);
		  		$('div.pp-cant').html('Resultado: <b>' + data.length + '</b> prácticas/paciente');

		  	} else $('div.pp-cant').html('Resultado: <b>0</b> prácticas/paciente');

		}).fail(function(){$alert('ERROR. Falló la carga de Prácticas');
		}).always(function(){$('div#ddap-loading-message').hide()});
	}


	function open_image( url ){
		openDialog('orden-medica.html?img='+url, 'Orden Médica', 550, 850);
	}



	function do_the_search() {
		$('input[name="between-date-from"]').val($('input#between-date-from').val());
		$('input[name="between-date-to"]').val($('input#between-date-to').val());
		draw_table(dt);
	}
