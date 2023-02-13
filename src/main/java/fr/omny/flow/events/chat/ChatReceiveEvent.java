package fr.omny.flow.events.chat;


import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.omny.flow.chat.ChatMessage;
import lombok.Getter;

@Getter
public class ChatReceiveEvent extends Event {

	public static final HandlerList HANDLER_LIST = new HandlerList();

	/**
	 * @return the handlerList
	 */
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	private ChatMessage chatMessage;

	/**
	 * @param isAsync
	 * @param chatMessage
	 */
	public ChatReceiveEvent( ChatMessage chatMessage) {
		super(true);
		this.chatMessage = chatMessage;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}

}
