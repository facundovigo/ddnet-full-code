package pgsql_bd.DTO;

public class LegacyPatientDTO {
	
	/**
	 * TABLA patient
	 */
	
	private long id;			//COLUMNA "pk"
	private String patID;		//COLUMNA "pat_id"
	private String patName;		//COLUMNA "pat_name"
	private String patSex;		//COLUMNA "pat_sex"
	private String patAge;		//COLUMNA "pat_custom3"
	
	public LegacyPatientDTO(long id, String patID, String patName, 
								String patSex, String patAge){
		
		this.id = id;
		this.patID = patID;
		this.patName = patName;
		this.patSex = patSex;
		this.patAge = patAge;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPatID() {
		return patID;
	}

	public void setPatID(String patID) {
		this.patID = patID;
	}

	public String getPatName() {
		return patName;
	}

	public void setPatName(String patName) {
		this.patName = patName;
	}

	public String getPatSex() {
		return patSex;
	}

	public void setPatSex(String patSex) {
		this.patSex = patSex;
	}

	public String getPatAge() {
		return patAge;
	}

	public void setPatAge(String patAge) {
		this.patAge = patAge;
	}
	
}
