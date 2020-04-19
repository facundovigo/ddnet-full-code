package app.dcm.dto;

public class StudyListDTO {
	
	private String studyUID;
	private String patientName;
	private String patientID;
	private String studyDate;
	private String modality;
	private String studyDescription;
	private int studyCantImages;
	
	public StudyListDTO(String studyUID, String patientName, String patientID, 
			String studyDate, String modality, String studyDescription, int studyCantImages) {
		super();
		this.studyUID = studyUID;
		this.patientName = patientName;
		this.patientID = patientID;
		this.studyDate = studyDate;
		this.modality = modality;
		this.studyDescription = studyDescription;
		this.studyCantImages = studyCantImages;
	}
	
	public String getStudyUID() {
		return studyUID;
	}

	public void setStudyUID(String studyUID) {
		this.studyUID = studyUID;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientID() {
		return patientID;
	}

	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}

	public String getStudyDate() {
		return studyDate;
	}

	public void setStudyDate(String studyDate) {
		this.studyDate = studyDate;
	}

	public String getModality() {
		return modality;
	}

	public void setModality(String modality) {
		this.modality = modality;
	}

	public String getStudyDescription() {
		return studyDescription;
	}

	public void setStudyDescription(String studyDescription) {
		this.studyDescription = studyDescription;
	}

	public int getStudyCantImages() {
		return studyCantImages;
	}

	public void setStudyCantImages(int studyCantImages) {
		this.studyCantImages = studyCantImages;
	}
	
}
