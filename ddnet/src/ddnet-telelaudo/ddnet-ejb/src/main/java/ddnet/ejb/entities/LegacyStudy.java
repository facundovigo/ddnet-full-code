package ddnet.ejb.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Table(name="study")
@Entity
public class LegacyStudy implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	
	@Column(name="pk")
	@Id
	private long id;

	@Column(name="study_iuid")
	private String studyID;
	
	@Column(name="study_desc")
	private String description;
	
	@Column(name="study_datetime")
	private Date date;
	
	@Column(name="mods_in_study")
	private String modalities;

	@Column(name="accession_no")
	private String accessionNumber;

	@Column(name="study_custom1")
	private String custom1;

	@Column(name="study_custom2")
	private String custom2;

	@Column(name="study_custom3")
	private String custom3;
	
	@Column(name="num_series")			
	private int totalSeries;			
	
	@Column(name="num_instances")
	private int totalInstances;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="study_custom3", referencedColumnName="related_ae_title", insertable=false, updatable=false)
	private Institution institution;
		
	@OneToOne
	@JoinColumn(name="patient_fk")
	private LegacyPatient legacyPatient;
	
	@OneToMany(mappedBy="legacyStudy", fetch=FetchType.LAZY)
	private Set<LegacySerie> legacySeries = new HashSet<LegacySerie>();	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStudyID() {
		return studyID;
	}

	public void setStudyID(String studyID) {
		this.studyID = studyID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFormattedDate() {
		return date != null ? DATE_FORMAT.format(date) : "";
	}
	
	public String getModalities() {
		return modalities;
	}

	public void setModalities(String modalities) {
		this.modalities = modalities;
	}

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}		
		
	public LegacyPatient getLegacyPatient() {
		return legacyPatient;
	}

	public void setLegacyPatient(LegacyPatient legacyPatient) {
		this.legacyPatient = legacyPatient;
	}	

	public Set<LegacySerie> getLegacySeries() {
		return legacySeries;
	}

	public void setLegacySeries(Set<LegacySerie> legacySeries) {
		this.legacySeries = legacySeries;
	}


	public String getCustom1() {
		return custom1;
	}

	public void setCustom1(String custom1) {
		this.custom1 = custom1;
	}

	public String getCustom2() {
		return custom2;
	}

	public void setCustom2(String custom2) {
		this.custom2 = custom2;
	}

	public String getCustom3() {
		return custom3;
	}

	public void setCustom3(String custom3) {
		this.custom3 = custom3;
	}
	
	public int getTotalSeries() {						
		return totalSeries;								
	}													
														
	public void setTotalSeries(int totalSeries) {		
		this.totalSeries = totalSeries;					
	}													

	public int getTotalInstances() {
		return totalInstances;
	}

	public void setTotalInstances(int totalInstances) {
		this.totalInstances = totalInstances;
	}
	
	public Institution getInstitution() {
		return institution;
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
		LegacyStudy other = (LegacyStudy) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("ID=[%d]", id);
	}
}
