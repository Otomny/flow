package fr.omny.flow.api.data;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

public non-sealed interface MongoRepository<T, ID> extends CrudRepository<T, ID> {

	public List<Document> executeQuery(Bson filter, Bson projection);

}
