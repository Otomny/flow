package fr.omny.flow.api.aop;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.bson.conversions.Bson;

import fr.omny.flow.api.data.CrudRepository;
import fr.omny.flow.api.data.implementation.MongoDBRepository;
import fr.omny.flow.api.data.query.MongoQuery;
import fr.omny.odi.Utils;
import fr.omny.odi.proxy.ProxyMarker;

public class MongoRepositoryProxy implements InvocationHandler {

	@SuppressWarnings("unchecked")
	public static <T, ID, C extends CrudRepository<T, ID>> C createRepositoryProxy(Class<? extends C> proxyClass,
			Class<? extends T> dataClass,
			CrudRepository<T, ID> crudRepository) {
		return (C) Proxy.newProxyInstance(
				proxyClass.getClassLoader(), new Class[] { proxyClass, ProxyMarker.class },
				new MongoRepositoryProxy(proxyClass, dataClass, crudRepository));
	}

	private CrudRepository<?, ?> repo;
	private Class<? extends CrudRepository<?, ?>> repoClass;

	private Class<?> proxiedClass;
	private Map<Method, Method> mappedMethod;
	private Method executeQuery;

	@SuppressWarnings("unchecked")
	public MongoRepositoryProxy(Class<?> proxiedClass, Class<?> dataClass,
			CrudRepository<?, ?> crudRepository) {
		this.proxiedClass = proxiedClass;
		this.repoClass = (Class<? extends CrudRepository<?, ?>>) crudRepository.getClass();
		this.repo = crudRepository;
		this.mappedMethod = new HashMap<>();
		this.executeQuery = Utils.findMethod(MongoDBRepository.class, m -> m.getName().equals("executeQuery"));
	}

	private Bson createProjection(Method method, MongoQuery query,
			Object[] arguments) {
		throw new UnsupportedOperationException();
	}

	private Bson createFilter(Method method, MongoQuery query,
			Object[] arguments) {
		throw new UnsupportedOperationException();
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

		int argumentsCount = arguments == null ? 0 : arguments.length;

		if (method.getName().equals(this.executeQuery.getName())) {
			return this.executeQuery.invoke(this.repo, arguments);
		}
		if (method.isAnnotationPresent(MongoQuery.class)) {
			var queryData = method.getAnnotation(MongoQuery.class);
			var filter = createFilter(method, queryData, arguments);
			var projection = createProjection(method, queryData, arguments);

			return this.executeQuery.invoke(repo, filter, projection);
		}
		if (this.mappedMethod.containsKey(method)) {
			return this.mappedMethod.get(method).invoke(repo, arguments);
		}

		for (Method remoteMethod : repo.getClass().getDeclaredMethods()) {
			if (remoteMethod.getParameterCount() == argumentsCount &&
					remoteMethod.getName().equals(methodName)) {
				this.mappedMethod.put(method, remoteMethod);
				return remoteMethod.invoke(repo, arguments);
			}
		}
		Class<?>[] parametersType = new Class<?>[method.getParameters().length];
		for (int i = 0; i < parametersType.length; i++) {
			parametersType[i] = method.getParameters()[i].getType();
		}
		return MethodHandles.lookup()
				.findSpecial(
						proxiedClass, method.getName(),
						MethodType.methodType(method.getReturnType(), parametersType),
						proxiedClass)
				.bindTo(proxy)
				.invokeWithArguments(arguments);
	}
}
