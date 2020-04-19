package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="study_on_fs")
@Entity
public class LegacyStudyOnFS implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="pk")
	@Id
	private long id;
	
	@Column(name="study_fk")
	private long studyID;
	
	@Column(name="filesystem_fk")
	private long fsID;
	
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

	public long getFsID() {
		return fsID;
	}

	public void setFsID(long fsID) {
		this.fsID = fsID;
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
