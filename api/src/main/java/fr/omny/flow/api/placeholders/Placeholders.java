package fr.omny.flow.api.placeholders;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import fr.omny.flow.api.attributes.Sendable;
import fr.omny.odi.Component;
import lombok.Getter;

@Component
@Getter
public class Placeholders {

	private final Map<String, Placeholder> placeholders = new HashMap<>();
	private final Pattern pattern = Pattern.compile("\\%(.*?)\\%");

	/**
	 * @param placeholder
	 */
	public void registerPlaceholder(Placeholder placeholder) {
		this.placeholders.put(placeholder.getName(), placeholder);
	}

	public String inject(String text, Sendable player) {
		return pattern.matcher(text).replaceAll(matchResult -> {
			String foundPattern = matchResult.group(1);
			if (!this.placeholders.containsKey(foundPattern)) {
				throw new IllegalStateException("No placeholder found with key " + foundPattern + ".");
			}
			return this.placeholders.get(foundPattern).apply(player);
		});
	}
}
