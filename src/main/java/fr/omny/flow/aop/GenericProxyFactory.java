package fr.omny.flow.aop;


import java.lang.reflect.InvocationHandler;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.TypeCache;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.PackageDefinitionStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

public class GenericProxyFactory {

	// private static final TypeCache<Class<?>> BYTEBUDDY_CACHE = new TypeCache<>();

	/**
	 * @param <T>
	 * @param clazz
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	// @SuppressWarnings("unchecked")
	public static <T> T newProxyInstance(Class<? extends T> clazz, InvocationHandler handler) throws Exception {

		// Class<? extends T> proxyClass = (Class<? extends T>) BYTEBUDDY_CACHE.findOrInsert(clazz.getClassLoader(), clazz,
		// () -> {
		// return new ByteBuddy().subclass(clazz).method(ElementMatchers.any())
		// .intercept(InvocationHandlerAdapter.of(handler)).make().load(clazz.getClassLoader(),
		// ClassLoadingStrategy.Default.INJECTION.with(PackageDefinitionStrategy.Trivial.INSTANCE))
		// .getLoaded();
		// });

		Class<? extends T> proxyClass = new ByteBuddy().subclass(clazz).method(ElementMatchers.any())
				.intercept(InvocationHandlerAdapter.of(handler)).make().load(clazz.getClassLoader(),
						ClassLoadingStrategy.Default.INJECTION.with(PackageDefinitionStrategy.Trivial.INSTANCE))
				.getLoaded();

		return proxyClass.getConstructor().newInstance();
	}

}
