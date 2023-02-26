package fr.omny.flow.utils;

import java.util.Locale;

import org.bukkit.entity.Player;

import fr.omny.flow.api.attributes.Sendable;
import fr.omny.flow.attributes.Playerable;

public class PlayerUtils {

	private PlayerUtils() {
	}

	public static Sendable wrapSendable(Player player) {
		return message -> player.sendMessage(message);
	}

	public static Playerable wrapPlayerable(Player player) {
		return () -> player;
	}

	public static FlowPlayer flowPlayer(Player player) {
		return new FlowPlayer(player);
	}

	/**
	 * Get the locale of a player
	 * 
	 * @param ipAddress
	 * @return
	 */
	public static Locale getLocaleOfPlayer(Player player) {
		String[] str = player.getLocale().split("\\(_|-)");
		return new Locale(str[0], str[1]);
	}

	public static class FlowPlayer implements Playerable, Sendable {

		private Player player;

		/**
		 * @param player
		 */
		public FlowPlayer(Player player) {
			this.player = player;
		}

		@Override
		public void send(String message) {
			player.sendMessage(message);
		}

		@Override
		public Player getPlayer() {
			return player;
		}

	}

}
