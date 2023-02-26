package fr.omny.flow.chat;


import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {

	private String serverName;
	private String playerName;
	private UUID playerUuid;
	private String fullMessage;

	/**
	 * 
	 */
	public ChatMessage() {}

	/**
	 * @param playerName
	 * @param playerUuid
	 * @param fullMessage
	 */
	public ChatMessage(String serverName, String playerName, UUID playerUuid, String fullMessage) {
		this.serverName = serverName;
		this.playerName = playerName;
		this.playerUuid = playerUuid;
		this.fullMessage = fullMessage;
	}

}
