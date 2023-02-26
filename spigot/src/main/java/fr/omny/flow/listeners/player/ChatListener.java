package fr.omny.flow.listeners.player;


import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import fr.omny.flow.api.process.Env;
import fr.omny.flow.chat.ChatMessage;
import fr.omny.flow.chat.ChatTransformer;
import fr.omny.flow.chat.DefaultChatFormater;
import fr.omny.flow.config.Config;
import fr.omny.flow.events.chat.ChatReceiveEvent;
import fr.omny.flow.listeners.aop.ListenerProvider;
import fr.omny.odi.Autowired;

public class ChatListener implements ListenerProvider {

	@Config("distributed.enable_broadcast_chat")
	private boolean enabled;

	public Listener provideBroadcastChat(@Autowired RedissonClient redissonClient) {
		RTopic chatTopic = redissonClient.getTopic("player_chat");
		chatTopic.addListener(ChatMessage.class, (channel, chatMessage) -> {
			ChatReceiveEvent event = new ChatReceiveEvent(chatMessage);
			Bukkit.getServer().getPluginManager().callEvent(event);
		});
		// If the server want to 
		return !enabled ? null : new Listener() {

			@Autowired Optional<ChatTransformer> chatFormat;
			@Autowired DefaultChatFormater fallbackChatFormat;

			@EventHandler
			public void onChat(AsyncPlayerChatEvent event) {
				event.setCancelled(true);
				String fullMessage = chatFormat.orElse(fallbackChatFormat).transform(event);
				String serverName = Env.getServerName();
				chatTopic.publish(
						new ChatMessage(serverName, event.getPlayer().getName(), event.getPlayer().getUniqueId(), fullMessage));
			}

			@EventHandler
			public void onSubChat(ChatReceiveEvent event) {
				var msg = event.getChatMessage();
				Bukkit.broadcastMessage(msg.getFullMessage());
			}
		};
	}

}
