package ddnet.ejb;

import java.util.Date;

public class StudySearchFilter {
	private final StudySearchDateType dateType;
	private final Date studyDateFrom;
	private final Date studyDateTo;
	private final String patientData;
	private final String userStudy;
	private final boolean urgente;
	private final boolean prioridad;
	private final boolean adjunto;
	private final boolean firmado;
	private final boolean preinformado;
	private final boolean noLeido;
	private final int anotherFilter;
	private final String accessionNumber;
	private final Date patientDOB;
	private final String modality;
	private final long institutionID;
	private final StudyReportStatus reportStatus;
	private final boolean newer;
	
	public StudySearchFilter(StudySearchDateType dateType, Date studyDateFrom,
			Date studyDateTo, String accessionNumber, String patientData, 
			Date patientDOB, String modality, long institutionID,
			StudyReportStatus reportStatus, String userStudy, 
			boolean urgente, boolean prioridad, boolean adjunto, 
			boolean firmado, boolean preinformado, boolean noLeido,
			int anotherFilter, boolean newer) {
		this.dateType = dateType;
		this.studyDateFrom = studyDateFrom;
		this.studyDateTo = studyDateTo;
		this.accessionNumber = accessionNumber;
		this.patientData = patientData;
		this.userStudy = userStudy;
		this.urgente = urgente;
		this.prioridad = prioridad;
		this.adjunto = adjunto;
		this.firmado = firmado;
		this.preinformado = preinformado;
		this.noLeido = noLeido;
		this.anotherFilter = anotherFilter;
		this.patientDOB = patientDOB;
		this.modality = modality;
		this.institutionID = institutionID;
		this.reportStatus = reportStatus;
		this.newer = newer;
	}
	
	public StudySearchDateType getDateType() {
		return dateType;
	}

	public Date getStudyDateFrom() {
		return studyDateFrom;
	}

	public Date getStudyDateTo() {
		return studyDateTo;
	}

	public String getPatientData() {
		return patientData;
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
	
	public long getInstitutionID() {
		return institutionID;
	}	

	public StudyReportStatus getReportStatus() {
		return reportStatus;
	}	
	
	public String getUserStudy() {
		return userStudy;
	}

	public boolean getUrgente() {
		return urgente;
	}

	public boolean getPrioridad() {
		return prioridad;
	}
	
	public boolean getAdjunto() {
		return adjunto;
	}
	
	public boolean getFirmado() {
		return firmado;
	}

	public boolean getPreinformado() {
		return preinformado;
	}

	public boolean getNoleido() {
		return noLeido;
	}
	
	public int getAnotherFilter() {
		return anotherFilter;
	}
	
	public boolean isNewer() {
		return newer;
	}


	public enum StudySearchDateType {
		any(1),
		today(2),
		yesterday(3),
		lastweek(4),
		lastmonth(5),
		between(6);		

		private int code;

		private StudySearchDateType(int code) {
			this.code = code;
		}

		public int getCode() {
			return this.code;
		}

		public static StudySearchDateType getByCode(int code) {
			for (StudySearchDateType item : StudySearchDateType.values())
				if (item.code == code)
					return item;
			return null;
		}
	}

	public enum StudyReportStatus {
		any(1),
		nonreported(2);
		
		private int code;

		private StudyReportStatus(int code) {
			this.code = code;
		}

		public int getCode() {
			return this.code;
		}

		public static StudyReportStatus getByCode(int code) {
			for (StudyReportStatus item : StudyReportStatus.values())
				if (item.code == code)
					return item;
			return null;
		}
	}
	
	public boolean isSetPatientData() {
		return patientData != null && !"".equals(patientData.trim());
	}
	public boolean isSetModality() {
		return modality != null && !"".equals(modality);
	}	
	public boolean isSetAccessionNumber() {
		return accessionNumber != null && !"".equals(accessionNumber.trim());
	}	
	public boolean isSetPatientDOB() {
		return patientDOB != null;
	}
	public boolean isSetInstitutionID() {
		return institutionID > 0;
	}

	public boolean isSetUserStudy() {
		return userStudy != null && !"".equals(userStudy.trim());
	}
	
	public boolean isUrgente(){
		return urgente;
	}
	
	public boolean isPrioridad(){
		return prioridad;
	}
	
	public boolean hasArchive(){
		return adjunto;
	}
	
	public boolean isFirmado(){
		return firmado;
	}
	
	public boolean isPreinformado(){
		return preinformado;
	}
	
	public boolean isNoLeido(){
		return noLeido;
	}
	
	
	public boolean isIncidencia() {
		return anotherFilter == 1;
	}
	public boolean isSecReading() {
		return anotherFilter == 2;
	}
	public boolean isTeachingFile() {
		return anotherFilter == 3;
	}
	public boolean isEmergencyCase() {
		return anotherFilter == 4;
	}
	public boolean isMultiple() {
		return anotherFilter == 5;
	}
	public boolean toCheck() {
		return anotherFilter == 6;
	}
	public boolean Solicited(){
		return anotherFilter == 7;
	}
	public boolean Checked() {
		return anotherFilter == 8;
	}
}