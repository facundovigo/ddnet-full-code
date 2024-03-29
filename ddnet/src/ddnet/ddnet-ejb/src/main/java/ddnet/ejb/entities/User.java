package ddnet.ejb.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Table(name="dd_user")
@Entity
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="login")
	private String login;
	
	@Column(name="password")
	private String password;
	
	@Column(name="has_password_expired")
	private boolean passwordExpired;
	
	@Column(name="is_administrator")
	private boolean administrator;
	
	@Column(name="deleted")
	private boolean deleted;
	
	@Column(name="is_patient")
	private boolean isPatient;
	
	//TODO: Analizar si es posible _NO_ utilizar la anotación propietaria "@LazyCollection".
	@OneToMany(mappedBy="user")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<UserInstitution> institutions = new HashSet<UserInstitution>();

	//TODO: Analizar si es posible _NO_ utilizar la anotación propietaria "@LazyCollection".
	@ManyToMany
	@JoinTable(name="dd_user_modality", 
		joinColumns={@JoinColumn(name="user_id")},
		inverseJoinColumns={@JoinColumn(name="modality_id")})
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<Modality> modalities = new HashSet<Modality>();
	
	@ManyToOne
	@JoinColumn(name="id", referencedColumnName="user_id", insertable=false, updatable=false)
	private UserProperty prop;
	
	@ManyToOne
	@JoinColumn(name="id", referencedColumnName="user_id", insertable=false, updatable=false)
	private UserProfile perfil;
	
	@ManyToOne
	@JoinColumn(name="id", referencedColumnName="user_id", insertable=false, updatable=false)
	private UserPermissions permissions;
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isPasswordExpired() {
		return passwordExpired;
	}

	public void setPasswordExpired(boolean passwordExpired) {
		this.passwordExpired = passwordExpired;
	}

	public boolean isAdministrator() {
		return administrator;
	}

	public void setAdministrator(boolean administrator) {
		this.administrator = administrator;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	public Set<UserInstitution> getInstitutions() {
		return institutions;
	}

	public void setInstitutions(Set<UserInstitution> institutions) {
		this.institutions = institutions;
	}
	
	public Set<Modality> getModalities() {
		return modalities;
	}

	public void setModalities(Set<Modality> modalities) {
		this.modalities = modalities;
	}

	public boolean checkPassword(String password) {
		return DigestUtils.md5Hex(password).equals(this.password);
	}
	
	public UserProperty getProp() {
		return prop;
	}

	public void setProp(UserProperty prop) {
		this.prop = prop;
	}

	public UserProfile getPerfil() {
		return perfil;
	}

	public void setPerfil(UserProfile perfil) {
		this.perfil = perfil;
	}

	public boolean isPatient() {
		return isPatient;
	}

	public void setPatient(boolean isPatient) {
		this.isPatient = isPatient;
	}

	public UserPermissions getPermissions() {
		return permissions;
	}

	public void setPermissions(UserPermissions permissions) {
		this.permissions = permissions;
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
		User other = (User) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s [%s, %s]", login, lastName, firstName);
	}

}
