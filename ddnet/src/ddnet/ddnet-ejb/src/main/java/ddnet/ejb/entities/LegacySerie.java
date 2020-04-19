package ddnet.ejb.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Table(name="series")
@Entity
public class LegacySerie implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="pk")
	@Id
	private long id;
	
	@OneToOne
	@JoinColumn(name="study_fk")
	private LegacyStudy legacyStudy;

	@Column(name="series_no")
	private String number;
	
	@Column(name="series_desc")
	private String description;

	@Column(name="pps_start")
	private Date performedOn;
	
	@Column(name="series_iuid")
	private String serieID;
	
	@Column(name="num_instances")
	private int cantInstances;
	
	@OneToMany(mappedBy="legacySerie", cascade= CascadeType.ALL)
	private Set<LegacyInstance> legacyInstances = new HashSet<LegacyInstance>();

	public LegacyStudy getLegacyStudy() {
		return legacyStudy;
	}

	public void setLegacyStudy(LegacyStudy legacyStudy) {
		this.legacyStudy = legacyStudy;
	}

	public Set<LegacyInstance> getLegacyInstances() {
		return legacyInstances;
	}

	public void setLegacyInstances(Set<LegacyInstance> legacyInstances) {
		this.legacyInstances = legacyInstances;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Date getPerformedOn() {
		return performedOn;
	}

	public void setPerformedOn(Date performedOn) {
		this.performedOn = performedOn;
	}
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getSerieID() {
		return serieID;
	}

	public void setSerieID(String serieID) {
		this.serieID = serieID;
	}

	public int getCantInstances() {
		return cantInstances;
	}

	public void setCantInstances(int cantInstances) {
		this.cantInstances = cantInstances;
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
		LegacySerie other = (LegacySerie) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
