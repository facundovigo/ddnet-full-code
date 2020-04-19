package pgsql_bd.DTO;

public class DatosClinicosDTO {
	
	/**
	 * TABLA dd_datos_clinicos
	 */
	
	private long id;			//COLUMNA "id_datos_clinicos"
	private long studyID;		//COLUMNA "id_study_datos_clinicos"
	private int priority;		//COLUMNA "prioridad_datos_clinicos"
	private boolean isOral;		//COLUMNA "cte_oral_datos_clinicos"
	private boolean isEv;		//COLUMNA "cte_ev_datos_clinicos"
	
	public DatosClinicosDTO(long id, long studyID, int priority,
								boolean isOral, boolean isEv){
		
		this.id = id;
		this.studyID = studyID;
		this.priority = priority;
		this.isOral = isOral;
		this.isEv = isEv;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getStudyID() {
		return studyID;
	}

	public void setStudyID(long studyID) {
		this.studyID = studyID;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isOral() {
		return isOral;
	}

	public void setOral(boolean isOral) {
		this.isOral = isOral;
	}

	public boolean isEv() {
		return isEv;
	}

	public void setEv(boolean isEv) {
		this.isEv = isEv;
	}
	
}
