package ddnet.ejb.entities;

public class StudyMedia {

	public static final int LOSSLESS = 0;
	public static final int LOSSY = 1;
	public static final String LOSSLESS_LABEL = "Lossless";
	public static final String LOSSY_LABEL = "Lossy";
	
	private Study study;
	private byte[] contents;
	
	public Study getStudy() {
		return study;
	}
	public void setStudy(Study study) {
		this.study = study;
	}
	
	/**
	 * Array de bytes correspondiente a un archivo .ZIP conteniendo:
	 * <ul>
	 * <li>Todos los archivos DICOM de un estudio</li>
	 * <li>Archivo DICOMDIR indexando los mencionados archivos DICOM</li>
	 * <li>Launchers Win/Linux/Mac para enviar el estudio al visualizador local que haya configurado el usuario solicitante.</li>
	 * </ul>  
	 */	
	public byte[] getContents() {
		return contents;
	}
	public void setContents(byte[] contents) {
		this.contents = contents;
	}

}
