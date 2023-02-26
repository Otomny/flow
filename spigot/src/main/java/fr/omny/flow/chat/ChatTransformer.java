package fr.omny.flow.chat;

import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * 
 */
public interface ChatTransformer {
	
	/**
	 * Transform chat
	 * @param event
	 * @return
	 */
	String transform(AsyncPlayerChatEvent event);

}
