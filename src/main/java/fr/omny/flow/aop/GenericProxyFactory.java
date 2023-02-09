package fr.omny.flow.aop;


import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

public class GenericProxyFactory {

	private static Map<String, Class<?>> proxyClasses = new HashMap<>();

	public static <T> T newProxyInstance(Class<? extends T> clazz, InvocationHandler handler) throws Exception {
		String className = clazz.getSimpleName();
		String proxyClassName = className + "$Proxy";
		if (proxyClasses.containsKey(proxyClassName)) {
			@SuppressWarnings("unchecked")
			Class<? extends T> proxyClass = (Class<? extends T>) proxyClasses.get(proxyClassName);
			return proxyClass.getConstructor().newInstance();
		}

		// ClassLoadingStrategy.UsingLookup;

		Class<? extends T> proxyClass = new ByteBuddy().subclass(clazz).method(ElementMatchers.any())
				.intercept(InvocationHandlerAdapter.of(handler)).make()
				.load(clazz.getClassLoader(), ClassLoadingStrategy.UsingLookup.withFallback(() -> MethodHandles.lookup()))
				.getLoaded();
		proxyClasses.put(proxyClassName, proxyClass);
		return proxyClass.getConstructor().newInstance();
	}

}
