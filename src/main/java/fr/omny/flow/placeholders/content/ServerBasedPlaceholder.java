package fr.omny.flow.placeholders.content;


import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import fr.omny.flow.placeholders.Placeholder;

public class ServerBasedPlaceholder extends Placeholder {

	private Function<Server, String> applier;

	public ServerBasedPlaceholder(String name, Function<Server, String> applier) {
		super(name);
		this.applier = applier;
	}

	@Override
	public String apply(Player player) {
		return applier.apply(Bukkit.getServer());
	}

}
