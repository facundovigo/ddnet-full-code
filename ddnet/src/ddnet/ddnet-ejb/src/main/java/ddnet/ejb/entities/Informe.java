package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Table(name="dd_informes")
@Entity
public class Informe implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="id_informe")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="study_id")
	private long studyID;
	
	@Column(name="usuario_informe")
	private String userLogin;
	
	@Column(name="estado_informe")
	private int state;
	
	@Column(name="cant_informes")
	private int mult;
	
	@Column(name="is_teaching_file")
	private boolean tFile;
	
	@Column(name="segunda_lectura")
	private boolean secondReading;
	
	@Column(name="comprobar_caso")
	private boolean checkCase;
	
	@Column(name="emergencia_medica")
	private boolean emergency;
	
	@Column(name="fecha_ultima_modificacion")
	private String lastChangeDate;
	
	@Column(name="fecha_firma_informe")
	private String signedDate;
	
	@ManyToOne
	@JoinColumn(name="id_informe", referencedColumnName="informe_id", insertable=false, updatable=false)
	private SegundaLectura segundaLectura;
	
	@Column(name="comprobar_caso_estado")
	private int checkState;
	
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getMult() {
		return mult;
	}

	public void setMult(int mult) {
		this.mult = mult;
	}

	public boolean istFile() {
		return tFile;
	}

	public void settFile(boolean tFile) {
		this.tFile = tFile;
	}

	public boolean isSecondReading() {
		return secondReading;
	}

	public void setSecondReading(boolean secondReading) {
		this.secondReading = secondReading;
	}

	public boolean isCheckCase() {
		return checkCase;
	}

	public void setCheckCase(boolean checkCase) {
		this.checkCase = checkCase;
	}

	public boolean isEmergency() {
		return emergency;
	}

	public void setEmergency(boolean emergency) {
		this.emergency = emergency;
	}

	public String getLastChangeDate() {
		return lastChangeDate;
	}

	public void setLastChangeDate(String lastChangeDate) {
		this.lastChangeDate = lastChangeDate;
	}

	public String getSignedDate() {
		return signedDate;
	}

	public void setSignedDate(String signedDate) {
		this.signedDate = signedDate;
	}

	public SegundaLectura getSegundaLectura() {
		return segundaLectura;
	}

	public void setSegundaLectura(SegundaLectura segundaLectura) {
		this.segundaLectura = segundaLectura;
	}

	public int getCheckState() {
		return checkState;
	}

	public void setCheckState(int checkState) {
		this.checkState = checkState;
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
		Informe other = (Informe) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override 
	public String toString() {
		return String.format("%d", state);
	}
}