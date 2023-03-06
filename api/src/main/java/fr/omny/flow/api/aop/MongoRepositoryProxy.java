package fr.omny.flow.api.aop;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.bson.Document;

import com.mongodb.client.model.Filters;

import fr.omny.flow.api.data.CrudRepository;
import fr.omny.flow.api.data.MongoRepository;
import fr.omny.flow.api.utils.StrUtils;
import fr.omny.odi.Utils;
import fr.omny.odi.proxy.ProxyMarker;

public class MongoRepositoryProxy implements InvocationHandler {

	@SuppressWarnings("unchecked")
	public static <T, ID, C extends MongoRepository<T, ID>> C createRepositoryProxy(Class<? extends C> proxyClass,
			Class<? extends T> dataClass,
			C crudRepository) {
		return (C) Proxy.newProxyInstance(
				proxyClass.getClassLoader(), new Class[] { proxyClass, ProxyMarker.class },
				new MongoRepositoryProxy(proxyClass, dataClass, crudRepository));
	}

	public static CrudRepository<?, ?> createRepositoryProxy(Class<? extends CrudRepository<?, ?>> proxyClass,
			Class<?> dataClass, CrudRepository<?, ?> crudRepository) {
		return (CrudRepository<?, ?>) Proxy.newProxyInstance(
				proxyClass.getClassLoader(), new Class[] { proxyClass, ProxyMarker.class },
				new MongoRepositoryProxy(proxyClass, dataClass, (MongoRepository<?, ?>) crudRepository));
	}

	private MongoRepository<?, ?> repo;
	private Class<? extends MongoRepository<?, ?>> repoClass;

	private Class<?> proxiedClass;
	private Map<Method, Method> mappedMethod;
	private Map<String, Function<Object[], Object>> mappedCustomQuery;
	private Method executeQuery;

	@SuppressWarnings("unchecked")
	public MongoRepositoryProxy(Class<?> proxiedClass, Class<?> dataClass,
			MongoRepository<?, ?> crudRepository) {
		this.proxiedClass = proxiedClass;
		this.repoClass = (Class<? extends MongoRepository<?, ?>>) crudRepository.getClass();
		this.repo = crudRepository;
		this.mappedMethod = new HashMap<>();
		this.mappedCustomQuery = new HashMap<>();
		this.executeQuery = Utils.findMethod(crudRepository.getClass(), m -> m.getName().equals("executeQuery"));

		// Create mapped custom query
		// found all fields
		for (Field field : dataClass.getDeclaredFields()) {
			Method foundMethod = Utils.findMethod(proxiedClass,
					method -> method.getName().equals("findBy" + StrUtils.capitalize(field.getName()))
							&& method.getReturnType().equals(Optional.class));
			if (foundMethod != null) {
				mappedCustomQuery.put(foundMethod.getName(), parameters -> {
					Object result = this.repo.executeQueryOne(Filters.eq(field.getName(), parameters[0]), new Document());
					return Optional.ofNullable(result);
				});
			}
			foundMethod = Utils.findMethod(proxiedClass,
					method -> method.getName().equals("findAllBy" + StrUtils.capitalize(field.getName()))
							&& method.getReturnType().equals(Iterable.class));
			if (foundMethod != null) {
				mappedCustomQuery.put(foundMethod.getName(), parameters -> {
					List<?> result = this.repo.executeQuery(Filters.eq(field.getName(), parameters[0]), new Document());
					return result;
				});
			}
		}
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

		if (this.mappedMethod.containsKey(method)) {
			return this.mappedMethod.get(method).invoke(repo, arguments);
		}
		if (this.mappedCustomQuery.containsKey(method.getName())) {
			return this.mappedCustomQuery.get(method.getName()).apply(arguments);
		}
		if (method.getName().equals(this.executeQuery.getName())) {
			return this.executeQuery.invoke(this.repo, arguments);
		}

		int argumentsCount = arguments == null ? 0 : arguments.length;

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
