package ddap.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Table(name="ap_practicas_paciente")
@Entity
public class PracticaxPaciente implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="id_pract_pat")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="fecha_pract_pat")
	private String regDate;
	
	@Column(name="hora_pract_pat")
	private String regTime;
	
	@Column(name="ingreso_pract_pat")
	private boolean inside;
	
	@Column(name="fecha_ingreso_pract_pat")
	private String inDate;
	
	@Column(name="hora_ingreso_pract_pat")
	private String inTime;
	
	@Column(name="termino_pract_pat")
	private boolean outside;
	
	@Column(name="fecha_termino_pract_pat")
	private String outDate;
	
	@Column(name="hora_termino_pract_pat")
	private String outTime;
	
	@Column(name="pdf_path_pract_pat")
	private String pdfPath;
	
	@Column(name="orden_medica_pract_pat")
	private String omPath;
	
	@Column(name="abonado_pract_pat")
	private boolean paid;
	
	@Column(name="codigo_pract_pat")
	private String code;
	
	@Column(name="cancelada_pract_pat")
	private boolean cancelled;
	
	@Column(name="fecha_cancelada_pract_pat")
	private String cancelDate;
	
	@Column(name="hora_cancelada_pract_pat")
	private String cancelTime;
	
	@OneToOne
	@JoinColumn(name="paciente_fk_pract_pat")
	private Paciente patient;
	
	@OneToOne
	@JoinColumn(name="practica_fk_pract_pat")
	private Practica practice;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}

	public String getRegTime() {
		return regTime;
	}

	public void setRegTime(String regTime) {
		this.regTime = regTime;
	}

	public boolean isInside() {
		return inside;
	}

	public void setInside(boolean inside) {
		this.inside = inside;
	}

	public String getInDate() {
		return inDate;
	}

	public void setInDate(String inDate) {
		this.inDate = inDate;
	}

	public String getInTime() {
		return inTime;
	}

	public void setInTime(String inTime) {
		this.inTime = inTime;
	}

	public boolean isOutside() {
		return outside;
	}

	public void setOutside(boolean outside) {
		this.outside = outside;
	}

	public String getOutDate() {
		return outDate;
	}

	public void setOutDate(String outDate) {
		this.outDate = outDate;
	}

	public String getOutTime() {
		return outTime;
	}

	public void setOutTime(String outTime) {
		this.outTime = outTime;
	}

	public String getPdfPath() {
		return pdfPath;
	}

	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}

	public String getOmPath() {
		return omPath;
	}

	public void setOmPath(String omPath) {
		this.omPath = omPath;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public String getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(String cancelDate) {
		this.cancelDate = cancelDate;
	}

	public String getCancelTime() {
		return cancelTime;
	}

	public void setCancelTime(String cancelTime) {
		this.cancelTime = cancelTime;
	}

	public Paciente getPatient() {
		return patient;
	}

	public void setPatient(Paciente patient) {
		this.patient = patient;
	}

	public Practica getPractice() {
		return practice;
	}

	public void setPractice(Practica practice) {
		this.practice = practice;
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
		PracticaxPaciente other = (PracticaxPaciente) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s", pdfPath);
	}

}
