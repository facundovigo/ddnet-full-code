/**
 * Funciones javascript de la página
 */

var limite;

$(document).ready(function(){	// Funciones al arranque
		
	setCookie();
	
		$('#btnRecuperar').button({								// configurar botón con jQueryUI
					label: 'Enviar',							// qué dice el botón
					icons: {primary: 'ui-icon-circle-check'}	// ícono del botón (CHECK)
								 })	
	
		// acciones al hacer click en el botón
					  .on('click', function(){
						  var check  = $('input[type="radio"]:checked');	// opción seleccionada
						  var op = check.val();
							  
						  if(check.length == 0){ alert('Seleccione una opción'); 
						  						 return;}	// No toma en cuenta si no hay opción seleccionada
						  
						  if($('#txtDato').val() == ''){ alert('No hay texto ingresado'); 
	  						 							 return;}	// No toma en cuenta si no hay dato
						  
						  DoTheRestore($('#txtDato').val(), op);	// restaurar contraseña
					  });
		
		centrarObjetos();	// centrar en la página los elementos
		
		// acciones al momento de redimensionar la ventana
		window.addEventListener( "resize" , function() { centrarObjetos(); } );
		
		// acciones al hacer click sobre cada radioButton
		$('#radLogin').on('click', function(){
			
			if($(this).prop('checked'))		// se ingresa el siguiente texto si fue marcado
				$('#lblInfo').text("Ingrese su nombre de usuario. Si está registrado en el sistema"
								+	" se le enviará un correo a la dirección que tenga asignada.");
						});
		$('#radMail').on('click', function(){
			
			if($(this).prop('checked'))		// se ingresa el siguiente texto si fue marcado
				$('#lblInfo').text("Ingrese su dirección de correo. Si está registrada en el "
							    +  "sistema se le enviará un e-mail a esta misma dirección.");
						});
		
		$('img').on('click', function(){
			window.location='login-form.html';
		});
});
	
function centrarObjetos(){	//función para centrar elementos
		
		var divAncho = $('.divPassword').width(),		//ancho del contenedor
			a = $('label[for="radLogin"]').width() + 10,//ancho etiqueta radioButton
			b = $('#txtDato').width(),					//ancho textBox
			c = $('#btnRecuperar').width();				//ancho botón
		
		$('input[type="radio"]').css('margin-left', (divAncho - a)/2 + 'px');	// aplicar margen a los radioButton
		$('#txtDato').css('margin-left', (divAncho - (b+c))/2 + 'px');			// aplicar margen al textBox
		$('#lblCant').css('margin-left', (divAncho - (b+c))/2 + 'px');			// aplicar margen al label
}

function DoTheRestore(val, op) {
	
	var host = getHost();
	
	$.post('authentication-servlet/restore', 'dato='+ val +'&op='+ op + '&host=' + host)
	  .done(function(data){
	  if(data && data.length > 0){
				
		var str0 = data.substring(data.indexOf('=')+1,data.indexOf(';'));		// leer los valores retornados
		var str1 = data.substring(data.lastIndexOf('=')+1,data.length);
		var flag = parseInt(str0);												// flag de dato válido
		var hecho = str1.startsWith('true');									// flag de operación correcta
				
			if(flag == 1){ 														// si el dato es correcto
				if(hecho){														// y el pedido fue exitoso
					alert('Su pedido se ha procesado correctamente.\n' 			// muestra el correspondiente mensaje
						+ 'En unos minutos se le enviará un e-mail\n'
						+ 'para que pueda restaurar su contraseña.\n'
						+ 'Si no llega el correo intente nuevamente\n'
						+ 'o póngase en contacto con el administrador');
					
					location.href='login-form.html';							// retorna al login
				
				} else{															// operación fallida
					if(op == 1){
						alert('La operación ha fallado, posiblemente\n'			// mensaje correspondiente al
							+ 'porque su usuario existe pero no tiene\n'		// pedido por usuario
							+ 'una dirección de correo asignada.\n'
							+ 'Intente de nuevo el pedido o póngase\n'
							+ 'en contacto con el administrador');
						
						location.reload();						
						
					} else{														
						alert('La operación ha fallado.\n'						// mensaje correspondiente al
							+ 'Inténtelo nuevamente o comuníquese\n'			// pedido por e-mail
							+ 'con el administrador.');
						
						location.reload();
					}
				}
			}
			else {																// si el dato no es válido
				
				alert('El dato ingresado no es válido');						// mensaje correspondiente
				
				var expires = new Date();
				var time = expires.getTime();
				time += 24*60*60*1000;
				expires.setTime(time);
				document.cookie = "CANT_MAX="+(limite-1) 
								+ ";expires="+expires.toGMTString();
				
				location.reload();
			}
		}
	  })
	  .fail(function(e0,e1,e2){alert(e2);});									// mensaje de error
}

function getHost() {
	
	var host = $( location ).attr( 'href' ).replace( 'http://' , '' );			// retornar el host donde
																		
	return host.substr(0, host.indexOf(':'));									// se realizó el pedido
}




function setCookie(){
	
	var cookies = document.cookie.split(';');
	var thereisCookie = false;
	var aux, i;
	
	for(i = 0; i < cookies.length; i++){
		
		aux = cookies[i].substr(0,cookies[i].indexOf('='));
		if(aux.endsWith('CANT_MAX')) thereisCookie = true;
	}	
	if(!thereisCookie) {
		
		var expires = new Date();
		var time = expires.getTime();
		time += 24*60*60*1000;
		expires.setTime(time);
		document.cookie = "CANT_MAX=3;expires="+expires.toGMTString();
	}
	
	cookies = document.cookie.split(';');
	
	for(i = 0; i < cookies.length; i++){
		
		aux = cookies[i].substr(0,cookies[i].indexOf('='));
		if(aux.endsWith('CANT_MAX')){
			
			var value = cookies[i].substr(cookies[i].indexOf('=')+1,cookies[i].length);
		
			limite = value;
			
			if(value > 0){
				
				$('.divPassword :input').attr( 'disabled' , false );
				$('#lblCant').text('Le queda(n) '+ value +' intento(s)');
			
			} else{

				$('.divPassword :input').attr( 'disabled' , true );
				$('#lblCant').text('Se ha quedado sin intentos. Vuelva a intentar más tarde');
			}
		}
	}
	
}



