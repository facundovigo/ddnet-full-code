package ddap.ejb;

import java.util.Date;

public class PracticaPacienteSearchFilter {
	private final ppSearchDateType dateType;
	private final Date betweenDateFrom;
	private final Date betweenDateTo;
	private final String practiceState;
	private final String accessionNumber;
	private final String patientFilter;
	private final String patSex;
	
	public PracticaPacienteSearchFilter(ppSearchDateType dateType, 
										Date betweenDateFrom, Date betweenDateTo,
										String practiceState, String accessionNumber,
										String patientFilter, String patSex) {
		
		this.dateType = dateType;
		this.betweenDateFrom = betweenDateFrom;
		this.betweenDateTo = betweenDateTo;
		this.practiceState = practiceState;
		this.accessionNumber= accessionNumber;
		this.patientFilter = patientFilter;
		this.patSex = patSex;
	}

	
	public ppSearchDateType getDateType() {
		return dateType;
	}

	public Date getBetweenDateFrom() {
		return betweenDateFrom;
	}

	public Date getBetweenDateTo() {
		return betweenDateTo;
	}

	public String getPracticeState() {
		return practiceState;
	}

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public String getPatientFilter() {
		return patientFilter;
	}

	public String getPatSex() {
		return patSex;
	}

	public enum ppSearchDateType {
		today(1),
		yesterday(2),
		lastweek(3),
		lastmonth(4),
		any(5),
		between(6);

		private int code;

		private ppSearchDateType(int code) {
			this.code = code;
		}

		public int getCode() {
			return this.code;
		}

		public static ppSearchDateType getByCode(int code) {
			for (ppSearchDateType item : ppSearchDateType.values())
				if (item.code == code)
					return item;
			return null;
		}
	}

	public boolean isSetPracticeState(){
		return practiceState != null && !"".equals(practiceState.trim());
	}
	public boolean isSetAccessionNumber(){
		return accessionNumber != null && !"".equals(accessionNumber.trim());
	}
	public boolean isSetPatientFilter(){
		return patientFilter != null && !"".equals(patientFilter.trim());
	}
	public boolean isSetPatSex(){
		return patSex != null && !"".equals(patSex.trim());
	}

}