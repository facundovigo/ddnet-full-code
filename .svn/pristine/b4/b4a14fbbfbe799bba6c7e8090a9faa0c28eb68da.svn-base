package ddnet.useragent.commands;

public class CommandExecutionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String commandName;
	
	public CommandExecutionException(String commandName, Throwable cause) {
		super(cause);
		
		this.commandName = commandName;
	}
	
	public String getCommandName() {
		return commandName;
	}
}
