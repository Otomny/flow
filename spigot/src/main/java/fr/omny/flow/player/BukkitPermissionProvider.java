package fr.omny.flow.player;

import org.bukkit.entity.Player;

public class BukkitPermissionProvider implements PermissionProvider {

	@Override
	public boolean hasPermission(Player player, String permission) {
		if (permission == null || permission.isBlank())
			return true;
		return player.hasPermission(permission);
	}

	@Override
	public <T> T getPermission(Player player, String permission) {
		throw new UnsupportedOperationException("getPermission for object is not supported with bukkit permission system");
	}

	@Override
	public <T> T getPermission(Player player, String permission, T defaultValue) {
		throw new UnsupportedOperationException("getPermission for object is not supported with bukkit permission system");
	}

	@Override
	public void addPermission(Player player, String permission) {
		throw new UnsupportedOperationException("addPermission is not supported with bukkit permission system");
	}

	@Override
	public <T> void addPermission(Player player, String permission, T value) {
		throw new UnsupportedOperationException("addPermission for object is not supported with bukkit permission system");
	}

}
