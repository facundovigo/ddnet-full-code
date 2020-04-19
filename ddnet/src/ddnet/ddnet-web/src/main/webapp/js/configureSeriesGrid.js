
var columns = [
				{ "title": "FECHA", "data": "date", "class": "left", "width": "110px" },
				{ "title": "HORA", "data": "time", "class": "left", "width": "90px" },
				{ "title": "DESCRIPCION DE LA SERIE", "data": "desc", "class": "left", "width": "350px" },
				{ "title": "IMAGENES", "data": "imgs", "class": "left", "width": "20px" }
			  
			  ];

var columnDefs = [];


function init_table( table, studyID, selec ){
	
	var dt = table.dataTable({
		
			"data": [],
			"lengthMenu": [ 10, 15, 20, 25, 30, 50, 75, 100, 1000 ],
			"pageLength": 10,
			"dom": 'rtT<"clear">p',
			"tableTools": {
				"sRowSelect": selec,
				"aButtons": [],
				fnRowSelected: function(o) { seriesSelectionChanged(); },
				fnRowDeselected: function(o) { seriesSelectionChanged(); }
			},
			"columns": columns,
			"columnDefs": columnDefs,
			"language": {
	            "lengthMenu": "Mostrar _MENU_ series por página",
	            "zeroRecords": "Sin series para mostrar",
	            "info": "Mostrando página _PAGE_ de _PAGES_",
	            "infoEmpty": "Sin series para mostrar",
	            "infoFiltered": "(filtrados de un total de _MAX_ series)",
			    "emptyTable":     "Sin series para mostrar",
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
	
	draw_table(dt, studyID);
	click_table(table);
}



function clean_table( table ){
	var dt = table.dataTable();
	dt.fnClearTable();
}

function draw_table( dt, studyID ){
	
	$.getJSON('rest/studies/currentStudy/series/'+ studyID)
	.done(function(data){
		dt.api().clear();
	  	if (data && data.length > 0) dt.fnAddData(data);
		
	}).fail(function(){
		$('.tbdiv').children().remove();
		$('.tbdiv').append('ERROR cargando las series<br><br><br>');
	});
}

function reinitialize_table( table, studyID, selec ){
	table.DataTable().destroy();
	init_table(table, studyID, selec);
}

function getSelectedSeries() {
	var oTT = TableTools.fnGetInstance('tb_study-series');				
    return oTT.fnGetSelectedData();								
}

function click_table( table ){
	table.on( 'click', 'tr', function () {
		
        if ( $(this).hasClass('selected') ) {
            $(this).removeClass('selected');
        }
        else {
        	seriesTable.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
    } );
}

function desmarcar_todo(){
	var oTT = TableTools.fnGetInstance('tb_study-series');
	if(oTT != null) oTT.fnSelectNone();
	else return;
}

function seriesSelectionChanged() {
	
	$('.study-series-buttons button').button('option', 'disabled', true);
	$('.study-series-buttons select').prop('disabled', true);
	
	var selectedSeries = getSelectedSeries();
    if (selectedSeries.length <= 0) {
    	return;
    	
    } else if (selectedSeries.length == 1) {
    	$('.study-series-buttons button').button('option', 'disabled', false);
    	$('.study-series-buttons select').prop('disabled', false);
    	
    } else {
    	$('#btn_download').button('option', 'disabled', false);
    	$('#sel_download-type').prop('disabled', false);
    }
}