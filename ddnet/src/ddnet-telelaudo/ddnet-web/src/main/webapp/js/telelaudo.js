/**
 * 	Funciones de la página principal ddnet - telelaudo
 * 
 */

var AltoPantalla = screen.height;
var AnchoPantalla = screen.width;

$(document).ready(function(){
	
	$("#divBarraMenu").buttonset();
	
	AjustePantalla();

	$("#divCuerpoPagina").load("study-manager.html");
	
});


function AjustePantalla(){
	
	var AltoVentana = window.innerHeight;
	var AnchoVentana = document.body.clientWidth;	
	var check = $( '#study-manager' ).prop('checked') ? true : false;
	
	if( !check ) {
	
		var aux = 70 * 100;
		var conversion = aux / AltoVentana;
		var porcentaje = 100 - conversion;

		$("#divCuerpoPagina").css("height", porcentaje+"%");
		$( "#PiedePagina" ).css( "height" , "40px" );
	
		var margen = AltoVentana - 40;
		$( '#PiedePagina' ).css( 'top' , margen+'px');
		$( '#PiedePagina' ).css( 'width' , AnchoVentana+'px');
		$( '#pageFooter' ).css( 'margin-top' , '0px');
		
	}
	
	else {
		
		var aux = 165 * 100;
		var conversion = aux / AltoVentana;
		var porcentaje = 100 - conversion;

		$("#divCuerpoPagina").css("height", porcentaje+"%");
		$( "#PiedePagina" ).css( "height" , "115px" );
		
		var margen = AltoVentana - 130;
		$( '#PiedePagina' ).css( 'top' , margen+'px');
		$( '#PiedePagina' ).css( 'width' , AnchoVentana+'px');
		$( '#pageFooter' ).css( 'margin-top' , '90px');
		$( '#pageFooter' ).css( 'width' , AnchoVentana+'px');
		
	}
	
}

function CambioPagina(elem){
	
	var id = elem.id;
	$("#divCuerpoPagina").children().remove();
	$("#divCuerpoPagina").html('<div id="cargando"><h4><img src="../images/cargando.png" height="25px" width="25px" />' +
									'&nbsp;&nbsp;la p&aacute;gina se est&aacute; cargando...</h4></div>');
	$("#divCuerpoPagina").load(id+".html");
	AjustePantalla();
	
	if(!$( '#study-manager' ).prop('checked')) addTbFoot();
}


window.addEventListener( "resize" , function() { AjustePantalla(); } );

function addTbFoot() {
	
	var check = $( '#study-manager' ).prop('checked') ? true : false ;
	
	if( check ) {
		
		$("#tableFooter").remove();
		
		var tbFoot = '<div id="tableFooter"></div>' ;
		
		$( '#PiedePagina' ).append( tbFoot );
		
		var tablePag = "<div id='tablePagination' class='dataTables_paginate'></div>";
		
		$( '#tableFooter' ).append(tablePag);
		
		var buttons = 	"<div id='studies-found-actions-container'>" +
						"<select id='study-request-images-quality'>" +
							"<option value='lossy' selected='selected'>LOSSY</option>" +
							"<option value='loseless'>LOSSLESS</option>" +
						"</select>" +	
						'<button type="button" class="study-action-button" id="study-get-images" disabled="disabled">' +
						'Descargar im&aacute;genes</button>'	+
						"<button type='button' id='study-assign-to' class='study-action-button'>" +
							"Asignar a:</button>" +
						"<select id='study-assign-target'>Seleccione...</select>" +
						"</div> ";
		
		$( '#tableFooter' ).append(buttons);
		
		var tbTools = "<div id='tbPages'></div><div id='tbLength'></div>";
		
		$( '#tablePagination' ).append(tbTools);
		
		var pages = "<span id='tbInfo'>Sin estudios para mostrar</span> " +
					"P&aacute;ginas: <span id='pgFirst' class='pgChange'><<</span>" +
					"&nbsp;&nbsp;<span id='pgPrev' class='pgChange'><</span>" +
					"<span id='pgIndex'></span>" +
					"&nbsp;&nbsp;<span id='pgNext' class='pgChange'>></span>" +
					"&nbsp;&nbsp;<span id='pgLast' class='pgChange'>>></span>" ;
		
		$( '#tbPages' ).append(pages);
		
		var length = 	"Mostrar <select id='selLength'> " +
						"<option value='10' selected='selected'>10</option>" +
						"<option value='15'>15</option>" +
						"<option value='20'>20</option>" +
						"<option value='25'>25</option>" +
						"<option value='30'>30</option>" +
						"<option value='50'>50</option>" +
						"<option value='75'>75</option>" +
						"<option value='100'>100</option>" +
						"</select>" +
						" estudios por p&aacute;gina" ;
		
		$( '#tbLength' ).append(length);
		
		$('#tbPages').on('mousedown', function(){return false;});
		
	}
	
	else $( '#tableFooter' ).remove();
}

function changeP(elem){
	
	var num = $(elem);
	
	var page = elem.id.replace( 'p' , '' );
	
	var oTable = $('#studies-found').dataTable();
	
	oTable.fnPageChange( page - 1 );

	$('#pgIndex').children().removeClass('pageNumSelected');
	
	num.addClass('pageNumSelected');
	
	setPageNum();
	
}


function ChangePs() {
	
	var oTable = null;
	var table = $("#studies-found").DataTable();
	
	$( '#pgFirst' ).on( 'click' , function(){
		
		oTable = $('#studies-found').dataTable();
		oTable.fnPageChange( 'first' );
		setPageNum();
		CompletePages(table.page.info(), 1);
	});
	
	$( '#pgPrev' ).on( 'click' , function(){
		
		oTable = $('#studies-found').dataTable();
		oTable.fnPageChange( 'previous' );
		setPageNum();
		
		var info = table.page.info();
		var index = parseInt(info.page / 10) + 1;
		if(info.page > 0) CompletePages(info, index);
		
	});

	$( '#pgNext' ).on( 'click' , function(){
	
		oTable = $('#studies-found').dataTable();
		oTable.fnPageChange( 'next' );
		setPageNum();
		
		var info = table.page.info();
		var index = parseInt(info.page / 10) + 1;
		if(info.page < info.pages) CompletePages(info, index);
	});

	$( '#pgLast' ).on( 'click' , function(){
	
		oTable = $('#studies-found').dataTable();
		oTable.fnPageChange( 'last' );
		setPageNum();
		
		var info = table.page.info();
		if(info.pages % 10 > 0)
			CompletePages(info, parseInt(info.pages / 10) + 1);
		else
			CompletePages(info, parseInt(info.pages / 10));
		
	});
	
	$( '#selLength' ).on( 'change' , function(){
		
		//var table = $("#studies-found").DataTable();
		table.page.len( $(this).val() ).draw();
		CompletePages( table.page.info(), 1 );
		setPageNum();
	});
}

function setPageNum(){
	
	var table = $("#studies-found").DataTable();
	var pageInfo = table.page.info();
	$( '#tbInfo' ).html( 'Mostrando p&aacute;gina ' +(pageInfo.page+1)+ ' de ' + pageInfo.pages );
	$('#pgIndex').children().removeClass('pageNumSelected');
	$('#p'+(pageInfo.page+1)).addClass('pageNumSelected');
	$('input[type="checkbox"]').prop('checked', $('#chkAll').prop('checked'));
}

function setMedicoGuardia(){
	
	var url = 'doctor-on-call.html';
	var dialogHeight = AltoPantalla / 1.5;
	var dialogWidth = AnchoPantalla / 4;
	var hideEffect = { effect: "fade", duration: 400 };
	
		$("#dynamicModalDialog").dialog({
			height: dialogHeight,
			width: dialogWidth,
			closeOnEscape: true,
			closeText: "Cerrar",
			modal: true,
			title: "Médico de Guardia",
			hide: hideEffect,
			clickOutside: false,
			open: function(ev, ui) {					        				
				$('#dynamicModalDialogIframe').attr('src', url);
			}
		});
}


function alturas(){
	
	alert(
			'screen.height = ' + screen.height + '\n' +
			'screen.availHeight = ' + screen.availHeight + '\n' +
			'window.outerHeight = ' + window.outerHeight + '\n' +
			'window.innerHeight = ' + window.innerHeight + '\n' +
			'document.body.clientHeight = ' + document.body.clientHeight + '\n' +
			'$(window).height() = ' + $(window).height() + '\n' +
			'$(document).height() = ' + $(document).height() + '\n'
	);
}

function anchuras(){
	
	alert(
			'screen.width = ' + screen.width + '\n' +
			'screen.availWidth = ' + screen.availWidth + '\n' +
			'window.outerWidth = ' + window.outerWidth + '\n' +
			'window.innerWidth = ' + window.innerWidth + '\n' +
			'document.body.clientWidth = ' + document.body.clientWidth + '\n' +
			'$(window).width() = ' + $(window).width() + '\n' +
			'$(document).width() = ' + $(document).width() + '\n'
	);
}



