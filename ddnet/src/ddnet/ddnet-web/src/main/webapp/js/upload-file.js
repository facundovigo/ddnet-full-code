

function chargeFileUploader(studyID){

	$("#fileuploader").uploadFile({
		url: 'rest/studies/' + studyID + '/files/',
		fileName: "study-file",
		autoSubmit: true,
		showDone: false,
		maxFileSize: 52428800,
	    dragDropStr: "<span><b>Arrastre Archivos aqu&iacute;</b></span>",
	    abortStr: "Abortar",
	    cancelStr: "Cancelar",
	    deletelStr: "Eliminar",
	    doneStr: "Finalizado",
	    multiDragErrorStr: "No se permite arrastrar y soltar m&uacute;ltiples archivos. Realice la acci&oacute;n de a un archivo a la vez.",
	    extErrorStr: "no est&aacute; permitido este tipo de archivo. Extensiones de archivo permitidas: ",
	    sizeErrorStr: " super&oacute; el tama&ntilde;o m&aacute;ximo de archivo. Tama&ntilde;o m&aacute;ximo permitido: ",
	    uploadErrorStr: "No permitido",
	    showStatusAfterSuccess: false,
	    showStatusAfterError: false,
		afterUploadAll:function(){ loadFiles(studyID); },
		onError: function(files,status,errMsg){ alert('Ha ocurrido un error cargando los nuevos archivos. Por favor, reintente la operación.'); }	
	});
}


function chargeOMUploader(studyID){

	$("#omuploader").uploadFile({
		url: 'rest/studies/' + studyID + '/files/ordenMedica',
		fileName: "study-file",
		autoSubmit: true,
		showDone: false,
	    dragDropStr: "<span><b>Arrastre Orden M&eacute;dica aqu&iacute;</b></span>",
	    abortStr: "Abortar",
	    cancelStr: "Cancelar",
	    deletelStr: "Eliminar",
	    doneStr: "Finalizado",
	    multiDragErrorStr: "No se permite arrastrar y soltar m&uacute;ltiples archivos. Realice la acci&oacute;n de a un archivo a la vez.",
	    extErrorStr: "no est&aacute; permitido este tipo de archivo. Extensiones de archivo permitidas: ",
	    sizeErrorStr: " super&oacute; el tama&ntilde;o m&aacute;ximo de archivo. Tama&ntilde;o m&aacute;ximo permitido: ",
	    uploadErrorStr: "No permitido",
	    showStatusAfterSuccess: false,
	    showStatusAfterError: false,
		afterUploadAll:function(){ loadFiles(studyID); },
		onError: function(files,status,errMsg){ 
			if(errMsg == 'No Aceptable') alert(errMsg+': '+files+'\nFormatos aceptados: jpg, jpeg, png, gif');
			else{ alert('Ha ocurrido un error cargando la Orden Médica. Por favor, reintente la operación.');}
		}
	});
}