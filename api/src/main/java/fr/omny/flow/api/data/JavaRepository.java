package fr.omny.flow.api.data;

/**
 * In-memory database
 */
public non-sealed interface JavaRepository<T, ID> extends CrudRepository<T, ID> {
	
}
