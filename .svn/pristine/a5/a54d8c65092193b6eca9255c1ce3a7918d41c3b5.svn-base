package ddnet.useragent.commands;

import java.util.Map;

public interface Command {
	String getCommandName();
	void addEventHandler(CommandEventHandler handler);
	void removeEventHandler(CommandEventHandler handler);	
	Map<String, Object> getProperties();	
	void execute();
}
