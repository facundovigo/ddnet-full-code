package ddnet.ejb.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="patient")
@Entity
public class LegacyPatient implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final SimpleDateFormat DOB_SOURCE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	@Column(name="pk")
	@Id
	private long id;
	
	@Column(name="pat_id")
	private String patientID;
	
	@Column(name="pat_name")
	private String name;

	@Column(name="pat_birthdate")
	private String dob;
		
	@Column(name="pat_custom3")
	private String age;

	@Column(name="pat_sex")
	private String sex;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getPatientID() {
		return patientID;
	}

	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDOB() {
		return dob;
	}

	public void setDOB(String dob) {
		this.dob = dob;
	}

	public String getAge() {
		return age;
	}
	
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	
	public String getCalculatedAge() {
		try {
			if (dob == null || dob.trim().isEmpty())
				return age;
			else {
				long calculatedAge = ((new Date().getTime() - DOB_SOURCE_FORMAT.parse(dob.trim()).getTime()) /
					(1000L * 60L * 60L * 24L * 365L));
				if (calculatedAge > 0)
					return String.valueOf(calculatedAge);
				else
					return age;
			}
		} catch (Throwable t) {
			return age;
		}
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
		LegacyPatient other = (LegacyPatient) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("ID=[%d]", id);
	}
}
