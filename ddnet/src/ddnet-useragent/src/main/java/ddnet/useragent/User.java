package ddnet.useragent;

public class User {
	private final String login;
	private final String token;
	private final String fullName;
	
	public User(String login, String token, String fullName) {
		this.login = login;
		this.token = token;
		this.fullName = fullName;
	}
	
	public String getLogin() {
		return login;
	}
	public String getToken() {
		return token;
	}
	public String getFullName() {
		return fullName;
	}
}
