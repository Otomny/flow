package fr.omny.flow.utils;


import java.util.Locale;

import org.bukkit.entity.Player;

public class PlayerUtils {

	private PlayerUtils() {}

	/**
	 * Get the locale of a player
	 * @param ipAddress
	 * @return
	 */
	public static Locale getLocaleOfPlayer(Player player) {
		String[] str = player.getLocale().split("\\(_|-)");
		return new Locale(str[0], str[1]);
	}

}
