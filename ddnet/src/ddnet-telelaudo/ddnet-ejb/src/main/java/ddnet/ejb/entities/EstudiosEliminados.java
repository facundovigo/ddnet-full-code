package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Table(name="dd_estudios_eliminados")
@Entity
public class EstudiosEliminados implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="id_estudios_eliminados")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="study_id")
	private long studyID;
	
	@Column(name="user_eliminado")
	private String userLogin;
	
	@Column(name="pat_id_eliminado")
	private String patientID;
	
	@Column(name="paciente_eliminado")
	private String patientName;
	
	@Column(name="modalidad_eliminado")
	private String modality;
	
	@Column(name="institucion_eliminado")
	private String institutionName;
	
	@Column(name="fecha_eliminado")
	private String deletedDate;
	
	@Column(name="filepath_eliminado")
	private String filePath;
	
	@Column(name="reportpath_eliminado")
	private String reportPath;
	
	@Column(name="estado_estudio_eliminado")
	private int state;

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

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getPatientID() {
		return patientID;
	}

	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getModality() {
		return modality;
	}

	public void setModality(String modality) {
		this.modality = modality;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public String getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(String deletedDate) {
		this.deletedDate = deletedDate;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
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
		EstudiosEliminados other = (EstudiosEliminados) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override 
	public String toString() {
		return String.format("%d", state);
	}
}