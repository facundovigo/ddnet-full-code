package ddnet.useragent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;

import ddnet.useragent.commands.CommandEvent;
import ddnet.useragent.commands.CommandEventHandler;
import ddnet.useragent.commands.CommandExecutor;
import ddnet.useragent.commands.local.LocalCommandExecutor;
import ddnet.useragent.downloads.DownloadManager;

public class UserAgent {
	private static final Logger log = Logger.getLogger(UserAgent.class);
	private static final Object syncObject = new Object();
	public static final int DEFAULT_SERVER_PORT = 6015;
	
	private boolean started = false;
	private Server server;
	private int serverPort;
	private User currentUser;
	private UserAgentWebSocketHandler userAgentWebSocketHandler;
	private final CommandExecutor localCommandExecutor = new LocalCommandExecutor();
	private final CommandHandler commandHandler = new CommandHandler(localCommandExecutor);
	private final DownloadManager downloadManager = new DownloadManager(localCommandExecutor);
	private final Collection<UserAgentEventHandler> eventHandlers = new ArrayList<UserAgentEventHandler>();

	public UserAgent() {
		this(DEFAULT_SERVER_PORT);
	}
	
	public UserAgent(int serverPort) {
		this.serverPort = serverPort;
		this.userAgentWebSocketHandler = new UserAgentWebSocketHandler();
		this.userAgentWebSocketHandler.setHandler(new DefaultHandler());
		this.userAgentWebSocketHandler.addEventHandler(new UserAgentWebSocketEventAdapter() {
			@Override
			public void onConnectionClosed(UUID connectionID, int closeCode, String message) {
				setCurrentUser(null);
			}
		});		
		this.commandHandler.addEventHandler(new  CommandEventHandler() {			
			@Override
			public void onEvent(CommandEvent event) {
				notifyCommandEvent(event);
			}
		});
		this.userAgentWebSocketHandler.addEventHandler(commandHandler);		
	}
	
	public void addEventHandler(UserAgentWebSocketEventHandler handler) {
		userAgentWebSocketHandler.addEventHandler(handler);
	}
	
	public void addEventHandler(UserAgentEventHandler handler) {
		if (handler == null || eventHandlers.contains(handler))
			return;
		
		eventHandlers.add(handler);
	}

	public void removeEventHandler(UserAgentWebSocketEventHandler handler) {
		userAgentWebSocketHandler.removeEventHandler(handler);
	}

	public void removeEventHandler(UserAgentEventHandler handler) {
		if (handler == null || !eventHandlers.contains(handler))
			return;
		
		eventHandlers.remove(handler);
	}
		
	public void start()  {
		synchronized (syncObject) {
			if (started)
				throw new RuntimeException("DDNET UserAgent ya se encuentra inicializado!");
			
			downloadManager.start(App.getInstance().getProperty("downloads.maximum-simultaneous", 1));
			
			startServer();
			
			started = true;
			log.info("DDNET UserAgent iniciado.");
		}
	}
	
	public void stop()  {
		synchronized (syncObject) {
			if (!started)
				return;
			
			stopServer();
			
			downloadManager.stop();
			
			started = false;
			log.info("DDNET UserAgent detenido.");
		}	
	}
	
	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
		notifyCurrentUserChanged(currentUser);
	}
	
	public void sendData(UUID connectionID, String data) throws IOException {
		userAgentWebSocketHandler.sendData(connectionID, data);
	}
	
	public DownloadManager getDownloadManager() {
		return downloadManager;
	}	
	
	private void startServer() {
		synchronized (syncObject) {
			server = new Server(serverPort);
			try {
	            server.setHandler(userAgentWebSocketHandler);	            
				server.start();
			} catch (Throwable t) {
				t.printStackTrace();
			}		
		}
	}

	private void stopServer() {
		if (server == null)
			return;
		
		try {
			server.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Collection<UserAgentEventHandler> getEventHandlers() {
		return new ArrayList<UserAgentEventHandler>(eventHandlers);		
	}
	
	private void notifyCurrentUserChanged(User newCurrentUser) {
		for(UserAgentEventHandler handler : getEventHandlers())
			try { handler.onCurrentUserChanged(newCurrentUser); } catch (Throwable ignore) { }
	}
	
	private void notifyCommandEvent(CommandEvent event) {
		for(UserAgentEventHandler handler : getEventHandlers())
			try { handler.onCommandEvent(event); } catch (Throwable ignore) { }		
	}	
}
