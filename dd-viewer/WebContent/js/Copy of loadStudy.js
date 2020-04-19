
var loadingImages;
	
function loadStudy(studyViewer, viewportModel, studyId, cantImgs) {

	$.ajax({
		method: 'GET',
		dataType: 'json',
		url: 'study/seriesList?studyID='+studyId,
		beforeSend: function(){
			var loading = $('div#loading-images').clone();
			$(loading).attr('id','loading'+studyId.replace(/\./g,'_'));
			$('body').append(loading);
			$(loading).find('label').text('Cargando '+cantImgs+' Imágenes');
			$(loading).show();
			loadingImages = loading;
		}
		
	}).done(function(data){
		
        var imageViewer = new ImageViewer(studyViewer, viewportModel);
        imageViewer.setLayout('1x1'); // default layout

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

        var seriesIndex = 0;

        // Create a stack object for each series
        data.seriesList.forEach(function(series) {
            var stack = {
                seriesDescription: series.seriesDescription,
                stackId: series.seriesNumber,
                cantImages: series.cantImages,
                imageIds: [],
                seriesIndex: seriesIndex,
                currentImageIdIndex: 0,
                frameRate: series.instanceList[0].frameRate,
                dicomInfo: []
            };


            // Populate imageIds array with the imageIds from each series
            // For series with frame information, get the image url's by requesting each frame
            if (series.instanceList[0].numberOfFrames !== undefined && series.instanceList[0].numberOfFrames>0) {
            	series.instanceList.forEach(function(image) {
            	var numberOfFrames = image.numberOfFrames;
            	for (var i = 0; i < numberOfFrames; i++) {
                    var imageId = image.imageId + "?frame=" + i;
                    if (imageId.substr(0, 4) !== 'http') {
                        imageId = "dicomweb:http://"+location.host+"/wado/wado?requestType=WADO&contentType=application%2Fdicom&frameNumber=" + i +"&studyUID="+data.studyId+"&seriesUID="+series.seriesUid+"&objectUID=" + image.imageId;
                    }
                    stack.imageIds.push(imageId);
                }
            	});
                // Otherwise, get each instance url
            } else {
                series.instanceList.forEach(function(image) {
                    var imageId = image.imageId;

                    if (image.imageId.substr(0, 4) !== 'http') {
                    	imageId = "dicomweb:http://"+location.host+"/wado/wado?requestType=WADO&contentType=application%2Fdicom&studyUID="+data.studyId+"&seriesUID="+series.seriesUid+"&objectUID=" + image.imageId;
                    }
                    stack.imageIds.push(imageId);
                    stack.dicomInfo.push('Sin datos. Vuelva a intentarlo');
                    //var wadoURL = imageId;
                    //wadoURL += '&contentType=application%2Fdicom&transferSyntax=1.2.840.10008.1.2.4.51';
                    //get_dicom_tags(wadoURL);
                });
            }
            // Move to next series
            seriesIndex++;

            // Add the series stack to the stacks array
            imageViewer.stacks.push(stack);
        });

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
    				stack.cantImages + ' Imágenes</div>'	+
    			'</a>';
            
			

            // Add to series list
            var seriesElement = $(seriesEntry).appendTo(seriesList);

            // Find thumbnail
            var thumbnail = $(seriesElement).find('div')[0];

            // Enable cornerstone on the thumbnail
            cornerstone.enable(thumbnail);

            // Have cornerstone load the thumbnail image
            cornerstone.loadAndCacheImage(imageViewer.stacks[stack.seriesIndex].imageIds[0]).then(function(image) {
                
            	// Make the first thumbnail active
                if (stack.seriesIndex === 0) {
                    $(seriesElement).addClass('active');
                }
                // Display the image
                cornerstone.displayImage(thumbnail, image);
                $(seriesElement).draggable({helper: "clone"});
            });

            // Handle thumbnail click
            $(seriesElement).on('click touchstart', function() {
              useItemStack(0, stackIndex);
            }).data('stack', stackIndex);
        });

        function useItemStack(item, stack) {
            var imageId = imageViewer.stacks[stack].imageIds[0], element = imageViewer.getElement(item);
            if ($(element).data('waiting')) {
                imageViewer.viewports[item].find('.overlay-text').remove();
                $(element).data('waiting', false);
            }
            $(element).data('useStack', stack);

            displayThumbnail(seriesList, $(seriesList).find('.list-group-item')[stack], element, imageViewer.stacks[stack], function(el, stack){
                if (!$(el).data('setup')) {
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
            
            //$(seriesList).height("100%");
            $('div.thumbnailSelector').height($('div#viewer-tabs').height()-71.5);
            
            $(parentDiv).width(width - $(studyViewer).find('.thumbnailSelector:eq(0)').width());
            $(parentDiv).css({height : ($('div#viewer-tabs').height()-100) + 'px'});
            $(imageViewerElement).css({height : ($('div#viewer-tabs').height()-100) + 'px'});

            imageViewer.forEachElement(function(el, vp) {
                cornerstone.resize(el, true);

                if ($(el).data('waiting')) {
                    var ol = vp.find('.overlay-text');
                    if (ol.length < 1) {
                        ol = $('<div class="overlay overlay-text">Please drag a stack onto here to view images.</div>').appendTo(vp);
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
        if (imageViewer.isSingle())
            useItemStack(0, 0);
        $('div#viewer-tabs li.closable').on('click',resizeStudyViewer);

    });
}
