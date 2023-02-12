package fr.omny.flow.data;


import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Repository of Entities Each get statement returns a Proxy object (find, findAll, etc...) SuppressWarnings rawtypes
 * because it's typename, and typename doesn't contains generic identifiers
 */
@SuppressWarnings("rawtypes")
public sealed interface CrudRepository<T, ID> permits RedisRepository, MongoRepository, JavaRepository {

	/**
	 * Returns the number of entities
	 * 
	 * @return the number of entities
	 */
	long count();

	/**
	 * remove an entity
	 * 
	 * @param entity
	 */
	void delete(T entity);

	/**
	 * Remove all entities
	 */
	void deleteAll();

	/**
	 * Remove entities contained in the collection
	 * 
	 * @param entities
	 */
	void deleteAll(Iterable<? extends T> entities);

	/**
	 * Remove an entity by it's id
	 * 
	 * @param id
	 */
	void deleteById(ID id);

	/**
	 * Delete all entities by id
	 * 
	 * @param ids
	 */
	void deleteAllById(Iterable<? extends ID> ids);

	/**
	 * Check if an entity already exists by it's id
	 * 
	 * @param id
	 * @return
	 */
	boolean existsById(ID id);

	/**
	 * Find an entity by it's ID
	 * 
	 * @param id
	 * @return
	 */
	Optional<T> findById(ID id);

	/**
	 * Get all entities
	 * 
	 * @return
	 */
	Iterable<T> findAll();

	/**
	 * Find All entities by ids
	 * 
	 * @param ids
	 * @return
	 */
	Iterable<T> findAllById(Iterable<ID> ids);

	/**
	 * Save an entity to a database
	 * 
	 * @param <S>
	 * @param entity
	 * @return
	 */
	<S extends T> boolean save(S entity);

	/**
	 * Save an entity to a database in asynchronous mode
	 * 
	 * @param <S>
	 * @param entity
	 * @return
	 */
	<S extends T> CompletableFuture<Boolean> saveAsync(S entity);

	/**
	 * Save all entites to a database
	 * 
	 * @param <S>
	 * @param entities
	 * @return
	 */
	<S extends T> boolean saveAll(Iterable<S> entities);

}