package pgsql_bd.DTO;

public class LegacyStudyDTO {
	
	/**
	 * TABLA study
	 */
	
	private long id;			//COLUMNA "pk"
	private long patientID;		//COLUMNA "patient_fk"
	private String studyUID;	//COLUMNA "study_iuid"
	private String studyDate;	//COLUMNA "study_datetime"
	private String studyDesc;	//COLUMNA "study_desc"
	private String studyMod;	//COLUMNA "mods_in_study"
	
	public LegacyStudyDTO(	long id, long patientID, String studyUID,
						String studyDate, String studyDesc, String studyMod){
		
		this.id = id;
		this.patientID = patientID;
		this.studyUID = studyUID;
		this.studyDate = studyDate;
		this.studyDesc = studyDesc;
		this.studyMod = studyMod;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPatientID() {
		return patientID;
	}

	public void setPatientID(long patientID) {
		this.patientID = patientID;
	}

	public String getStudyUID() {
		return studyUID;
	}

	public void setStudyUID(String studyUID) {
		this.studyUID = studyUID;
	}

	public String getStudyDate() {
		return studyDate;
	}

	public void setStudyDate(String studyDate) {
		this.studyDate = studyDate;
	}

	public String getStudyDesc() {
		return studyDesc;
	}

	public void setStudyDesc(String studyDesc) {
		this.studyDesc = studyDesc;
	}

	public String getStudyMod() {
		return studyMod;
	}

	public void setStudyMod(String studyMod) {
		this.studyMod = studyMod;
	}
	
}
