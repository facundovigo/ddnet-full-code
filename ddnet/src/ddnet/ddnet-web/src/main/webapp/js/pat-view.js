
var ddConfig = null;


function getConfig(name) {
	var entry = $.grep(ddConfig, function(element, index){
	      return element.name == name;
	});
	
	if (entry && entry.length > 0)
		return entry[0].value;
	return null;
}

$(document).ready(function(){
	
	$('#ddnet-version').text('DDnet Version 2016.08.11');
	
	readUserInfo();
	
	$(document).on({
	     ajaxStart: function() { doBlockUI(); },
	     ajaxStop:  function() { doUnblockUI(); },    
	     ajaxError: function() { doUnblockUI(); },
	     ajaxComplete: function(event, jqXHR, ajaxOptions) 
	     {var contentType = jqXHR.getResponseHeader("Content-Type");
	      if (!contentType || !contentType.match("^application/json")){}
	   	 }
	});
	
	// Obtener los valores de configuración
    $.getJSON('rest/config/all')
		  .done(function(result) {
			  	ddConfig = result;
		  })
		  .fail(function() {
			  window.location.href = '../authentication-servlet/logout';
		  });
		

	$('button').on('click', function(){
		alert('ok');
	});
    
	var dt = $('#studies-found').dataTable({
						"data": [],
						"order": [[ 0, "desc" ]],
						"lengthMenu": [ 10, 15, 20, 25, 30, 50, 75, 100 ],
						"pageLength": 10,
						"pagingType": "full_numbers",
						"dom": '<"img"><"cant">Trt<"clear">ipl',
						"tableTools": {
							"sRowSelect": "none",
							"aButtons": [],
							fnRowSelected: function(o) {
								//studiesSelectionChanged();
							},
							fnRowDeselected: function(o) {
								//studiesSelectionChanged();
							}											
						},
						"columns": [																	
									
							{ "title": "FECHA",	"data": "date", 
									"defaultContent": "?", "class": "center", "width": "120px" },
											
							{ "title": "ESTUDIO", "data": "desc", 
									"defaultContent": "", "class": "left", "width": "300px" },

							{ "title": "MODALIDAD", "data": "mod", 
									"defaultContent": "?", "class": "center", "width": "70px" },
							
							{ "title": "CENTRO", "data": "inst", 
									"defaultContent": "?", "class": "left", "width": "250px" },
											
							{ "title": "DR.", "data": "dr", 											
									"defaultContent": "---", "class": "center", "width": "200px" },
							
							{ "title": "INFORME", "data": "rs",																						
									"defaultContent": "?", "class": "center", "width": "60px" },		
																										
							{ "title": "IMAGENES", "data": "uid", 												
									"defaultContent": "?", "class": "center", "width": "60px" }
									
						],	
						"columnDefs": [
									    {
						            	   "targets": 5,											
						            	   "render": function ( data, type, row ) {
						            		   var url = 'rest/reporting/reports/pdf/' + row.uid;
					            		   	
						                       return data ? "<a href='" + url + "'><img src='../images/informado.png' /></a>" : 
						                    	   			 "Pendiente";
						                   }	                   
						               },
						               {
						            	   "targets": 6,
						            	   "render": function( data, type, row ){
						            		   
						            		   $('button').button();
						            		   
						            		   return 	'<button id="btn_'+data+'" class="ui-button ui-widget ui-state-default ui-corner-all" ' +
						            		   			'role="button" aria-disabled="false" onclick="viewImages(this);">'	+
						            		   			'<span class="ui-button-text">VER</span>'	+
						            		   			'</button>' ;
						            	   }
						               }
							],
						"language": {
				            "lengthMenu": "Mostrar _MENU_ estudios por página",
				            "zeroRecords": "No se encontraron registros para mostrar con los criterios especificados.",
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
					});
	
	cargarTabla(dt);
	
	$('.img').html('<img src="../utils/mainlogo" />');
	
});


function cargarTabla(dt){
	
	$.getJSON("rest/studies/patient")
	.done(function(info){
		dt.fnClearTable();
		if (info && info.length > 0) {	dt.fnAddData(info);
										$('.cant').text(info.length + ' ' + (info.length == 1 ? 'ESTUDIO':'ESTUDIOS'));	
		
		}else{ $('.cant').text('0 ESTUDIOS'); }
		
	}).fail(function(){
		alert('Sucedió un error al momento de cargar sus estudios');
		window.location.href = '../authentication-servlet/logout';
	});
}


function readUserInfo() {
	$.getJSON("rest/user/info")
	  .done(function(info) {
		  	window.userinfo = info;
		  	
		  	// Info general del usuario.
	    	$('#userinfo').text('Paciente: ' + info.login + ' [' + info.lastName + ']');
	    	
	  }).fail(function(){window.location.href = '../authentication-servlet/logout';});
}

function viewImages(btn){
	
	var id = btn.id.replace('btn_','');
	
	var url = getConfig('actions.study.simple-view.url').replace('${STUDYID}', id).replace('&seriesUID=${SERIEID}', '').replace('${HOST}', location.hostname) ;
	
	window.open(url, 'Visualización simple para estudio: ', 800, 600);
}
