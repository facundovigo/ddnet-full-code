package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;


@Table(name="dd_user_study")
@Entity
public class UserStudy implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	//TODO: Analizar si es posible _NO_ utilizar la anotación propietaria "@LazyCollection".
	@ManyToOne
	@JoinColumn(name="user_id")
	@LazyCollection(LazyCollectionOption.FALSE)	
	private User user;
	
	//TODO: Analizar si es posible _NO_ utilizar la anotación propietaria "@LazyCollection".
	@ManyToOne
	@JoinColumn(name="study_id")
	@LazyCollection(LazyCollectionOption.FALSE)	
	private Study study;
	
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
