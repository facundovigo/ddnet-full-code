
	var studyId;

	$(document).ready(function(){
		
		studyId = $.url().param('studyUID');
		
		if(studyId && studyId!=null){
			$('div#single-viewer').tabs();

			$.ajax({
				method: 'GET',
				dataType: 'json',
				url: 'study?&studyID=' + studyId
				//beforeSend: function(){$('div#loading-images').show();}
				
			}).done(function(study){
				if(study && study!=null){
					$('div.top-of-page').find('label').text('Estudio de '+study.patName+' del día '+study.studyDate);
					
					var tabs = $('div#single-viewer');
					var studyUID = study.studyId.replace(/\./g,'_');
					tabs.append( '<div id="' + studyUID + '"></div>' );
					var studyViewer;
					$.ajax({url:'visualizador.html',async:false,dataType:'html',success:function(html){studyViewer=html;}});
					var viewport;
					$.ajax({url:'viewportTemplate.html',async:false,dataType:'html',success:function(html){viewport=html;}});
					var studyViewerCopy = $(studyViewer).clone();
					studyViewerCopy.attr('id','viewer-'+studyUID);
					studyViewerCopy.removeClass('hidden');
					studyViewerCopy.appendTo('#'+studyUID);
					loadStudy(studyViewerCopy,viewport,study.studyId);
					
				} else {}
				//$('div#loading-images').hide(500);
				
			}).fail(function(obj,msj,err){
				//$('div#loading-images').hide();
				alert('ERROR: '+msj);
			});
			
			resize_handler_1();
		
		} else{}
		
	});
	
	window.addEventListener('resize', resize_handler_1);
	
	
	function resize_handler_1() {
		
		var tabs = $('div#single-viewer'),
			top = $('div.top-of-page');
		
		tabs.css('width', '99.5%');
		$('.ui-widget-content').height(window.innerHeight - 80);
		top.css('width', '99.9%');
		
		if(tabs.width() < 800) {
			tabs.css('width', '800px');
			top.css('width', '800px');
		}
	}