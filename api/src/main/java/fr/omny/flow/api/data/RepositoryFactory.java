package fr.omny.flow.api.data;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import fr.omny.flow.api.aop.MongoRepositoryProxy;
import fr.omny.flow.api.aop.RepositoryProxy;
import fr.omny.flow.api.data.implementation.InMemoryRepository;
import fr.omny.flow.api.data.implementation.MongoDBRepository;
import fr.omny.flow.api.data.implementation.RedissonRepository;
import fr.omny.flow.api.utils.Objects;
import fr.omny.flow.api.utils.StrUtils;
import fr.omny.flow.api.utils.tuple.Tuple;
import fr.omny.flow.api.utils.tuple.Tuple2;
import fr.omny.odi.Component;
import fr.omny.odi.Utils;
import fr.omny.odi.utils.Reflections;
import jodd.typeconverter.TypeConversionException;

/**
 *
 */
@Component(proxy = false)
public class RepositoryFactory {

	/**
	 * Create a setter function for a specific field
	 *
	 * @param <T>
	 * @param dataClass
	 * @param field
	 * @return
	 */
	public static <T> BiFunction<T, Object, Object> createSetter(Class<?> dataClass, Field field) {
		var fieldName = field.getName();
		var setterMethodName = "set" + fieldName.substring(0, 1).toUpperCase() +
				fieldName.substring(1);
		try {
			var method = dataClass.getDeclaredMethod(
					setterMethodName, new Class<?>[] { field.getType() });
			return new BiFunction<T, Object, Object>() {
				@Override
				public Object apply(T data, Object fieldData) {
					try {
						return (Object) method.invoke(data, fieldData);
					} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				}
			};
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create a getter function for a specific field
	 *
	 * @param <T>
	 * @param dataClass
	 * @param field
	 * @return
	 */
	public static <T> Function<T, Object> createGetter(Class<?> dataClass,
			Field field) {
		var fieldName = field.getName();
		var getterMethodName = "get" + fieldName.substring(0, 1).toUpperCase() +
				fieldName.substring(1);
		Method method = Utils.findMethod(dataClass,
				m -> m.getName().equals(getterMethodName) && m.getParameterCount() == 0);
		if (method != null) {
			return new Function<T, Object>() {
				@Override
				public Object apply(T data) {
					try {
						return (Object) method.invoke(data);
					} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
					return null;
				}
			};
		}
		throw new IllegalStateException("No getter method found for field " + field);
	}

	/**
	 * 
	 * @param <T>
	 * @param <ID>
	 * @param <C>
	 * @param repositoryClass
	 * @return
	 */
	public static Tuple2<Class<?>, Class<?>> findTypeAndId(
			Class<?> repositoryClass, Class<?> interfaceType) {
		var typeNames = Reflections.findTypeName(
				repositoryClass.getGenericInterfaces(), interfaceType);
		Class<?>[] classes = List.of(typeNames)
				.stream()
				.map(m -> {
					try {
						return Class.forName(m);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					return null;
				})
				.filter(Objects::notNull)
				.toArray(Class<?>[]::new);
		var dataType = classes[0];
		var keyType = classes[1];
		return Tuple.of(dataType, keyType);
	}

	/**
	 * @param <T>
	 * @param <ID>
	 * @param repositoryClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T, ID> Function<T, ID> mappingFactory(Class<?> repositoryClass, Class<?> repositoryInterface) {
		var typeNames = Reflections.findTypeName(
				repositoryClass.getGenericInterfaces(), repositoryInterface);
		Class<?>[] classes = List.of(typeNames)
				.stream()
				.map(m -> {
					try {
						return Class.forName(m);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					return null;
				})
				.filter(Objects::notNull)
				.toArray(Class<?>[]::new);
		var dataType = classes[0];
		var keyType = classes[1];

		Field field = Utils.findField(dataType,
				f -> f.isAnnotationPresent(Id.class) && keyType.isAssignableFrom(f.getType()));
		if (field != null) {
			return (Function<T, ID>) createGetter(dataType, field);
		}
		throw new TypeConversionException(
				dataType.getCanonicalName() +
						" hasn't declared a field key with annotation @Id and type " +
						keyType.getCanonicalName());
	}

	private Map<Class<?>, Function<Class<? extends CrudRepository<?, ?>>, ? extends CrudRepository<?, ?>>> repositoryFactories = new HashMap<>();

	public RepositoryFactory() {

	}

	/**
	 * Register a custom factory method to generate repository proxies
	 * 
	 * Keep in mind that no autowire and no proxies will be applied, it's all to the
	 * developper will to implement them
	 * 
	 * @param repoClass The repository interface that will be check for producing
	 *                  repository
	 * @param producer  The function that create the repository
	 */
	public void registerFactory(Class<?> repoClass,
			Function<Class<? extends CrudRepository<?, ?>>, ? extends CrudRepository<?, ?>> producer) {
		this.repositoryFactories.put(repoClass, producer);
	}

	/**
	 * @param <T>
	 * @param <ID>
	 * @param repositoryClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T, ID, C extends CrudRepository<T, ID>> C createRepository(Class<? extends C> repositoryClass) {
		var typeParameters = repositoryClass.getGenericInterfaces();
		for (var itype : typeParameters) {
			var ptype = (ParameterizedType) itype;
			Class<?> type = (Class<?>) ptype.getRawType();
			if (type.equals(RedisRepository.class)) {
				return Utils.autowireNoException(RepositoryProxy.createRepositoryProxy(
						repositoryClass, createRedisRepository(repositoryClass)));
			} else if (type.equals(MongoRepository.class)) {
				return (C) Utils.autowireNoException(
						createMongoRepository((Class<MongoRepository<T, ID>>) repositoryClass));
			} else if (type.equals(JavaRepository.class)) {
				return Utils.autowireNoException(RepositoryProxy.createRepositoryProxy(
						repositoryClass, createJavaRepository(repositoryClass)));
			}
			if (this.repositoryFactories.containsKey(type)) {
				return (C) this.repositoryFactories.get(type).apply(repositoryClass);
			}
		}
		throw new UnsupportedOperationException(
				"Unknown repository is not implemented");
	}

	/**
	 * @param <T>
	 * @param <ID>
	 * @param repositoryClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T, ID> JavaRepository<T, ID> createJavaRepository(Class<?> repositoryClass) {
		try {
			var typeNames = Reflections.findTypeName(
					repositoryClass.getGenericInterfaces(), JavaRepository.class);
			Class<?>[] classes = List.of(typeNames)
					.stream()
					.map(m -> {
						try {
							return Class.forName(m);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						return null;
					})
					.filter(Objects::notNull)
					.toArray(Class<?>[]::new);
			var dataType = classes[0];
			var keyType = classes[1];
			Repository repositoryData = repositoryClass.getAnnotation(Repository.class);
			String collectionName = repositoryData.value().equals("__class")
					? StrUtils.toSnakeCase(dataType.getSimpleName())
					: repositoryData.value();
			return Utils.callConstructor(
					InMemoryRepository.class, false, dataType, keyType, collectionName,
					mappingFactory(repositoryClass, JavaRepository.class));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param <T>
	 * @param <ID>
	 * @param repositoryClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T, ID, C extends MongoRepository<T, ID>> C createMongoRepository(Class<? extends C> repositoryClass) {
		var typeAndId = findTypeAndId(repositoryClass, MongoRepository.class);
		Class<? extends T> typeClass = (Class<? extends T>) typeAndId.getKey();
		Class<? extends ID> idClass = (Class<? extends ID>) typeAndId.getValue();
		Repository repositoryData = repositoryClass.getAnnotation(Repository.class);
		String collectionName = repositoryData.value().equals("__class")
				? StrUtils.toSnakeCase(typeClass.getSimpleName())
				: repositoryData.value();
		try {
			return (C) MongoRepositoryProxy.createRepositoryProxy(
					repositoryClass, typeClass,
					Utils.callConstructor(
							MongoDBRepository.class, false, typeClass, idClass, collectionName,
							mappingFactory(repositoryClass, MongoRepository.class)));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param <T>
	 * @param <ID>
	 * @param repositoryClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T, ID> RedisRepository<T, ID> createRedisRepository(Class<?> repositoryClass) {
		var typeNames = Reflections.findTypeName(
				repositoryClass.getGenericInterfaces(), RedisRepository.class);
		Class<?>[] classes = List.of(typeNames)
				.stream()
				.map(m -> {
					try {
						return Class.forName(m);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					return null;
				})
				.filter(Objects::notNull)
				.toArray(Class<?>[]::new);
		var dataType = classes[0];
		var keyType = classes[1];
		Repository repositoryData = repositoryClass.getAnnotation(Repository.class);
		String collectionName = repositoryData.value().equals("__class")
				? StrUtils.toSnakeCase(dataType.getSimpleName())
				: repositoryData.value();
		try {
			return Utils.callConstructor(
					RedissonRepository.class, false, dataType, keyType, collectionName,
					mappingFactory(repositoryClass, RedisRepository.class));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

}
