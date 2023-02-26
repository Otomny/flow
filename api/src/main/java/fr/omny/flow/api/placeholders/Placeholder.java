package fr.omny.flow.api.placeholders;

import fr.omny.flow.api.attributes.Sendable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class Placeholder {
	
	private String name;

	public abstract String apply(Sendable player);

}
