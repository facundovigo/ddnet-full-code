package ddap.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="ap_privilegios")
@Entity
public class Privilegio implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="id_privilegio") @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="user_id_privilegio") private long userId;
	@Column(name="alta_paciente_privilegio") private boolean doPatient;
	@Column(name="alta_practica_paciente_privilegio") private boolean doPatientPractice;
	@Column(name="alta_practica_privilegio") private boolean doPractice;
	@Column(name="alta_equipo_privilegio") private boolean doEquipment;
	@Column(name="alta_logger_privilegio") private boolean doLogger;
	@Column(name="abm_privilegio") private boolean doAbm;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public boolean isDoPatient() {
		return doPatient;
	}

	public void setDoPatient(boolean doPatient) {
		this.doPatient = doPatient;
	}

	public boolean isDoPatientPractice() {
		return doPatientPractice;
	}

	public void setDoPatientPractice(boolean doPatientPractice) {
		this.doPatientPractice = doPatientPractice;
	}

	public boolean isDoPractice() {
		return doPractice;
	}

	public void setDoPractice(boolean doPractice) {
		this.doPractice = doPractice;
	}

	public boolean isDoEquipment() {
		return doEquipment;
	}

	public void setDoEquipment(boolean doEquipment) {
		this.doEquipment = doEquipment;
	}

	public boolean isDoLogger() {
		return doLogger;
	}

	public void setDoLogger(boolean doLogger) {
		this.doLogger = doLogger;
	}

	public boolean isDoAbm() {
		return doAbm;
	}

	public void setDoAbm(boolean doAbm) {
		this.doAbm = doAbm;
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
		Privilegio other = (Privilegio) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s","1234");
	}

}
