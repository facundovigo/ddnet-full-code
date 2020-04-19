package ddnet.ejb;

import java.util.Date;

public class StudySearchFilter {
	private final Date studyDateFrom;
	private final Date studyDateTo;
	private final String patientID;
	private final String patientName;
	private final String accessionNumber;
	private final Date patientDOB;
	private final String modality;
	
	public StudySearchFilter(Date studyDateFrom, Date studyDateTo, String accessionNumber, String patientID, 
			String patientName,	Date patientDOB, String modality) {
		this.studyDateFrom = studyDateFrom;
		this.studyDateTo = studyDateTo;
		this.accessionNumber = accessionNumber;
		this.patientID = patientID;
		this.patientName = patientName;
		this.patientDOB = patientDOB;
		this.modality = modality;
	}
	
	public Date getStudyDateFrom() {
		return studyDateFrom;
	}

	public Date getStudyDateTo() {
		return studyDateTo;
	}

	public String getPatientID() {
		return patientID;
	}

	public String getPatientName() {
		return patientName;
	}

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public Date getPatientDOB() {
		return patientDOB;
	}

	public String getModality() {
		return modality;
	}
	
	public boolean isSetPatientID() {
		return patientID != null && !"".equals(patientID.trim());
	}	
	public boolean isSetPatientName() {
		return patientName != null && !"".equals(patientName.trim());
	}	
	public boolean isSetModality() {
		return modality != null && !"".equals(modality.trim());
	}	
	public boolean isSetAccessionNumber() {
		return accessionNumber != null && !"".equals(accessionNumber.trim());
	}	
	public boolean isSetPatientDOB() {
		return patientDOB != null;
	}
	public boolean areSetDates() {
		return studyDateFrom != null && studyDateTo != null;
	}	
}