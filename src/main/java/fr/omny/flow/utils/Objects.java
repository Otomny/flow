package fr.omny.flow.utils;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Objects {

	private Objects() {}

	/**
	 * @param o
	 * @return
	 */
	public static boolean notNull(Object o) {
		return o != null;
	}

	/**
	 * 
	 * @param method
	 * @return
	 */
	public static boolean isSetter(Method method) {
		return Modifier.isPublic(method.getModifiers()) && method.getReturnType().equals(void.class)
				&& method.getParameterTypes().length == 1 && method.getName().matches("^set[A-Z].*");
	}

}
