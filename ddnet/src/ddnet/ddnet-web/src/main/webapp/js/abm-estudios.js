var dt= null;

function do_the_search() {
	var buttons= $('div.study-list-buttons').find('button');
	$.ajax({type:'GET',dataType:'json',url:'rest/studies?'+$('form#studyFilter').serialize(),beforeSend:function(){$('div#ddnet-loading').show()}})
	.done(function(result) {
		TableTools.fnGetInstance('studyList').fnSelectNone();
	  	dt.fnClearTable();
	  	if (result && result.length>0){
  		dt.fnAddData(result);
  		$('div#studyListCant').html('Resultado: <b>'+result.length+' estudios</b>');
  		$(buttons[1]).button('option','disabled',(!userinfo.permissions.canGetStatistics));
	  	} else{$('div#studyListCant').html('Resultado: <b>0 estudios</b>');$(buttons[1]).button('option','disabled',true)}
	})
	.fail(function(){$alert('Ocurrió un error con su consulta de estudios.\nPor favor, reintente la operación realizada.')})
	.always(function(){$('div#ddnet-loading').hide(600)});
}
function simple_view(studyId,mod){
	if(!studyId||studyId==null)return; if(!mod||mod==null)return;
	var url = mod.indexOf('XA')==-1 ? getConfig('actions.study.dd-viewer.url'):getConfig('actions.study.simple-view.url');
	url= url.replace('${HOST}',location.hostname).replace('${STUDYID}',studyId).replace('&seriesUID=${SERIEID}','');
	window.open(url,'Visualización simple para estudio: '+studyId,800,800);
}

function preset_form(){
	var form= $('form#studyFilter'); $(form).on('submit',function(){return false});
	var between= $('input.between-date');
	set_as_datepicker(between);
	$(between).datepicker('setDate',new Date()).prop('disabled',true);
	$('select#dateSelector').on('change',function(){
		var value= parseInt($(this).val());
		$(between).prop('disabled',true);
		switch(value){
		case 1:$(between).datepicker('setDate','');
			break;
		case 2:$(between).datepicker('setDate',new Date());
			break;
		case 3:var yesterday=new Date();
			   yesterday.setDate(yesterday.getDate()-1);
			   $(between).datepicker('setDate',yesterday);
			break;
		case 4:var week=new Date();
		       week.setDate(week.getDate()-7);
		       $(between[0]).datepicker('setDate',week);
		       $(between[1]).datepicker('setDate',new Date());
			break;
		case 5:var month=new Date();
			   month.setDate(month.getDate()-30);
		       $(between[0]).datepicker('setDate',month);
		       $(between[1]).datepicker('setDate',new Date());
			break;
		case 6:
			$(between).prop('disabled',false);
			break;
		}
		//if(value<6) do_the_search();
	});
	$(form).find('button').button({label:'Buscar',icons:{primary:'ui-icon-search'}}).on('click',do_the_search);
	$.getJSON('rest/user/info').done(function(user){
		var modalities = $('select#modalities');
		modalities.find('option').remove().end();			    		
    	if (user.modalities && user.modalities.length > 0) {
    	modalities.append('<option value="">Todas</option>');
    	$.each(user.modalities, function(index, modality) {
    		modalities.append('<option value="'+modality.name.replace(/\\/g,'_')+'">'+modality.name+'</option>');
        });
    	//modalities.on('change',do_the_search);
    	modalities.val('');
    	}
	})
	.fail(function(){
		$('select#modalities').find('option').remove().end();
		$('select#modalities').append('<option value="">Error</option>');
	});
}
function preset_table(){
	$('table#studyList').on('click','img',function(){
		var studyId= $(this).attr('studyId');
		var mod= $(this).attr('mod');
		simple_view(studyId, mod);
	});
}

function delete_studies(){
	var selectedStudies= jqDataTable.getSelected('studyList');
	if(selectedStudies.length<=0)return false;
	//else if(selectedStudies.length>10) $alert('El sistema no permite Eliminar más de 10 estudios en la misma operación');
	else{
		openDialog('../ABM/EliminarEstudios.html','ELIMINAR ESTUDIOS',700,600);
		$('div#dynamicModalDialog').data('selectedStudies',selectedStudies);
	}
}
function get_the_statistics(){
	$.fileDownload('rest/estadisticas/excel?'+$('form#studyFilter').serialize())
	.done(function(){toastr['success']('Se ha descargado las Estadísticas','LISTO')})
	.fail(function(){toastr['error']('Falló la obtención de Estadísticas','ERROR')});
	return false;
}
function preset_buttons(){
	var buttons= $('div.study-list-buttons').find('button');
	$(buttons[0]).button({label:'Eliminar Estudios',icons:{primary:'ui-icon-trash'},disabled:true}).on('click',delete_studies);
	$(buttons[1]).button({label:'Ver Estadísticas',icons:{primary:'ui-icon-calculator'},disabled:true}).on('click',get_the_statistics);
}

function clear_report(elem){
	var studyId= $(elem).attr('studyid');
	
	var do_the_clean= function(){
		$.post('rest/studies/clear-report/'+studyId)
		.done(function(){
			toastr['success']('Informe Abierto','LISTO');
			do_the_search();
		})
		.fail(function(){
			toastr['error']('No se pudo abrir el Informe','ERROR');
		});	
	};
	$confirm('¿Confirma la apertura del Informe?', do_the_clean);
}

	window.addEventListener('resize',ajustes.abmEstudios);

	$(document).ready(function(){
		preset_form();
		dt= jqDataTable.studyABM();
		$('div#studyListCant').html('Resultado: <b>0 estudios</b>');
		preset_table();
		preset_buttons();
		ajustes.abmEstudios();
	});