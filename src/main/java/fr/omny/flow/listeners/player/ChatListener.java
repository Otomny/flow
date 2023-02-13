package fr.omny.flow.listeners.player;


import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import fr.omny.flow.chat.ChatMessage;
import fr.omny.flow.chat.ChatTransformer;
import fr.omny.flow.chat.DefaultChatFormater;
import fr.omny.flow.events.chat.ChatReceiveEvent;
import fr.omny.flow.plugins.Env;
import fr.omny.odi.Autowired;

public class ChatListener implements Listener {

	private RTopic chatTopic;

	@Autowired
	private Optional<ChatTransformer> chatFormat;

	@Autowired
	private DefaultChatFormater fallbackChatFormat;

	public ChatListener(@Autowired RedissonClient redissonClient) {
		this.chatTopic = redissonClient.getTopic("player_chat");
		this.chatTopic.addListener(ChatMessage.class, (channel, chatMessage) -> {
			ChatReceiveEvent event = new ChatReceiveEvent(chatMessage);
			Bukkit.getServer().getPluginManager().callEvent(event);
		});
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);
		String fullMessage = chatFormat.orElse(fallbackChatFormat).transform(event);
		String serverName = Env.getServerName();
		chatTopic.publish(
				new ChatMessage(serverName, event.getPlayer().getName(), event.getPlayer().getUniqueId(), fullMessage));
	}

	@EventHandler
	public void onSubChat(ChatReceiveEvent event){
		Bukkit.broadcastMessage(event.getChatMessage().getFullMessage());
	}

}
