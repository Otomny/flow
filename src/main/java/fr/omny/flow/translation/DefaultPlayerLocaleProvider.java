package fr.omny.flow.translation;

import org.bukkit.entity.Player;

public class DefaultPlayerLocaleProvider implements PlayerToLocaleProvider{

	@Override
	public String locale(Player player) {
		return "en";
	}
	
}
