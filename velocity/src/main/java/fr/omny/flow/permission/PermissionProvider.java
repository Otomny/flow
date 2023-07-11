package fr.omny.flow.permission;

import com.velocitypowered.api.proxy.Player;

public interface PermissionProvider {
	

	/**
	 * Add a permission to a player
	 * 
	 * @param player     The player
	 * @param permission The permission
	 */
	void addPermission(Player player, String permission);

	/**
	 * Add a permission to a player
	 * 
	 * @param <T>        The type of the value stored for that permission
	 * @param player     The player
	 * @param permission The permission
	 * @param value      The value to store for that permission
	 */
	<T> void addPermission(Player player, String permission, T value);

	/**
	 * Check if a player has a specific permission
	 * 
	 * @param player     The player
	 * @param permission The permission
	 * @return
	 */
	boolean hasPermission(Player player, String permission);

	/**
	 * Get the permission value of a specific permission
	 * 
	 * @param <T>        The type stored for that permission
	 * @param player     The player
	 * @param permission The permission
	 * @return
	 */
	<T> T getPermission(Player player, String permission);

	/**
	 * Get the permission value of a specific permission
	 * 
	 * @param <T>          The type stored for that permission
	 * @param player       The player
	 * @param permission   The permission
	 * @param defaultValue The default value if the permission is not set
	 * @return
	 */
	<T> T getPermission(Player player, String permission, T defaultValue);

}
