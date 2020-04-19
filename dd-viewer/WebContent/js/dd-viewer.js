
	var dt = null;

	$(document).ready(function(){
		
		$('div#viewer-tabs').tabs();
		init_jqbuttons();
		set_as_datepicker($('input.date-picker'));
		set_radio_button();
		init_study_table();
		get_the_modalities();
		resize_handler();
		
	});
	
	window.addEventListener('resize', resize_handler);
	
	
	
	
	
	
	function add_new_tab(studyData) {
		
		var tabs = $('div#viewer-tabs');
		var studyID =  studyData.studyId.replace(/\./g,'_');

		if( tabs.find('a[href="#'+ studyID +'"]').length > 0 ) {
			$('a[href="#'+ studyID +'"]').click();
			return;
		}
		if(tabs.find('li.closable').length>=5){
			$alert('El visualizador admite hasta 5 estudios al mismo tiempo');
			return;
		}
		
		var newTab = '<li class="closable">'
				   + '<a href="#' + studyID + '">'
				   + studyData.patName
				   + '</a>'
				   + '<span class="ui-icon ui-icon-close" role="presentation" studyID="'+studyID+'">'
				   + 'Cerrar Pestaña</span>'
				   + '</li>';
		
		tabs.find( '.ui-tabs-nav' ).append( newTab );
		tabs.append( '<div id="' + studyID + '"></div>' );
	    tabs.tabs( "refresh" );
		set_closable_tabs();
		
		var studyViewer;
		$.ajax({url:'visualizador.html',async:false,dataType:'html',success:function(html){studyViewer=html;}});
		var viewport;
		$.ajax({url:'viewportTemplate.html',async:false,dataType:'html',success:function(html){viewport=html;}});
		
		
		var studyViewerCopy = $(studyViewer).clone();
		studyViewerCopy.attr('id','viewer-'+studyID);
		studyViewerCopy.removeClass('hidden');
		studyViewerCopy.appendTo('#'+studyID);
		$('a[href="#'+studyID+'"]').click();
		
		loadStudy(studyViewerCopy,viewport,studyData.studyId);
	}




	
	
	
	function clean_the_search() {
		$('form#study-filter input[type="text"]').val('');
		$('form#study-filter select').val('');
		$('input#date-today').click();
	}
	
	function cancel_the_search() {
		clean_the_search();
		show_or_hide();
	}
	
	
	function do_the_search() {
		
		$('input[name="date-between-from"]').val($('input#date-between-from').val());
		$('input[name="date-between-to"]').val($('input#date-between-to').val());
		
		$.ajax({
			method: 'GET',
			dataType: 'json',
			url: 'studies/find?' + $('form#study-filter').serialize(),
			beforeSend: function(){$('div#loading-images').show();}
			
		}).done(function(result){
			dt.fnClearTable();
			show_or_hide();
			if(result && result.length > 0){
				dt.fnAddData(result);
				$('div.std-cant').html('Resultado: <b>'+ result.length +'</b> estudios');
			
			} else $('div.std-cant').html('Resultado: <b>0</b> estudios');
			
			$('div#loading-images').hide(500);
			
		}).fail(function(obj,msj,err){
			$('div#loading-images').hide();
			alert('ERROR: '+msj);
		});
	}
	
	
	
	function get_the_modalities() {
		
		$.getJSON('modalities/all')
		.done(function(data){
			var select = $('select#study-modalities');
			select.find('option').remove().end();			    		
			select.append('<option value="" selected="selected" >Todas</option>');
			
			if(data && data.length > 0){
				$.each(data, function(i, mod){
					if(mod != null)
						select.append('<option value="'+mod.replace(/\\/g,'_')+'">'+mod+'</option>');
				});
			}
		})
		.fail(function(obj,msj,err){
			alert(msj);
		});
	}
	
	
	
	
	function init_study_table() {
		
		var columns = [
			{ "title": "NOMBRE", "data": "patName", "class": "left" },
			{ "title": "PAT-ID", "data": "patId", "class": "left", "width": "150px", "defaultContent": "---" },
			{ "title": "FECHA", "data": "studyDate", "class": "center", "width": "100px" },
			{ "title": "MOD", "data": "studyMod", "class": "center", "width": "30px" },
			{ "title": "DESCRIPCIÓN", "data": "studyDesc", "class": "center", "defaultContent": "---" },
			{ "title": "# I", "data": "numImgs", "class": "right", "width": "25px" }
		];
		var columnDefs = [
		    /*{	"targets": 0,
				"render": function( data, type, row ){
					return data.substr(0,10);
				}
		    }*/
		];

		dt = $('table#list-of-studies').dataTable({
			"data": [],
			"lengthMenu": [ 5, 10, 20, 30 ],
			"pageLength": 10,
			"pagingType": "full_numbers",
			"order": [[ 2, "desc" ]],
			"dom": '<"std-cant">lrtTip',
			"tableTools": {
				"aButtons": [],
				fnRowSelected: function(){},
				fnRowDeselected: function(){}											
			},
			"columns": columns,
			"columnDefs": columnDefs,
			"language": {
	            "lengthMenu": "Mostrar _MENU_ estudios por página",
	            "zeroRecords": "Sin estudios para mostrar",
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
			
		}).on('mousedown', function(){return false;});
		
		$('div.std-cant').html('Resultado: <b>0</b> estudios');
		
		$('table#list-of-studies').on('dblclick', 'td', function() {
			
			var studyPos = $('table#list-of-studies').dataTable().fnGetPosition( this );
			var fila = studyPos[0];
			var studyData = $('table#list-of-studies').dataTable().fnGetData(fila);
			add_new_tab(studyData);
		});
	}
	
	
	
	
	
	function show_or_hide() {
		
		if($('div.study-search').is(':visible')) {
			$('div.study-search').slideUp();
			$('img#filter-arrow').attr('src','images/downarrow.png');
		
		} else {
			$('div.study-search').slideDown();
			$('img#filter-arrow').attr('src','images/uparrow.png');
		}
	}
	
	
	
	function set_radio_button() {
		
		var from = $('input#date-between-from'), to = $('input#date-between-to'); 
		
		$('input[type="radio"]:not(#date-between)').on('click', function(){
			$('input.date-picker').prop('disabled', true);
			
			$('input[type="radio"]').parent().css('color','#ccc');
			$('label[for="date-between"]').css('color','#ccc');
			$(this).parent().css('color','#0972a5');
		});
		
		$('input#date-today').on('click', function(){
			$('input.date-picker').datepicker('setDate', new Date());
		});
		$('input#date-yesterday').on('click', function(){
			var yesterday = new Date();
			yesterday.setDate(yesterday.getDate() - 1);
			from.datepicker("setDate", yesterday);
			to.datepicker("setDate", yesterday);
		});
		$('input#date-week').on('click', function(){
			var week = new Date();
			week.setDate(week.getDate() - 7);
			from.datepicker("setDate", week);
			to.datepicker("setDate", new Date());
		});
		$('input#date-month').on('click', function(){
			var month = new Date();
			month.setDate(month.getDate() - 30);
			from.datepicker("setDate", month);
			to.datepicker("setDate", new Date());
		});
		$('input#date-any').on('click', function(){
			$('input.date-picker').datepicker('setDate', '');
		});
		$('input#date-between').on('click', function(){
			$('input.date-picker').prop('disabled', false);
			
			$('input[type="radio"]').parent().css('color','#ccc');
			$('label[for="date-between"]').css('color','#0972a5');
		});
		
		$('input#date-today').click();
	}
	
	
	
	function init_jqbuttons() {
		$('button#do-search').button({
			label: 'Buscar',
			icons: {'primary': 'ui-icon-search'}
		
		}).on('click', do_the_search );
		
		$('button#do-clean').button({
			label: 'Limpiar',
			icons: {'primary': 'ui-icon-refresh'}
		
		}).on('click', clean_the_search );
		
		$('button#do-cancel').button({
			label: 'Cancelar',
			icons: {'primary': 'ui-icon-close'}
		
		}).on('click', cancel_the_search );
	}
	
	
	
	
	
	
	function set_closable_tabs(){
		var tabs = $('div#viewer-tabs');
		
		tabs.delegate( "span.ui-icon-close", "click", function() {
		      var studyID = $(this).attr('studyID');
		      var panelId = $( this ).closest( "li" ).remove().attr( "aria-controls" );
		      $( "#" + panelId ).remove();
		      $( '#' + studyID ).remove();
		      tabs.tabs( "refresh" );
		});
		$('div#viewer-tabs li.closable').on('click', function(ev){
			switch(ev.which){
				case 2: var panelId = $( this ).remove().attr( "aria-controls" );
						var stdID = $(this).find('a').attr('href').replace('#','');
						$( "#" + panelId ).remove();
						$( '#' + stdID ).remove();
						tabs.tabs( "refresh" );
						break;
			}
			return true;
		});
		$('div#viewer-tabs li.closable').css('cursor','pointer');
		$('div#viewer-tabs li.closable').find('a').css('cursor', 'pointer');
		
	}
	
	
	function resize_handler() {
		
		var tabs = $('div#viewer-tabs'),
			top = $('div.top-of-page');
		var	theDiv = $('div.study-list');
		var	theTable = $('div#list-of-studies_wrapper');
		
		tabs.css('width', '99.5%');
		$('.ui-tabs.ui-widget-content').height(window.innerHeight - 80);
		
		top.css('width', '99.9%');
		theDiv.css('width', '99%');
		theTable.css('width', '85%');
		
		if(tabs.width() < 800) {
			tabs.css('width', '800px');
			top.css('width', '800px');
			theDiv.css('width', '800px');
			theTable.css('width', '800px');
		}
	}
	
	
	

	function $wait( studyID ){
		
		var cuerpo = '';
			
		cuerpo += '<div id="wait-message" title="Procesando"><br>';
		cuerpo += '<p>Espere un momento...</p>';
		cuerpo += '<div id="progressbar"></div><br></div>';
		
		$('body').append(cuerpo);
		
		$('#wait-message').dialog({
		   modal: true,
		   //closeOnEscape: false,
		   open: function(){
			   $(".ui-dialog-titlebar-close").hide();
			   $( "div#progressbar" ).progressbar({ value: false });
		   },
		   close: function() { $( this ).remove(); }
		});
	}
	
	function $stop_wait( studyID ){
		
		$(".ui-dialog-titlebar-close").show();
		$('#wait-message').dialog('close');
	}
	
	
	
	

	function openDialog(url, title, height, width){
		
		var iframe = '<div id="dynamicModalDialog">' +
						'<iframe id="dynamicModalDialogIframe" src="" ></iframe>' +
					 '</div>';

		$('body').append(iframe);
		
		$('#dynamicModalDialogIframe').css('width', '99%');
		$('#dynamicModalDialogIframe').css('height', '98.5%');

		$("#dynamicModalDialog").dialog({
			height: height,
			width: width,
			closeOnEscape: true,
			closeText: "Cerrar",
			modal: true,
			title: title,
			open: function(ev, ui) {
				$('#dynamicModalDialogIframe').attr('src', url);
			},
			close: function(ev, ui) {$('#dynamicModalDialogIframe').attr('src', 'about:blank');
									 $("#dynamicModalDialog").remove();}
		});
		
	}
	
	
	function open_loading_images(){
		
		$("div#loading-dialog").dialog({
			height: 200,
			width: 300,
			closeOnEscape: false,
			closeText: '',
			modal: true,
			title: '',
			open: function(ev, ui) {
				$('div#dialog-iframe').attr('src', 'loading.html');
			},
			close: function(ev, ui) {$('div#loading-iframe').attr('src', 'about:blank');
									 $("div#loading-dialog").hide();}
		});
	}
	
	function $alert(mensaje){
		
		if(!mensaje || mensaje == null) return false;
		
		mensaje = mensaje.toString();
		
		var cuerpo = '';
		cuerpo += '<div id="dialog-message" title="Mensaje"><br>';
		cuerpo += mensaje.replace(/\n/g, '<br>');
		cuerpo += '</div>';
		
		$('body').append(cuerpo);
		
		$( "#dialog-message" ).dialog({
		   modal: true,
		   buttons: {
		      OK: function() {
		        $( this ).dialog( "close" );
		        $( this ).remove();
		      }
		   }
		});
//		    <span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
	}
	
	
	
	
	