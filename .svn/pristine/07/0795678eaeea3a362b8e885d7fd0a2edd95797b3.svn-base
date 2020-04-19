package ddnet.ejb;

import java.util.Date;

public class StudySearchFilter {
	private final StudySearchDateType dateType;
	private final Date studyDateFrom;
	private final Date studyDateTo;
	private final String patientID;
	private final String patientName;
//..................................................................................................//
	private final String medico;
	private final int urgencia;
	private final int preferente;
	private final int archivo;
	private final int firmado;
	private final int preinformado;
	private final int noleido;
	private final int incidencia;
	private final int newStudy;
	private final int auxFilter;
//..................................................................................................//
	private final String accessionNumber;
	private final Date patientDOB;
	private final String modality;
	private final long institutionID;
	private final StudyReportStatus reportStatus;
	
	public StudySearchFilter(StudySearchDateType dateType, Date studyDateFrom,
			Date studyDateTo, String accessionNumber, String patientID, 
			String patientName,	Date patientDOB, String modality, long institutionID,
			StudyReportStatus reportStatus, String medico, 
			int urgencia, int preferente, int archivo, 
			int firmado, int preinformado, int noleido,
			int incidencia, int newStudy, int auxFilter) {
		this.dateType = dateType;
		this.studyDateFrom = studyDateFrom;
		this.studyDateTo = studyDateTo;
		this.accessionNumber = accessionNumber;
		this.patientID = patientID;
		this.patientName = patientName;
//..................................................................................................//
		this.medico = medico;
		this.urgencia = urgencia;
		this.preferente = preferente;
		this.archivo = archivo;
		this.firmado = firmado;
		this.preinformado = preinformado;
		this.noleido = noleido;
		this.incidencia = incidencia;
		this.newStudy = newStudy;
		this.auxFilter = auxFilter;
//..................................................................................................//
		this.patientDOB = patientDOB;
		this.modality = modality;
		this.institutionID = institutionID;
		this.reportStatus = reportStatus;
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
	
	public long getInstitutionID() {
		return institutionID;
	}	

	public StudyReportStatus getReportStatus() {
		return reportStatus;
	}	
	
//..................................................................................................//
	
	public String getMedico() {
		return medico;
	}

	public int getUrgencia() {
		return urgencia;
	}

	public int getPreferente() {
		return preferente;
	}
	
	public int getArchivo() {
		return archivo;
	}
	
	public int getFirmado() {
		return firmado;
	}

	public int getPreinformado() {
		return preinformado;
	}

	public int getNoleido() {
		return noleido;
	}
	
	public int getIncidencia() {
		return incidencia;
	}

	public int getNewStudy() {
		return newStudy;
	}
	
	public int getAuxFilter() {
		return auxFilter;
	}
	
//..................................................................................................//

	
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
	public boolean isSetInstitutionID() {
		return institutionID > 0;
	}

//..................................................................................................//
	public boolean isSetMedico() {
		return medico != null && !"".equals(medico.trim());
	}
	
	public boolean isUrgence(){
		return urgencia == 1;
	}
	
	public boolean isPreferent(){
		return preferente == 1;
	}
	
	public boolean hasArchive(){
		return archivo == 1;
	}
	
	public boolean isSigned(){
		return firmado == 1;
	}
	
	public boolean isPreinformed(){
		return preinformado == 1;
	}
	
	public boolean UnRead(){
		return noleido == 1;
	}
	
	public boolean hasIncidence(){
		return incidencia == 1;
	}
	
	public boolean isNew(){
		return newStudy == 1;
	}
	
	public boolean isIncidencia() {
		return auxFilter == 1;
	}
	public boolean isSecReading() {
		return auxFilter == 2;
	}
	public boolean isTeachingFile() {
		return auxFilter == 3;
	}
	public boolean isMultiple() {
		return auxFilter == 4;
	}
	public boolean toCheck() {
		return auxFilter == 5;
	}
	public boolean Solicited(){
		return auxFilter == 6;
	}
	public boolean Checked() {
		return auxFilter == 7;
	}
	
//..................................................................................................//
}