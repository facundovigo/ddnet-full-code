package ddhemo.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Table(name="dd_study")
@Entity
public class Study implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="id")
	@Id
	private long id;
	
	@Column(name="usuario_informe")
	private String userReport;
	
	@Column(name="is_reported")
	private boolean isReported;

	@Column(name="report_body")
	private String report;

	@Column(name="notes")
	private String notes;
	
	@OneToOne
	@JoinColumn(name="id")
	private LegacyStudy legacyStudy;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isReported() {
		return isReported;
	}

	public void setReported(boolean isReported) {
		this.isReported = isReported;
	}

	private ReportStatusCode getReportStatusCode() {
		return isReported ? ReportStatusCode.CLOSED : 
			report == null || report.trim().isEmpty() ? ReportStatusCode.TO_BE_INFORMED : ReportStatusCode.TO_BE_CONFIRMED;
	}
	
	public int getReportStatus() {
		return getReportStatusCode().value();
	}
		
	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}
	
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public boolean hasNotes() {
		return notes != null && notes.trim().length() > 0;
	}
		
	public LegacyStudy getLegacyStudy() {
		return legacyStudy;
	}

	public void setLegacyStudy(LegacyStudy legacyStudy) {
		this.legacyStudy = legacyStudy;
	}

	public Institution getInstitution() {
		return legacyStudy != null ? legacyStudy.getInstitution() : null;
	}

	public String getUserReport() {
		return userReport;
	}

	public void setUserReport(String userReport) {
		this.userReport = userReport;
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
		Study other = (Study) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("ID=[%d]", id);
	}
	
	public enum ReportStatusCode {
		TO_BE_INFORMED(0),
		TO_BE_CONFIRMED(1),
		CLOSED(2);
		
		private int value;
		
		ReportStatusCode(int value) {
			this.value = value;
		}
		
		public int value() {
			return value;
		}
	}
}
