package ddap.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="ap_pacientes")
@Entity
public class Paciente implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="id_pacientes")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="apellido_pacientes")
	private String lastName;
	
	@Column(name="nombre_pacientes")
	private String firstName;
	
	@Column(name="tipo_documento_pacientes")
	private String docType;
	
	@Column(name="nro_documento_pacientes")
	private String docNumber;
	
	@Column(name="sexo_pacientes")
	private String patSex;
	
	@Column(name="fecha_nacimiento_pacientes")
	private String birthDate;
	
	@Column(name="nro_telefono_pacientes")
	private String phoneNumber;
	
	@Column(name="nro_celular_pacientes")
	private String cellPhoneNumber;
	
	@Column(name="email_pacientes")
	private String mailAddress;
	
	@Column(name="ocupacion_pacientes")
	private String occupation;
	
	@Column(name="domicilio_pacientes")
	private String address;
	
	@Column(name="localidad_pacientes")
	private String location;
	
	@Column(name="provincia_pacientes")
	private String province;
	
	@Column(name="codigo_postal_pacientes")
	private String zipCode;
	
	@Column(name="nacionalidad_pacientes")
	private String nationality;
	
	@Column(name="obra_social_pacientes")
	private String medInsurance;
	
	@Column(name="tipo_plan_pacientes")
	private String plan;
	
	@Column(name="nro_plan_pacientes")
	private String numPlan;
	
	@Column(name="observaciones_pacientes")
	private String observation;
	
	@Column(name="fecha_de_alta_pacientes")
	private String insertDate;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public String getPatSex() {
		return patSex;
	}

	public void setPatSex(String patSex) {
		this.patSex = patSex;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCellPhoneNumber() {
		return cellPhoneNumber;
	}

	public void setCellPhoneNumber(String cellPhoneNumber) {
		this.cellPhoneNumber = cellPhoneNumber;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
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

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getMedInsurance() {
		return medInsurance;
	}

	public void setMedInsurance(String medInsurance) {
		this.medInsurance = medInsurance;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public String getNumPlan() {
		return numPlan;
	}

	public void setNumPlan(String numPlan) {
		this.numPlan = numPlan;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}
	
	public String getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
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
		Paciente other = (Paciente) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s", docNumber);
	}

}
