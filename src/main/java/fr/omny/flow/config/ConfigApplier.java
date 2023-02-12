package fr.omny.flow.config;


import java.lang.reflect.Field;
import java.util.Optional;

import org.bukkit.configuration.file.FileConfiguration;

import fr.omny.odi.Injector;
import fr.omny.odi.listener.OnConstructorCallListener;
import fr.omny.odi.listener.OnPreWireListener;

public class ConfigApplier implements OnConstructorCallListener, OnPreWireListener {

	private FileConfiguration configFile;

	public ConfigApplier(FileConfiguration configFile) {
		this.configFile = configFile;
	}

	@Override
	public void newInstance(Object instance) {
		Class<?> klass = instance.getClass();
		for (Field field : klass.getDeclaredFields()) {
			if (field.isAnnotationPresent(Config.class)) {
				field.setAccessible(true);
				Config configData = field.getAnnotation(Config.class);
				String pathToConfigValue = configData.value();
				try {
					boolean exists = this.configFile.contains(pathToConfigValue);
					var currentValue = field.get(instance);
					if (field.getType() == Optional.class) {
						Injector.getLogger().ifPresent(log -> log.warning("Could not resolve path '" + pathToConfigValue
								+ "' for field '" + field.getName() + "' of class '" + klass.getCanonicalName() + "'"));
						if (exists) {
							field.set(instance, Optional.ofNullable(this.configFile.get(pathToConfigValue, currentValue)));
						} else {
							field.set(instance, Optional.empty());
						}
					} else {
						field.set(instance, this.configFile.get(pathToConfigValue, currentValue));
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	public void wire(Object instance) {
		newInstance(instance);
	}

}
