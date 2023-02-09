package fr.omny.flow.utils.mongodb;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.bson.Document;

import fr.omny.flow.data.Val;
import fr.omny.odi.Utils;

public class MongoSerializer {

	public static Document transform(Object instance) {
		Document document = new Document();
		Class<?> klass = instance.getClass();
		for (Field field : klass.getDeclaredFields()) {
			if (field.isAnnotationPresent(Val.class)) {
				field.setAccessible(true);
				try {
					document.append(field.getName(), field.get(instance));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return document;
	}

	public static <T> T from(Document document, Class<? extends T> klass) {
		try {
			T instance = Utils.callConstructor(klass, document);
			for(Field field : klass.getDeclaredFields()){
				if(field.isAnnotationPresent(Val.class)){
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
