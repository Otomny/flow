package fr.omny.flow.data;


import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

public class DummyFileConfiguration extends FileConfiguration {

	public DummyFileConfiguration(Map<String, Object> data) {
		data.forEach((key, val) -> this.set(key, val));
	}

	@Override
	public void loadFromString(String arg0) throws InvalidConfigurationException {}

	@Override
	public String saveToString() {
		return null;
	}

}
