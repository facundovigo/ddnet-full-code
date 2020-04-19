package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="dd_incidencias")
@Entity
public class Incidencia implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name="id_incidencia")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="study_id")
	private long studyID;
	
	@Column(name="estado_incidencia")
	private int incidencia;
	
	@Column(name="mensaje_incidencia")
	private String mensaje;
	
	@Column(name="fecha_incidencia")
	private String inc_date;
	
	@Column(name="fecha_mensaje")
	private String msj_date;
	
	@Column(name="usuario_incidencia")
	private String user;

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

	public int getIncidencia() {
		return incidencia;
	}

	public void setIncidencia(int incidencia) {
		this.incidencia = incidencia;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getInc_date() {
		return inc_date;
	}

	public void setInc_date(String inc_date) {
		this.inc_date = inc_date;
	}

	public String getMsj_date() {
		return msj_date;
	}

	public void setMsj_date(String msj_date) {
		this.msj_date = msj_date;
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
		Incidencia other = (Incidencia) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return mensaje;
	}
}
