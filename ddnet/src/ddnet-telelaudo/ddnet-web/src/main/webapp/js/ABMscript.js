/**
 *	Script para el ABM estudios-usuarios 
 */

//variables globales

var AltoVentana = window.innerHeight;
var AnchoVentana;
var tabs;
var tablaU = null;
var tablaE = null;
var editor;

//funciones a realizar al momento de cargar la página
$(document).ready(function(){
	
	tabs = $('#tabs');
	tablaU = $('#tbUsuarios');
	tablaE = $('#tbEstudios');
	comparoAlturas();
	AnchoVentana = document.body.clientWidth;
	$('#tabs').tabs();
	seteoTablaEstudio();
	seteoBotonesEstudio();
	seteoTablaUsuario();
	seteoBotonesUsuario();
	$('.dataTables_length').css('float','left');
	$('#tbEstudios').css('width', '98%');
	
	$(document).on({
	     ajaxStart: function() { doBlockUI(); },
	     ajaxStop:  function() { doUnblockUI(); },    
	     ajaxError: function() { doUnblockUI(); },
	     ajaxComplete: function(event, jqXHR, ajaxOptions) 
	     {var contentType = jqXHR.getResponseHeader("Content-Type");
	      if (!contentType || !contentType.match("^application/json")){}
	   	 }
	});
});

window.addEventListener( "resize" , function() { 
	
	comparoAlturas();
	$('#tbEstudios').css('width', '98%');
});

//configuro la tabla de USUARIOS
function seteoTablaUsuario(){
	
	
	var columnas = [{ 	"title": "USUARIO", "data": "login", "defaultContent": "", 
						"class": "center", "width": "150px" },
					{ 	"title": "NOMBRE", "data": "fname", "defaultContent": "", 
						"class": "right", "width": "150px" },
					{ 	"title": "APELLIDO", "data": "lname", "defaultContent": "", 
						"class": "right", "width": "150px" },
					{ 	"title": "ROL", "data": "role", "defaultContent": "", 
						"class": "right", "width": "150px" },
					{ 	"title": "HABILITADO", "data": "blocked", "defaultContent": "", 
						"class": "center", "width": "100px" },
					{ 	"title": "MODALIDADES", "data": "mod", "defaultContent": "", 
						"class": "left"/*, "width": "100px"*/ }
			  ];
	var columnasDef = [{	"targets": 0,
		 					"render": function ( data, type, row ) {return '<b>'+data+'</b>';}
						},
						{	"targets": 4,
							"render": function ( data, type, row ) {return data ? 'NO':'SÍ';} 
						},
						{	"targets": 5,
							"render": function ( data, type, row ) {
								
								var datos = '';
								
								$.each(data, function(ind,v){
									if(v==null) v = '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
									v += ' ';
									datos += v;	});
								return datos;} 
						}
					  ];
	var dt = tablaU.dataTable({
		"data": [],
		"lengthMenu": [ 5, 10, 15, 20, 25, 30, 50, 75, 100 ],
		"pageLength": 10,
		"dom": 'l<"cant">Trt<"clear">ip',
		"pagingType": "full_numbers",
		
		"tableTools": {
			"sRowSelect": "os",
			"aButtons": [ { "sExtends":"select_all",
							"sButtonText":"Marcar Todos"}, 
						  { "sExtends": "select_none",
			                "sButtonText": "Desmarcar Todos"}	 
						],
			fnRowSelected: function(o) {
				itemsSelectionChangedU();
			},
			fnRowDeselected: function(o) {
				itemsSelectionChangedU();
			}											
		},
		
		"columns": columnas,					
		"columnDefs": columnasDef,
		
		"language": {
            "lengthMenu": "Mostrar _MENU_ usuarios por página",
            "zeroRecords": "No se encontraron usuarios para mostrar con los criterios especificados.",
            "info": "Mostrando página _PAGE_ de _PAGES_",
            "infoEmpty": "Sin usuarios para mostrar",
            "infoFiltered": "(filtrados de un total de _MAX_ usuarios)",
		    "emptyTable":     "Sin usuarios para mostrar",
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
		}})
		.on('dblclick', 'td', function(){dobleClick(this);})
		.on('mousedown', function(){return false;});
	
	CargarTablaUsuario(dt);
	
}

function CargarTablaUsuario(dt){
	$.getJSON("rest/abm/user")
	.done(function(info){
		dt.fnClearTable();
		if (info && info.length > 0) {	dt.fnAddData(info);
										$('.cant').text(info.length + ' USUARIOS');	}
	})
	.fail(function(){alert('Sucedió un error al momento de cargar los USUARIOS');});
	
}

//configuro botones de USUARIO
function seteoBotonesUsuario(){
	
	$('#btnNewUser').button({icons: { primary: 'ui-icon-circle-plus',
									  secondary: 'ui-icon-person'},
							 label: 'Nuevo Usuario'})
					.on('click', function(){openDialog('../ABM/NuevoUsuario.html', 'Nuevo Usuario');});
	$('#btnNewInstitution').button({icons: { primary: 'ui-icon-circle-plus',
											 secondary: 'ui-icon-home'},
									label: 'Nueva Institución'})
						   .on('click', function(){openDialog('../ABM/NuevaInstitucion.html', 'Nueva Institución');});
	$('#btnAssignInstitution').button({icons: { primary: 'ui-icon-link',
		 										secondary: 'ui-icon-home'},
		 							   label: 'Asignar Institución',
		 							   disabled: true})
		 					  .on('click', function(){openDialog('../ABM/AsignarInstitucion.html?new=false' +
		 							  								'&login=' + infodeUsuario()[0] +
		 							  								'&Fname=' + infodeUsuario()[1] + 
		 							  								'&Lname=' + infodeUsuario()[2] +
		 							  								'&Rname=' + infodeUsuario()[3],
		 							  								'Agregar/Quitar Institución');});
	
	$('#btnDeleteUser').button({icons: { primary: 'ui-icon-trash',
		  								 secondary: 'ui-icon-person'},
		  						label: 'Borrar Usuario',
		  						disabled: true})
		  				.on('click', function(){deleteUser();});
	
	$('#btnNewModalitie').button({icons: { primary: 'ui-icon-circle-plus',
		 								   secondary: 'ui-icon-tag'},
		 						  label: 'Nueva Modalidad'})
		 				 .on('click', function(){openDialog('../ABM/NuevaModalidad.html', 'Nueva Modalidad');});
}

function openDialog(url, title){
	
	var height = AltoVentana * (2/3);
	var width = AnchoVentana / 2.2;

	$('#dynamicModalDialogIframe').css('width', '99.7%');
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
		close: function(ev, ui) {$('#dynamicModalDialogIframe').attr('src', 'about:blank');}
	});
}


function dobleClick(cell){
	
	var posicion = tablaU.dataTable().fnGetPosition(cell);
	var fila = posicion[0];
	var columna = posicion[1];
	var data = tablaU.dataTable().fnGetData(fila);
	
	switch(columna){
	case 0: //changeLogin($(cell), data.login);
		break;
	case 1: changeFirstName($(cell), data.login, data.fname);
		break;
	case 2: changeLastName($(cell), data.login, data.lname);
		break;
	case 3: changeRole($(cell), [data.login, data.fname, data.lname], data.role);
		break;
	case 4: setBlocked($(cell), data.login, data.blocked);
		break;
	case 5: changeModalities($(cell), [data.login, data.fname, data.lname], data.mod);
		break;
	default: alert('Ocurrió un error, intente la operación nuevamente');
		break;
	}
}


function changeLogin(cell, value){
	
	
	cell.html('<input type="text">').find('input[type="text"]').val(value).focus()
		.on('keypress', function(){
			var tecla = event.which || event.keyCode;
			if(tecla == 13) {
				if(!$(this).val()) alert('Complete el campo');
				else {if(confirm('¿Confirma el cambio de login?'))
						UpdateUserData($(this).val(), value, 0);}
			}
			else return;
		})
		.on('blur', function(){cell.html('<b>'+value+'</b>');})
		.css('text-align','center');
}

function changeFirstName(cell, login, value){
	
	cell.html('<input type="text">').find('input[type="text"]').val(value).focus()
	.on('keypress', function(){
		var tecla = event.which || event.keyCode;
		if(tecla == 13) {
			if(!$(this).val()) alert('Complete el campo');
			else {if(confirm('¿Confirma el cambio de nombre?'))
					UpdateUserData($(this).val(), login, 1);}
		}
		else return;
	})
	.on('blur', function(){cell.html(value);})
	.css('text-align','center');
}

function changeLastName(cell, login, value){
	
	cell.html('<input type="text">').find('input[type="text"]').val(value).focus()
	.on('keypress', function(){
		var tecla = event.which || event.keyCode;
		if(tecla == 13) {
			if(!$(this).val()) alert('Complete el campo');
			else {if(confirm('¿Confirma el cambio de apellido?'))
					UpdateUserData($(this).val(), login, 2);}
		}
		else return;
	})
	.on('blur', function(){cell.html(value);})
	.css('text-align','center');
}



function changeRole(cell, user, value){
	
	/*var select = '<select id="cmbRol"></select>';
	tablaU.unbind('mousedown');
	
	$.getJSON("rest/abm/allroles")
	.done(function(data){
		if(data && data.length > 0){
			$.each(data, function(i , rol){
				
				if(rol.name == value)
				$('#cmbRol').append('<option value="'+rol.name+'" selected="selected">'+rol.name+'</option>');
				else
				$('#cmbRol').append('<option value="'+rol.name+'">'+rol.name+'</option>');
			});
		}
	})
	.fail(function(){alert('fail');});
	
	cell.html(select).find('select').focus()
	.on('blur', function(){cell.html(value);tablaU.on('mousedown', function(){return false;});})
	.on('change', function(){ if(confirm('¿Confirma el cambio de rol?'))
								UpdateUserData($(this).val(), login, 3);});*/
	
	var url = '../ABM/AsignarRol.html?login=' + user[0] + '&Fname=' + user[1] + '&Lname=' + user[2] + 
	'&role=' + value + '&new=false';
	
	openDialog(url, 'ASIGNAR ROL A USUARIO');
}




function setBlocked(cell, login, value){
	
	var bloqueo = 'bloqueo';
	cell.html('<input type="checkbox">').find('input[type="checkbox"]').prop('checked',!value).focus()
	.on('blur', function(){cell.html(value ? 'NO':'SÍ');})
	.on('click', function(){if($(this).prop('checked')) bloqueo = 'desbloqueo';
							if(confirm('¿Confirma el '+bloqueo+' de usuario?'))
								UpdateUserData($(this).prop('checked'), login, 4);});
}

function changeModalities(cell, user, value){
	
	var assignedMods = [];
	$.each(value, function(i, v){ if(v && v!='') assignedMods.push(v); });
	
	var url = '../ABM/AsignarModalidad.html?login=' + user[0] + '&Fname=' + user[1] + '&Lname=' + user[2] + 
							'&mods=' + assignedMods + '&new=false';
	openDialog(url, 'AGREGAR/QUITAR MODALIDADES');
}


function UpdateUserData(value, login, numCol){
	
	var dto = {
			login: login,
			value: value,
			numCol: numCol
	};
	
	$.ajax({
  	  type: "POST",
  	  url: 'rest/abm/update-user',
  	  data: dto,
  	  success: function() {CargarTablaUsuario(tablaU.dataTable());},
	  error: function(){alert('Ocurrió un error al momento de cargar los datos');}
  	});
}



function comparoAlturas(){
	
	if(tabs.height() > tabs.parent().height()) tabs.parent().css('overflow-y','scroll');
	else tabs.parent().css('overflow-y','none');
}



function getSelectedItems(tabla) {
	var oTT = TableTools.fnGetInstance(tabla);
    return oTT.fnGetSelectedData();								
}

function itemsSelectionChangedU(){
	
	var selectedUsers = getSelectedItems('tbUsuarios');
	
	if (selectedUsers.length <= 0) {
		$('#btnAssignInstitution').button('option', 'disabled', true);
		$('#btnDeleteUser').button('option', 'disabled', true);
    } else if (selectedUsers.length == 1) {
    	$('#btnAssignInstitution').button('option', 'disabled', false);
    	$('#btnDeleteUser').button('option', 'disabled', false);
    } else {
    	$('#btnAssignInstitution').button('option', 'disabled', true);
    	$('#btnDeleteUser').button('option', 'disabled', true);
    }
}

function infodeUsuario(){
	
	var selectedUsers = getSelectedItems('tbUsuarios'),
		info = [];
	
	if(selectedUsers.length <= 0) {alert('seleccione un usuario'); return;}
	
	$.each(selectedUsers, function(index, user) {
    	info[0] = user.login;
    	info[1] = user.fname;
    	info[2] = user.lname;
    	info[3] = user.role;
    });
	
	return info;
}

function deleteUser(){
	
	var selectedUser = getSelectedItems('tbUsuarios');			
    if (selectedUser.length > 0) {
    	$.each(selectedUser, function(index, user) {
    		//alert(user.login);
    		if(confirm('el usuario "'+user.login+'" se borrará')){
    		
    		$.post('rest/abm/deleteUser', 'login='+user.login)
    		.done(function(){alert("el usuario " + user.login + " se ha eliminado correctamente");})
    		.fail(function(){alert("ocurrió un error al intentar borrar el usuario");});
    		var interval = window.setInterval(function(){
    		CargarTablaUsuario(tablaU.dataTable());
    		$('#btnAssignInstitution').button('option', 'disabled', true);
    		$('#btnDeleteUser').button('option', 'disabled', true);
    		clearInterval(interval);
    		}, 1500);}
    	});
    }
}

/*****************************************************************************************************************/

//configuro la tabla de ESTUDIOS
function seteoTablaEstudio(){
	
	var columnas = [/*{ 	"title": "<input type='checkbox' id='chkAll'>", 
						"class": "left", "width": "15px", "data":"id" },*/
					
					{ 	"title": "ESTUDIO", "data": "desc", "defaultContent": "", 
						"class": "left"/*, "width": "20px"*/ },
					
					{ 	"title": "ID PACIENTE", "data": "pid", "defaultContent": "", 
						"class": "left", "width": "200px" },
					
					{ 	"title": "PACIENTE", "data": "pn", "defaultContent": "", 
						"class": "left" },
					
					{ 	"title": "FECHA", "data": "date", "defaultContent": "", 
						"class": "center", "width": "100px" },
					
					{ 	"title": "HORA", "data": "date", "defaultContent": "", 
						"class": "center", "width": "50px" },
					
					{ 	"title": "MOD", "data": "mod", "defaultContent": "", 
						"class": "center", "width": "50px" },
					
					{ 	"title": "INSTITUCION", "data": "c", "defaultContent": "", 
						"class": "center", "width": "200px" },
					
					{ 	"title": "IMG", "data": "ti", "defaultContent": "", 
						"class": "center", "width": "30px" },
					
					{ 	"title": "INFORMADO", "data": "rs", "defaultContent": "", 
						"class": "center", "width": "60px" },	
						
					{ 	"title": "VER", "data": "id", "defaultContent": "", 
						"class": "center", "width": "30px" },		
						
			  ];
	var columnasDef = [/*{	"targets": 0,
							"bSortable": false,
							"render": function ( data, type, row ) {
								
								return '<input type="checkbox" id="chk_'+ data +'" />';
							}
					   },*/
					   {	"targets": 3,
							"render": function ( data, type, row ) {
								
								return data.substr(0, 10);
							}
					   },
					   {	"targets": 4,
							"render": function ( data, type, row ) {
								
								return data.substr(10,17);
							}
					   },
					   {	"targets": 8,
							"render": function ( data, type, row ) {
								
								return data == 3 ? 'SÍ':'NO'; 
							}
					   },
					   {	"targets": 9,
							"bSortable": false,
							"render": function ( data, type, row ) {
								
								return "<a href='javascript:openOviyam(\""+ data +"\");'>WADO</a>";
							}
					   }
					  ];
	
	var dt = tablaE.dataTable({
		"data": [],
		"order": [[3,'desc']],
		"lengthMenu": [ 5, 10, 15, 20, 25, 30, 50, 75, 100 ],
		"pageLength": 10,
		"dom": 'l<"cantS">Trt<"clear">ip<"#divButtons">',
		"pagingType": "full_numbers",
		
		"tableTools": {
			"sRowSelect": "multi",
			"aButtons": [ { "sExtends":"select_all",
							"sButtonText":"Marcar Todos"}, 
						  { "sExtends": "select_none",
			                "sButtonText": "Desmarcar Todos"}	 
						],
			fnRowSelected: function(o) {
				itemsSelectionChangedE();
			},
			fnRowDeselected: function(o) {
				itemsSelectionChangedE();
			}											
		},
		
		"columns": columnas,					
		"columnDefs": columnasDef,
		
		"language": {
            "lengthMenu": "Mostrar _MENU_ estudios por página",
            "zeroRecords": "No se encontraron estudios para mostrar con los criterios especificados.",
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
		}})
		.on('mousedown', function(){return false;});
		
		$('.cantS').text('0 ESTUDIOS');
}


function seteoBotonesEstudio(){
	
	$('#btnSearch').button({icons: {"primary": "ui-icon-search"}})
				   .on('click', function(){ doTheSearch(); });
	
	$('#txtPatient').css('border-color', '#888');
	
	$('#divButtons').append('<button id="btnDelete">ELIMINAR</button>&nbsp;&nbsp;'
						+	'<button id="btnViewDeleted">Ver Eliminados</button>')
					.css('margin-top', '10px')
					.css('float', 'right')
					.css('margin-right', '30px');
	
	$('#btnDelete').button({icons:{'secondary': 'ui-icon-trash'},
							disabled: true})
				   .on('click', function(){ deleteStudies(); });
	
	$('#btnViewDeleted').button({icons:{'secondary': 'ui-icon-clipboard'}})
						.on('click', function(){openDeletedStudies();});
	
	$.getJSON("rest/user/info")
	  .done(function(info) {
		  	// Cargamos las modalidades.
	    	var modalitiesSelect = $("#cmbMod"); 
	    	modalitiesSelect.find('option').remove().end();			    		
	    	if (info.modalities && info.modalities.length > 0) {
		    	modalitiesSelect.append('<option value="">Todas</option>');				    						    			
		    	$.each(info.modalities, function(index, modality) {
		    		modalitiesSelect.append('<option value="' + modality.name + '">' + modality.name + '</option>');
		        });					    	
		    	modalitiesSelect.val('');
	    	}
	    	
	    	// Cargamos las instituciones.
	    	var institutionsSelect = $("#cmbInst"); 
	    	institutionsSelect.find('option').remove().end();	    		
	    	if (info.institutions.length > 0) {
	    		institutionsSelect.append('<option value="-1">Todas</option>');				    						    			
		    	$.each(info.institutions, function(index, i) {
		    		institutionsSelect.append('<option value="' + i.institution.id + '">' + i.institution.name + '</option>');
		        });					    	
		    	institutionsSelect.val('-1');
	    	}
	  })
	  .fail(function() {alert('no se pudieron cargar los datos');});
	
	$('#cmbDate').on('change', function(){ doTheSearch(); });
	$('#cmbMod').on('change', function(){ doTheSearch(); });
	$('#cmbInst').on('change', function(){ doTheSearch(); });
}

function doTheSearch(){
	
	$('input[name="study-date-type"]').val($('#cmbDate').val());
	$('input[name="patient-data-name"]').val($('#txtPatient').val());
	$('input[name="study-modality"]').val($('#cmbMod').val());
	$('input[name="study-diagnostic-center"]').val($('#cmbInst').val());
	
	$.getJSON('rest/studies?' + $('#study-search-form').serialize())
		.done(function(result){
			
			var dt = tablaE.dataTable();
			dt.fnClearTable();
			$('#chkAll').prop('checked', false);
			if (result && result.length > 0){
				dt.fnAddData(result);
				$('.cantS').text(result.length + ' ESTUDIOS');
			}
		
		}).fail(function(){alert('Ocurrió un error en la búsqueda de estudios');});
}

function getConfig(name) {
	var entry = $.grep(ddConfig, function(element, index){
	      return element.name == name;
	});
	
	if (entry && entry.length > 0)
		return entry[0].value;
	return null;
}

function openOviyam(studyID){

	var href = $( location ).attr( 'href' ).replace( 'http://' , '' );
	href = href.substring(0, href.indexOf(':'));
	url = getConfig('actions.study.simple-view.url').replace('${STUDYID}', studyID).replace('&seriesUID=${SERIEID}', '').replace('${HOST}', href) ;

	window.open(url, 'Visualización simple para estudio: ', 800, 600);
}

function itemsSelectionChangedE(){
	
	var selectedStudies = getSelectedItems('tbEstudios');
	
	if (selectedStudies.length > 0) {
		
		$('#btnDelete').button('option', 'disabled', false);
    } else{
    	
    	$('#btnDelete').button('option', 'disabled', true);
    }
}


function deleteStudies(){
	
	var selectedStudies = getSelectedItems('tbEstudios');
	
	if(selectedStudies.length == 0){
		$('#btnDelete').button('option', 'disabled', true);
		return;
		
	}else{
		
		var msj = 'Se eliminarán los estudios de:\n';
		for(var i=0; i<selectedStudies.length; i++){
			msj += '\n_ ' + selectedStudies[i].pn;
		}
		msj += '\n\nLos datos eliminados NO pueden recuperarse';
		
		if(confirm(msj)){
			
			$.each(selectedStudies, function(i, study){

				$.post('rest/abm/deleteStudy', 'studyUID='+study.id)
				.done(function(data){
				
				if(data = true)
					alert('El estudio del paciente '+study.pn+' se ha eliminado correctamente');
					
				else alert('No se pudo eliminar el estudio de '+study.pn);
					
				}).fail(function(){alert('Ocurrió un error al momento de eliminar');});
				
			});

			$('#tbEstudios').DataTable().rows('.selected').remove().draw();
			$('.cantS').text($('#tbEstudios').dataTable().fnGetData().length + ' ESTUDIOS');
		}
	}
	
}

function openDeletedStudies(){
	
	var height = AltoVentana * (2/3);
	var width = AnchoVentana - 20 ;

	$('#dynamicModalDialogIframe').css('width', '99.7%');
	$('#dynamicModalDialogIframe').css('height', '98.5%');

	$("#dynamicModalDialog").dialog({
		height: height,
		width: width,
		closeOnEscape: true,
		closeText: "Cerrar",
		modal: true,
		title: 'ESTUDIOS ELIMINADOS',
		open: function(ev, ui) {					        	
			$('#dynamicModalDialogIframe').attr('src', '../ABM/EstudiosEliminados.html');
		},
		close: function(ev, ui) {$('#dynamicModalDialogIframe').attr('src', 'about:blank');}
	});
}











