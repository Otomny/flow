package fr.omny.flow.api.aop;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import fr.omny.flow.api.data.CrudRepository;
import fr.omny.odi.proxy.ProxyMarker;

public class RepositoryProxy implements InvocationHandler {

	@SuppressWarnings("unchecked")
	public static <T, ID, C extends CrudRepository<T, ID>> C createRepositoryProxy(Class<? extends C> proxyClass,
			CrudRepository<T, ID> crudRepository) {
		return (C) Proxy.newProxyInstance(
				proxyClass.getClassLoader(), new Class[] { proxyClass, ProxyMarker.class },
				new RepositoryProxy(proxyClass, crudRepository));
	}

	private CrudRepository<?, ?> repo;
	private Class<? extends CrudRepository<?, ?>> repoClass;
	private Class<?> proxiedClass;

	@SuppressWarnings("unchecked")
	private RepositoryProxy(Class<?> proxiedClass,
			CrudRepository<?, ?> crudRepository) {
		this.proxiedClass = proxiedClass;
		this.repoClass = (Class<? extends CrudRepository<?, ?>>) crudRepository.getClass();
		this.repo = crudRepository;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] arguments)
			throws Throwable {
		String methodName = method.getName();
		if (methodName.equalsIgnoreCase("getOriginalClass")) {
			return this.repoClass;
		} else if (methodName.equalsIgnoreCase("getOriginalInstance")) {
			return this.repo;
		}

		try {
			return repoClass.getDeclaredMethod(methodName, method.getParameterTypes()).invoke(repo, arguments);
		} catch (NoSuchMethodException e) {
		}
		return MethodHandles.lookup()
				.findSpecial(
						proxiedClass, method.getName(),
						MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
						proxiedClass)
				.bindTo(proxy)
				.invokeWithArguments(arguments);
	}
}
