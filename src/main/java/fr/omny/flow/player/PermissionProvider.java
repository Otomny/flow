package fr.omny.flow.player;

import org.bukkit.entity.Player;

/**
 * 
 */
public interface PermissionProvider {
	
	/**
	 * Check if a player has a specific permission
	 * @param player The player 
	 * @param permission The permission
	 * @return
	 */
	boolean hasPermission(Player player, String permission);

	/**
	 * Get the permission value of a specific permission
	 * @param <T> The type stored for that permission
	 * @param player The player 
	 * @param permission The permission
	 * @return
	 */
	<T> T getPermission(Player player, String permission);

}
