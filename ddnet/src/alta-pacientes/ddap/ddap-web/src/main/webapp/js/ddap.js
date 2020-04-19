
	$(document).ready(function(){$('#tabs').tabs({beforeLoad:function(){$('.ui-datepicker').remove()}});
	
		$.getJSON('rest/user')
		.done(function(user){ 
			$('#spn_user-info').html('Usuario: <b>' + user.login + '</b>');
			var li= $('div#tabs').find('li');
			if(!user.privilegio) window.location.href= '../authentication-servlet/logout';
			if(!user.privilegio.doPatient)$(li[0]).remove();
			if(!user.privilegio.doPatientPractice)$(li[1]).remove();
			if(!user.privilegio.doPractice)$(li[2]).remove();
			if(!user.privilegio.doEquipment)$(li[3]).remove();
			if(!user.privilegio.doLogger)$(li[4]).remove();
			if(!user.privilegio.doAbm)$(li[5]).remove();
		})
		.fail(function(){ window.location.href= '../authentication-servlet/logout' });
	
		$('.ui-tabs.ui-widget-content').height(window.innerHeight - 55);
		$('#spn_version').text('Versión 2016.08.16');
	
	});

	window.addEventListener('resize', function(){$('.ui-tabs.ui-widget-content').height(window.innerHeight - 55);});

	
	
	
	
	
	
	
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
	
	
	
	function $confirm(mensaje, callback){
		
		if(!mensaje || mensaje == null) return false;
		
		mensaje = mensaje.toString();
		
		var cuerpo = '';
		cuerpo += '<div id="dialog-confirm" title="Confirmar"><br>';
		cuerpo += mensaje.replace(/\n/g, '<br>');
		cuerpo += '</div>';
		
		$('body').append(cuerpo);
		
		$( "#dialog-confirm" ).dialog({
			resizable: false,
		    modal: true,
		    buttons: {
		        "OK": function() {
			        $( this ).dialog( "close" );
			        $( this ).remove();
		        	callback();
		        },
		        Cancelar: function() {
		        	$( this ).dialog( "close" );
		        	$( this ).remove();
		        }
		   }
		});
//		    <span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
	}

	
	function do_the_test(){
		
		$.ajax({
			method: 'GET',
			dataType: 'json',
			url: 'rest/patient/search/38277317',
			beforeSend: $wait
			
		}).done(function(data){
			$stop_wait();
			alert(data[0].docType);
		});
		
	}
	
	
	function set_as_datepicker(elem){
		
		var dayNames = ["Domingo", "Lunes", "Martes", "Mi&eacute;rcoles", "Jueves", "Viernes", "S&aacute;bado"]; 
		var dayNamesMin = ["DOM", "LUN", "MAR", "MIE", "JUE", "VIE", "SAB"];
		var monthNames = ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Setiembre", "Octubre", "Noviembre", "Diciembre"];
		var monthNamesShort = ['Ene','Feb','Mar','Abr','May','Jun', 'Jul','Ago','Sep', 'Oct','Nov','Dic'];
		        		
		elem.datepicker({ dateFormat: "dd/mm/yy", 
						  changeYear: true, 
						  yearRange: "-100:+0", 
						  changeMonth: true,
						  dayNames: dayNames, 
						  dayNamesMin: dayNamesMin, 
						  monthNames: monthNames,
						  monthNamesShort: monthNamesShort});
		
		$('.ui-datepicker.ui-widget').css('font-size', '0.9em');
	}
	
	