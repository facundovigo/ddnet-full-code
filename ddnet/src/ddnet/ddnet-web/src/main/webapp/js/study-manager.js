var dt = null , userinfo = null, disconnectionNotified = false, singleConnectionAllowedNotified = false;

function toggle_filter(){
	$('div#filterToggle').on('click',function(){
	var filter=$('div.form-holder');var arrowUp='ui-icon-circle-triangle-n',arrowDown='ui-icon-circle-triangle-s';
	if(filter.is(':visible')){
		filter.slideUp(700, function(){
			$('.chrome div.dataTables_scrollBody').height($('div#tabPrincipal .ui-widget-content').height()-200);
			$('.gecko div.dataTables_scrollBody').height($('div#tabPrincipal .ui-widget-content').height()-205);
		});
		$(this).find('span').removeClass(arrowUp).addClass(arrowDown);
	}
	else{filter.slideDown(700);
		$(this).find('span').removeClass(arrowDown).addClass(arrowUp);
		$('.chrome div.dataTables_scrollBody').height($('div#tabPrincipal .ui-widget-content').height()-370);
		$('.gecko div.dataTables_scrollBody').height($('div#tabPrincipal .ui-widget-content').height()-380)}})
}
function preset_form(){
	$('form#studySearchFilter').on('submit',function(){return false});
	var between = $('input.between-date-text');
	set_as_datepicker(between);
	$(between).datepicker('setDate',new Date()).prop('disabled',true);
	$('input[type="radio"]').on('click',function(){
		$('label.study-date-type').removeClass('selected');$(this).closest('label').addClass('selected');
		var value = parseInt($(this).val());
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
		if(value<6) do_the_search();
	});
	$('span.filter-type-selector').on('click',function(){
		$('span.filter-type-selector').removeClass('current');
		$(this).addClass('current');
		$(this).attr('op')=='2'? $('div.advanced-filter').show():(
			$('div.advanced-filter').find('input').prop('checked',false),
			$('span#formInstitutions-button').length>0?$('select#formInstitutions').val('-1').selectmenu('refresh'):undefined,
			$('span#formUsers-button').length>0?$('select#formUsers').val('').selectmenu('refresh'):undefined,
			$('span#ui-id-1-button').length>0?$('select#ui-id-1').val('0').selectmenu('refresh'):undefined,
			$('div.advanced-filter').hide()
		);
	});
	$('span[op="1"]').click();
	$('td.filter-align-right').find('select').selectmenu();
	
	$('button#doTheSearch').button({
		label:'Buscar',
		icons:{primary:'ui-icon-search'}
	}).on('click',do_the_search);
	$('button#getNewStudies').button({
		label:'Ver Nuevos',
		icons:{primary:'ui-icon-plus'}
	}).on('click',get_the_new_studies);
	$('button#cleanTheForm').button({
		label:'Limpiar',
		icons:{primary:'ui-icon-refresh'}
	}).on('click',clean_the_filter);
	var cbxs=$('td.filter-align-right').find('input');
	$(cbxs[0]).on('click',function(){$(cbxs[2]).prop('checked',false);$(cbxs[4]).prop('checked',false)});
	$(cbxs[1]).on('click',function(){$(cbxs[3]).prop('checked',false)});
	$(cbxs[2]).on('click',function(){$(cbxs[0]).prop('checked',false);$(cbxs[4]).prop('checked',false)});
	$(cbxs[3]).on('click',function(){$(cbxs[1]).prop('checked',false)});
	$(cbxs[4]).on('click',function(){$(cbxs[0]).prop('checked',false);$(cbxs[2]).prop('checked',false)});
	

	setTimeout(ddUserAgent.announceUser, 100);
	
	var modalities = $('select#formModalities');
	modalities.find('option').remove().end();
	if (userinfo.modalities && userinfo.modalities.length > 0) {
    	$.each(userinfo.modalities, function(index, modality) {
    		modalities.append('<option value="'+modality.name.replace(/\\/g,'_')+'">'+modality.name+'</option>');
        });
    	modalities.val('');
	}
	
	$(modalities).multiselect({
		checkAllText:'Todas',
		uncheckAllText:'Ninguna',
		noneSelectedText:'Seleccione',
		selectedText:'# Mods.'
	});
	$(modalities).multiselect('checkAll');
	$('button#formModalities_ms').find('span.ui-icon').addClass('black');
	
	var institutions = $("select#formInstitutions"); 
	institutions.find('option').remove().end();	    		
	if (userinfo.institutions.length > 0) {
		institutions.append('<option value="-1">Todos</option>');				    						    			
    	$.each(userinfo.institutions, function(index, i) {
    		institutions.append('<option value="'+ i.institution.id+'">'+i.institution.name+'</option>');
        });
	}
	institutions.selectmenu({change:do_the_search});
	institutions.val('-1').selectmenu('refresh');

	if(!userinfo.permissions.canViewAllStudies){
		var radio= $('form#studySearchFilter').find('input[type="radio"]');
		$(radio[4]).click();
		do_the_search();
	}
	
	
	$.getJSON('rest/user/medicos').done(function(users){
		var userStudy=$('select#formUsers');
		userStudy.find('option').remove().end();
		userStudy.append('<option value="">Todos</option>');
		var physiciansSelect=$("select#studyAssignTarget");
    	physiciansSelect.find('option').remove().end();
		physiciansSelect.append('<option value="">Seleccione...</option>');
		if(users&&users.length>0){
			$.each(users, function(i, dr) {
    			if(dr.perfil!=null && dr.perfil.fancyName!=null)
	    		userStudy.append('<option value="'+dr.perfil.fancyName+'">'+dr.login+" ("+dr.perfil.fancyName+")"+'</option>');
    			else userStudy.append('<option value="'+dr.login+'">'+dr.login+" ("+dr.firstName+" "+dr.lastName+")"+'</option>');
    			physiciansSelect.append('<option value="'+dr.login+'">'+dr.firstName+' '+dr.lastName+'</option>');
	        });
		}
		userStudy.selectmenu({change:do_the_search});
		userStudy.val('').selectmenu('refresh');
	}).fail(function(){
		$('select#formUsers').find('option').remove().end();
		$('select#formUsers').append('<option value="">Error</option>');
	});
	$('form#studySearchFilter').on('keydown','input[type="text"]',function(event){
	    if (event.which == 13){event.preventDefault();do_the_search();}
	});
}
function init_UserAgent(){
	ddUserAgent.initialize({
		'master': true,
		'onConnected': function(){
	    	disconnectionNotified = false;
	    	singleConnectionAllowedNotified = false;
	    	if(userinfo) ddUserAgent.announceUser();
	    	try{ $('#dd-useragent-status-text').text('Conectado').attr('href','javascript:ddUserAgent.showUserAgent();')} catch(ignore){}
	    	toastr['success']('UserAgent conectado','DDNET');
		},
		'onDisconnected': function(reason){
	    	try { $('#dd-useragent-status-text').text('Desconectado').attr('href','javascript: ddUserAgent.startUserAgent();')} catch(ignore){}
	    	if (reason == 4500) {
	    		if(!singleConnectionAllowedNotified){ singleConnectionAllowedNotified=true;
	    			toastr['error']('Sólo una instancia de la aplicación web DDNET puede conectarse con UserAgent','DDNET');		
	    		}
	    	} else { if(!disconnectionNotified){ toastr['warning']('UserAgent desconectado','DDNET'); disconnectionNotified= true}}
		}
	});
}
function do_the_search() {
	
	var mods= $('select#formModalities').val();
	if(!mods||mods==null) $('input[name="study-modality"]').val('');
	else $('input[name="study-modality"]').val(mods);
	
	$.ajax({type:'GET',dataType:'json',url:'rest/studies?'+$('form#studySearchFilter').serialize(),beforeSend:function(){$('div#ddnet-loading').show()}})
	.done(function(result) {
		var inf=0,pinf=0;
	  	dt.fnClearTable();
	  	
	  	if (result && result.length>0){
	  		dt.fnAddData(result);
	  		if($('div.form-holder').is(':visible'))$('div#filterToggle').click();
	  		$.each(result,function(i,std){
	  			if(std.rs==3)inf++;
	  			if(std.rs==2)pinf++;
	  		});
	  		$('div#studyListInfo').html('Resultado: <b>'+result.length+' estudios</b> [ '+inf+' Informados, '+pinf+' Preinformados ]');
  		
	  	} else {$('div#studyListInfo').html('Resultado: <b>0 estudios</b>');}
	})
	.fail(function(){$alert('Ocurrió un error con su consulta de estudios.\nPor favor, reintente la operación realizada.')})
	.always(function(){$('div#ddnet-loading').hide(600)});
}
function clean_the_filter(){
	$('form#studySearchFilter').find('input[type="text"]').val('');
	$('form#studySearchFilter').find('input[type="checkbox"]').prop('checked',false);
	$('form#studySearchFilter').find('input[type="radio"]').prop('checked',false).closest('label').removeClass('selected');
	$('input#dateToday').prop('checked',true).closest('label').addClass('selected');
	$('input.between-date-text').datepicker('setDate', new Date()).prop('disabled',true);
	$('select#formModalities').val('');
	$('span#formInstitutions-button').length>0?$('select#formInstitutions').val('-1').selectmenu('refresh'):undefined;
	$('span#formUsers-button').length>0?$('select#formUsers').val('').selectmenu('refresh'):undefined;
	$('span#ui-id-1-button').length>0?$('select#ui-id-1').val('0').selectmenu('refresh'):undefined;
}
function preset_actions_buttons(){
	var buttons=$('div.study-actions-buttons').find('button');
	$(buttons[0]).button({label:'Descargar Imgs',icons:{primary:'ui-icon-arrowthickstop-1-s'},disabled:true}).on('click',get_study_images);
	$(buttons[1]).button({label:'Visualización',icons:{primary:'ui-icon-zoomout'},disabled:true}).on('click',simple_view);
	$(buttons[2]).button({label:'Visual. Avanzada',icons:{primary:'ui-icon-zoomin'},disabled:true}).on('click',advanced_view);
	$(buttons[3]).button({label:'Asignar a',icons:{primary:'ui-icon-person'},disabled:true}).on('click',assign_study_to);
	$('div.study-actions-buttons').find('select').prop('disabled',true);
}
function get_the_new_studies(){
	clean_the_filter();
	$.ajax({type:'GET',dataType:'json',url:'rest/studies/find-new',beforeSend:function(){$('div#ddnet-loading').show()}})
	.done(function(result){
	  	dt.fnClearTable();
	  	if($('div.form-holder').is(':visible'))$('div#filterToggle').click();
	  	if (result && result.length>0){
  		dt.fnAddData(result);
  		$('div#studyListInfo').html('Resultado: <b>'+result.length+' estudios nuevos</b>');
	  	} else {$('div#studyListInfo').html('Resultado: <b>0 estudios</b>');}
	})
	.fail(function(){$alert('ERROR al traer los nuevos estudios.\nPor favor, reintente la operación realizada.')})
	.always(function(){$('div#ddnet-loading').hide(600)});
}
function simple_view(){
	
	var selectedStudies = jqDataTable.getSelected('study-list');
	if(selectedStudies.length!=1)return false;
	var studyId = selectedStudies[0].id;
	
	var ddViewerUrl= getConfig('actions.study.dd-viewer.url').replace('${HOST}',location.hostname).replace('${STUDYID}',studyId);
	var oviyamUrl= getConfig('actions.study.simple-view.url').replace('${HOST}',location.hostname).replace('${STUDYID}',studyId).replace('&seriesUID=${SERIEID}','');
	
	var cuerpo='';
	cuerpo+= '<div id="visualDialog" title="Visualizar" align="center"> <br> '
		  +	 '<img class="visualization" src="../images/oviyam.png" onclick="visualization(\''+oviyamUrl+'\',\''+studyId+'\')" /> &nbsp;&nbsp; '
		  +  '<img class="visualization" src="../images/dd-viewer.png" onclick="visualization(\''+ddViewerUrl+'\',\''+studyId+'\')" /> <br> '
		  +  '<span class="simpleView">Simple</span><span class="intermedView">Intermedia</span></div>';
	
	$('body').append(cuerpo);
	$('#visualDialog').dialog({
		modal:true,
		close:function(ev,ui){$(this).remove()}
		//buttons:{Cancelar:function(){$(this).dialog('close');$(this).remove();}}
	});
}
function visualization(url, studyId){
	window.open(url,'Visualización simple para estudio: '+studyId,800,800);
	$('#visualDialog').dialog('close');
}

function advanced_view(){
	var selectedStudies = jqDataTable.getSelected('study-list');
	if(selectedStudies.length!=1)return false;
	var studyId = selectedStudies[0].id;
	var url= getConfig('actions.study.advanced-view.url').replace('${HOST}',location.hostname).replace('${STUDYID}',studyId).replace('&seriesUID=${SERIEID}','');
	window.open(url,'Visualización avanzada para estudio: '+studyId,800,800);
}
function assign_study_to(){
	var selectedStudies = jqDataTable.getSelected('study-list');
	if(selectedStudies.length<=0)return false;
	var selectedPhysician = $('select#studyAssignTarget').val();
	if(!selectedPhysician||selectedPhysician==''){$alert('Seleccione el Usuario para asignar');return}
	var dto= {physicianCode:selectedPhysician,studiesIDs:[]}; var assigned= 0;
	$.each(selectedStudies,function(i,study){if(!study.dr=='')assigned++;dto.studiesIDs.push(study.id)});
	if(assigned>0){$alert('No se puede realizar la asignación porque al menos un estudio ya está asignado');return}
	$.ajax({type:'POST',url:'rest/study/assignments',data:dto,success:function(){toastr['success']('estudios asignados al usuario: '+selectedPhysician,'LISTO!');do_the_search()}});
}
function get_study_images(){
	var selectedStudies = jqDataTable.getSelected('study-list');
	if(selectedStudies.length<=0)return false;
	var quality = $('select#study-request-images-quality').val();
	if(quality=='dicomdir') get_dicomdir();
	else{ddUserAgent.isUserAgentActive()? get_selected_study_images():ddUserAgent.startUserAgent(get_selected_study_images(),warnUserAgentNotStarted());}
}
function get_selected_study_images(){
	var selectedStudies = jqDataTable.getSelected('study-list');
	if(selectedStudies.length <= 0)return;
	var quality = $('select#study-request-images-quality').val();
	$.each(selectedStudies,function(i,study){getStudyImagesFor(study,quality)});
}
function get_dicomdir(){
	var selectedStudies= jqDataTable.getSelected('study-list');
	if(selectedStudies.length<=0)return;
	$.each(selectedStudies,function(i,study){
		var url= 'rest/studies/'+study.id+'/media?quality=0&options=nonexec';
		$.fileDownload(url);
	});
}




	window.addEventListener('resize',ajustes.study_manager);

	$(document).ready(function(){
		$.ajax({method:'GET',dataType:'json',url:'rest/user/info',async:false})
		.done(function(user){ userinfo=user });
		init_UserAgent();
		toggle_filter();
		preset_form();
		preset_actions_buttons();
		dt = jqDataTable.studyManager();
		$('div#studyListInfo').html('Resultado: <b>0 estudios</b>');
		ajustes.study_manager();
		if(!userinfo.permissions.canUseAgent){
			$('select#study-request-images-quality').find('option[value="lossy"]').remove();
			$('select#study-request-images-quality').find('option[value="loseless"]').remove();
		}
		if(!userinfo.permissions.canDownloadDicomdir)
			$('select#study-request-images-quality').find('option[value="dicomdir"]').remove();
	});
	