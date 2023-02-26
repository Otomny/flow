package fr.omny.flow.placeholder;

import java.util.List;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import fr.omny.flow.api.placeholders.Placeholder;
import fr.omny.flow.api.placeholders.PlaceholderProvider;

public class PlayerInfoPlaceholderProvider implements PlaceholderProvider {

	List<Placeholder> playerInfos() {
		return List.of(
				// Player based
				new PlayerBasedPlaceholder("playerName", Player::getName),
				new PlayerBasedPlaceholder("playerHealth", player -> String.valueOf(player.getHealth())),
				new PlayerBasedPlaceholder("playerWorld", player -> player.getWorld().getName()),
				new PlayerBasedPlaceholder("playerLevel", player -> String.valueOf(player.getLevel())),
				new PlayerBasedPlaceholder("playerIp", player -> player.getAddress().getAddress().getHostName()),
				new PlayerBasedPlaceholder("playerLocale", player -> player.getLocale()),
				// Server based
				new ServerBasedPlaceholder("serverName", Server::getName),
				new ServerBasedPlaceholder("serverVersion", Server::getVersion));
	}

}
