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

	public static Player unwrap(Sendable sendable) {
		return ((Playerable) sendable).getPlayer();
	}

	public static Player unwrap(Playerable playerable) {
		return playerable.getPlayer();
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
		return player.locale();
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
