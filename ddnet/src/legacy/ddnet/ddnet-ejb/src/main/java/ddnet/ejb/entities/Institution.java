package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="dd_institution")
@Entity
public class Institution implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="name")
	private String name;
	
	@Column(name = "related_ae_title") 
	private String relatedAET;
	
	@Column(name = "adm_enabled") 
	private boolean administrativelyEnabled;

	@Column(name="deleted")
	private boolean deleted;

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRelatedAET() {
		return relatedAET;
	}

	public void setRelatedAET(String relatedAET) {
		this.relatedAET = relatedAET;
	}

	public boolean isAdministrativelyEnabled() {
		return administrativelyEnabled;
	}

	public void setAdministrativelyEnabled(boolean administrativelyEnabled) {
		this.administrativelyEnabled = administrativelyEnabled;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
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
		Institution other = (Institution) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}	
}
