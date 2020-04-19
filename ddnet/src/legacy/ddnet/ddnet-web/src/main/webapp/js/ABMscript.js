/**
 *	Script para el ABM estudios-usuarios 
 */

//variables globales

var AltoVentana = window.innerHeight;
var AnchoVentana;
var tabs;
var tablaU = null;
var editor;

//funciones a realizar al momento de cargar la página
$(document).ready(function(){
	
	tabs = $('#tabs');
	tablaU = $('#tbUsuarios');
	AnchoVentana = document.body.clientWidth;
	$('#tabs').tabs();
	readUserInfo();
	seteoTablaUsuario();
	seteoBotonesUsuario();
	$('.dataTables_length').css('float','left');
	comparoAlturas();
});

window.addEventListener( "resize" , function() { comparoAlturas(); } );

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
						"class": "center"/*, "width": "100px"*/ }
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
				itemsSelectionChanged();
			},
			fnRowDeselected: function(o) {
				itemsSelectionChanged();
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
}

function openDialog(url, title){
	
	var iframe = '<div id="dynamicModalDialog">' +
					'<iframe id="dynamicModalDialogIframe" src="" ></iframe>' +		
				 '</div>';

	$('body').append(iframe);
	
	var height = 600;
	var width = 550;

	$('#dynamicModalDialogIframe').css('width', '99%');
	$('#dynamicModalDialogIframe').css('height', '98%');

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


function dobleClick(cell){
	
	var posicion = tablaU.dataTable().fnGetPosition(cell);
	var fila = posicion[0];
	var columna = posicion[1];
	var data = tablaU.dataTable().fnGetData(fila);
	
	switch(columna){
	case 0: changeLogin($(cell), data.login);
		break;
	case 1: changeFirstName($(cell), data.login, data.fname);
		break;
	case 2: changeLastName($(cell), data.login, data.lname);
		break;
	case 3: changeRole($(cell), data.login, data.role);
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



function changeRole(cell, login, value){
	
	var select = '<select id="cmbRol"></select>';
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
								UpdateUserData($(this).val(), login, 3);});
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

function itemsSelectionChanged(){
	
	var selectedUsers = getSelectedItems('tbUsuarios');
	
	if (selectedUsers.length <= 0) {
		$('#btnAssignInstitution').button('option', 'disabled', true);
    } else if (selectedUsers.length == 1) {
    	$('#btnAssignInstitution').button('option', 'disabled', false);
    } else {
    	$('#btnAssignInstitution').button('option', 'disabled', true);
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

function readUserInfo() {
	$.getJSON("rest/user/info")
	  .done(function(info) {
	    	$('#userinfo').text(info.login + ' [' + info.lastName + ', ' + info.firstName +  ']');
	  })
	  .fail(function() { window.location.href = '../authentication-servlet/logout'; });
	}