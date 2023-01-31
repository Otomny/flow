package fr.omny.flow.data;

/**
 * 
 */
public class RepositoryFactory {
	
	public static <T, ID> CrudRepository<T, ID> createRepository(Class<?> klass){
		throw new UnsupportedOperationException("Creating repository is not implemented");
	}

	public static <T, ID> JavaRepository<T, ID> createJavaRepository(Class<?> klass){
		throw new UnsupportedOperationException("Creating java repository is not implemented");
	}

	public static <T, ID> MongoRepository<T, ID> createMongoRepository(Class<?> klass){
		throw new UnsupportedOperationException("Creating mongo repository is not implemented");
	}

	public static <T, ID> MongoRepository<T, ID> createRedisRepository(Class<?> klass){
		throw new UnsupportedOperationException("Creating redis repository is not implemented");
	}

}
