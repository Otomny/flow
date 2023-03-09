package fr.omny.flow.config;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Optional;

import org.bukkit.configuration.file.FileConfiguration;

import fr.omny.odi.Autowired;
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
				wire(instance, klass, field, pathToConfigValue);
			} else if (field.isAnnotationPresent(Autowired.class)) {
				field.setAccessible(true);
				Autowired autowiredData = field.getAnnotation(Autowired.class);
				String pathToConfigValue = autowiredData.value();
				wire(instance, klass, field, pathToConfigValue);
			}
		}
	}

	public void wire(Object instance, Class<?> instanceClass, Field field, String pathToConfigValue) {
		try {
			boolean exists = this.configFile.contains(pathToConfigValue);
			var currentValue = field.get(instance);
			if (field.getType() == Optional.class) {
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

	@Override
	public void wire(Object instance) {
		newInstance(instance);
	}

	@Override
	public Object value(Parameter parameter) {
		if (parameter.isAnnotationPresent(Config.class)) {
			var annotationData = parameter.getAnnotation(Config.class);
			String pathToConfigValue = annotationData.value();
			return parameterValue(parameter, pathToConfigValue);
		} else if (parameter.isAnnotationPresent(Autowired.class)) {
			var annotationData = parameter.getAnnotation(Autowired.class);
			String pathToConfigValue = annotationData.value();
			return parameterValue(parameter, pathToConfigValue);
		}
		return null;
	}

	public Object parameterValue(Parameter parameter, String pathToConfigValue) {
		boolean exists = this.configFile.contains(pathToConfigValue);
		if (parameter.getType() == Optional.class) {
			if (exists) {
				return Optional.ofNullable(this.configFile.get(pathToConfigValue));
			} else {
				return Optional.empty();
			}
		}
		return this.configFile.get(pathToConfigValue);
	}

}
