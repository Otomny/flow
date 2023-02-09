package fr.omny.flow.utils.mongodb;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.bson.Document;

import fr.omny.flow.data.Val;
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
					var method = klass.getDeclaredMethod(getterMethodName, new Class<?>[]{});
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
			T instance = Utils.callConstructor(klass, document);
			for (Field field : klass.getDeclaredFields()) {
				if (field.isAnnotationPresent(Val.class)) {
					field.setAccessible(true);
					field.set(instance, document.get(field.getName(), field.getType()));
				}
			}
			return instance;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e) {
			throw new RuntimeException(e);
		}
	}

}
