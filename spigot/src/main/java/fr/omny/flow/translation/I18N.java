package fr.omny.flow.translation;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.omny.flow.api.attributes.Sendable;
import fr.omny.flow.api.placeholders.Placeholder;
import fr.omny.flow.api.placeholders.Placeholders;
import fr.omny.flow.attributes.Playerable;
import fr.omny.odi.Autowired;
import fr.omny.odi.Component;

@Component(proxy = false)
public class I18N {

	@Autowired
	private TranslationRepository repository;
	@Autowired
	private Optional<PlayerToLocaleProvider> provider;
	@Autowired
	private Placeholders placeholders;

	public I18N() {
	}

	/**
	 * Return the locale used by a player
	 * 
	 * @param player
	 * @return
	 */
	public String getLocale(Player player) {
		return this.provider.orElse(new DefaultPlayerLocaleProvider()).locale(player);
	}

	/**
	 * Set a translation
	 * 
	 * @param locale      The language locale in lowercase (ISO 639-1)
	 * @param key         The key of the translation
	 * @param translation The translation, or a default one
	 */
	public void put(String locale, String key, String translation) {
		var possibleTranslation = this.repository.findById(locale);
		if (possibleTranslation.isEmpty()) {
			Translation trns = new Translation();
			trns.setKey(locale);
			trns.setTranslations(new HashMap<>());
			trns.getTranslations().put(key, translation);
			this.repository.save(trns);
			return;
		}
		var trns = possibleTranslation.get();
		trns.getTranslations().put(key, translation);
		this.repository.save(trns);
	}

	/**
	 * Send a translation to the player
	 * Apply placeholders transformation to it
	 * 
	 * @param player The player
	 * @param key    The translation key
	 */
	public <T extends Sendable & Playerable> void send(T player, String key) {
		send(player, key, List.of());
	}

	/**
	 * Send a translation to the player
	 * Apply placeholders transformation to it
	 * 
	 * @param player            The player
	 * @param key               The translation key
	 * @param paramPlaceholders The placeholders parameters
	 */
	public <T extends Sendable & Playerable> void send(T player, String key, List<Placeholder> paramPlaceholders) {
		String translation = get(player, key);
		String translationReplaced = placeholders.inject(translation, player, paramPlaceholders);
		player.send(ChatColor.translateAlternateColorCodes('&', translationReplaced));
	}

	/**
	 * Get a translation for player (Calls {@link I18N#get(String, String)})
	 * 
	 * @param player the player
	 * @param key    the translation key
	 * @return the translation or a default one
	 */
	public <T extends Sendable & Playerable> String get(T player, String key) {
		return get(this.provider.orElse(new DefaultPlayerLocaleProvider()).locale(player.getPlayer()), key);
	}

	/**
	 * Get a translation
	 * 
	 * @param locale The language locale in lowercase (ISO 639-1)
	 * @param key    The key of the translation
	 * @return The translation, or a default one
	 */
	public String get(String locale, String key) {
		var possibleTranslation = this.repository.findById(locale);
		if (possibleTranslation.isEmpty())
			return "§ctranslation_not_found_for_" + locale;
		return possibleTranslation.get().getTranslations().getOrDefault(key,
				"§ctranslation_not_found_for_" + locale + "_" + key);
	}

}
