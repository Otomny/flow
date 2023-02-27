package fr.omny.flow.api.placeholders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

	/**
	 * 
	 * @param text
	 * @param player
	 * @param paramPlaceholders
	 * @return
	 */
	public String inject(String text, Sendable player, List<Placeholder> paramPlaceholders) {
		return inject(text, player, paramPlaceholders.stream()
				.collect(Collectors.toMap(Placeholder::getName, Function.identity())));
	}

	/**
	 * 
	 * @param text
	 * @param player
	 * @param paramPlaceholders
	 * @return
	 */
	public String inject(String text, Sendable player, Map<String, Placeholder> paramPlaceholders) {
		return pattern.matcher(text).replaceAll(matchResult -> {
			String foundPattern = matchResult.group(1);
			if (paramPlaceholders.containsKey(foundPattern)) {
				return paramPlaceholders.get(foundPattern).apply(player);
			}
			if (!this.placeholders.containsKey(foundPattern)) {
				return "NOT_FOUND_" + foundPattern;
			}
			return this.placeholders.get(foundPattern).apply(player);
		});
	}

	/**
	 * 
	 * @param text
	 * @param player
	 * @return
	 */
	public String inject(String text, Sendable player) {
		return pattern.matcher(text).replaceAll(matchResult -> {
			String foundPattern = matchResult.group(1);
			if (!this.placeholders.containsKey(foundPattern)) {
				return "NOT_FOUND_" + foundPattern;
			}
			return this.placeholders.get(foundPattern).apply(player);
		});
	}
}
