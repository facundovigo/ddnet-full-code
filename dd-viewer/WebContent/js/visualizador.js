
	var fnOnNewImage;
	var fnOnRenderImage;
	var loadingImages;

	function loadStudyJson(studyViewer, studyID) {
		
		$.ajax({
			method: 'GET',
			dataType: 'json',
			url: 'study/seriesList?studyID='+studyID,
			beforeSend: function(){
				var loading = $('div#loading-images').clone();
				$(loading).attr('id','loading'+studyID.replace(/\./g,'_'));
				$('body').append(loading);
				$(loading).show();
				loadingImages = loading;
			}
			
		}).done(function(data){
		//$.getJSON('study/seriesList?studyID='+studyID, function(data){
			var loading = loadingImages;
			var element = $(studyViewer).find('.viewport')[0];
            var parent = $(element).parent();
            var childDivs = $(parent).find('.overlay');
            var topLeft = $(childDivs[0]).find('div');
            $(topLeft[0]).text(data.patientName);
            $(topLeft[1]).text(data.patientId);
            var topRight = $(childDivs[1]).find('div');
            $(topRight[0]).text(data.studyDescription);
            $(topRight[1]).text(data.studyDate);
 
//...........................................................................................................            
            
            var stacks = [];
            var currentStackIndex = 0;
            var seriesIndex = 0;
            
            data.seriesList.forEach(function(series) {
            	
            	var stack = {
                    seriesDescription: series.seriesDescription,
                    stackId : series.seriesNumber,
                    cantImages: series.cantImages,
                    imageIds: [],
                    seriesIndex : seriesIndex,
                    currentImageIdIndex: 0,
                    frameRate: series.frameRate,
                    dicomInfo: []
            	};
            	/*
            	if(series.numberOfFrames && series.numberOfFrames > 0){
            		var numberOfFrames = series.numberOfFrames;
            		alert(numberOfFrames);
            		//var imageId = image.imageId;
            		var imageId = series.instanceList[0].imageId + "?frame=" + i;
            		for(var i=0; i < numberOfFrames; i++) {
            			if(imageId.substr(0, 4) !== 'http') {
            				imageId = "http://"+location.host+"/wado/wado?requestType=WADO&studyUID="+data.studyId+"&seriesUID="+series.seriesUid+"&objectUID=" + series.instanceList[0].imageId + "&frame=" + i;
            			}
            			stack.imageIds.push(imageId);
            		}
            	
            	} else {*/
            		series.instanceList.forEach(function(image) {
                        var imageId = image.imageId;
                        if(image.imageId.substr(0, 4) !== 'http') {
                            imageId = "http://"+location.host+"/wado/wado?requestType=WADO&studyUID="+data.studyId+"&seriesUID="+series.seriesUid+"&objectUID=" + image.imageId;
                        }
                        stack.imageIds.push(imageId);
                        
                        stack.dicomInfo.push('Sin datos. Vuelva a intentarlo');
                        var wadoURL = imageId;
                        wadoURL += '&contentType=application%2Fdicom&transferSyntax=1.2.840.10008.1.2.4.51';
                        get_dicom_tags(wadoURL);
                        
                    });
            	//}
            	seriesIndex++;
            	stacks.push(stack);
            	
            });
            

			
//...........................................................................................................            
            
            
         // resize the parent div of the viewport to fit the screen
            var imageViewer = $(studyViewer).find('.imageViewer')[0];
            var viewportWrapper = $(imageViewer).find('.viewportWrapper')[0];
            var parentDiv = $(studyViewer).find('.viewer')[0];
            viewportWrapper.style.width = (parentDiv.style.width - 10) + "px";
            viewportWrapper.style.height= ($('div#viewer-tabs').height()-100) + "px";
            var studyRow = $(studyViewer).find('.studyRow')[0];
            var width = $(studyRow).width();
            $(parentDiv).width(width - 170);
            viewportWrapper.style.width = (parentDiv.style.width - 10) + "px";
            viewportWrapper.style.height= ($('div#viewer-tabs').height()-100) + "px";
            
            
            
            
         // image enable the dicomImage element and activate a few tools
            var bottomLeft = $(childDivs[2]).find('div');
            var bottomRight = $(childDivs[3]).find('div');
            
            function onNewImage(e, eventData) {
                // if we are currently playing a clip then update the FPS
                var playClipToolData = cornerstoneTools.getToolState(element, 'playClip');
                if(playClipToolData !== undefined && playClipToolData.data.length > 0 && playClipToolData.data[0].intervalId !== undefined && eventData.frameRate !== undefined) {
                	$(bottomLeft[0]).text("FPS: " + Math.round(eventData.frameRate));
                } else {
                    if($(bottomLeft[0]).text().length > 0) {
                        $(bottomLeft[0]).text("");
                    }
                }
                $(bottomLeft[1]).text("Cant. Imágenes: " + stacks[currentStackIndex].imageIds.length);
                $(bottomLeft[2]).text("Imagen #: " + (stacks[currentStackIndex].currentImageIdIndex + 1) + "/" + stacks[currentStackIndex].imageIds.length);
                
                var imageId = stacks[currentStackIndex].imageIds[stacks[currentStackIndex].currentImageIdIndex];
                var n = imageId.indexOf('objectUID=') + 10;
                imageId = imageId.substr(n,imageId.length);
                setTimeout( function(){
                	if($('input#img'+imageId.replace(/\./g,'_')).length>0){
                		eventData.image.columnPixelSpacing = calculate_pixel_spacing($('input#img'+imageId.replace(/\./g,'_')).val());
                		eventData.image.rowPixelSpacing = calculate_pixel_spacing($('input#img'+imageId.replace(/\./g,'_')).val());
                		$('input#img'+imageId.replace(/\./g,'_')).remove();
                	}
                	if($('input#dcm'+imageId.replace(/\./g,'_')).length>0){
                		stacks[currentStackIndex].dicomInfo[stacks[currentStackIndex].currentImageIdIndex] =
                			$('input#dcm'+imageId.replace(/\./g,'_')).val();
                		$('input#dcm'+imageId.replace(/\./g,'_')).remove();
                	}
                }, 800);
                $(loading).remove();
            }
            $(element).on("CornerstoneNewImage", onNewImage);
            fnOnNewImage = onNewImage;
            
            function onImageRendered(e, eventData) {
            	$(bottomRight[0]).text("Zoom:" + eventData.viewport.scale.toFixed(2));
            	$(bottomRight[1]).text("WW/WL:" + Math.round(eventData.viewport.voi.windowWidth) + "/" + Math.round(eventData.viewport.voi.windowCenter));
            	//$(bottomLeft[1]).text("Render Time:" + eventData.renderTimeInMs + " ms");
            }
            $(element).on("CornerstoneImageRendered", onImageRendered);
            fnOnRenderImage = onImageRendered;
            
            var imageId = stacks[currentStackIndex].imageIds[0];

            // image enable the dicomImage element
              cornerstone.enable(element);
              cornerstone.loadAndCacheImage(imageId).then(function(image) {
              cornerstone.displayImage(element, image);
              cornerstoneTools.mouseInput.enable(element);
              cornerstoneTools.mouseWheelInput.enable(element);
              cornerstoneTools.touchInput.enable(element);

              // Enable all tools we want to use with this element
              cornerstoneTools.wwwc.activate(element, 1); // ww/wc is the default tool for left mouse button
              cornerstoneTools.pan.activate(element, 2); // pan is the default tool for middle mouse button
              cornerstoneTools.zoom.activate(element, 4); // zoom is the default tool for right mouse button
              cornerstoneTools.probe.enable(element);
              cornerstoneTools.length.enable(element);
              cornerstoneTools.ellipticalRoi.enable(element);
              cornerstoneTools.rectangleRoi.enable(element);
              cornerstoneTools.wwwcTouchDrag.activate(element);
              cornerstoneTools.zoomTouchPinch.activate(element);
              
              // stack tools
              cornerstoneTools.addStackStateManager(element, ['playClip']);
              cornerstoneTools.addToolState(element, 'stack', stacks[0]);
              cornerstoneTools.stackScrollWheel.activate(element);
              cornerstoneTools.stackPrefetch.enable(element);
              
              
              function disableAllTools()
              {
                  cornerstoneTools.wwwc.disable(element);
                  cornerstoneTools.pan.activate(element, 2); // 2 is middle mouse button
                  cornerstoneTools.zoom.activate(element, 4); // 4 is right mouse button
                  cornerstoneTools.probe.deactivate(element, 1);
                  cornerstoneTools.length.deactivate(element, 1);
                  cornerstoneTools.ellipticalRoi.deactivate(element, 1);
                  cornerstoneTools.rectangleRoi.deactivate(element, 1);
                  cornerstoneTools.stackScroll.deactivate(element, 1);
                  cornerstoneTools.wwwcTouchDrag.deactivate(element);
                  cornerstoneTools.zoomTouchDrag.deactivate(element);
                  cornerstoneTools.panTouchDrag.deactivate(element);
                  cornerstoneTools.stackScrollTouchDrag.deactivate(element);
                  //$('ul.dropdown-menu').slideUp();
              }

              var buttons = $(studyViewer).find('button');
              // Tool button event handlers that set the new active tool
              $(buttons[0]).on('click touchstart',function() {
                  disableAllTools();
                  cornerstoneTools.wwwc.activate(element, 1);
                  cornerstoneTools.wwwcTouchDrag.activate(element);
              });
              $(buttons[1]).on('click touchstart',function() {
                  disableAllTools();
                  var viewport = cornerstone.getViewport(element);
                  if(viewport.invert === true) {
                      viewport.invert = false;
                  }
                  else {
                      viewport.invert = true;
                  }
                  cornerstone.setViewport(element, viewport);
              });
              $(buttons[2]).on('click touchstart',function() {
                  disableAllTools();
                  cornerstoneTools.zoom.activate(element, 5); // 5 is right mouse button and left mouse button
                  cornerstoneTools.zoomTouchDrag.activate(element);
              });
              $(buttons[3]).on('click touchstart',function() {
                  disableAllTools();
                  cornerstoneTools.pan.activate(element, 3); // 3 is middle mouse button and left mouse button
                  cornerstoneTools.panTouchDrag.activate(element);
              });
              $(buttons[4]).on('click touchstart',function() {
            	  disableAllTools();
            	  var viewport = cornerstone.getViewport(element);
                  viewport.rotation+=90;
                  cornerstone.setViewport(element, viewport);
                  return false;
              });
              $(buttons[5]).on('click touchstart',function() {
                  disableAllTools();
                  cornerstoneTools.length.activate(element, 1);
              });
              $(buttons[6]).on('click touchstart',function() {
            	  disableAllTools();
                  cornerstoneTools.ellipticalRoi.activate(element, 1);
              });
              $(buttons[7]).on('click touchstart',function() {
            	  disableAllTools();
                  cornerstoneTools.rectangleRoi.activate(element, 1);
              });
              /*$(buttons[8]).on('click touchstart',function() {
            	  disableAllTools();
            	  if(!$('ul.dropdown-menu').is(':visible')){
            		  $('ul.dropdown-menu').slideDown();
            	  }
              });*/
              $(buttons[8]).on('click touchstart',function() {
            	  disableAllTools();
            	  $('div#dicom-tags').text('');
            	  $('div#dicom-tags').html('');
            	  $('div#dicom-tags').html(stacks[currentStackIndex].dicomInfo[stacks[currentStackIndex].currentImageIdIndex]);
            	  openDialog('dicom-info.html', 'DICOM Tags', 650, 750);
              });
              $(buttons[9]).on('click touchstart',function() {
            	  var frameRate = stacks[currentStackIndex].frameRate;
                  if(frameRate === undefined) {
                      frameRate = 5;
                  }
                  cornerstoneTools.playClip(element, 5);
              });
              $(buttons[10]).on('click touchstart',function() {
            	  cornerstoneTools.stopClip(element);
              });
              /*$.each($('ul.dropdown-menu').find('a'),function(i,a){
            	  $(a).on('click',function(){ switch_display($(this).attr('value')); });
              });*/
              
              $(buttons[0]).tooltip();
              $(buttons[1]).tooltip();
              $(buttons[2]).tooltip();
              $(buttons[3]).tooltip();
              $(buttons[4]).tooltip();
              $(buttons[5]).tooltip();
              $(buttons[6]).tooltip();
              $(buttons[7]).tooltip();
              $(buttons[8]).tooltip();
              $(buttons[9]).tooltip();
              $(buttons[10]).tooltip();
              //$(buttons[11]).tooltip();
              
              
            var seriesList = $(studyViewer).find('.thumbnails')[0];
            stacks.forEach(function(stack) {
            	var seriesEntry = 	'<a class="list-group-item ui-draggable ui-draggable-handle active" + ' +
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
            	
            	var seriesElement = $(seriesEntry).appendTo(seriesList);
            	var thumbnail = $(seriesElement).find('div')[0];
                cornerstone.enable(thumbnail);
                cornerstone.loadAndCacheImage(stacks[stack.seriesIndex].imageIds[0]).then(function(image) {
                    if(stack.seriesIndex === 0) {
                        $(seriesElement).addClass('active');
                    }
                    cornerstone.displayImage(thumbnail, image);
                });
                
                $(seriesElement).on('click touchstart', function () {
                    $(loading).appendTo('body');
                	// make this series visible
                	var activeThumbnails = $(seriesList).find('a').each(function() {
                        $(this).removeClass('active');
                    });
                    $(seriesElement).addClass('active');

                    cornerstoneTools.stopClip(element);
                    cornerstoneTools.stackScroll.disable(element);
                    cornerstoneTools.stackScroll.enable(element, stacks[stack.seriesIndex], 0);
                    cornerstone.loadAndCacheImage(stacks[stack.seriesIndex].imageIds[0]).then(function(image) {
                        var defViewport = cornerstone.getDefaultViewport(element, image);
                        currentStackIndex = stack.seriesIndex;
                        cornerstone.displayImage(element, image, defViewport);
                        cornerstone.fitToWindow(element);
                        var stackState = cornerstoneTools.getToolState(element, 'stack');
                        stackState.data[0] = stacks[stack.seriesIndex];
                        stackState.data[0].currentImageIdIndex = 0;
                        cornerstoneTools.stackPrefetch.enable(element);
                        

                        if(data.modality == 'XA'){
                        if(stacks[stack.seriesIndex].frameRate !== undefined) {
                            cornerstoneTools.playClip(element, stacks[stack.seriesIndex].frameRate);
                        }}
                    });
                });
            });
              
              
  //...........................................................................................................            
              
              function resizeStudyViewer() {
              
            	  //if($('ul.dropdown-menu').attr('layout')=='1x1'){
            		  var studyRow = $(studyViewer).find('.studyRow')[0];
                	  var height = $(studyRow).height();
                      var width = $(studyRow).width();
                      
                      $('div.thumbnailSelector').height($('div#viewer-tabs').height()-70);
                      $(parentDiv).width(width - 130);
                      viewportWrapper.style.height= ($('div#viewer-tabs').height()-100) + "px";
                      cornerstone.resize(element, true);
            	  
            	  /*} else {
            		  var studyRow = $(studyViewer).find('.studyRow')[0];
                	  var height = $(studyRow).height();
                      var width = $(studyRow).width();
                      var theFirst = viewportWrapper;
                      var theSecond = $(parentDiv).find('.viewportWrapper')[1];
                      var secElement = $(theSecond).find('.viewport')[0];
                      
                      $('div.thumbnailSelector').height($('div#viewer-tabs').height()-70);
                      $(parentDiv).width(width - 130);
                      theFirst.style.height= ($('div#viewer-tabs').height()-100) + "px";
                      theSecond.style.height= ($('div#viewer-tabs').height()-100) + "px";
                      cornerstone.resize(element, true);
                      cornerstone.resize(secElement, true);
            	  }*/
            	  
              }
              resizeStudyViewer();
              window.addEventListener('resize', resizeStudyViewer);
            
            });
            
            /*
            function switch_display(layout) {
          		
          		if( layout == '1x2' ){
          			if($('ul.dropdown-menu').attr('layout')=='1x2'){$('ul.dropdown-menu').slideUp();return;}
          			$('ul.dropdown-menu').attr('layout',layout);
          			
                    var width = $(studyRow).width();
                    viewportWrapper.style.width = ((width-140)/2.02) + "px";
                    viewportWrapper.style.height= ($('div#viewer-tabs').height()-100) + "px";
                    $(viewportWrapper).css('float','left');
          			
                    var columnTwo = $(viewportWrapper).clone();
          			$(columnTwo).find('canvas').remove();
          			var secElement = $(columnTwo).find('.viewport')[0];
          			
          			$(viewportWrapper).parent().append(columnTwo);
                    $(secElement).on("CornerstoneNewImage", onNewImage);
                    $(secElement).on("CornerstoneImageRendered", onImageRendered);
                    cornerstone.enable(secElement);
                    if(stacks[1] !== undefined){
                    	var imageId = stacks[1].imageIds[0];
                    	cornerstone.loadAndCacheImage(imageId).then(function(image) {
                            cornerstone.displayImage(secElement, image);
                    	});
                    }
                      
          			cornerstone.resize(element, true);

                    $(viewportWrapper).addClass('viewer-selected');
          			$(columnTwo).addClass('viewer-not-selected');
                    $(viewportWrapper).on('click',function(){
                      	$(columnTwo).removeClass('viewer-selected');
                      	$(columnTwo).addClass('viewer-not-selected');
                      	$(this).removeClass('viewer-not-selected');
                      	$(this).addClass('viewer-selected');
                    });
                    $(columnTwo).on('click',function(){
                      	$(viewportWrapper).removeClass('viewer-selected');
                      	$(viewportWrapper).addClass('viewer-not-selected');
                      	$(this).removeClass('viewer-not-selected');
                      	$(this).addClass('viewer-selected');
                    });
                    $(parentDiv).width(width-130);
          			
          		} else {
          			if($('ul.dropdown-menu').attr('layout')=='1x1'){$('ul.dropdown-menu').slideUp();return;}
          			$('ul.dropdown-menu').attr('layout',layout);
          			
          			var theSecond = $(parentDiv).find('.viewportWrapper')[1];
          			$(theSecond).remove();
                    var width = $(studyRow).width();
                    $(parentDiv).width(width-130);
                    viewportWrapper.style.width = (width-140) + "px";
                    viewportWrapper.style.height= ($('div#viewer-tabs').height()-100) + "px";
          			cornerstone.resize(element, true);
          			$(viewportWrapper).removeClass('viewer-selected');
                  	$(viewportWrapper).removeClass('viewer-not-selected');
                  	$(viewportWrapper).unbind('click');
          		}
          		$('ul.dropdown-menu').slideUp();
          	}
            */
            
		}).fail(function(obj,msj,err){alert(err+'\n'+msj);$(loadingImages).remove();});
	}
	
	function calculate_pixel_spacing(spacing){spacing=spacing.toLowerCase();spacing=spacing.replace(/\\/g,'/');try{var firstVar=spacing.split('/')[0];var secondVar,thirdVar,aux,finalPixelSpacing;if(firstVar.split('e')[1]!==undefined){secondVar=parseFloat(firstVar.split('e')[0]);thirdVar=parseInt(firstVar.split('e')[1]);aux=Math.pow(10,thirdVar);finalPixelSpacing=secondVar*aux;}else{finalPixelSpacing=parseFloat(firstVar.split('e')[0]);}}catch(err){alert(err);}return finalPixelSpacing;}
	
	/*
	function switch_display( a,layout ) {
		
		if( layout == '1x2' ){
			if($('ul.dropdown-menu').attr('layout')=='1x2'){$('ul.dropdown-menu').slideUp();return;}
			$('ul.dropdown-menu').attr('layout',layout);
			
			var studyRow = $(a).closest('.studyRow')[0];
			var parentDiv = $(a).closest('.viewer')[0];
			var viewportWrapper = $(parentDiv).find('.viewportWrapper')[0];
			var element = $(viewportWrapper).find('.viewport')[0];
			
            var height = $(studyRow).height();
            var width = $(studyRow).width();
            viewportWrapper.style.width = ((width-140)/2.02) + "px";
            viewportWrapper.style.height= ($('div#viewer-tabs').height()-100) + "px";
            $(viewportWrapper).css('float','left');
			var columnTwo = $(viewportWrapper).clone();
			$(columnTwo).find('canvas').remove();
			var secElement = $(columnTwo).find('.viewport')[0];
			
			$(viewportWrapper).parent().append(columnTwo);
            $(secElement).on("CornerstoneNewImage", fnOnNewImage);
            $(secElement).on("CornerstoneImageRendered", fnOnRenderImage);
            cornerstone.enable(secElement);
            
			cornerstone.resize(element, true);

            $(viewportWrapper).addClass('viewer-selected');
			$(columnTwo).addClass('viewer-not-selected');
            $(viewportWrapper).on('click',function(){
            	$(columnTwo).removeClass('viewer-selected');
            	$(columnTwo).addClass('viewer-not-selected');
            	$(this).removeClass('viewer-not-selected');
            	$(this).addClass('viewer-selected');
            });
            $(columnTwo).on('click',function(){
            	$(viewportWrapper).removeClass('viewer-selected');
            	$(viewportWrapper).addClass('viewer-not-selected');
            	$(this).removeClass('viewer-not-selected');
            	$(this).addClass('viewer-selected');
            });
            $(parentDiv).width(width-135);
			
		} else {
			if($('ul.dropdown-menu').attr('layout')=='1x1'){$('ul.dropdown-menu').slideUp();return;}
			$('ul.dropdown-menu').attr('layout',layout);
			
			var studyRow = $(a).closest('.studyRow')[0];
			var parentDiv = $(a).closest('.viewer')[0];
			var viewportWrapper = $(parentDiv).find('.viewportWrapper')[0];
			var theSecond = $(parentDiv).find('.viewportWrapper')[1];
			var element = $(viewportWrapper).find('.viewport')[0];
			$(theSecond).remove();
            var height = $(studyRow).height();
            var width = $(studyRow).width();
            $(parentDiv).width(width-130);
            viewportWrapper.style.width = (width-140) + "px";
            viewportWrapper.style.height= ($('div#viewer-tabs').height()-100) + "px";
			cornerstone.resize(element, true);
			$(viewportWrapper).removeClass('viewer-selected');
        	$(viewportWrapper).removeClass('viewer-not-selected');
        	$(viewportWrapper).unbind('click');
		}

        $('ul.dropdown-menu').slideUp();
	}
	*/

	
	
	
	