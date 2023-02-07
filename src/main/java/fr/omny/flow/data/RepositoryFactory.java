package fr.omny.flow.data;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.function.Function;

import fr.omny.flow.aop.RepositoryProxy;
import fr.omny.flow.data.implementation.InMemoryRepository;
import fr.omny.flow.data.implementation.MongoDBRepository;
import fr.omny.flow.data.implementation.RedissonRepository;
import fr.omny.flow.utils.Objects;
import fr.omny.odi.Utils;
import fr.omny.odi.utils.Reflections;
import jodd.typeconverter.TypeConversionException;

/**
 * 
 */
public class RepositoryFactory {

	/**
	 * @param <T>
	 * @param <ID>
	 * @param repositoryClass
	 * @return
	 */
	public static <T, ID, C extends CrudRepository<T, ID>> C createRepository(Class<? extends C> repositoryClass) {
		var typeParameters = repositoryClass.getGenericInterfaces();
		for (var itype : typeParameters) {
			var ptype = (ParameterizedType) itype;
			var typeName = ptype.getRawType().getTypeName();
			if (typeName.equals(RedisRepository.class.getCanonicalName())) {
				return Utils.autowireNoException(
						RepositoryProxy.createRepositoryProxy(repositoryClass, createRedisRepository(repositoryClass)));
			} else if (typeName.equals(MongoRepository.class.getCanonicalName())) {
				return Utils.autowireNoException(
						RepositoryProxy.createRepositoryProxy(repositoryClass, createMongoRepository(repositoryClass)));
			} else if (typeName.equals(JavaRepository.class.getCanonicalName())) {
				return Utils.autowireNoException(
						RepositoryProxy.createRepositoryProxy(repositoryClass, createJavaRepository(repositoryClass)));
			}
		}
		throw new UnsupportedOperationException("Unknown repository is not implemented");
	}

	/**
	 * @param <T>
	 * @param <ID>
	 * @param repositoryClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T, ID> JavaRepository<T, ID> createJavaRepository(Class<?> repositoryClass) {
		try {
			return Utils.callConstructor(InMemoryRepository.class, mappingFactory(repositoryClass, JavaRepository.class));
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
	public static <T, ID> MongoRepository<T, ID> createMongoRepository(Class<?> repositoryClass) {
		var typeNames = Reflections.findTypeName(repositoryClass.getGenericInterfaces(), MongoRepository.class);
		Class<?>[] classes = List.of(typeNames).stream().map(m -> {
			try {
				return Class.forName(m);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}).filter(Objects::notNull).toArray(Class<?>[]::new);
		var dataType = classes[0];
		var keyType = classes[1];
		try {
			return Utils.callConstructor(MongoDBRepository.class, dataType, keyType,
					mappingFactory(repositoryClass, MongoRepository.class));
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
	public static <T, ID> RedisRepository<T, ID> createRedisRepository(Class<?> repositoryClass) {
		var typeNames = Reflections.findTypeName(repositoryClass.getGenericInterfaces(), RedisRepository.class);
		Class<?>[] classes = List.of(typeNames).stream().map(m -> {
			try {
				return Class.forName(m);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}).filter(Objects::notNull).toArray(Class<?>[]::new);
		var dataType = classes[0];
		var keyType = classes[1];
		try {
			return Utils.callConstructor(RedissonRepository.class, dataType, keyType,
					mappingFactory(repositoryClass, RedisRepository.class));
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
	public static <T, ID> Function<T, ID> mappingFactory(Class<?> repositoryClass, Class<?> repositoryInterface) {
		var typeNames = Reflections.findTypeName(repositoryClass.getGenericInterfaces(), repositoryInterface);
		Class<?>[] classes = List.of(typeNames).stream().map(m -> {
			try {
				return Class.forName(m);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}).filter(Objects::notNull).toArray(Class<?>[]::new);
		var dataType = classes[0];
		var keyType = classes[1];

		for (var field : dataType.getDeclaredFields()) {
			if (field.isAnnotationPresent(Id.class) && keyType.isAssignableFrom(field.getType())) {
				return new Function<T, ID>() {

					@Override
					@SuppressWarnings("unchecked")
					public ID apply(T data) {
						try {
							return (ID) field.get(data);
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
						return null;
					}

				};
			}
		}
		throw new TypeConversionException(dataType.getCanonicalName()
				+ " hasn't declared a field key with annotation @Id and type " + keyType.getCanonicalName());
	}

}
