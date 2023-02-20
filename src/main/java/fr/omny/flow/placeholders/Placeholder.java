package fr.omny.flow.placeholders;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class Placeholder {
	
	private String name;

	public abstract String apply(Player player);

}
