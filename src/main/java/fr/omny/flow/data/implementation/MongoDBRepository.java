package fr.omny.flow.data.implementation;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.bson.Document;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import fr.omny.flow.data.MongoRepository;
import fr.omny.flow.data.ObjectUpdate;
import fr.omny.flow.utils.StrUtils;
import fr.omny.flow.utils.mongodb.MongoSerializer;
import fr.omny.odi.Autowired;

public class MongoDBRepository<T, ID> implements MongoRepository<T, ID> {

	private Class<?> dataClass;
	private Class<?> idClass;
	private MongoDatabase db;
	private MongoCollection<Document> collection;
	private Map<ID, T> cachedData = new HashMap<>();
	private String collectionName;

	// Mapping function
	private Function<T, ID> getId;
	private Function<T, Document> toDocument;
	private Function<Document, T> fromDocument;
	private RTopic topic;

	public MongoDBRepository(Class<?> dataClass, Class<?> idClass, Function<T, ID> mappingFunction,
			@Autowired RedissonClient redissonClient, @Autowired MongoClient client) {
		this.collectionName = StrUtils.toSnakeCase(dataClass.getSimpleName());
		this.dataClass = dataClass;
		this.idClass = idClass;
		this.db = client.getDatabase("flow");
		this.collection = db.getCollection(this.collectionName);
		this.getId = mappingFunction;

		this.toDocument = (obj) -> MongoSerializer.transform(obj, dataClass);

		this.topic = redissonClient.getTopic(this.collectionName);
		this.topic.addListener(ObjectUpdate.class, (channel, objectUpdate) -> {
			throw new UnsupportedOperationException("Field update is not implemented");
		});
	}

	@Override
	public long count() {
		return this.collection.countDocuments();
	}

	@Override
	public void delete(T entity) {
		throw new UnsupportedOperationException("Delete is not implemented");
	}

	@Override
	public void deleteAll() {
		throw new UnsupportedOperationException("Delete all is not implemented");

	}

	@Override
	public void deleteAll(Iterable<? extends T> entities) {
		throw new UnsupportedOperationException("Delete all is not implemented");
	}

	@Override
	public void deleteById(ID id) {
		throw new UnsupportedOperationException("Delete by id is not implemented");

	}

	@Override
	public void deleteAllById(Iterable<? extends ID> ids) {
		throw new UnsupportedOperationException("Delete all by id is not implemented");

	}

	@Override
	public boolean existsById(ID id) {
		var projectionFields = Projections.fields(Projections.include("_id"));
		return this.collection.find(Filters.eq("_id", id.toString())).projection(projectionFields).first() != null;
	}

	@Override
	public Optional<T> findById(ID id) {
		throw new UnsupportedOperationException("Find is not implemented");
	}

	@Override
	public Iterable<T> findAll() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Iterable<T> findAllById(Iterable<ID> ids) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public <S extends T> boolean save(S entity) {
		var id = getId.apply(entity).toString();
		Document document = toDocument.apply(entity);
		document.append("_id", id);
		var result = this.collection.insertOne(document);
		return result.wasAcknowledged();
	}

	@Override
	public <S extends T> boolean saveAll(Iterable<S> entities) {
		throw new UnsupportedOperationException("not implemented");
	}

}
