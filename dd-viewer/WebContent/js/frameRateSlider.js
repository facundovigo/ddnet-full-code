		

	function init_slider(studyViewer, studyId){
		
		var element = $(studyViewer).find('.viewport')[0];
		
		var slider = $(studyViewer).find('div.frame-rate-slider');
        $(slider).attr('id','slider'+studyId.replace(/\./g,'_'));
        $(slider).tooltip();
        $(slider).slider({
        	disabled: true,
        	min: 1,
        	max: 100,
        	value: 10,
        	change: function(ev,ui){
        		cornerstoneTools.stopClip(element);
        		cornerstoneTools.clearToolState(element, 'playClip');
        		cornerstoneTools.playClip(element,parseInt(ui.value));
        	}
        });
        
        var checkSlider = $(studyViewer).find('input.enable-frame-rate');
        $(checkSlider).attr('id','fr'+studyId.replace(/\./g,'_'));
        $(checkSlider).on('click',function(){
        	if($(this).prop('checked')) $(slider).slider('option','disabled',false);
        	else $(slider).slider('option','disabled',true);
        });
        
	}