package ddnet.useragent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import ddnet.useragent.commands.Command;
import ddnet.useragent.commands.CommandEventHandler;
import ddnet.useragent.commands.CommandExecutor;
import ddnet.useragent.commands.CommandFactory;

public class CommandHandler extends UserAgentWebSocketEventAdapter {
	private static final Logger log = Logger.getLogger(CommandHandler.class);
	public static final String DOWNLOAD_PROPERTY = "download";
	
	private final CommandExecutor commandExecutor;
	private final Collection<CommandEventHandler> eventHandlers = new ArrayList<CommandEventHandler>();

	public CommandHandler(CommandExecutor commandExecutor) {
		if (commandExecutor == null)
			throw new IllegalArgumentException("commandExecutor");
		
		this.commandExecutor = commandExecutor;
	}
	
	public void addEventHandler(CommandEventHandler handler) {
		if (handler == null || eventHandlers.contains(handler))
			return;
		
		eventHandlers.add(handler);
	}

	public void removeEventHandler(CommandEventHandler handler) {
		if (handler == null || !eventHandlers.contains(handler))
			return;
		
		eventHandlers.remove(handler);
	}
	
	private Collection<CommandEventHandler> getEventHandlers() {
		return new ArrayList<CommandEventHandler>(eventHandlers);		
	}
	
	@Override
	public void onMessageReceived(UUID connectionID, String data) {
		String commandName = null;
		try {
			// JSONParser NO ES thread-safe.
			Object parsedData = new JSONParser().parse(data);
			if (!(parsedData instanceof JSONObject))
				// Los comandos llegan como objetos, asi que si no es un objeto JSON, terminamos el análisis acá.
				return; 
			
			JSONObject commandData = (JSONObject)parsedData;
			commandName = (String)commandData.get("command");
			Object parameters = commandData.get("parameters");
			
			Command command = CommandFactory.getInstance().create(commandName, parameters, getEventHandlers());			
			this.commandExecutor.execute(command);
		} catch (Throwable t) {
			log.error("Error recibiendo comando para su ejecución. CommandName = " + commandName, t);
		}
	}
}
