package app.dcm.dto;

import java.util.List;

public class StudySeriesListDTO {
	
	private String patientName;
	private String patientId;
	private String studyDate;
	private String modality;
	private String studyDescription;
	private int numImages;
	private String studyId;
	private List<SeriesListDTO> seriesList;
	
	public StudySeriesListDTO(String patientName, String patientId,
			String studyDate, String modality, String studyDescription,
			int numImages, String studyId, List<SeriesListDTO> seriesList) {
		super();
		this.patientName = patientName;
		this.patientId = patientId;
		this.studyDate = studyDate;
		this.modality = modality;
		this.studyDescription = studyDescription;
		this.numImages = numImages;
		this.studyId = studyId;
		this.seriesList = seriesList;
	}
	
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
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
	public int getNumImages() {
		return numImages;
	}
	public void setNumImages(int numImages) {
		this.numImages = numImages;
	}
	public String getStudyId() {
		return studyId;
	}
	public void setStudyId(String studyId) {
		this.studyId = studyId;
	}
	public List<SeriesListDTO> getSeriesList() {
		return seriesList;
	}
	public void setSeriesList(List<SeriesListDTO> seriesList) {
		this.seriesList = seriesList;
	}
}
