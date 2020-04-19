package ddnet.useragent.commands.local;

import ddnet.useragent.App;
import ddnet.useragent.commands.AbstractCommand;

public class ShowWindowCommand extends AbstractCommand {
	public static final String COMMAND_NAME = "show-window";
	
	public ShowWindowCommand(Object parameters) {
		super(parameters);
	}
	
	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}

	@Override
	public void doExecute() {
		App.getInstance().showMainWindow();
	}
}
