package ddnet.useragent.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public abstract class CommandExecutor {
	protected static final Logger log = Logger.getLogger(CommandExecutor.class);
			
	private final List<Command> history = new ArrayList<Command>();
	
	public void execute(Command command) {
		if (command == null)
			throw new IllegalArgumentException("command");
		
		synchronized (history) {
			if (history.size() >= 100)
				history.remove(0);
			history.add(command);
		}
		
		try {
			doExecuteCommand(command);
		} catch (Throwable t) {
			log.error(String.format("Error ejecutando comando '%s'", command.getCommandName()), t);
			throw t;
		}
	}
	
	protected abstract void doExecuteCommand(Command command);
}
