package pgsql_bd.DTO;

public class MailDTO {
	
	/**
	 * TABLA dd_envio_correo
	 */
	
	private long id;				//COLUMNA "id"
	private long studyID;			//COLUMNA "study_id"
	private long userID;			//COLUMNA "user_id"
	private String receiver;		//COLUMNA "mail_destino"
	private int subject;			//COLUMNA "mail_asunto"
	private boolean hasSent;		//COLUMNA "mail_envio"
	private boolean hasReceived;	//COLUMNA "mail_llego"
	private String dateRecord;		//COLUMNA "mail_fecha_registro"
	private String dateSent;		//COLUMNA "mail_fecha_envio"
	private String errorDetail;		//COLUMNA "mail_detalle_error"
	private String restoreUUID;		//COLUMNA "restore_uuid"
	
	public MailDTO(long id, long studyID, long userID, int subject, 
					boolean hasSent, boolean hasReceived, String receiver, 
					String dateRecord, String dateSent, String errorDetail,
					String restoreUUID){
		
		this.id = id;
		this.studyID = studyID;
		this.userID = userID;
		this.receiver = receiver;
		this.subject = subject;
		this.hasSent = hasSent;
		this.hasReceived = hasReceived;
		this.dateRecord = dateRecord;
		this.dateSent = dateSent;
		this.errorDetail = errorDetail;
		this.restoreUUID = restoreUUID;
	}

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
	
	
}
