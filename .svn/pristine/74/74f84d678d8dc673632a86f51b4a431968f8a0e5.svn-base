package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Table(name="dd_datos_clinicos")
@Entity
public class DatosClinicos implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="id_datos_clinicos")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="id_study_datos_clinicos")
	private long studyID;
	
	@Column(name="prioridad_datos_clinicos")
	private int priority;
	
	@Column(name="cte_oral_datos_clinicos")
	private boolean isOral;
	
	@Column(name="cte_ev_datos_clinicos")
	private boolean isEv;
	
	@Column(name="observaciones_datos_clinicos")
	private String notes;
	
	@Column(name="user_datos_clinicos")
	private String user;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public long getStudyID() {
		return studyID;
	}

	public void setStudyID(long studyID) {
		this.studyID = studyID;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DatosClinicos other = (DatosClinicos) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override 
	public String toString() {
		return String.format("%d", priority);
	}
}