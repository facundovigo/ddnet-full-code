var dt= null;

function set_datatable(){

	var table= $('table#userList');
	var columns= [
		{'title':'USUARIO','data':'login','class':'left','width':'70%'},
		{'title':'PRIVILEGIOS','data':'privilegio','class':'center','width':'30%'}
	];
	var columnDefs= [
	    {'targets':1,'render':function(data,type,row){return '<span class="ui-icon ui-icon-person black"></span>'}}
	];
	dt= $(table).dataTable({
		'data':[],
		'lengthMenu':[5,10,20,30],
		'pageLength':10,
		'pagingType':'full_numbers',
		'order':[[0,'desc']],
		'dom':'<"#userListCant">frtpl',
		'tableTools':{},
		'columns':columns,
		'columnDefs':columnDefs,
		'language':{
            'lengthMenu':'Mostrar _MENU_ usuarios por página',
            'zeroRecords':'Sin usuarios para mostrar',
            'info':'Mostrando página _PAGE_ de _PAGES_',
            'infoEmpty':'Sin usuarios para mostrar',
            'infoFiltered':'(filtrados de un total de _MAX_ usuarios)',
		    'emptyTable':'Sin usuarios para mostrar',
		    'infoPostFix':'',
		    'thousands':'.',
		    'loadingRecords':'Cargando...',
		    'processing':'Procesando...',
		    'search':'Buscar:',
		    'paginate':{
		    'first':'Primera',
		    'last':'Última',
		    'next':'Siguiente',
		    'previous':'Anterior'
		    },
		    'aria':{
		    'sortAscending':': Click para ordenar ascendentemente',
		    'sortDescending':': Click para ordenar descendentemente'
		    }
		}
	});
	$(table).on('click','td',function(){
		var pos= $(table).dataTable().fnGetPosition(this);
		var fila= pos[0], columna= pos[1];
		var user= $(table).dataTable().fnGetData(fila);
		if(columna==1){
			openDialog('user-privileges.html','Privilegios de Usuario',400,400);
			$('div#dynamicModalDialog').data('user',user);
		}
	});
}

function create_new_user(){
	var input= $('form#new-user').find('input:not(:checkbox)');
	var checked= $('form#new-user').find('input:checked');
	if(!$(input[0]).val()||!$(input[1]).val()||!$(input[2]).val()){ $alert('Hay campos obligatorios sin completar');return }
	if($(input[1]).val()!=$(input[2]).val()){$alert('Las contraseñas no coinciden');return }
	if(checked.length<=0){ $alert('Seleccione al menos un Privilegio');return }
	$.post('rest/user/new',$('form#new-user').serialize())
	.done(function(){
		$alert('Se ha creado el Usuario '+$(input[0]).val());
		$(input).val('');
		$('form#new-user').find('input:checkbox').prop('checked',false);
		charge_table();
	})
	.fail(function(obj,err,status){
		if(status=='No Aceptable') $alert('ya existe el Usuario '+$(input[0]).val());
		else $alert('ERROR. Falló el alta del nuevo Usuario');
	});
}
function preset_buttons(){
	var buttons= $('button');
	$(buttons).button({
		label:'Crear Usuario',
		icons:{primary:'ui-icon-circle-check',secondary:'ui-icon-person'}
	}).on('click',create_new_user);
}

function charge_table(){
	$.ajax({
		method:'GET',
		dataType:'json',
		url:'rest/user/all',
		beforeSend:function(){$('div#ddap-loading-message').show()}
	})
	.done(function(result){
		dt.fnClearTable();
		if(result && result.length>0){
			dt.fnAddData(result);
	  		$('div#userListCant').html('Resultado: <b>'+result.length+' Usuarios</b>');
	  } else $('div#userListCant').html('Resultado: <b>0 Usuarios</b>');
	})
	.fail(function(){$alert('ERROR. No se pudo cargar los Usuarios');})
	.always(function(){$('div#ddap-loading-message').hide()});
}

$(document).ready(function(){
	set_datatable();
	preset_buttons();
	charge_table();
});





