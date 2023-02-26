package fr.omny.flow.api.utils.mongodb;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import org.bson.Document;

import fr.omny.flow.api.data.Id;
import fr.omny.flow.api.data.Val;
import fr.omny.odi.Utils;

public class MongoSerializer {

	/**
	 * @param instance
	 * @param klass
	 * @return
	 */
	public static Document transform(Object instance, Class<?> klass) {
		Document document = new Document();
		for (Field field : klass.getDeclaredFields()) {
			if (field.isAnnotationPresent(Val.class)) {
				field.setAccessible(true);
				try {
					var fieldName = field.getName();
					var getterMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					var method = klass.getDeclaredMethod(getterMethodName, new Class<?>[] {});
					document.append(field.getName(), method.invoke(instance));
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException
						| InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return document;
	}

	/**
	 * @param <T>
	 * @param document
	 * @param klass
	 * @return
	 */
	public static <T> T from(Document document, Class<? extends T> klass) {
		try {
			T instance = Utils.callConstructor(klass, true, document);
			for (Field field : klass.getDeclaredFields()) {
				if (field.isAnnotationPresent(Val.class)) {
					field.setAccessible(true);
					if (field.getType() == boolean.class) {
						field.set(instance, document.get(field.getName(), Boolean.class).booleanValue());
					} else if (field.getType() == byte.class) {
						field.set(instance, document.get(field.getName(), Byte.class).byteValue());
					} else if (field.getType() == char.class) {
						field.set(instance, document.get(field.getName(), Character.class).charValue());
					} else if (field.getType() == short.class) {
						field.set(instance, document.get(field.getName(), Short.class).shortValue());
					} else if (field.getType() == int.class) {
						field.set(instance, document.get(field.getName(), Integer.class).intValue());
					} else if (field.getType() == long.class) {
						field.set(instance, document.get(field.getName(), Long.class).longValue());
					} else if (field.getType() == double.class) {
						field.set(instance, document.get(field.getName(), Double.class).doubleValue());
					} else if (field.getType() == float.class) {
						field.set(instance, document.get(field.getName(), Float.class).floatValue());
					} else {
						field.set(instance, document.get(field.getName(), field.getType()));
					}
				} else if (field.isAnnotationPresent(Id.class)) {
					field.setAccessible(true);
					if(field.getType() == UUID.class){
						field.set(instance, UUID.fromString(document.get("_id", String.class)));
					}else{
						field.set(instance, document.get("_id", field.getType()));
					}
				}
			}
			return instance;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e) {
			throw new RuntimeException(e);
		}
	}

}
