package ddap.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="dd_worklist")
@Entity
public class Worklist implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="id_worklist")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="accession_number")
	private String accNumb;
	
	@Column(name="patient_id")
	private String patID;
	
	@Column(name="nombre")
	private String firstName;
	
	@Column(name="apellido")
	private String lastName;
	
	@Column(name="sexo")
	private String patSex;
	
	@Column(name="fec_nacimiento")
	private String birthDate;
	
	@Column(name="referring_physician")
	private String refPhysician;
	
	@Column(name="performing_physician")
	private String perPhysician;
	
	@Column(name="modality")
	private String mod;
	
	@Column(name="fec_estudio")
	private String stdDate;
	
	@Column(name="hs_estudio")
	private String stdTime;
	
	@Column(name="exam_sala")
	private String practRoom;
	
	@Column(name="exam_descripcion")
	private String practDesc;
	
	@Column(name="study_uid")
	private String studyID;
	
	@Column(name="procedure_id")
	private String procID;
	
	@Column(name="procedure_step_id")
	private String procStepID;
	
	@Column(name="institucion")
	private String inst;
	
	@Column(name="scheduled_aet")
	private String schedAET;
	
	@Column(name="asignado")
	private String assigned;
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccNumb() {
		return accNumb;
	}

	public void setAccNumb(String accNumb) {
		this.accNumb = accNumb;
	}

	public String getPatID() {
		return patID;
	}

	public void setPatID(String patID) {
		this.patID = patID;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPatSex() {
		return patSex;
	}

	public void setPatSex(String patSex) {
		this.patSex = patSex;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getRefPhysician() {
		return refPhysician;
	}

	public void setRefPhysician(String refPhysician) {
		this.refPhysician = refPhysician;
	}

	public String getPerPhysician() {
		return perPhysician;
	}

	public void setPerPhysician(String perPhysician) {
		this.perPhysician = perPhysician;
	}

	public String getMod() {
		return mod;
	}

	public void setMod(String mod) {
		this.mod = mod;
	}

	public String getStdDate() {
		return stdDate;
	}

	public void setStdDate(String stdDate) {
		this.stdDate = stdDate;
	}

	public String getStdTime() {
		return stdTime;
	}

	public void setStdTime(String stdTime) {
		this.stdTime = stdTime;
	}

	public String getPractRoom() {
		return practRoom;
	}

	public void setPractRoom(String practRoom) {
		this.practRoom = practRoom;
	}

	public String getPractDesc() {
		return practDesc;
	}

	public void setPractDesc(String practDesc) {
		this.practDesc = practDesc;
	}

	public String getStudyID() {
		return studyID;
	}

	public void setStudyID(String studyID) {
		this.studyID = studyID;
	}

	public String getProcID() {
		return procID;
	}

	public void setProcID(String procID) {
		this.procID = procID;
	}

	public String getProcStepID() {
		return procStepID;
	}

	public void setProcStepID(String procStepID) {
		this.procStepID = procStepID;
	}

	public String getInst() {
		return inst;
	}

	public void setInst(String inst) {
		this.inst = inst;
	}

	public String getSchedAET() {
		return schedAET;
	}

	public void setSchedAET(String schedAET) {
		this.schedAET = schedAET;
	}

	public String getAssigned() {
		return assigned;
	}

	public void setAssigned(String assigned) {
		this.assigned = assigned;
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
		Worklist other = (Worklist) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s", accNumb);
	}

}
