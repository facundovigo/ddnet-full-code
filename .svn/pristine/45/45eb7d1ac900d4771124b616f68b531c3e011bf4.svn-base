package ddnet.useragent.commands.local;

import org.json.simple.JSONObject;

import ddnet.useragent.App;
import ddnet.useragent.User;
import ddnet.useragent.commands.AbstractCommand;

public class AnnounceUserCommand extends AbstractCommand {
	public static final String COMMAND_NAME = "announce-user";
	
	private String login;
	private String token;
	private String fullName;
	
	public AnnounceUserCommand(Object parameters) {
		super(parameters);
		
		JSONObject announceData = (JSONObject)parameters;
		this.login = (String)announceData.get("login");
		this.token = (String)announceData.get("token");
		this.fullName = (String)announceData.get("fullName");
	}
	
	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}
	
	@Override
	public void doExecute() {
		App.getInstance().getUserAgent().setCurrentUser(new User(login, token, fullName));
	}
}
