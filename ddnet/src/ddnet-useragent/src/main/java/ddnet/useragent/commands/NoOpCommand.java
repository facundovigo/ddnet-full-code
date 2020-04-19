package ddnet.useragent.commands;

public class NoOpCommand extends AbstractCommand {
	public static final String COMMAND_NAME = "no-op";
	private static final NoOpCommand INSTANCE = new NoOpCommand();
	
	private NoOpCommand() {
		super(null);
	}
	
	public static NoOpCommand getInstance() {
		return INSTANCE;
	}
	
	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}
	
	@Override
	public void doExecute() {
		// No Operation :)
	}
}
