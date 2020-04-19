var altoContainer = ( screen.height - 140 );
var anchoContainer = ( screen.width - 215 );

$( document ).ready(function(){	
	
	$("#menu").menu({items: "> :not(.ui-widget-header)"});
	AjustePantalla();
	FechaDelDia();
});

function AjustePantalla(){

	$("#Container").css("width",anchoContainer);
	if((altoContainer)<477){$("#bFoot").css("top","540");}
}

function FechaDelDia(){

	var meses = new Array	("Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre");
	var diasSemana = new Array("Domingo","Lunes","Martes","Miércoles","Jueves","Viernes","Sábado");
	var f = new Date(); 
	document.getElementById("bFecha").innerHTML = diasSemana[f.getDay()] + " " + f.getDate() + " de " + meses[f.getMonth()] + " de " + f.getFullYear();
}

function CargarOpcion(op){

	$("#Container").children().remove();
	var id = op.id.replace('op_','');	
	//alert(id);
	$("#Container").load(id+".html");
}

