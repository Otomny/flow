package fr.omny.flow.player;


import fr.omny.odi.Component;

@Component
public class Permissions {

	@Component("bukkit")
	private PermissionProvider getDefaultProvider() {
		return new BukkitPermissionProvider();
	}

}
