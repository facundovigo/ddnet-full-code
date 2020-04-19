package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Table(name="dd_user_study")
@Entity
public class UserStudy implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="id", updatable=false)
	@Id
	@SequenceGenerator(name="dd_user_study_id_seq", sequenceName="dd_user_study_id_seq", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="dd_user_study_id_seq")
	private long id;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name="study_id")
	private Study study;
	
	@OneToOne
	@JoinColumn(name="user_id", referencedColumnName="user_medico_fk", insertable=false, updatable=false)
	private Medico medico;
	
	public UserStudy() {
		
	}

	public UserStudy(User user, Study study) {
		this.user = user;
		this.study = study;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public Study getStudy() {
		return study;
	}

	public void setStudy(Study study) {
		this.study = study;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Medico getMedico() {
		return medico;
	}

	public void setMedico(Medico medico) {
		this.medico = medico;
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
		UserStudy other = (UserStudy) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("Assigned study '%s' to user '%s'", getStudy(), user);
	}
}
