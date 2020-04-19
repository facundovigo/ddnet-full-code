/**
 * Funciones javascript de la página
 */

var key = null;
var validKey;
var userID = null;
var rDone = null;
var rTimes = null;
var rDate = null;
var prss = [];

$(document).ready(function(){	// Funciones al arranque
	
	$('body').children().hide();
	key = $.url().param('key');
	
	/**	¿Ingresó una clave?	**/
	if(key && key != null){
		
		$.get('restore-servlet/new', 'key='+key)
		.done(function(data){
			
		/**	¿La clave ingresada es correcta?	**/	
			if(data && data.length > 0){
				
			$.each(data, function(i, pr){
				
				/**	¿Ya se utilizó esta clave?	**/
				if(pr.done){window.location='login-form.html';}
				else{
				
				/**	¿Expiró la clave?	**/
				if(expiroFecha(pr.limitDate)) {window.location='login-form.html';}
				else{
				
				/**	¿Superó el límite de ingresos?	**/
				if(pr.times > 5) {window.location='login-form.html';}
				else{ $('#btnConfirm').on('click', function(){changeThePassword(pr.userID, pr.key);}); 
					  $('.textBox').on('keypress', function(){
					
						  	var tecla = event.which || event.keyCode;
						  	if(tecla == 13) changeThePassword(pr.userID, pr.key);
						  	else return;
					  });
				}}}
			});
		
			}else{window.location='login-form.html';}
		
		}).fail(function(){window.location='login-form.html';});
		
		$('img').on('click', function(){
			window.location='login-form.html';
		});
		
		$('#btnConfirm').button({label: "Confirmar",
								 icons: {primary: "ui-icon-circle-check"}});
		
		$('body').children().show();
		
		centrarObjetos();
		window.addEventListener( "resize" , function() { centrarObjetos(); } );
		
	} else{
		
		window.location='login-form.html';
	}
	
	
});

function centrarObjetos(){//función para centrar elementos
	
	var divAncho = $('.divPassword').width(),		//ancho del contenedor
		a = $('.textBox').width(),					//ancho textBox
		b = $('#btnConfirm').width();				//ancho botón
	
	$('label').css('margin-left', (divAncho - (150+a))/2 + 'px');		// aplicar margen al label
	$('#btnConfirm').css('margin-left', (divAncho - b)/2 + 'px');		// aplicar margen al botón
}

function expiroFecha(limitDate){
	
	var today = new Date();
	var year = today.getFullYear(),
		month = today.getMonth() + 1,
		date = today.getDate(),
		hour = today.getHours(),
		min = today.getMinutes(),
		sec = today.getSeconds();
	
	date = date<10 ? '0'+date : date;
	month = month<10 ? '0'+month : month;
	hour = hour<10 ? '0'+hour : hour;
	min = min<10 ? '0'+min : min;
	sec = sec<10 ? '0'+sec : sec;
	
	var day = year+''+month+''+date,
		time = hour+''+min+''+sec;
		lday = limitDate.substring(0,8),
		ltime = limitDate.substring(9,17).replace(/:/g, '');
		
	if(lday - day < 0)	return true;
	else if(lday - day == 0){
		if(ltime - time <= 0) return true;
		else return false;
	}
	
}

function changeThePassword(userID, key){
	
	var txt1 = $('#txtPassword'),
		txt2 = $('#txtcPassword');
	
	if(txt1.val() == '' || txt2.val() == '') 
		{	alert('complete todos los campos');
			return;	}
	
	if(txt1.val() != txt2.val())
		{	alert('las contraseñas no coinciden');
			return;	}
	
	$.post('restore-servlet/new', 'key=' +key+ '&userID=' +userID+ '&newpwd=' +txt1.val())
	
	.done(function(){alert('La contraseña se ha restaurado correctamente');
					 window.location="login-form.html"; })
					 
	.fail(function(){alert('Ocurrió un error al momento de cambiar la contraseña.\n'
						+	'Intente nuevamente o comuníquese con el administrador');
					 $('#txtPassword').val('');
					 $('#txtcPassword').val(''); });
}