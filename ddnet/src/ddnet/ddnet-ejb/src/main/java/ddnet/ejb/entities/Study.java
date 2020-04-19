package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Table(name="dd_study")
@Entity
public class Study implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="id")
	@Id
	private long id;
	
	@Column(name="is_reported")
	private boolean isReported;

	@Column(name="report_body")
	private String report;
	
	@OneToOne(cascade= CascadeType.REFRESH)
	@JoinColumn(name="id")
	private LegacyStudy legacyStudy;
	
	@Column(name="estado_incidencia")
	private int state;
	
	@ManyToOne
	@JoinColumn(name="id", referencedColumnName="study_id", insertable=false, updatable=false)
	private UserStudy Ustudy;
	
	@ManyToOne
	@JoinColumn(name="id", referencedColumnName="id_study_datos_clinicos", insertable=false, updatable=false)
	private DatosClinicos datosclinicos;
	
	@Column(name="archivos_adjuntos")
	private boolean hasArchive;
	
	@Column(name="has_orden_medica")
	private boolean hasOrdenMed;
	
	@ManyToOne
	@JoinColumn(name="id", referencedColumnName="study_id", insertable=false, updatable=false)
	private Informe informe;
	
	@Column(name="remuneration")
	private String remuneracion;
	
	@Column(name="study_mp3") private boolean mp3;
	@Column(name="audio_report") private boolean audioReport;
	
	
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
			report == null || report.trim().isEmpty() ? (audioReport? ReportStatusCode.AUDIO_RECORD : ReportStatusCode.TO_BE_INFORMED) 
					: ReportStatusCode.TO_BE_CONFIRMED;
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
	
	public LegacyStudy getLegacyStudy() {
		return legacyStudy;
	}

	public void setLegacyStudy(LegacyStudy legacyStudy) {
		this.legacyStudy = legacyStudy;
	}

	public Institution getInstitution() {
		return legacyStudy != null ? legacyStudy.getInstitution() : null;
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public UserStudy getUstudy() {
		return Ustudy;
	}

	public void setUstudy(UserStudy ustudy) {
		Ustudy = ustudy;
	}

	public DatosClinicos getDatosclinicos() {
		return datosclinicos;
	}

	public void setDatosclinicos(DatosClinicos datosclinicos) {
		this.datosclinicos = datosclinicos;
	}
	
	public boolean hasArchive() {
		return hasArchive;
	}

	public void setArchive(boolean hasArchive) {
		this.hasArchive = hasArchive;
	}
	
	public boolean hasOrdenMed() {
		return hasOrdenMed;
	}

	public void setOrdenMed(boolean hasOrdenMed) {
		this.hasOrdenMed = hasOrdenMed;
	}

	public Informe getInforme() {
		return informe;
	}

	public void setInforme(Informe informe) {
		this.informe = informe;
	}
	
	
	public String getRemuneracion() {
		return remuneracion;
	}

	public void setRemuneracion(String remuneracion) {
		this.remuneracion = remuneracion;
	}

	public boolean isMp3() {
		return mp3;
	}

	public void setMp3(boolean mp3) {
		this.mp3 = mp3;
	}

	public boolean isAudioReport() {
		return audioReport;
	}

	public void setAudioReport(boolean audioReport) {
		this.audioReport = audioReport;
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
		AUDIO_RECORD(1),
		TO_BE_CONFIRMED(2),
		CLOSED(3);
		
		private int value;
		
		ReportStatusCode(int value) {
			this.value = value;
		}
		
		public int value() {
			return value;
		}
	}
}
