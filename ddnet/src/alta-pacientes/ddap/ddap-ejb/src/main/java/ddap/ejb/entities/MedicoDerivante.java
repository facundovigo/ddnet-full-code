package ddap.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="ap_medicos_derivantes")
@Entity
public class MedicoDerivante implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="id_medico_derivante") @Id @GeneratedValue(strategy= GenerationType.IDENTITY) private long id;
	@Column(name="nombre_medico_derivante") private String name;
	@Column(name="tipo_matricula_medico_derivante") private String licenseType;
	@Column(name="nro_matricula_medico_derivante") private String licenseNumber;
	
	public long getId(){ return id; }
	public void setId(long id){ this.id= id; }
	public String getName(){ return name; }
	public void setName(String name){ this.name= name; }
	public String getLicenseType(){ return licenseType; }
	public void setLicenseType(String licenseType){	this.licenseType= licenseType; }
	public String getLicenseNumber(){ return licenseNumber;	}
	public void setLicenseNumber(String licenseNumber){	this.licenseNumber= licenseNumber; }
	
	
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
		MedicoDerivante other = (MedicoDerivante) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s", name);
	}

}
