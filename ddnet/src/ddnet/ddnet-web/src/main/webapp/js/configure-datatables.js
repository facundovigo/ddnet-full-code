



var jqDataTable = {
	getSelected:function(t){var oTT=TableTools.fnGetInstance(t);return oTT.fnGetSelectedData()},
	studyManager: function(){
		var dt = null;
		var table = $('table#study-list');
		var lengthMenu = [10,15,20,25,30,50,75,100];
		var order = [[12,'desc'],[1,'desc'],[9,'desc']];
		var dom = '<"#studyListInfo">TCrtpl';
		var disableAll = function(){$('div.study-actions-buttons').find('button').button('option','disabled',true);$('div.study-actions-buttons').find('select').prop('disabled',true)};
		var rowSelected = function(){
			disableAll();
			var selectedStudies = jqDataTable.getSelected('study-list');
			var buttons = $('div.study-actions-buttons').find('button');
			var selects = $('div.study-actions-buttons').find('select');
	    	var getImgsOK = true; var sViewOK = true; var aViewOK = true; var assignToOK = true;
			if(selectedStudies.length <= 0) return;
			else if(selectedStudies.length==1){
				
				//getImgsOK  = getImgsOK && actionIsAllowed(userinfo,selectedStudies[0].iid,'request-study-images');
		    	//sViewOK = sViewOK && actionIsAllowed(userinfo,selectedStudies[0].iid,'simple-study-visualization');
		    	//aViewOK = aViewOK && actionIsAllowed(userinfo,selectedStudies[0].iid,'advanced-study-visualization');
		    	//assignToOK = assignToOK && actionIsAllowed(userinfo,selectedStudies[0].iid,'assign-study-to');
		    	
		    	getImgsOK= getImgsOK && (userinfo.permissions.canUseAgent || userinfo.permissions.canDownloadDicomdir);
		    	sViewOK= sViewOK && (userinfo.permissions.canSimpleView || userinfo.permissions.canIntermedView);
		    	aViewOK= aViewOK && userinfo.permissions.canAdvancedView;
		    	assignToOK= assignToOK && userinfo.permissions.canAssignStudy;
		    	
		    	$(selects[0]).prop('disabled',!getImgsOK);
			    $(buttons[0]).button('option','disabled',!getImgsOK);
				$(buttons[1]).button('option','disabled',!sViewOK);
				$(buttons[2]).button('option','disabled',!aViewOK);
				$(buttons[3]).button('option','disabled',!assignToOK);
				$(selects[1]).prop('disabled',!assignToOK);
			} else{
			    $.each(selectedStudies, function(index, study) {
			    	
			    	getImgsOK= getImgsOK && (userinfo.permissions.canUseAgent || userinfo.permissions.canDownloadDicomdir);
			    	assignToOK= assignToOK && userinfo.permissions.canAssignStudy;
			    	
			    });
			    $(selects[0]).prop('disabled',!getImgsOK);
			    $(buttons[0]).button('option','disabled',!getImgsOK);
				$(buttons[1]).button('option','disabled',true);
				$(buttons[2]).button('option','disabled',true);
				$(buttons[3]).button('option','disabled',!assignToOK);
				$(selects[1]).prop('disabled',!assignToOK);
			}
		};
		var columns = [
		    {'title':'Acc.Number','data':'an','defaultContent':'---','class':'left','width':'110px'},
		    {'title':'Msj','data':'inc','defaultContent':'','class':'center','width':'25px'},
			{'title':'Inf','data':'rs','defaultContent':'---','class':'center','width':'25px'},
			{'title':'A','data':'fc','defaultContent':'---','class':'center','width':'25px'},
			{'title':'Asignado','data':'dr','defaultContent':'','class':'center open-study','width':'80px'},
			{'title':'PatientID','data':'pid','defaultContent':'---','class':'left open-study','width':'100px'},
			{'title':'PACIENTE','data':'pn','defaultContent':'---','class':'left open-study'},
			{'title':'Mod','data':'mod','defaultContent':'---','class':'center open-study','width':'25px'},
			{'title':'ESTUDIO','data':'desc','defaultContent':'...','class':'left open-study'},
			{'title':'FECHA','data':'date','defaultContent':'---','class':'center open-study','width':'100px'},
			{'title':'Centro','data':'c','defaultContent':'---','class':'left open-study','width':'150px'},
			{'title':'#I','data':'ti','defaultContent':'---','class':'center open-study','width':'35px'},
			{'title':'URGENTE-PREFERENTE-NORMAL','data':'urg'}
		];
		var columnDefs = [
		    //{'targets':0,'bSortable':false,'render':function(value,t,study){return '<input type="checkbox" class="row-selector">'}},
		    {'targets':1,'render':function(value,t,study){return value==2?'<div class="view-study-notes" align="center" studyid="'+study.id+'" iid="'+study.iid+'"><span class="ui-icon ui-icon-mail-open red"></span></div>':value==1? '<div class="view-study-notes" align="center" studyid="'+study.id+'" iid="'+study.iid+'"><span class="ui-icon ui-icon-mail-closed black"></span></div>':''}},
		    {'targets':2,'render':function(value,t,study){var url='rest/reporting/reports/pdf/'+study.id;return value==3?(userinfo.permissions.canDoReports?'<a href="'+url+'"><img src="../images/informado.png"/></a>':'<img src="../images/informado.png"/>'):value==2?'<img src="../images/aconfirmar.png"/>':value==1?'<div align="center"><span class="ui-icon ui-icon-volume-on black"></span></div>':''}},
		    {'targets':3,'render':function(value,t,study){if(!value||value<=0)return '<span></span>';var studyID=study.id;var IID=study.iid;var imgID='clipimg-'+studyID;return '<span><img id="'+imgID+'" src="../images/paperclip3_black.png" studyid="'+studyID+ '" iid="'+IID+'" class="datable-row-icon"/></span>'}},
		    {'targets':[0,4,5,6,7,8,9,11],'render':function(value,t,study){return study.urg==2?'<span class="urgent-study">'+value+'</span>':study.urg==1?'<span class="priority-study">'+value+'</span>':'<span class="normal-study">'+value+'</span>'}},
		    {'targets':10,'visible':false,'render':function(value,t,study){return study.urg==2?'<span class="urgent-study">'+value+'</span>':study.urg==1?'<span class="priority-study">'+value+'</span>':'<span class="normal-study">'+value+'</span>'}},
		    {'targets':12,'visible':false}
		];
		var aButtons= [{'sExtends':'select_all','sButtonText':'Marcar Todos'},{'sExtends':'select_none','sButtonText':'Desmarcar Todos'}];
		var colVis= {'exclude':[0,6,7,9,12],'label':function(index,title,th){return title}};
		
		dt=set_table_as_jqdatatable(table,columns,columnDefs,10,lengthMenu,order,dom,'os',aButtons,rowSelected,'estudios',void 0,'100',colVis);
		$(table).on('click','img.datable-row-icon',function(){if($(".ui-dialog").is(":visible"))return;var studyID=this.attributes["studyid"].value;var url='study-files.html?s='+studyID+'&e=1';var iid=this.attributes["iid"].value;if(userinfo.permissions.canDoFiles)openDialog(url,'ARCHIVOS',500,650)});
		$(table).on('click','div.view-study-notes',function(){if($(".ui-dialog").is(":visible"))return;var studyId=this.attributes["studyid"].value;var url='study-notes.html?s='+studyId;var iid=this.attributes["iid"].value;if(userinfo.permissions.canDoMessages)openDialog(url,'MENSAJES',600,540)});
		$(table).on('dblclick','td.open-study',function(){var pos= dt.fnGetPosition(this);var study= dt.fnGetData(pos[0]); if(userinfo.permissions.canOpenStudyView)window.open('study.html?s='+study.id,''); else return false;})
		$(table).on('mousedown','td.open-study',function(){return false});
		var visCol= new $.fn.dataTable.ColVis(dt,{'buttonText':'Ver Columnas'});
		
		return dt;
	},
	userABM: function(){
		var dt= null;
		var table= $('table#userList');
		var lengthMenu= [5,10,15,20];
		var order= [[3,'asc']];
		var dom= '<"#userListCant">frtTlp';
		var rowSelected= function(){};
		var columns= [
		    {'title':'USUARIO','data':'login','defaultContent':'','class':'center','width':'200px'},
			{'title':'NOMBRE','data':'firstName','defaultContent':'','class':'right','width':'150px'},
			{'title':'APELLIDO','data':'lastName','defaultContent':'','class':'right','width':'150px'},
			{'title':'ROL','data':'institutions','defaultContent':'','class':'right','width':'150px'},
			{'title':'MODS.','data':'modalities','defaultContent':'','class':'center','width':'100px'},
			{'title':'INSTITUCIONES','data':'institutions','defaultContent':'','class':'center','width':'150px'},
			{'title':'HABILITADO','data':'deleted','defaultContent':'','class':'center','width':'100px'},
			{'title':'BORRAR','data':'id','defaultContent':'','class':'center','width':'90px'},
			{'title':'PASS.','data':'','defaultContent':'','class':'center','width':'70px'}
		];
		var columnDefs= [
			{'targets':0,'render':function(value,t,user){return '<b>'+value+'</b>'}},
			{'targets':3,'render':function(value,t,user){return value!=null&&value[0]!=null?value[0].role.name:'<span style="color:#a00">AGREGAR ROL</span>'}},
			{'targets':4,'render':function(value,t,user){return value && value[0]!=null? '<div align="center"><span class="ui-icon ui-icon-tag black"></span></div>':'<div align="center"><span class="ui-icon ui-icon-tag"></span></div>'}},
			{'targets':5,'render':function(value,t,user){return value && value[0]!=null? '<div align="center"><span class="ui-icon ui-icon-home black"></span></div>':'<div align="center"><span class="ui-icon ui-icon-home"></span></div>'}},
			{'targets':6,'render':function(value,t,user){return value?'<div align="center"><span class="ui-icon ui-icon-close black"></span></div>':'<div align="center"><span class="ui-icon ui-icon-check black"></span></div>'}},
			{'targets':7,'render':function(value,t,user){return '<div align="center"><span class="ui-icon ui-icon-trash black"></span></div>'}},
			{'targets':8,'render':function(value,t,user){return '<div align="center"><span class="ui-icon ui-icon-pencil black"></span></div>'}}
		];
		var fnRow= function(){};
		dt= set_table_as_jqdatatable(table,columns,columnDefs,10,lengthMenu,order,dom,'none',[],rowSelected,'usuarios',fnRow,'');
		$(table).on('mousedown',function(){return false});
		return dt;
	},
	studyABM: function(){
		var dt= null;
		var table= $('table#studyList');
		var lengthMenu= [5,10,15,20];
		var order= [[0,'desc']];
		var dom= '<"#studyListCant">Trtlp';
		var aButtons= [{'sExtends':'select_all','sButtonText':'Marcar Todos'},{'sExtends':'select_none','sButtonText':'Desmarcar Todos'}];
		var rowSelected= function(){
			var selectedStudies = jqDataTable.getSelected('studyList');
			var buttons= $('div.study-list-buttons').find('button');
			if(selectedStudies.length<=0 /*|| selectedStudies.length>10*/) {
				$(buttons[0]).button('option','disabled',true);
			}
			else {
				$(buttons[0]).button('option','disabled',!userinfo.permissions.canDeleteStudies);
			}
		};
		var columns= [
		    {'title':'Fecha','data':'date','defaultContent':'','class':'center','width':'100px'},
			{'title':'Centro','data':'c','defaultContent':'','class':'left','width':'200px'},
			{'title':'PatientID','data':'pid','defaultContent':'','class':'left','width':'150px'},
			{'title':'Paciente','data':'pn','defaultContent':'','class':'center'},
			{'title':'Mod.','data':'mod','defaultContent':'','class':'right','width':'70px'},
			{'title':'Estudio','data':'desc','defaultContent':'','class':'center','width':'215px'},
			{'title':'Inf.','data':'rs','defaultContent':'','class':'center','width':'50px'},
			{'title':'#I','data':'ti','defaultContent':'','class':'center','width':'50px'},
			{'title':'Ver','data':'id','defaultContent':'','class':'center','width':'50px'}
		];
		var columnDefs= [
			{'targets':6,'render':function(value,t,study){
				return value==3 ?
					'<div align="center"><span class="ui-icon ui-icon-check black" studyid="'+study.id+'" ondblclick="clear_report(this);"></span></div>' :
					'<div align="center"><span class="ui-icon ui-icon-close black"></span></div>'
			}},
			{'targets':8,'render':function(value,t,study){return '<div align="center"><img src="../images/placa.jpg" studyId="'+value+'" mod="'+study.mod+'" /></div>'}}
		];
		var fnRow= function(){};
		dt= set_table_as_jqdatatable(table,columns,columnDefs,10,lengthMenu,order,dom,'multi',aButtons,rowSelected,'estudios',fnRow,'');
		return dt;
	},
	studyFiles: function(studyId){
		var dt= null;
		var table= $('#dynamicModalDialogIframe').contents().find('table#studyFiles');
		var lengthMenu= [5,10,15,25,50,75,100];
		var order= [[1,'asc']];
		var dom= '<"#filesListCant">frtTlp';
		var rowSelected= function(){};
		var columns= [
		    {'title':'ARCHIVO','data':'filename','defaultContent':'---','class':'left'},
		    {'title':'DESC.','data':'desc','defaultContent':'---','class':'center','width':'100px'}
		];
		var columnDefs= [
		    {'targets':0,'render':function(value,t,file){
         		var url= 'rest/study/files/file/'+studyId+'?name='+file.filename+'&type='+(file.desc=='Archivo'?'1':file.desc=='Orden Médica'?'2':'3');
                return '<a target="_blank" href="'+url+'"><span>'+value+'</span></a>';
		    }}
		];
		var fnRow= function(){};
		dt= set_table_as_jqdatatable(table,columns,columnDefs,10,lengthMenu,order,dom,'none',[],rowSelected,'archivos',fnRow,'');
		return dt;
	},
	studySeries: function(){
		var dt= null;
		var table= $('#dynamicModalDialogIframe').contents().find('table#seriesList');
		var lengthMenu= [5,10,15,25,50,75,100];
		var order= [];
		var dom= '<"#seriesCant">Trtlp';
		var aButtons= [{'sExtends':'select_all','sButtonText':'Marcar Todos'},{'sExtends':'select_none','sButtonText':'Desmarcar Todos'}];
		var rowSelected= function(){};
		var columns= [
			{'title':'Fecha','data':'date','class':'center','width':'90px','defaultContent':'---'},
			{'title':'Hora','data':'time','class':'center','width':'70px','defaultContent':'---'},
			{'title':'Descripción','data':'desc','class':'center','defaultContent':'---'},
			{'title':'# Imgs','data':'imgs','class':'center','width':'45px','defaultContent':'---'}
		];
		var columnDefs= [];
		var fnRow= function(){};
		dt= set_table_as_jqdatatable(table,columns,columnDefs,10,lengthMenu,order,dom,'multi',aButtons,rowSelected,'series',fnRow,'');
		return dt;
	},
	filesList: function(studyId){
		var dt= null;
		var table= $('table#filesList');
		var lengthMenu= [5,10,15,25];
		var order= [];
		var dom= '<"#filesListCant">frtTlp';
		var columns= [
  		    {'title':'NOMBRE','data':'filename','defaultContent':'---','class':'center'}
  		];
  		var columnDefs= [
  		    {'targets':0,'render':function(value,t,file){
  		    	//if(actionIsAllowed(userinfo,userinfo.institutions[0].institution.id,'download-study-file')) {
  		    		var url= 'rest/study/files/file/'+studyId+'?name='+file.filename+'&type=1';
  		    		return '<a target="_blank" href="'+url+'" class="study-files"><span>'+value+'</span></a>';
  		    	//} else return '<span>'+value+'</span>';
  		    }}
  		];
		//if(actionIsAllowed(userinfo,userinfo.institutions[0].institution.id,'delete-study-file')){
			columns.push({'title':'BORRAR','defaultContent':'---','class':'center','width':'80px'});
			columnDefs.push({'targets':1,'render':function(value,t,file){
				var url= 'rest/study/files/file/'+studyId+'?name='+file.filename+'&type=1';
				return '<div align="center"><span class="ui-icon ui-icon-trash black" onclick="studyActions.studyFiles.delete_file(\''+file.filename+'\',\''+url+'\');"></span></div>'
			}});
		//}
		dt= set_table_as_jqdatatable(table,columns,columnDefs,10,lengthMenu,order,dom,'none',[],void 0,'archivos',void 0,'');
		return dt;
	},
	medOrderList: function(studyId){
		var dt= null;
		var table= $('table#medOrderList');
		var lengthMenu= [5,10,15,25];
		var order= [];
		var dom= '<"#medOrderListCant">frtTlp';
		var columns= [
  		    {'title':'NOMBRE','data':'filename','defaultContent':'---','class':'center'}
  		];
  		var columnDefs= [
  		    {'targets':0,'render':function(value,t,file){
  		    	//if(actionIsAllowed(userinfo,userinfo.institutions[0].institution.id,'download-study-file')) {
  		    		var url= 'rest/study/files/file/'+studyId+'?name='+file.filename+'&type=2';
  		    		return '<a target="_blank" href="'+url+'" class="study-files"><span>'+value+'</span></a>';
  		    	//} else return '<span>'+value+'</span>';
  		    }}
  		];
		//if(actionIsAllowed(userinfo,userinfo.institutions[0].institution.id,'delete-study-file')){
			columns.push({'title':'BORRAR','defaultContent':'---','class':'center','width':'80px'});
			columnDefs.push({'targets':1,'render':function(value,t,file){
				var url= 'rest/study/files/file/'+studyId+'?name='+file.filename+'&type=2';
				return '<div align="center"><span class="ui-icon ui-icon-trash black" onclick="studyActions.studyFiles.delete_file(\''+file.filename+'\',\''+url+'\');"></span></div>'
			}});
		//}
		dt= set_table_as_jqdatatable(table,columns,columnDefs,10,lengthMenu,order,dom,'none',[],void 0,'ord.médicas',void 0,'');
		return dt;
	},
	mp3List: function(studyId){
		var dt= null;
		var table= $('table#mp3List');
		var lengthMenu= [5,10,15,25];
		var order= [];
		var dom= '<"#mp3ListCant">frtTlp';
		var columns= [
  		    {'title':'NOMBRE','data':'filename','defaultContent':'---','class':'center'}
  		];
  		var columnDefs= [
  		    {'targets':0,'render':function(value,t,file){
  		    	//if(actionIsAllowed(userinfo,userinfo.institutions[0].institution.id,'download-study-file')) {
  		    		var url= 'rest/study/files/file/'+studyId+'?name='+file.filename+'&type=3';
  		    		return '<a target="_blank" href="'+url+'" class="study-files"><span>'+value+'</span></a>';
  		    	//} else return '<span>'+value+'</span>';
  		    }}
  		];
		//if(actionIsAllowed(userinfo,userinfo.institutions[0].institution.id,'delete-study-file')){
			columns.push({'title':'BORRAR','defaultContent':'---','class':'center','width':'80px'});
			columnDefs.push({'targets':1,'render':function(value,t,file){
				var url= 'rest/study/files/file/'+studyId+'?name='+file.filename+'&type=3';
				return '<div align="center"><span class="ui-icon ui-icon-trash black" onclick="studyActions.studyFiles.delete_file(\''+file.filename+'\',\''+url+'\');"></span></div>'
			}});
		//}
		dt= set_table_as_jqdatatable(table,columns,columnDefs,10,lengthMenu,order,dom,'none',[],void 0,'audios',void 0,'');
		return dt;
	},
	loggerList: function(){
		var dt= null;
		var table= $('table#loggerList');
		var lengthMenu= [5,10,15,25];
		var order= [[0,'desc']];
		var dom= '<"#loggerListCant"><"#refreshLog">frtlp';
		var columns= [
		    {'title':'Fecha','data':'date','defaultContent':'','class':'center','width':'25%'},
		    {'title':'Usuario','data':'user','defaultContent':'','class':'center','width':'25%'},
		    {'title':'Acción','data':'action','defaultContent':'','class':'center','width':'25%'},
		    {'title':'Parámetros','data':'param','defaultContent':'','class':'center','width':'25%'}
		];
		var columnDefs= [];
		
		dt= set_table_as_jqdatatable(table, columns, columnDefs, 10, lengthMenu, order, dom, 'none', [], void 0, 'datos', void 0, '');
		$('div#refreshLog').html('<button type="button"></button>');
		return dt;
	}
};



















	function set_table_as_jqdatatable(table,columns,columnDefs,pageLength,lengthMenu,order,dom,
									  rowSelection,aButtons,rowSelected,language,fnRow,scrollY,colVis){
		var dt= null;
		
		dt= $(table).dataTable({
			'data':[],
			'pageLength':pageLength,
			'lengthMenu':lengthMenu,
			'pagingType':'full_numbers',
			'order':order,
			'dom':dom,
			'colVis':colVis,
			'tableTools':{
				'sRowSelect':rowSelection,
				'aButtons':aButtons,
				fnRowSelected:rowSelected,
				fnRowDeselected:rowSelected
			},
			'scrollY':scrollY,
			'scrollCollapse':false,
			'columns':columns,
			'columnDefs':columnDefs,
			'fnRowCallback':fnRow,
			'language':{
				'lengthMenu':'Mostrar _MENU_ '+language+' por página',
	            'zeroRecords':'Sin '+language+' para mostrar',
	            'info':'Mostrando página _PAGE_ de _PAGES_',
	            'infoEmpty':'Sin '+language+' para mostrar',
	            'infoFiltered':'(filtrados de un total de _MAX_ '+language+')',
			    'emptyTable':'Sin '+language+' para mostrar',
			    'infoPostFix':'',
			    'thousands':'.',
			    'loadingRecords':'Cargando...',
			    'processing':'Procesando...',
			    'search':'Buscar:',
			    'paginate':{'first':'Primera','last':'Última','next':'Siguiente','previous':'Anterior'},
			    'aria':{'sortAscending':': Click para ordenar ascendentemente','sortDescending':': Click para ordenar descendentemente'}
			}
		});
		
		return dt;
	}