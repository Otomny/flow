package fr.omny.flow.translation;


import org.bukkit.entity.Player;

public interface PlayerToLocaleProvider {

	/**
	 * Map a player to his locale
	 * 
	 * @param player The player
	 * @return The ISO 639-1 locale code (lowercase, 2 char)
	 */
	String locale(Player player);

}
