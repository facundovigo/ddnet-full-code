package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Table(name="files")
@Entity
public class LegacyFile implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="pk")
	@Id
	private long id;
	
	@OneToOne
	@JoinColumn(name="instance_fk")
	private LegacyInstance legacyInstance;
	
	@Column(name="filepath")	
	private String filepath;

	@OneToOne
	@JoinColumn(name="filesystem_fk")
	private LegacyFileSystem legacyFileSystem; 
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LegacyInstance getLegacyInstance() {
		return legacyInstance;
	}

	public void setLegacyInstance(LegacyInstance legacyInstance) {
		this.legacyInstance = legacyInstance;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
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
		LegacyFile other = (LegacyFile) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
