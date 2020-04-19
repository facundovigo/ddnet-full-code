package ddnet.useragent.commands;

public class CommandEvent {
	public static final int INFO = 0;
	public static final int WARN = 1;
	public static final int ERROR = 2;
	
	private final Command command;
	private final int type;
	private final String text;
	private final String details;
	
	public CommandEvent(Command command, int type, String text, String details) {
		this.command = command;
		this.type = type;
		this.text = text;
		this.details = details;
	}

	public Command getCommand() {
		return command;
	}

	public int getType() {
		return type;
	}

	public String getText() {
		return text;
	}

	public String getDetails() {
		return details;
	}

	public boolean isInfo() {
		return this.type == INFO;
	}	
	
	public boolean isWarn() {
		return this.type == WARN;
	}	

	public boolean isError() {
		return this.type == ERROR;
	}	
}
