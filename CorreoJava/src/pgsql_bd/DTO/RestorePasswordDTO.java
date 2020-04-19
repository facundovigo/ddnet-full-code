package pgsql_bd.DTO;

public class RestorePasswordDTO {
	
	/**
	 * TABLA dd_password_restore
	 */
	
	private long id;				//COLUMNA "id"
	private long userID;			//COLUMNA "user_id"
	private String restKey;			//COLUMNA "restore_key"
	private boolean restDone;		//COLUMNA "restore_done"
	private int restTimes;			//COLUMNA "restore_times"
	private String restInitDate;	//COLUMNA "restore_init_date"
	private String restLimitDate;	//COLUMNA "restore_limit_date"
	private String restHost;		//COLUMNA "restore_host_requested"
	
	public RestorePasswordDTO(long id, long userID, String restKey,
							  boolean restDone, int restTimes,
							  String restInitDate, String restLimitDate,
							  String restHost){
		
		this.id = id;
		this.userID = userID;
		this.restKey = restKey;
		this.restDone = restDone;
		this.restTimes = restTimes;
		this.restInitDate = restInitDate;
		this.restLimitDate = restLimitDate;
		this.restHost = restHost;
	}

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

	public String getRestKey() {
		return restKey;
	}

	public void setRestKey(String restKey) {
		this.restKey = restKey;
	}

	public boolean isRestDone() {
		return restDone;
	}

	public void setRestDone(boolean restDone) {
		this.restDone = restDone;
	}

	public int getRestTimes() {
		return restTimes;
	}

	public void setRestTimes(int restTimes) {
		this.restTimes = restTimes;
	}

	public String getRestInitDate() {
		return restInitDate;
	}

	public void setRestInitDate(String restInitDate) {
		this.restInitDate = restInitDate;
	}

	public String getRestLimitDate() {
		return restLimitDate;
	}

	public void setRestLimitDate(String restLimitDate) {
		this.restLimitDate = restLimitDate;
	}

	public String getRestHost() {
		return restHost;
	}

	public void setRestHost(String restHost) {
		this.restHost = restHost;
	}

	
}
