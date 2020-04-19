package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Table(name="dd_comprobar_caso")
@Entity
public class ComprobarCaso implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="id_cc")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="informe_id")
	private long informeID;
	
	@Column(name="estado_cc")
	private int state;
	
	@Column(name="fecha_cc")
	private String date;
	
	@Column(name="usuario_cc")
	private String user;
	
	@Column(name="observacion_cc")
	private String notes;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getInformeID() {
		return informeID;
	}

	public void setInformeID(long informeID) {
		this.informeID = informeID;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
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
		ComprobarCaso other = (ComprobarCaso) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override 
	public String toString() {
		return String.format("%d", state);
	}
}