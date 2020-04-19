package ddnet.useragent;

import java.util.UUID;

public interface UserAgentWebSocketEventHandler {
	void onConnectionOpen(UUID connectionID);
	void onConnectionClosed(UUID connectionID, int closeCode, String message);
	void onMessageReceived(UUID connectionID, String data);
	void onMessageSent(UUID connectionID, String data);
}

