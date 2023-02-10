package fr.omny.flow.data.implementation;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import fr.omny.flow.attributes.ServerInfo;
import fr.omny.flow.data.MongoRepository;
import fr.omny.flow.data.ObjectUpdate;
import fr.omny.flow.events.data.DataUpdateEvent;
import fr.omny.flow.utils.StrUtils;
import fr.omny.flow.utils.mongodb.MongoSerializer;
import fr.omny.flow.utils.mongodb.ProxyMongoObject;
import fr.omny.odi.Autowired;

public class MongoDBRepository<T, ID> implements MongoRepository<T, ID>, ServerInfo {

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

	@SuppressWarnings("unchecked")
	public MongoDBRepository(Class<?> dataClass, Class<?> idClass, Function<T, ID> mappingFunction,
			@Autowired RedissonClient redissonClient, @Autowired MongoClient client) {
		this.collectionName = StrUtils.toSnakeCase(dataClass.getSimpleName());
		this.dataClass = dataClass;
		this.idClass = idClass;
		this.db = client.getDatabase("flow");
		this.collection = db.getCollection(this.collectionName);
		this.getId = mappingFunction;

		this.toDocument = (obj) -> MongoSerializer.transform(obj, dataClass);
		this.fromDocument = (doc) -> {
			var initial = MongoSerializer.from(doc, dataClass);
			try {
				var proxy = ProxyMongoObject.createProxy(initial, (fieldData) -> {
					throw new UnsupportedOperationException("Field update topic publish is not implemented");
				});
				return (T) proxy;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};

		this.topic = redissonClient.getTopic(this.collectionName);
		this.topic.addListener(ObjectUpdate.class, (channel, objectUpdate) -> {
			var event = new DataUpdateEvent(this, this.dataClass, objectUpdate);
			Bukkit.getServer().getPluginManager().callEvent(event);
			throw new UnsupportedOperationException("Field update listener is not implemented");
		});
	}

	protected <S extends T> boolean isProxy(S entity) {
		var entClass = entity.getClass();
		return !this.dataClass.isAssignableFrom(entClass) && this.dataClass != entClass;
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
		Document document = this.collection.find(Filters.eq("_id", id.toString())).first();
		return document == null ? Optional.empty() : Optional.of(this.fromDocument.apply(document));
	}

	@Override
	public Iterable<T> findAll() {
		return StreamSupport.stream(this.collection.find().spliterator(), false)
			.map(this.fromDocument)
			.toList();
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

	@Override
	public void serverStart(Plugin plugin) {

	}

	@Override
	public void serverStop(Plugin plugin) {

	}

}
