package pgsql_bd.DTO;

public class UserDTO {
	
	/**
	 * TABLA dd_user
	 */
	
	private long id;				//COLUMNA "id"
	private String firstName;		//COLUMNA "first_name"
	private String lastName;		//COLUMNA "last_name"
	private String login;			//COLUMNA "login"
	private String password;		//COLUMNA "password"
	private boolean isDeleted;		//COLUMNA "deleted"
	
	public UserDTO(long id, String firstName, String lastName, 
				String login, String password, boolean isDeleted){
		
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.login = login;
		this.password = password;
		this.isDeleted = isDeleted;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	
}
