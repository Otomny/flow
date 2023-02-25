package fr.omny.flow.data;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

public non-sealed interface MongoRepository<T, ID> extends CrudRepository<T, ID> {

  List<Document> executeQuery(Bson filter, Bson projection);
  
}
