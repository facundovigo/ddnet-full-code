
var uploader= null;

	$(document).ready(function(){
		
		var patientData= window.parent.$('div#dynamicModalDialog').data('patientData');
		var documento= patientData.docType+' '+patientData.docNumber;
		
		uploader = $("#omuploader").uploadFile({
			url: 'rest/practica/'+documento+'/files',
			fileName: "orden-medica-paciente",
			autoSubmit: false,
			showDone: true,
		    dragDropStr: false,
		    uploadStr: 'Cargar',
		    cancelStr: 'Borrar',
		    acceptFiles:"image/*",
		    showPreview:true,
		    previewHeight: "100px",
		    previewWidth: "100px",
		    multiDragErrorStr: "No se permite arrastrar y soltar m&uacute;ltiples archivos. Realice la acci&oacute;n de a un archivo a la vez.",
		    extErrorStr: "no est&aacute; permitido este tipo de archivo. Extensiones de archivo permitidas: ",
		    sizeErrorStr: " super&oacute; el tama&ntilde;o m&aacute;ximo de archivo. Tama&ntilde;o m&aacute;ximo permitido: ",
		    uploadErrorStr: "No permitido",
		    showStatusAfterSuccess: false,
		    showStatusAfterError: false,
		    
		    onSelect: add_handler,
		    afterUploadAll:function(){},
			
			onError: function(files,status,errMsg){console.log(errMsg);}
		});
		
	});
	
	
	
	function add_handler(){
		
		
	}
