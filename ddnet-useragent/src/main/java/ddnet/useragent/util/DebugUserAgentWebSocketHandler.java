package ddnet.useragent.util;

import java.util.UUID;

import org.apache.log4j.Logger;

import ddnet.useragent.UserAgentWebSocketEventAdapter;

public class DebugUserAgentWebSocketHandler extends UserAgentWebSocketEventAdapter {
	private static final Logger log = Logger.getLogger(DebugUserAgentWebSocketHandler.class);
	private static final DebugUserAgentWebSocketHandler INSTANCE = new DebugUserAgentWebSocketHandler();
	
	private DebugUserAgentWebSocketHandler() { }
	
	public static DebugUserAgentWebSocketHandler getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void onMessageSent(UUID connectionID, String data) {
		log.debug(String.format("[%s] Enviado: %s", connectionID, data));
	}
	
	@Override
	public void onMessageReceived(UUID connectionID, String data) {
		log.debug(String.format("[%s] Recibido: %s", connectionID, data));				
	}
	
	@Override
	public void onConnectionOpen(UUID connectionID) {
		log.debug(String.format("[%s] Conectado", connectionID));
	}
	
	@Override
	public void onConnectionClosed(UUID connectionID, int closeCode, String message) {
		log.debug(String.format("[%s] Desconectado. CloseCode=%d - CloseMessage: %s", 
				connectionID, closeCode, message));
	}
}
