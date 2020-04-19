package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="dd_user_profile")
@Entity
public class UserProfile implements Serializable {
	
private static final long serialVersionUID = 1L;
	
	@Column(name="id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="user_id")
	private long userID;
	
	@Column(name="user_firstname")
	private String firstName;
	
	@Column(name="user_lastname")
	private String lastName;
	
	@Column(name="user_fancyname")
	private String fancyName;
	
	@Column(name="user_email")
	private String email;
	
	@Column(name="user_phone1")
	private String phone1;
	
	@Column(name="user_phone2")
	private String phone2;
	
	@Column(name="user_address")
	private String address;
	
	@Column(name="user_location")
	private String location;
	
	@Column(name="user_province")
	private String province;
	
	@Column(name="user_skype")
	private String skype;
	
	@Column(name="user_title")
	private String title;
	
	@Column(name="user_mn")
	private String MN;
	
	@Column(name="user_mp")
	private String MP;
	
	@Column(name="user_addinfo")
	private String additionalInfo;
	

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

	public String getFancyName() {
		return fancyName;
	}

	public void setFancyName(String fancyName) {
		this.fancyName = fancyName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getSkype() {
		return skype;
	}

	public void setSkype(String skype) {
		this.skype = skype;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMN() {
		return MN;
	}

	public void setMN(String mN) {
		MN = mN;
	}

	public String getMP() {
		return MP;
	}

	public void setMP(String mP) {
		MP = mP;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
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
		UserProfile other = (UserProfile) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return lastName;
	}
	
}
