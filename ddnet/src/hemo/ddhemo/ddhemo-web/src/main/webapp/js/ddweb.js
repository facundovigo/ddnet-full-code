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