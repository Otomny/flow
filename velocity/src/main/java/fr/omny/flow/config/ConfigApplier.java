package fr.omny.flow.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;

import com.moandjiezana.toml.Toml;

import fr.omny.flow.api.config.Config;
import fr.omny.odi.Autowired;
import fr.omny.odi.Utils;
import fr.omny.odi.listener.OnConstructorCallListener;
import fr.omny.odi.listener.OnPreWireListener;

public class ConfigApplier implements OnConstructorCallListener, OnPreWireListener {

	private Method get;
	private Toml configFile;

	public ConfigApplier(Toml configFile) {
		this.configFile = configFile;
		this.get = Utils.findMethod(Toml.class,
				m -> m.getName().equalsIgnoreCase("get") && m.getReturnType() == Object.class);
		this.get.setAccessible(true);
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

	private Object getValue(String pathToConfig, Class<?> type) {
		if (!this.configFile.contains(pathToConfig)) {
			return null;
		}
		if (type == int.class || type == Integer.class)
			return this.configFile.getLong(pathToConfig).intValue();
		if (type == long.class || type == Long.class)
			return this.configFile.getLong(pathToConfig);
		if (type == float.class || type == Float.class)
			return this.configFile.getDouble(pathToConfig).floatValue();
		if (type == double.class || type == Double.class)
			return this.configFile.getDouble(pathToConfig);
		if (type == boolean.class || type == Boolean.class)
			return this.configFile.getBoolean(pathToConfig);
		if (type == String.class)
			return this.configFile.getString(pathToConfig);
		throw new UnsupportedOperationException("Type is different than primitive / String value");
	}

	public void wire(Object instance, Class<?> instanceClass, Field field, String pathToConfigValue) {
		try {
			boolean exists = this.configFile.contains(pathToConfigValue);
			var currentValue = field.get(instance);
			if (field.getType() == Optional.class) {
				if (exists) {
					ParameterizedType type = (ParameterizedType) field.getGenericType();
					Class<?> dataType = (Class<?>) type.getActualTypeArguments()[0];
					field.set(instance,
							Optional.ofNullable(getValue(pathToConfigValue, dataType)).or(() -> Optional.of(currentValue)));
				} else {
					field.set(instance, Optional.empty());
				}
			} else {
				// field.set(instance, this.configFile.get(pathToConfigValue, currentValue));
				field.set(instance, Optional.ofNullable(getValue(pathToConfigValue, field.getType())).orElse(currentValue));
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
		try {
			boolean exists = this.configFile.contains(pathToConfigValue);
			if (parameter.getType() == Optional.class) {
				if (exists) {
					ParameterizedType type = (ParameterizedType) parameter.getParameterizedType();
					Class<?> dataType = (Class<?>) type.getActualTypeArguments()[0];
					return Optional.ofNullable(getValue(pathToConfigValue, dataType));
				} else {
					return Optional.empty();
				}
			}
			return getValue(pathToConfigValue, parameter.getType());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
