
function setupButtons(studyViewer) {
    // Get the button elements
    var buttons = $(studyViewer).find('button');
    var slider = $(studyViewer).find('div.frame-rate-slider');
    var checkSlider = $(studyViewer).find('input.enable-frame-rate');

    // Tool button event handlers that set the new active tool

    // WW/WL
    $(buttons[0]).on('click touchstart', function() {
        disableAllTools();
        forEachViewport(function(element) {
            cornerstoneTools.wwwc.activate(element, 1);
            cornerstoneTools.wwwcTouchDrag.activate(element);
        });
        $(this).addClass('current');
    });

    // Invert
    $(buttons[1]).on('click touchstart', function() {
        disableAllTools();
        forEachViewport(function(element) {
            var viewport = cornerstone.getViewport(element);
            // Toggle invert
            if (viewport.invert === true) {
                viewport.invert = false;
            } else {
                viewport.invert = true;
            }
            cornerstone.setViewport(element, viewport);
        });
        $(this).addClass('current');
    });

    // Zoom
    $(buttons[2]).on('click touchstart', function() {
        disableAllTools();
        forEachViewport(function(element) {
            cornerstoneTools.zoom.activate(element, 5); // 5 is right mouse button and left mouse button
            cornerstoneTools.zoomTouchDrag.activate(element);
        });
        $(this).addClass('current');
    });

    // Pan
    $(buttons[3]).on('click touchstart', function() {
        disableAllTools();
        forEachViewport(function(element) {
            cornerstoneTools.pan.activate(element, 3); // 3 is middle mouse button and left mouse button
            cornerstoneTools.panTouchDrag.activate(element);
        });
        $(this).addClass('current');
    });

    // Stack scroll
    $(buttons[4]).on('click touchstart', function() {
        disableAllTools();
        forEachViewport(function(element) {
            cornerstoneTools.stackScroll.activate(element, 1);
            cornerstoneTools.stackScrollTouchDrag.activate(element);
        });
        $(this).addClass('current');
    });

    // Length measurement
    $(buttons[5]).on('click touchstart', function() {
        disableAllTools();
        forEachViewport(function(element) {
            cornerstoneTools.length.activate(element, 1);
        });
        $(this).addClass('current');
    });

    // Angle measurement
    $(buttons[6]).on('click touchstart', function() {
        disableAllTools();
        forEachViewport(function(element) {
            cornerstoneTools.angle.activate(element, 1);
        });
        $(this).addClass('current');
    });

    // Pixel probe
    $(buttons[7]).on('click touchstart', function() {
        disableAllTools();
        forEachViewport(function(element) {
            cornerstoneTools.probe.activate(element, 1);
        });
        $(this).addClass('current');
    });

    // Elliptical ROI
    $(buttons[8]).on('click touchstart', function() {
        disableAllTools();
        forEachViewport(function(element) {
            cornerstoneTools.ellipticalRoi.activate(element, 1);
        });
        $(this).addClass('current');
    });

    // Rectangle ROI
    $(buttons[9]).on('click touchstart', function() {
        disableAllTools();
        forEachViewport(function (element) {
            cornerstoneTools.rectangleRoi.activate(element, 1);
        });
        $(this).addClass('current');
    });
    
    // Reset Image
    $(buttons[10]).on('click touchstart', function() {
        forEachViewport(function (element) {
        	var enabledImage = cornerstone.getEnabledElement(element);
        	var image = enabledImage.image;
        	var defViewport = cornerstone.getDefaultViewport(element, image);
        	cornerstone.setViewport(element, defViewport);
        	cornerstone.fitToWindow(element);
        	cornerstoneTools.clearToolState(element, 'length');
        	cornerstoneTools.clearToolState(element, 'angle');
        	cornerstoneTools.clearToolState(element, 'probe');
        	cornerstoneTools.clearToolState(element, 'ellipticalRoi');
        	cornerstoneTools.clearToolState(element, 'rectangleRoi');
            cornerstone.updateImage(element);
        });
    });

    // Play clip
    $(buttons[11]).on('click touchstart', function() {
        forEachViewport(function(element) {
          var stackState = cornerstoneTools.getToolState(element, 'stack');
          var frameRate = stackState.data[0].frameRate;
          cornerstoneTools.clearToolState(element, 'playClip');
          // Play at a default 10 FPS if the framerate is not specified
          if (frameRate === undefined || frameRate<=0) {
            frameRate = 5;
          }
          if($(checkSlider).prop('checked')){
        	  frameRate=parseInt($(slider).slider('option','value'));
          }
          cornerstoneTools.playClip(element, frameRate);
        });
    });

    // Stop clip
    $(buttons[12]).on('click touchstart', function() {
        forEachViewport(function(element) {
            cornerstoneTools.stopClip(element);
        });
    });
    
    $(buttons[13]).on('click touchstart', function() {
        if(!$('ul.dropdown-menu').is(':visible')) $('ul.dropdown-menu').slideDown();
        else $('ul.dropdown-menu').slideUp();
    });

    // Tooltips
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
    $(buttons[11]).tooltip();
    $(buttons[12]).tooltip();
    $(buttons[13]).tooltip();

};