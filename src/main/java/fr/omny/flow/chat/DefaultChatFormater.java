package fr.omny.flow.chat;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.omny.odi.Component;

@Component
public class DefaultChatFormater implements ChatTransformer {

	@Override
	public String transform(AsyncPlayerChatEvent event) {
		return "§f"+event.getPlayer().getName()+": "+event.getMessage();
	}
	
}
