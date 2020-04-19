package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Table(name="instance")
@Entity
public class LegacyInstance implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="pk")
	@Id
	private long id;
	
	@OneToOne
	@JoinColumn(name="series_fk")
	private LegacySerie legacySerie;

	@OneToOne(mappedBy="legacyInstance")
	private LegacyFile legacyFile;

	public LegacySerie getLegacySerie() {
		return legacySerie;
	}

	public void setLegacySerie(LegacySerie legacySerie) {
		this.legacySerie = legacySerie;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LegacyFile getLegacyFile() {
		return legacyFile;
	}

	public void setLegacyFile(LegacyFile legacyFile) {
		this.legacyFile = legacyFile;
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
		LegacyInstance other = (LegacyInstance) obj;
		if (id != other.id)
			return false;
		return true;
	}	
}
