package fr.omny.flow.placeholder;

import java.util.function.Function;

import org.bukkit.entity.Player;

import fr.omny.flow.api.attributes.Sendable;
import fr.omny.flow.api.placeholders.Placeholder;
import fr.omny.flow.attributes.Playerable;

public class PlayerBasedPlaceholder extends Placeholder {

	private Function<Player, String> applier;

	public PlayerBasedPlaceholder(String name, Function<Player, String> applier) {
		super(name);
		this.applier = applier;
	}

	@Override
	public String apply(Sendable player) {
		return this.applier.apply(((Playerable) player).getPlayer());
	}

}
