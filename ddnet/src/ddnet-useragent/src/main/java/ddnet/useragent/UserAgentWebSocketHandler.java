package ddnet.useragent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;

public class UserAgentWebSocketHandler extends WebSocketHandler {

	private final Map<UUID, UserAgentWebSocket> connections = new HashMap<UUID, UserAgentWebSocket>();
	private final Collection<UserAgentWebSocketEventHandler> eventHandlers = new ArrayList<UserAgentWebSocketEventHandler>();
	   	
	public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
		return new UserAgentWebSocket();
	}

	public void sendData(UUID connectionID, String data) throws IOException {
		UserAgentWebSocket connection = connections.get(connectionID);
		if (connection == null) 
			throw new RuntimeException(String.format("La conexión '%s' ya no se encuentra activa.", connectionID));
		
		connection.sendData(data);
	}

	public void addEventHandler(UserAgentWebSocketEventHandler handler) {
		if (handler == null || eventHandlers.contains(handler))
			return;		
		eventHandlers.add(handler);
	}

	public void removeEventHandler(UserAgentWebSocketEventHandler handler) {
		if (handler == null || !eventHandlers.contains(handler))
			return;		
		eventHandlers.remove(handler);
	}
	
	private Collection<UserAgentWebSocketEventHandler> getEventHandlers() {
		return new ArrayList<UserAgentWebSocketEventHandler>(eventHandlers);		
	}
	
	private void notifyConnectionOpen(UUID connectionID) {
		for(UserAgentWebSocketEventHandler handler : getEventHandlers())
			try { handler.onConnectionOpen(connectionID); } catch (Throwable ignore) { }
	}
	
	private void notifyConnectionClosed(UUID connectionID, int closeCode, String message) {
		for(UserAgentWebSocketEventHandler handler : getEventHandlers())
			try { handler.onConnectionClosed(connectionID, closeCode, message); } catch (Throwable ignore) { }
	}

	private void notifyMessageReceived(UUID connectionID, String data) {
		for(UserAgentWebSocketEventHandler handler : getEventHandlers())
			try { handler.onMessageReceived(connectionID, data); } catch (Throwable ignore) { }
	}
	
	private void notifyMessageSent(UUID connectionID, String data) {
		for(UserAgentWebSocketEventHandler handler : getEventHandlers())
			try { handler.onMessageSent(connectionID, data); } catch (Throwable ignore) { }
	}
	
	private class UserAgentWebSocket implements WebSocket.OnTextMessage {	
		private final UUID id;
		private Connection connection;
		
		public UserAgentWebSocket() {
			this.id = UUID.randomUUID();			
		}
		
		public void onOpen(Connection connection) {
			boolean connectionAdded = false;
			synchronized (connections) {
				if (connections.isEmpty()) {
					connectionAdded = true;
					this.connection = connection;
					connections.put(this.id, this);
				}
			}
			
			if (connectionAdded)
				notifyConnectionOpen(this.id);
			else
				connection.close(4500, "¡Sólo se permite una conexión a DDNET UserAgent por equipo!");
		}

		public void onMessage(String data) {
			notifyMessageReceived(this.id, data);
		}		
		
		public void onClose(int closeCode, String message) {
			connections.remove(this.id);
			notifyConnectionClosed(this.id, closeCode, message);
		}

		public void sendData(String data) throws IOException {
			this.connection.sendMessage(data);
			notifyMessageSent(this.id, data);
		}
	}	
}
