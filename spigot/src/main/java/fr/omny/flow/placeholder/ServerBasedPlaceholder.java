package fr.omny.flow.placeholder;


import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import fr.omny.flow.api.attributes.Sendable;
import fr.omny.flow.api.placeholders.Placeholder;


public class ServerBasedPlaceholder extends Placeholder {

	private Function<Server, String> applier;

	public ServerBasedPlaceholder(String name, Function<Server, String> applier) {
		super(name);
		this.applier = applier;
	}

	@Override
	public String apply(Sendable player) {
		return applier.apply(Bukkit.getServer());
	}

}
