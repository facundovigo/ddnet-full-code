package ddnet.useragent.commands.local;

import ddnet.useragent.commands.AbstractCommand;

public class KeepAliveCommand extends AbstractCommand {
	public static final String COMMAND_NAME = "keep-alive";

	public KeepAliveCommand(Object parameters) {
		super(parameters);
	}
	
	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}

	@Override
	public void doExecute() {
		// Nada que hacer!
	}
}
