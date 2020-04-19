package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="filesystem")
@Entity
public class LegacyFileSystem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="pk")
	@Id
	private long id;
	
	@Column(name="dirpath")
	private String dirpath;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDirpath() {
		return dirpath;
	}

	public void setDirpath(String dirpath) {
		this.dirpath = dirpath;
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
		LegacyFileSystem other = (LegacyFileSystem) obj;
		if (id != other.id)
			return false;
		return true;
	}		
}
