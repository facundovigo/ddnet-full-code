package pgsql_bd.DTO;

public class StudyInfoDTO {
	
	/**
	 * TABLA study
	 */
	
	private Long id;				// Study PK
	private String patName;			// Nombre del Paciente
	private String studyDate;		// Fecha del Estudio
	private String studyMod;		// Modalidad del Estudio
	private boolean isReported;		// Estudio Firmado?
	private String reportBody;		// Texto del Informe
	
	public StudyInfoDTO(long id, String patName, String studyDate, String studyMod,
						boolean isReported, String reportBody){
		
		this.id = id;
		this.patName = patName;
		this.studyDate = studyDate;
		this.studyMod = studyMod;
		this.isReported = isReported;
		this.reportBody = reportBody;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPatName() {
		return patName;
	}

	public void setPatName(String patName) {
		this.patName = patName;
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

	public boolean isReported() {
		return isReported;
	}

	public void setReported(boolean isReported) {
		this.isReported = isReported;
	}

	public String getReportBody() {
		return reportBody;
	}

	public void setReportBody(String reportBody) {
		this.reportBody = reportBody;
	}
}
