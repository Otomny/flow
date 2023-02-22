package fr.omny.flow.translation;


import java.util.HashMap;
import java.util.Optional;

import org.bukkit.entity.Player;

import fr.omny.flow.placeholders.Placeholders;
import fr.omny.odi.Autowired;
import fr.omny.odi.Component;

@Component
public class I18N {

	@Autowired
	private TranslationRepository repository;
	@Autowired
	private Optional<PlayerToLocaleProvider> provider;
	@Autowired
	private Placeholders placeholders;

	public I18N() {}

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
	 * @param key The translation key
	 */
	public void send(Player player, String key){
		String translation = get(player, key);
		String translationReplaced = placeholders.inject(translation, player);
		player.sendMessage(translationReplaced);
	}

	/**
	 * Get a translation for player (Calls {@link I18N#get(String, String)})
	 * 
	 * @param player the player
	 * @param key    the translation key
	 * @return the translation or a default one
	 */
	public String get(Player player, String key) {
		return get(this.provider.orElse(new DefaultPlayerLocaleProvider()).locale(player), key);
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
