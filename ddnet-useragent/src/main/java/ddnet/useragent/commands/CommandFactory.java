package ddnet.useragent.commands;

import java.util.Collection;

import ddnet.useragent.commands.local.AnnounceUserCommand;
import ddnet.useragent.commands.local.DismissUserCommand;
import ddnet.useragent.commands.local.KeepAliveCommand;
import ddnet.useragent.commands.local.RecordStudyAudioReportCommand;
import ddnet.useragent.commands.local.RetrieveImagesCommand;
import ddnet.useragent.commands.local.SendImagesToViewerCommand;
import ddnet.useragent.commands.local.ShowWindowCommand;


public class CommandFactory {
	private static final CommandFactory INSTANCE = new CommandFactory();
	
	private CommandFactory() { }
	
	public static CommandFactory getInstance() {
		return INSTANCE;
	}
	
	public Command create(String commandName, Object parameters) {
		return create(commandName, parameters, null);
	}
	
	public Command create(String commandName, Object parameters, Collection<CommandEventHandler> eventHandlers) {
		Command command = null;		
		switch(commandName) {
			case KeepAliveCommand.COMMAND_NAME: command = new KeepAliveCommand(parameters); break;
			case ShowWindowCommand.COMMAND_NAME: command = new ShowWindowCommand(parameters); break;
			case AnnounceUserCommand.COMMAND_NAME: command = new AnnounceUserCommand(parameters); break;
			case DismissUserCommand.COMMAND_NAME: command = new DismissUserCommand(parameters); break;
			case RetrieveImagesCommand.COMMAND_NAME: command = new RetrieveImagesCommand(parameters); break;
			case SendImagesToViewerCommand.COMMAND_NAME: command = new SendImagesToViewerCommand(parameters); break;
			case RecordStudyAudioReportCommand.COMMAND_NAME: command = new RecordStudyAudioReportCommand(parameters); break;
		}
		
		if (command == null)
			throw new RuntimeException("Comando desconocido: " + commandName);
		
		if (eventHandlers != null)
			for(CommandEventHandler handler : eventHandlers)
				command.addEventHandler(handler);
		
		return command;
	}
}
