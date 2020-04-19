
var loadingImages;
	
function loadStudy(studyViewer, viewportModel, studyId) {

	$.ajax({
		method: 'GET',
		dataType: 'json',
		url: 'study/seriesList?studyID='+studyId,
		beforeSend: function(){
			var loading = $('div#loading-images').clone();
			$(loading).attr('id','loading'+studyId.replace(/\./g,'_'));
			$(studyViewer).append(loading);
			loadingImages = loading;
		}
		
	}).done(function(data){
		
        var imageViewer = new ImageViewer(studyViewer, viewportModel);
        imageViewer.setLayout('1x1'); // default layout
        
        init_slider(studyViewer, studyId);

        function initViewports() {
            imageViewer.forEachElement(function(el) {
                cornerstone.enable(el);
                $(el).droppable({
                    drop : function(evt, ui) {
                        var fromStack = $(ui.draggable.context).data('stack'), toItem = $(this).data('index');
                        useItemStack(toItem, fromStack);
                    }
                });
            });
        }

        // setup the tool buttons
        setupButtons(studyViewer);

        // layout choose
        $(studyViewer).find('.choose-layout a').click(function(){
            $('ul.dropdown-menu').slideUp();
        	var previousUsed = [];
            imageViewer.forEachElement(function(el, vp, i){
                if (!isNaN($(el).data('useStack'))) {
                    previousUsed.push($(el).data('useStack'));
                }
            });

            var type = $(this).text();
            imageViewer.setLayout(type);
            initViewports();
            resizeStudyViewer();
            if (previousUsed.length > 0) {
                previousUsed = previousUsed.slice(0, imageViewer.viewports.length);
                var item = 0;
                previousUsed.forEach(function(v){
                    useItemStack(item++, v);
                });
            }
            $('.viewer button').removeClass('current');
            $($('.viewer').find('button')[0]).addClass('current');
        });

        if (data.modality.indexOf('XA')>=0) {
        	//$(loadingImages).find('label').text('Cargando Imágenes');
        	//$(loadingImages).show();
        	$(studyViewer).find('.hemo-text').show();
        	
        	var seriesIndex = 0; 
        	data.seriesList.forEach(function(series){
        		series.instanceList.forEach(function(image){
        			var imageIndex=1;
        			var stack = {
        	            	seriesUid:series.seriesUid,
        	                seriesIndex:seriesIndex,
        	                stackId:series.seriesNumber,
        	                seriesDescription:series.seriesDescription,
        	                cantImages:series.cantImages,
        	                imageIds:[],
        	                currentImageIdIndex:0,
        	                frameRate:series.instanceList[0].frameRate,
        	                playClip:false,
        	                allFrames:true
        			};
        			var numberOfFrames=0;
            		var frameRate=0;
            		var imageIdToLoad =
            			'http://'+location.host+'/wado?requestType=WADO&contentType=application%2Fdicom'
            			+'&studyUID='+data.studyId+'&seriesUID='+series.seriesUid+'&objectUID='+image.imageId;
            		
            		cornerstoneWADOImageLoader.dataSetCacheManager.load(imageIdToLoad).then(function(dataSet){
            			numberOfFrames=dataSet.intString('x00280008');
            			frameRate = dataSet.floatString('x00181063');
	                    if(!frameRate) return;
	                    frameRate = 1000/frameRate;
            		});
            		
            		if(numberOfFrames>0){
	            		for(var i=0; i<numberOfFrames; i++){
	            			var imageId = "dicomweb:http://"+location.host+"/wado?requestType=WADO&contentType=application%2Fdicom"	+
	            						  //"&imageNum="+imageIndex+"&cantImgs="+series.cantImages	+
	            						  "&imageNum=1&cantImgs=1"	+
	            						  "&studyUID="+data.studyId+"&seriesUID="+series.seriesUid+"&objectUID="+image.imageId
	            						  + "&frame="+i;
		            	
	                        stack.imageIds.push(imageId);
	                        stack.frameRate=frameRate;
	                        stack.playClip=true;
		            	}
	                    cornerstoneWADOImageLoader.dataSetCacheManager.unload(imageIdToLoad);
            		} else{
            			var imageId = "dicomweb:http://"+location.host+"/wado?requestType=WADO&contentType=application%2Fdicom"	+
			            			//"&imageNum="+imageIndex+"&cantImgs="+series.cantImages	+
									  "&imageNum=1&cantImgs=1"	+
            						  "&studyUID="+data.studyId+"&seriesUID="+series.seriesUid+"&objectUID="+image.imageId;
            			stack.imageIds.push(imageId);
            			stack.allFrames=false;
            		}
	            	imageIndex++;
        			seriesIndex++;
    	            imageViewer.stacks.push(stack);
        		});
        	});
        
        } else {
	        var seriesIndex = 0; 
	        // Create a stack object for each series
	        data.seriesList.forEach(function(series) {
	        	var imageIndex=1;
	        	
	            var stack = {
	            	seriesUid:series.seriesUid,
	                seriesIndex:seriesIndex,
	                stackId:series.seriesNumber,
	                seriesDescription:series.seriesDescription,
	                cantImages:series.cantImages,
	                imageIds:[],
	                currentImageIdIndex:0,
	                frameRate:series.instanceList[0].frameRate
	            };

	        	series.instanceList.forEach(function(image) {
	                var imageId = 	"dicomweb:http://"+location.host+"/wado/wado?"	+
	                				"requestType=WADO&contentType=application%2Fdicom&imageNum="+imageIndex+"&cantImgs="+series.cantImages	+
	                				"&studyUID="+data.studyId+"&seriesUID="+series.seriesUid+"&objectUID="+image.imageId;
	                
	                stack.imageIds.push(imageId);
	                imageIndex++;
	            });
	            
	            seriesIndex++;
	            imageViewer.stacks.push(stack);
	        });
        
        }

        // Resize the parent div of the viewport to fit the screen
        var imageViewerElement = $(studyViewer).find('.imageViewer')[0];
        var viewportWrapper = $(imageViewerElement).find('.viewportWrapper')[0];
        var parentDiv = $(studyViewer).find('.viewer')[0];

        var studyRow = $(studyViewer).find('.studyRow')[0];
        var width = $(studyRow).width();

        // Get the viewport elements
        var element = $(studyViewer).find('.viewport')[0];

        // Image enable the dicomImage element
        initViewports();

        // Get series list from the series thumbnails (?)
        var seriesList = $(studyViewer).find('.thumbnails')[0];
        
        //if(data.modality.indexOf('XA')>=0 && !imageViewer.stacks[0].playClip){
        //	loadStudy(studyViewer, viewportModel, studyId);
        	
        //} else{
            imageViewer.stacks.forEach(function(stack, stackIndex) {

                // Create series thumbnail item
                var seriesEntry = '<a class="list-group-item" + ' +
                    'oncontextmenu="return false"' +
                    'unselectable="on"' +
                    'onselectstart="return false;"' +
                    'onmousedown="return false;">' +
                    '<div class="csthumbnail"' +
                    'oncontextmenu="return false"' +
                    'unselectable="on"' +
                    'onselectstart="return false;"' +
                    'onmousedown="return false;"></div>' +
                    "<div class='text-center small'>" +
        				(stack.seriesDescription ? stack.seriesDescription:'Sin Descripción') + '<br>'	+
        				(data.modality.indexOf('XA')>=0 ? '1':stack.cantImages) + ' Imágenes</div>'	+
        			'</a>';

                // Add to series list
                var seriesElement = $(seriesEntry).appendTo(seriesList);

                // Find thumbnail
                var thumbnail = $(seriesElement).find('div')[0];
                $(thumbnail).attr('id','th'+stack.seriesUid.replace(/\./g,'_'));

                // Enable cornerstone on the thumbnail
                cornerstone.enable(thumbnail);
                
                var imagePromise = cornerstone.imageCache.getImagePromise(imageViewer.stacks[stack.seriesIndex].imageIds[0]);
                if(imagePromise===undefined) {
                	$(thumbnail).append('<img src="images/serie-loading.gif" style="position:absolute;left:45px;top:35px;">');
                    $(thumbnail).append('<label style="position:absolute;left:30px;top:45px;color:#0972a5;font-size:8.5pt;">cargando</label>');
                }
                
                // Have cornerstone load the thumbnail image
                cornerstone.loadAndCacheImage(imageViewer.stacks[stack.seriesIndex].imageIds[0]).then(function(image){
                	// Make the first thumbnail active
                    if (stack.seriesIndex === 0) {
                        $(seriesElement).addClass('active');
                    }
                    // Display the image
                    cornerstone.displayImage(thumbnail, image);
                    $(seriesElement).draggable({helper: "clone"});
                });

                // Handle thumbnail click
                $(seriesElement).on('click touchstart', function(){
                	useItemStack(0, stackIndex);
                }).data('stack', stackIndex);
                
                $(thumbnail).on("CornerstoneNewImage", function(){
                	$(this).find('img').remove();
                	$(this).find('label').remove();
                });
                //$(loadingImages).hide();
            });
       // }
        
        function useItemStack(item, stack) {
            var imageId = imageViewer.stacks[stack].imageIds[0], element = imageViewer.getElement(item);
            var cantImgs = imageViewer.stacks[stack].cantImages;

            if ($(element).data('waiting')) {
                imageViewer.viewports[item].find('.overlay-text').remove();
                $(element).data('waiting', false);
            }
            $(element).data('useStack', stack);

            if(data.modality.indexOf('XA')<0 && imageViewer.stacks[stack].cantImages>1){
            var imagePromise=cornerstone.imageCache.getImagePromise(imageViewer.stacks[stack].imageIds[1]);
            	if(imagePromise===undefined){
            		$(loadingImages).addClass('imagesLoading');
		        	$(loadingImages).find('label').text('Cargando imagen 1 de '+imageViewer.stacks[stack].cantImages);
		            $(loadingImages).show();
	            }
            }

            displayThumbnail(seriesList,$(seriesList).find('.list-group-item')[stack],element,imageViewer.stacks[stack],function(el,stack){
                if (!$(el).data('setup')){
                    setupViewport(el, stack, this);
                    setupViewportOverlays(el, data);
                    $(el).data('setup', true);
                }
            });
        }

        // Resize study viewer
        function resizeStudyViewer() {
            var studyRow = $(studyViewer).find('.studyContainer')[0];
            var height = $(studyRow).height();
            var width = $(studyRow).width();

            if($('div#viewer-tabs').length>0){
                $('div.thumbnailSelector').height($('div#viewer-tabs').height()-71.5);
                $(parentDiv).width(width - $(studyViewer).find('.thumbnailSelector:eq(0)').width());
                $(parentDiv).css({height : ($('div#viewer-tabs').height()-100) + 'px'});
                $(imageViewerElement).css({height : ($('div#viewer-tabs').height()-100) + 'px'});
            
            } else{
                $('div.thumbnailSelector').height($('div#single-viewer').height()-20);
                $(parentDiv).width(width - 70 - $(studyViewer).find('.thumbnailSelector:eq(0)').width());
                $(parentDiv).css({height : ($('div#single-viewer').height()-45) + 'px'});
                $(imageViewerElement).css({height : ($('div#single-viewer').height()-45) + 'px'});
                $('.frame-rate-slider').height(13.7);
            }

            imageViewer.forEachElement(function(el, vp) {
                cornerstone.resize(el, true);

                if ($(el).data('waiting')){
                    var ol = vp.find('.overlay-text');
                    if (ol.length < 1){
                        ol = $('<div class="overlay overlay-text">Arrastre una serie aquí para ver sus imágenes.</div>').appendTo(vp);
                    }
                    var ow = vp.width() / 2, oh = vp.height() / 2;
                    ol.css({top : oh, left : ow - (ol.width() / 2)});
                }
            });
        }
        // Call resize viewer on window resize
        $(window).resize(function() {
            resizeStudyViewer();
        });
        resizeStudyViewer();
        if (imageViewer.isSingle()) $('.overlay-text').text('Haga click o arrastre la serie si terminó de cargarse');

        $('div#viewer-tabs li.closable').on('click',resizeStudyViewer);
    });
}
