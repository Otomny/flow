package fr.omny.flow.api.repository.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.ReplaceOptions;

import fr.omny.flow.api.attributes.ProcessInfo;
import fr.omny.flow.api.tasks.Dispatcher;
import fr.omny.flow.api.utils.generic.Consumers;
import fr.omny.flow.api.utils.mongodb.ProxyMongoObject;
import fr.omny.odi.Autowired;
import fr.omny.odi.proxy.ProxyFactory;

public class CustomMongoDBRepository<T, ID> implements CustomRepository<T, ID>, ProcessInfo {

	public static final ReplaceOptions UPSERT_OPTIONS = new ReplaceOptions().upsert(true);

	private MongoDatabase db;
	private MongoCollection<T> collection;
	private Map<ID, T> cachedData = new HashMap<>();
	private String collectionName;
	private Dispatcher dispatcher;

	// Mapping function
	private Function<T, ID> getId;

	// Synch utils

	public CustomMongoDBRepository(Class<T> dataClass, Class<ID> idClass,
			Function<T, ID> mappingFunction,
			String collectionName,
			@Autowired MongoClient client,
			@Autowired Dispatcher dispatcher,
			@Autowired("databaseName") String dbName) {
		this.dispatcher = dispatcher;
		this.collectionName = collectionName;
		this.db = client.getDatabase(dbName);
		this.collection = db.getCollection(this.collectionName, dataClass);
		this.getId = mappingFunction;
	}

	@Override
	public long count() {
		return this.collection.countDocuments();
	}

	@Override
	public void delete(T entity) {
		var id = this.getId.apply(entity);
		this.cachedData.remove(id);
		this.collection.deleteOne(Filters.eq("_id", id));
	}

	@Override
	public void deleteAll() {
		this.cachedData.clear();
		this.collection.deleteMany(new Document());
	}

	@Override
	public void deleteAll(Iterable<? extends T> entities) {
		throw new UnsupportedOperationException("Delete all is not implemented");
	}

	@Override
	public void deleteById(ID id) {
		this.cachedData.remove(id);
		this.collection.deleteOne(Filters.eq("_id", id));
	}

	@Override
	public void deleteAllById(Iterable<? extends ID> ids) {
		throw new UnsupportedOperationException("Delete all by id is not implemented");
	}

	@Override
	public boolean existsById(ID id) {
		var projectionFields = Projections.fields(Projections.include("_id"));
		return this.collection.find(Filters.eq("_id", id)).projection(projectionFields).first() != null;
	}

	@Override
	public Optional<T> findById(ID id) {
		if (this.cachedData.containsKey(id)) {
			try {
				return Optional.of(ProxyMongoObject.createProxy(this.cachedData.get(id), Consumers.empty()));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		T object = this.collection.find(Filters.eq("_id", id)).first();
		if (object == null) {
			return Optional.empty();
		}
		this.cachedData.put(id, object);
		try {
			return Optional.of(ProxyMongoObject.createProxy(object, Consumers.empty()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public CompletableFuture<Optional<T>> findByIdAsync(ID id) {
		return this.dispatcher.submit(() -> findById(id));
	}

	@Override
	public Iterable<T> findAll() {
		List<T> availableObjets = new ArrayList<>();
		var entitiesFound = this.collection.find();

		for (T object : entitiesFound) {
			var id = this.getId.apply(object);
			this.cachedData.put(id, object);
			availableObjets.add(object);
		}
		return availableObjets;
	}

	@Override
	public Iterable<T> findAllById(Iterable<ID> ids) {
		List<T> availableObjets = new ArrayList<>();
		List<ID> notFoundInCache = new ArrayList<>();
		for (ID id : ids) {
			if (this.cachedData.containsKey(id)) {
				availableObjets.add(ProxyMongoObject.createProxySilent(this.cachedData.get(id), Consumers.empty()));
			} else {
				notFoundInCache.add(id);
			}
		}
		var entitiesFound = this.collection.find(Filters.in("_id", notFoundInCache.stream().map(ID::toString).toList()));

		for (T object : entitiesFound) {
			var id = this.getId.apply(object);
			this.cachedData.put(id, object);
			availableObjets.add(ProxyMongoObject.createProxySilent(object, Consumers.empty()));
		}
		return availableObjets;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <S extends T> boolean save(S sEntity) {
		var id = this.getId.apply(sEntity);
		S entity = (S) ProxyFactory.getOriginalInstance(sEntity);
		Bson filter = Filters.eq("_id", id);
		var result = this.collection.replaceOne(filter, entity, UPSERT_OPTIONS);
		if (result.wasAcknowledged()) {
			this.cachedData.remove(id);
		}
		return result.wasAcknowledged();
	}

	@Override
	public <S extends T> CompletableFuture<Boolean> saveAsync(S entity) {
		return this.dispatcher.submit(() -> this.save(entity));
	}

	@Override
	public <S extends T> boolean saveAll(Iterable<S> entities) {
		throw new UnsupportedOperationException("saveAll by id is not implemented");
	}

	@Override
	public List<T> executeQuery(Bson filter, Bson projection) {
		return this.collection
				.find(filter)
				.projection(projection)
				.into(new ArrayList<>());
	}

	@Override
	public T executeQueryOne(Bson filter, Bson projection) {
		return this.collection
				.find(filter)
				.projection(projection)
				.first();
	}

	@Override
	public void processStart() {

	}

	@Override
	public void processStop() {
		saveAll(this.cachedData.values());
	}

}
