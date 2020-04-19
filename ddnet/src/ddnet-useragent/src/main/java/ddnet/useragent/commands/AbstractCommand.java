package ddnet.useragent.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

public abstract class AbstractCommand implements Command {
	protected static final Logger log = Logger.getLogger(AbstractCommand.class);

	private final Collection<CommandEventHandler> eventHandlers = new ArrayList<CommandEventHandler>();
	private final Map<String, Object> properties = new ConcurrentHashMap<String, Object>();

	public AbstractCommand(Object parameters) {
		// Nada que hacer por defecto.
	}
	
	@Override
	public void addEventHandler(CommandEventHandler handler) {
		if (handler == null || eventHandlers.contains(handler))
			return;
		
		eventHandlers.add(handler);
	}

	@Override
	public void removeEventHandler(CommandEventHandler handler) {
		if (handler == null || !eventHandlers.contains(handler))
			return;
		
		eventHandlers.remove(handler);
	}
		
	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}
	
	protected Collection<CommandEventHandler> getEventHandlers() {
		return new ArrayList<CommandEventHandler>(eventHandlers);		
	}
	
	protected void info(String text) {
		info(text, null);
	}
	protected void info(String text, String details) {
		notifyCommandEvent(CommandEvent.INFO, text, details);
	}
	protected void warn(String text) {
		warn(text, null);
	}
	protected void warn(String text, String details) {
		notifyCommandEvent(CommandEvent.WARN, text, details);
	}
	protected void error(String text) {
		error(text, "");
	}
	protected void error(String text, String details) {
		notifyCommandEvent(CommandEvent.ERROR, text, details);
	}
	protected void error(String text, Throwable details) {
		notifyCommandEvent(CommandEvent.ERROR, text, ExceptionUtils.getFullStackTrace(details));
	}
	
	private void notifyCommandEvent(int type, String text, String details) {
		CommandEvent event = new CommandEvent(this, type, text, details);
		for(CommandEventHandler handler : getEventHandlers())
			try { handler.onEvent(event); } catch (Throwable ignore) { }		
	}
	
	@Override
	public void execute() throws CommandExecutionException {
		try {
			doExecute();
		} catch(Throwable t) {
			throw new CommandExecutionException(getCommandName(), t);
		}
	}

	protected abstract void doExecute();
	
	@Override
	public String toString() {
		return getCommandName();
	}
}
