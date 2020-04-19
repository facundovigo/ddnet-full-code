package ddnet.useragent.commands.local;

import ddnet.useragent.commands.Command;
import ddnet.useragent.commands.CommandExecutor;

public class LocalCommandExecutor extends CommandExecutor {
	@Override
	protected void doExecuteCommand(Command command) {
		command.execute();
	}
}
