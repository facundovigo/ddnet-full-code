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
					  monthNamesShort: monthNamesShort	});
	
	$('.ui-datepicker.ui-widget').css('font-size', '0.9em');
}