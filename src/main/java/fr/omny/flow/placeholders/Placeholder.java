package fr.omny.flow.placeholders;

import java.util.function.Function;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Placeholder {
	
	private String name;
	private Function<Player, String> applier;

}