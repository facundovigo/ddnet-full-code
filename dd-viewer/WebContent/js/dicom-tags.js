
            

        	function get_dicom_tags( wadoURL ) {
        		var oReq = new XMLHttpRequest();
        		
        		try {
        	        oReq.open("get", wadoURL, true);
        	    
        	    } catch(err) { return false; }
        	    
        	    oReq.responseType = "arraybuffer";
        	    oReq.onreadystatechange = function(oEvent) {
        	        if(oReq.readyState == 4) {
        	            if(oReq.status == 200) {
        	                var byteArray = new Uint8Array(oReq.response);
        	        	    dumpByteArray(byteArray, wadoURL);
        	           
        	            } else return false;
        	        }
        	    };
        	    
        	    oReq.send();
        	    return false;
        	}
        	
        	
        	function dumpByteArray( byteArray, wadoURL ) {
        		
        	    setTimeout(function() {
        	    	var dataSet;
        	        try {
        	            dataSet = dicomParser.parseDicom(byteArray);
        	            var output = [];
        	            dumpDataSet(dataSet, output, wadoURL);
        	            //dumpEncapsulatedInfo(dataSet);
        	        
        	        } catch(err) {alert(err);}
        	        
        	    },10);
        	}
        	
        	function dumpDataSet( dataSet, output, wadoURL ){
        		
        		//var dicomTags = '';
            	var imageId = wadoURL;
                var n = imageId.indexOf('objectUID=') + 10;
                var l = imageId.indexOf('&contentType');
                imageId = imageId.substr(0,l);
                imageId = imageId.substr(n,imageId.length);
        		
                var pixelSpacing = dataSet.string('x00280030');
                if($('input#img'+imageId.replace(/\./g,'_')).length <= 0){
                	var hidden = '<input type="hidden" id="img' + imageId.replace(/\./g,'_') + '" value="' + pixelSpacing + '">';
                	$('div#dicom-info').append(hidden);
                }
                
        		/*function getTag(tag){
        	        var group = tag.substring(1,5);
        	        var element = tag.substring(5,9);
        	        var tagIndex = ("("+group+","+element+")").toUpperCase();
        	        var attr = TAG_DICT[tagIndex];
        	        return attr;
        	    }*/
        		
        		/*for(var propertyName in dataSet.elements) {
        	        var element = dataSet.elements[propertyName];

        	        var text = '';
        	        var tag = getTag(element.tag);
        	        if(tag === undefined){
        	            text += element.tag;
        	            text += '; length=' + element.length;

        	            if(element.hadUndefinedLength) {
        	                text += ' <strong>(-1)</strong>';
        	            }
        	            if(element.vr) {
        	                text += ' VR=' + element.vr +'; ';
        	            }
        	        }
        	        else
        	        {
        	            text += tag.name;
        	            if(tag.name == 'PixelSpacing') {
        	            	if($('input#img'+imageId.replace(/\./g,'_')).length <= 0){
        	                	var hidden = '<input type="hidden" id="img' + imageId.replace(/\./g,'_') + '" value="' + dataSet.string(propertyName) + '">';
        	                	$('div#dicom-info').append(hidden);
        	                }
        	            	break;
        	            }
        	            text += '(' + element.tag + ') :';
        	        }

        	        if(element.items) {
        	            
        	        }
        	        else {
        	            // use VR to display the right value
        	            var vr;
        	            if(element.vr !== undefined) vr = element.vr;
        	            else {
        	                if(tag !== undefined) vr = tag.vr;
        	            }

        	            if(element.length < 128) {
        	                if(element.vr === undefined && tag === undefined) {
        	                    if(element.length === 2)
        	                    {
        	                        text += ' (' + dataSet.uint16(propertyName) + ')';
        	                        
        	                    }
        	                    else if(element.length === 4)
        	                    {
        	                        text += ' (' + dataSet.uint32(propertyName) + ')';
        	                    }

        	                    var str = dataSet.string(propertyName);
        	                    var stringIsAscii = isASCII(str);

        	                    if(stringIsAscii) {
        	                        if(str !== undefined) {
        	                        	text += '<b>' + str + '</b>';
        	                        	
        	                        }
        	                        
        	                    } else {
        	                        if(element.length !== 2 && element.length !== 4) text += '<i>binary data</i>';
        	                    }
        	                
        	                } else {
        	                    function isStringVr(vr)
        	                    {
        	                        if(vr === 'AT'
        	                                || vr === 'FL'
        	                                || vr === 'FD'
        	                                || vr === 'OB'
        	                                || vr === 'OF'
        	                                || vr === 'OW'
        	                                || vr === 'SI'
        	                                || vr === 'SQ'
        	                                || vr === 'SS'
        	                                || vr === 'UL'
        	                                || vr === 'US'
        	                                )
        	                        {
        	                            return false;
        	                        }
        	                        return true;
        	                    }

        	                    if(isStringVr(vr))
        	                    {
        	                        var str = dataSet.string(propertyName);
        	                        var stringIsAscii = isASCII(str);

        	                        if(stringIsAscii) {
        	                            if(str !== undefined) text += '<b>' + str + '</b>';
        	                        
        	                        } else {
        	                            if(element.length !== 2 && element.length !== 4) text += '<i>binary data</i>';
        	                        }
        	                    }
        	                    else if (vr == 'US')
        	                    {
        	                        text += dataSet.uint16(propertyName);
        	                    }
        	                    else if(vr === 'SS')
        	                    {
        	                        text += dataSet.int16(propertyName);
        	                    }
        	                    else if (vr == 'UL')
        	                    {
        	                        text += dataSet.uint32(propertyName);
        	                    }
        	                    else if(vr === 'SL')
        	                    {
        	                        text += dataSet.int32(propertyName);
        	                    }
        	                    else if(vr == 'FD')
        	                    {
        	                        text += dataSet.double(propertyName);
        	                    }
        	                    else if(vr == 'FL')
        	                    {
        	                        text += dataSet.float(propertyName);
        	                    }
        	                    else if(vr === 'OB' || vr === 'OW' || vr === 'UN' || vr === 'OF' || vr ==='UT')
        	                    {
        	                        text += '<i>binary data of length ' + element.length + ' and VR ' + vr + '</i>';
        	                    }
        	                    else {
        	                        text += '<i>no display code for VR ' + vr + ' yet, sorry!</i>';
        	                    }
        	                }

        	                if(element.length ===0) {
        	                }
        	            }
        	            else {
        	            	text += '<i>data of length ' + element.length + ' for VR + ' + vr + ' too long to show</i>';
        	            }
        	        }
        	        
        	        	dicomTags += text + '<br>';
        	    }*/
        		/*
        		if($('input#dcm'+imageId.replace(/\./g,'_')).length <= 0){
        			var hidden = '<input type="hidden" id="dcm' + imageId.replace(/\./g,'_') + '" value="' + dicomTags + '">';
        			$('div#dicom-info').append(hidden);
        		}*/
        	}
        	
        	function isTransferSyntaxEncapsulated(transferSyntax) {
        	    if(transferSyntax === "1.2.840.10008.1.2.4.50") // jpeg baseline
        	    {
        	        return true;
        	    }
        	    return false;
        	}
        	
        	function dumpEncapsulatedInfo(dataSet){
        	    var transferSyntax = dataSet.string('x00020010');
        	    if(transferSyntax === undefined) {
        	        return;
        	    }
        	    if(isTransferSyntaxEncapsulated(transferSyntax) === false)
        	    {
        	        return;
        	    }
        	    var numFrames = dataSet.intString('x00280008');
        	    if(numFrames === undefined) {
        	        numFrames = 1;
        	    }
        	    for(var frame=0; frame < numFrames; frame++) {
        	        var pixelData = dicomParser.readEncapsulatedPixelData(dataSet, frame);
        	    }
        	}
        	
        	function isASCII(str) {
        	    return /^[\x00-\x7F]*$/.test(str);
        	}
            
            
            /*
            function get_pixel_spacing(spacing) {
        		
        		spacing = spacing.toLowerCase();
        		spacing = spacing.replace(/\\/g,'/');
        		
        		try{
        			var firstVar = spacing.split('/')[0];
        			var secondVar, thirdVar, aux, finalPixelSpacing;
        			
        			if(firstVar.split('e')[1] !== undefined){
        				secondVar = parseFloat(firstVar.split('e')[0]);
        				thirdVar = parseInt(firstVar.split('e')[1]);
        				aux = Math.pow(10, thirdVar);
        				finalPixelSpacing = secondVar * aux;
        				
        			} else {
        				finalPixelSpacing = parseFloat(firstVar.split('e')[0]);
        			}
        		
        		}catch(err){alert(err);}
        		
        		return finalPixelSpacing;
        	}*/
	
    
	
	
	
	
	