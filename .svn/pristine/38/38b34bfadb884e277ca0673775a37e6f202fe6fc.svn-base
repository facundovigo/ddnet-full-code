package ddnet.centeragent.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="dd_study")
@Entity
public class Study implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="id")
	@Id
	private long id;

	@Column(name="is_reported")
	private boolean isReported;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isReported() {
		return isReported;
	}

	public void setReported(boolean isReported) {
		this.isReported = isReported;
	}		
}
