package fr.omny.flow.placeholders.content;

import java.util.function.Function;

import org.bukkit.entity.Player;

import fr.omny.flow.placeholders.Placeholder;

public class PlayerBasedPlaceholder extends Placeholder{

	private Function<Player, String> applier;

	public PlayerBasedPlaceholder(String name, Function<Player, String> applier) {
		super(name);
		this.applier = applier;
	}

	@Override
	public String apply(Player player) {
			return this.applier.apply(player);
	}
	
}
