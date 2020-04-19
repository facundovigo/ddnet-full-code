package app.bd.dto;

public class StudyDTO {
	
	private String studyId;
	private String patName;
	private String patId;
	private String studyDate;
	private String studyMod;
	private String studyDesc;
	private int numSeries;
	private int numImgs;
	
	public StudyDTO(String studyId, String patName, String patId,
			String studyDate, String studyMod, String studyDesc,
			int numSeries, int numImgs) {
		super();
		this.studyId = studyId;
		this.patName = patName;
		this.patId = patId;
		this.studyDate = studyDate;
		this.studyMod = studyMod;
		this.studyDesc = studyDesc;
		this.numSeries = numSeries;
		this.numImgs = numImgs;
	}

	public String getStudyId() {
		return studyId;
	}

	public void setStudyId(String studyId) {
		this.studyId = studyId;
	}

	public String getPatName() {
		return patName;
	}

	public void setPatName(String patName) {
		this.patName = patName;
	}

	public String getPatId() {
		return patId;
	}

	public void setPatId(String patId) {
		this.patId = patId;
	}

	public String getStudyDate() {
		return studyDate;
	}

	public void setStudyDate(String studyDate) {
		this.studyDate = studyDate;
	}

	public String getStudyMod() {
		return studyMod;
	}

	public void setStudyMod(String studyMod) {
		this.studyMod = studyMod;
	}

	public String getStudyDesc() {
		return studyDesc;
	}

	public void setStudyDesc(String studyDesc) {
		this.studyDesc = studyDesc;
	}

	public int getNumSeries() {
		return numSeries;
	}

	public void setNumSeries(int numSeries) {
		this.numSeries = numSeries;
	}

	public int getNumImgs() {
		return numImgs;
	}

	public void setNumImgs(int numImgs) {
		this.numImgs = numImgs;
	}
}
