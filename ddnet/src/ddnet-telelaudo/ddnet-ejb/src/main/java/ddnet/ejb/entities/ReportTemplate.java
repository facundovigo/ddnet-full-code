package ddnet.ejb.entities;

public class ReportTemplate {
	private static final String PATIENT_ID_VARIABLE = "PATIENT-ID";
	private static final String PATIENT_NAME_VARIABLE = "PATIENT-NAME";
	private static final String PATIENT_AGE_VARIABLE = "PATIENT-AGE";
	private static final String STUDY_ID_VARIABLE = "STUDY-ID";
	private static final String STUDY_DATE_VARIABLE = "STUDY-DATE";
	private static final String STUDY_DESCRIPTION_VARIABLE = "STUDY-DESCRIPTION";
	
	private String name;
	private String body;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}

	public void readVariablesValuesFrom(Study study) {
		if (body == null)
			return;
		
		try {
			body = body.replace(getVariablePlaceholder(PATIENT_ID_VARIABLE), 
					study.getLegacyStudy().getLegacyPatient().getPatientID());
		} catch (Throwable ignore) {}

		try {
			body = body.replace(getVariablePlaceholder(PATIENT_NAME_VARIABLE), 
					study.getLegacyStudy().getLegacyPatient().getName().replace("^^^^", "").replace("^^^", "").replace("^^", "").replace("^", " "));
		} catch (Throwable ignore) {}

		try {
			body = body.replace(getVariablePlaceholder(PATIENT_AGE_VARIABLE), 
					study.getLegacyStudy().getLegacyPatient().getCalculatedAge());
		} catch (Throwable ignore) {}
		
		try {
			body = body.replace(getVariablePlaceholder(STUDY_ID_VARIABLE), 
				study.getLegacyStudy().getStudyID());
		} catch (Throwable ignore) {}
		
		try {
			body = body.replace(getVariablePlaceholder(STUDY_DATE_VARIABLE), 
					study.getLegacyStudy().getFormattedDate());
		} catch (Throwable ignore) {}

		try {
			body = body.replace(getVariablePlaceholder(STUDY_DESCRIPTION_VARIABLE), 
					study.getLegacyStudy().getDescription());
		} catch (Throwable ignore) {}
	}

	private String getVariablePlaceholder(String variable) {
		return String.format("${%s}", variable.trim());
	}	
}
