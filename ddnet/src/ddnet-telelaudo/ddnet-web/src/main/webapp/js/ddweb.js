function doBlockUI() {
	$.blockUI({ message: 
		'<h4><img src="../images/loading.gif" height="20px" width="20px" />&nbsp;&nbsp;Por favor, aguarde un momento...</h4>' });
};

function doUnblockUI() {
	$.unblockUI();
};

function actionIsAllowed(userinfo, iid, action) {
	var allowed = false;
	$.each(userinfo.institutions, function(i, v) {
	    if (v.institution.id == iid) {	    	
	    	$.each(v.role.allowedActions, function(i2, v2) {
	    		if (v2.name == action) {
	    			allowed = true;
	    			return;
	    		}	    			
	    	});	    	
	        return;
	    }
	});	
	return allowed;
}

function getStudyImagesFor(study, quality, seriesID) {
	var qualityValue = quality == "loseless" ? 0 : 1;
	
	var url = location.protocol + '//' + location.host + 
		'/ddnet-web/restricted/rest/studies/' + (study.id || study.studyID) + '/media?quality=' + qualityValue;

	var seriesDescription = "";		
	if (seriesID) {
		$.each(seriesID, function(index, s) {
			url = url + '&series=' + s;
			if (study.series && study.series.length > 0) {
				$.each(study.series, function(i, si) {
					if (s == si.serieID && si.description)
						seriesDescription = seriesDescription + "[" + si.description + "]";
				});
			}				    		
	    });				
	}
		
	if (ddUserAgent.isUserAgentActive()) {
		url = url + "&options=nonexec";
		
		var callingAET = '';
		var viewerHostname = '';
		var viewerPort = 104;
		var viewerAET = '';
		
		if(window.userinfo.prop != null){
			if (window.userinfo.prop.callingAet)
				callingAET = window.userinfo.prop.callingAet;
			if (window.userinfo.prop.hostname)
				viewerHostname = window.userinfo.prop.hostname;
			if (window.userinfo.prop.port)
				viewerPort = parseInt(window.userinfo.prop.port);
			if (window.userinfo.prop.aet)
				viewerAET = window.userinfo.prop.aet;
		}
		
		ddUserAgent.sendUserAgentCommand('retrieve-images', 
			{
				"patientName": (study.pn || study.patientName),
				"description":(study.desc || study.description) + " ("+ quality.toUpperCase() +")" + (seriesDescription != "" ? ("-SERIES-" + seriesDescription) : ""),
				"url": url,
				"callingAET": callingAET,
				"viewerHostname": viewerHostname,
				"viewerPort": viewerPort,
				"viewerAET": viewerAET
			}
		);
	} else {
		warnUserAgentNotStarted();		        
	}				
}

function warnUserAgentNotStarted() {
	alert('DDNET UserAgent no se inició en tiempo. Por favor, instale y/o inicie la aplicación y reintente la operación.');				
}