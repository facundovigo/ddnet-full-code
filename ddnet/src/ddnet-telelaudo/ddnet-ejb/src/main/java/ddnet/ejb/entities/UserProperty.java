package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Table(name="dd_user_property")
@Entity
public class UserProperty implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="user_id")
	private long userID;
	
	@Column(name="hostname")
	private String hostname;
	
	@Column(name="port")
	private String port;
	
	@Column(name="aet")
	private String aet;
	
	@Column(name="calling_aet")
	private String callingAet;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getAet() {
		return aet;
	}

	public void setAet(String aet) {
		this.aet = aet;
	}

	public String getCallingAet() {
		return callingAet;
	}

	public void setCallingAet(String callingAet) {
		this.callingAet = callingAet;
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
		UserProperty other = (UserProperty) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return aet;
	}
}
