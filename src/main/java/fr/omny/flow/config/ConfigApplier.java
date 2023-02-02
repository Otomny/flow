package fr.omny.flow.config;


import java.lang.reflect.Field;

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
				Config configData = field.getAnnotation(Config.class);
				String pathToConfigValue = configData.value();
				if (this.configFile.contains(pathToConfigValue)) {
					field.setAccessible(true);
					try {
						var currentValue = field.get(instance);
						field.set(instance, this.configFile.get(pathToConfigValue, currentValue));
					} catch (Exception e) {
						e.printStackTrace();
					}
					field.setAccessible(false);
				} else {
					Injector.getLogger().ifPresent(log -> log.warning("Could not resolve path '" + pathToConfigValue
							+ "' for field '" + field.getName() + "' of class '" + klass.getCanonicalName() + "'"));
				}
			}
		}
	}

	@Override
	public void wire(Object instance) {
		newInstance(instance);
	}

}
