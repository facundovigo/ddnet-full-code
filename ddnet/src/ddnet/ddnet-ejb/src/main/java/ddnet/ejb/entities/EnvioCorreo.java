package ddnet.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Table(name="dd_envio_correo")
@Entity
public class EnvioCorreo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="study_id")
	private long studyID;
	
	@Column(name="user_id")
	private long userID;
	
	@Column(name="mail_destino")
	private String receiver;
	
	@Column(name="mail_asunto")
	private int subject;
	
	@Column(name="mail_envio")
	private boolean hasSent;
	
	@Column(name="mail_llego")
	private boolean hasReceived;
	
	@Column(name="mail_fecha_registro")
	private String dateRecord;
	
	@Column(name="mail_fecha_envio")
	private String dateSent;
	
	@Column(name="mail_detalle_error")
	private String errorDetail;
	
	@Column(name="restore_uuid")
	private String restoreUUID;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getStudyID() {
		return studyID;
	}

	public void setStudyID(long studyID) {
		this.studyID = studyID;
	}

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public int getSubject() {
		return subject;
	}

	public void setSubject(int subject) {
		this.subject = subject;
	}

	public boolean isHasSent() {
		return hasSent;
	}

	public void setHasSent(boolean hasSent) {
		this.hasSent = hasSent;
	}

	public boolean isHasReceived() {
		return hasReceived;
	}

	public void setHasReceived(boolean hasReceived) {
		this.hasReceived = hasReceived;
	}

	public String getDateRecord() {
		return dateRecord;
	}

	public void setDateRecord(String dateRecord) {
		this.dateRecord = dateRecord;
	}

	public String getDateSent() {
		return dateSent;
	}

	public void setDateSent(String dateSent) {
		this.dateSent = dateSent;
	}

	public String getErrorDetail() {
		return errorDetail;
	}

	public void setErrorDetail(String errorDetail) {
		this.errorDetail = errorDetail;
	}

	public String getRestoreUUID() {
		return restoreUUID;
	}

	public void setRestoreUUID(String restoreUUID) {
		this.restoreUUID = restoreUUID;
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
		EnvioCorreo other = (EnvioCorreo) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override 
	public String toString() {
		return String.format("%d", subject);
	}
}