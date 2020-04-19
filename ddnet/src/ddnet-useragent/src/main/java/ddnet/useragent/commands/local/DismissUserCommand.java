package ddnet.useragent.commands.local;

import ddnet.useragent.App;
import ddnet.useragent.commands.AbstractCommand;

public class DismissUserCommand extends AbstractCommand {
	public static final String COMMAND_NAME = "dismiss-user";
	
	public DismissUserCommand(Object parameters) {
		super(parameters);
	}
	
	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}

	@Override
	public void doExecute() {
		App.getInstance().getUserAgent().setCurrentUser(null);
	}
}
