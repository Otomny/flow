package fr.omny.flow.api.data;

import java.util.List;

import org.bson.conversions.Bson;

public non-sealed interface MongoRepository<T, ID> extends CrudRepository<T, ID> {

	public List<T> executeQuery(Bson filter, Bson projection);

	public T executeQueryOne(Bson filter, Bson projection);

}
