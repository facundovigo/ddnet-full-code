package app.filters;

import java.util.Date;

public class StudySearchFilter {
	private final StudySearchDateType dateType;
	private final Date dateBetweenFrom;
	private final Date dateBetweenTo;
	private final String modality;
	private final String studyFilter;
	private final String patientFilter;
	
	public StudySearchFilter(StudySearchDateType dateType, Date dateBetweenFrom,
			Date dateBetweenTo, String modality, String studyFilter, String patientFilter) {
		this.dateType = dateType;
		this.dateBetweenFrom = dateBetweenFrom;
		this.dateBetweenTo = dateBetweenTo;
		this.modality = modality;
		this.studyFilter = studyFilter;
		this.patientFilter = patientFilter;
	}
	
	public StudySearchDateType getDateType() {
		return dateType;
	}

	public Date getDateBetweenFrom() {
		return dateBetweenFrom;
	}

	public Date getDateBetweenTo() {
		return dateBetweenTo;
	}
	
	public String getModality() {
		return modality;
	}
	
	public String getStudyFilter() {
		return studyFilter;
	}

	public String getPatientFilter() {
		return patientFilter;
	}


	public enum StudySearchDateType {
		today(1),
		yesterday(2),
		lastweek(3),
		lastmonth(4),
		any(5),
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
	
	public boolean isSetModality() {
		return modality != null && !"".equals(modality.trim());
	}
	public boolean isSetStudyFilter() {
		return studyFilter != null && !"".equals(studyFilter.trim());
	}
	public boolean isSetPatientFilter() {
		return patientFilter != null && !"".equals(patientFilter.trim());
	}

}




