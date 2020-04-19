package ddnet.ejb.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Table(name="dd_role")
@Entity
public class Role implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="name")
	private String name;	
	
	//TODO: Analizar si es posible _NO_ utilizar la anotación propietaria "@LazyCollection".
	@ManyToMany
	@ElementCollection(fetch=FetchType.EAGER)
	@JoinTable(name="dd_role_action", 
		joinColumns={@JoinColumn(name="role_id")}, 
		inverseJoinColumns={@JoinColumn(name="action_id")})
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<Action> allowedActions = new HashSet<Action>();
	
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
	
	public Set<Action> getAllowedActions() {
		return allowedActions;
	}

	public void setAllowedActions(Set<Action> allowedActions) {
		this.allowedActions = allowedActions;
	}
	
	public boolean allowsTo(String actionName) {
		for(Action action : allowedActions)
			if (action.getName().equals(actionName))
				return true;
		return false;
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
		Role other = (Role) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
}
