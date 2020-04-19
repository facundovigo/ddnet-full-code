package ddnet.ejb.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Table(name="series")
@Entity
public class LegacySerie implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="pk")
	@Id
	private long id;
	
	@OneToOne
	@JoinColumn(name="study_fk")
	private LegacyStudy legacyStudy;
	
	@OneToMany(mappedBy="legacySerie")
	private Set<LegacyInstance> legacyInstances = new HashSet<LegacyInstance>();

	public LegacyStudy getLegacyStudy() {
		return legacyStudy;
	}

	public void setLegacyStudy(LegacyStudy legacyStudy) {
		this.legacyStudy = legacyStudy;
	}		

	public Set<LegacyInstance> getLegacyInstances() {
		return legacyInstances;
	}

	public void setLegacyInstances(Set<LegacyInstance> legacyInstances) {
		this.legacyInstances = legacyInstances;
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
		LegacySerie other = (LegacySerie) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
