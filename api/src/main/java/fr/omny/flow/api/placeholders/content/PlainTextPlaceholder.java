package fr.omny.flow.api.placeholders.content;

import fr.omny.flow.api.attributes.Sendable;
import fr.omny.flow.api.placeholders.Placeholder;

public class PlainTextPlaceholder extends Placeholder {

	private String value;

	public PlainTextPlaceholder(String name, String value) {
		super(name);
		this.value = value;
	}

	@Override
	public String apply(Sendable player) {
		return this.value;
	}

}
