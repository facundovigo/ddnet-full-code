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
	
	@Column(name="study_id_incidencia")
	private long studyID;

	@Column(name="mensaje_incidencia")
	private String mensaje;

	@Column(name="fecha_mensaje_incidencia")
	private String fecha;

	@Column(name="usuario_incidencia")
	private String usuario;
	
	@Column(name="estado_incidencia")
	private int incidencia;
	
	@Column(name="inicio_incidencia")
	private String inicio;

	@Column(name="resolucion_incidencia")
	private String resolucion;
	
	@Column(name="incidencia_resuelta")
	private long refInc;

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

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public int getIncidencia() {
		return incidencia;
	}

	public void setIncidencia(int incidencia) {
		this.incidencia = incidencia;
	}

	public String getInicio() {
		return inicio;
	}

	public void setInicio(String inicio) {
		this.inicio = inicio;
	}

	public String getResolucion() {
		return resolucion;
	}

	public void setResolucion(String resolucion) {
		this.resolucion = resolucion;
	}
	
	public long getRefInc() {
		return refInc;
	}

	public void setRefInc(long refInc) {
		this.refInc = refInc;
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
