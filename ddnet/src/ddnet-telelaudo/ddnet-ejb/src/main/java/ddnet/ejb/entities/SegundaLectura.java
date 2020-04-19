package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Table(name="dd_segunda_lectura")
@Entity
public class SegundaLectura implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="informe_id")
	private long informeID;
	
	@Column(name="usuario_informe")
	private String informante;
	
	@Column(name="grado_discrepancia")
	private int grado;
	
	@Column(name="usuario_discrepante")
	private String discrepante;
	
	@Column(name="mensaje")
	private String msj;
	
	@Column(name="fecha_segunda_lectura")
	private String date;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getInformeID() {
		return informeID;
	}

	public void setInformeID(long informeID) {
		this.informeID = informeID;
	}

	public String getInformante() {
		return informante;
	}

	public void setInformante(String informante) {
		this.informante = informante;
	}

	public int getGrado() {
		return grado;
	}

	public void setGrado(int grado) {
		this.grado = grado;
	}

	public String getDiscrepante() {
		return discrepante;
	}

	public void setDiscrepante(String discrepante) {
		this.discrepante = discrepante;
	}

	public String getMsj() {
		return msj;
	}

	public void setMsj(String msj) {
		this.msj = msj;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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
		SegundaLectura other = (SegundaLectura) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override 
	public String toString() {
		return String.format("%d", grado);
	}
}