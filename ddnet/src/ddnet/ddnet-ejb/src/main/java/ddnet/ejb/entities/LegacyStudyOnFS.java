package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Table(name="study_on_fs")
@Entity
public class LegacyStudyOnFS implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="pk")
	@Id
	private long id;
	
	@OneToOne
	@JoinColumn(name="study_fk")
	private LegacyStudy legacyStudy;
	
	@OneToOne
	@JoinColumn(name="filesystem_fk")
	private LegacyFileSystem legacyFileSystem;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LegacyStudy getLegacyStudy() {
		return legacyStudy;
	}

	public void setLegacyStudy(LegacyStudy legacyStudy) {
		this.legacyStudy = legacyStudy;
	}

	public LegacyFileSystem getLegacyFileSystem() {
		return legacyFileSystem;
	}

	public void setLegacyFileSystem(LegacyFileSystem legacyFileSystem) {
		this.legacyFileSystem = legacyFileSystem;
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
		LegacyStudyOnFS other = (LegacyStudyOnFS) obj;
		if (id != other.id)
			return false;
		return true;
	}		
}
