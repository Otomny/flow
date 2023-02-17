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
	 * Cast an object as boolean
	 * @param o
	 * @return
	 */
	public static boolean asBoolean(Object o){
		if(o == null)
			return false;
		if(o instanceof Boolean b){
			return b.booleanValue();
		}
		return false;
	}

	/**
	 * @param method
	 * @return
	 */
	public static boolean isSetter(Method method) {
		return Modifier.isPublic(method.getModifiers()) && method.getReturnType().equals(void.class)
				&& method.getParameterTypes().length == 1 && method.getName().matches("^set[A-Z].*");
	}

	public static boolean isGetter(Method method) {
		String name = method.getName();
		int numParams = method.getParameterCount();
		Class<?> returnType = method.getReturnType();

		// Check that method name matches "get[A-Z].*" or "is[A-Z].*"
		if (name.matches("^(get|is)[A-Z].*") && numParams == 0) {
			// Check that the return type is not void
			return returnType != void.class && Modifier.isPublic(method.getModifiers());
		}

		return false;
	}

}
