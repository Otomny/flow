package fr.omny.flow.player;

import fr.omny.odi.Component;

@Component
public class Permissions {

	@Component(value = "bukkit", proxy = false)
	public PermissionProvider getDefaultProvider() {
		return new BukkitPermissionProvider();
	}

}
