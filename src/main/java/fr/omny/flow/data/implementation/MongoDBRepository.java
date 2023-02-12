package fr.omny.flow.data.implementation;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonDocumentWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;

import fr.omny.flow.attributes.ServerInfo;
import fr.omny.flow.data.MongoRepository;
import fr.omny.flow.data.ObjectUpdate;
import fr.omny.flow.data.RepositoryFactory;
import fr.omny.flow.events.data.DataEmitEvent;
import fr.omny.flow.events.data.DataUpdateEvent;
import fr.omny.flow.tasks.Dispatcher;
import fr.omny.flow.utils.StrUtils;
import fr.omny.flow.utils.mongodb.FlowCodec;
import fr.omny.flow.utils.mongodb.MongoSerializer;
import fr.omny.flow.utils.mongodb.ProxyMongoObject;
import fr.omny.flow.utils.mongodb.ProxyMongoObject.FieldData;
import fr.omny.odi.Autowired;

public class MongoDBRepository<T, ID> implements MongoRepository<T, ID>, ServerInfo {

	public static final UpdateOptions UPSERT_OPTIONS = new UpdateOptions().upsert(true);

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
	private Consumer<FieldData<T>> fieldUpdater;

	// Codecs
	private FlowCodec codecs;

	// Synch utils
	/**
	 * A set containing object that getting updated
	 */
	private List<ID> updating = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public MongoDBRepository(Class<?> dataClass, Class<?> idClass, Function<T, ID> mappingFunction,
			@Autowired RedissonClient redissonClient, @Autowired MongoClient client, @Autowired FlowCodec codecs,
			@Autowired Dispatcher dispatcher) {
		this.collectionName = StrUtils.toSnakeCase(dataClass.getSimpleName());
		this.codecs = codecs;
		this.dataClass = dataClass;
		this.idClass = idClass;
		this.db = client.getDatabase("flow");
		this.collection = db.getCollection(this.collectionName);
		this.getId = mappingFunction;

		this.toDocument = (obj) -> MongoSerializer.transform(obj, dataClass);
		this.fromDocument = (doc) -> {
			return (T) MongoSerializer.from(doc, dataClass);
		};

		this.fieldUpdater = (fieldData) -> {
			dispatcher.submit(() -> {
				T data = (T) fieldData.instance();
				Field field = fieldData.field();
				Object obj = fieldData.newValue();
				Codec<Object> codec = (Codec<Object>) codecs.getCodecRegistries().get(field.getType());
				BsonDocument container = new Document().toBsonDocument();

				var writer = new BsonDocumentWriter(container);

				writer.writeStartDocument();
				writer.writeName(field.getName());
				codec.encode(writer, obj, EncoderContext.builder().isEncodingCollectibleDocument(true).build());
				writer.writeEndDocument();

				ID id = this.getId.apply(data);
				var json = container.toJson();
				ObjectUpdate update = new ObjectUpdate(id.toString(), this.collectionName, field.getName(), json);

				var event = new DataEmitEvent(this, this.dataClass, update);
				Bukkit.getServer().getPluginManager().callEvent(event);
				this.updating.add(id);
				this.topic.publish(update);
			});
		};

		this.topic = redissonClient.getTopic(this.collectionName);
		this.topic.addListener(ObjectUpdate.class, (channel, objectUpdate) -> {
			var event = new DataUpdateEvent(this, this.dataClass, objectUpdate);
			Bukkit.getServer().getPluginManager().callEvent(event);
			ID id = null;
			if (this.idClass == UUID.class) {
				id = (ID) UUID.fromString(objectUpdate.getObjectId());
			} else if (this.idClass == Long.class) {
				id = (ID) Long.valueOf(objectUpdate.getObjectId());
			} else if (this.idClass == String.class) {
				id = (ID) objectUpdate.getObjectId();
			} else if (this.idClass == Integer.class) {
				id = (ID) Integer.valueOf(objectUpdate.getObjectId());
			}
			if (id == null) {
				throw new IllegalStateException("Could not deserialize " + objectUpdate.getObjectId() + " to type " + idClass);
			}
			if (!this.cachedData.containsKey(id)) {
				return;
			}

			T objectData = this.cachedData.get(dispatcher);

			if (this.updating.contains(id)) {
				this.updating.remove(id);
				return;
			}
			try {
				Field field = this.dataClass.getDeclaredField(objectUpdate.getFieldName());
				var setter = RepositoryFactory.createSetter(dataClass, field);
				var getter = RepositoryFactory.createGetter(dataClass, field);
				Document container = Document.parse(objectUpdate.getJsonData());
				BsonDocumentReader dReader = new BsonDocumentReader(container.toBsonDocument());
				T object = (T) this.codecs.getCodecRegistries().get(dataClass).decode(dReader,
						DecoderContext.builder().checkedDiscriminator(true).build());
				setter.apply(objectData, getter.apply(object));
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
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
		if (this.cachedData.containsKey(id)) {
			try {
				return Optional.of(ProxyMongoObject.createProxy(this.cachedData.get(id), this.fieldUpdater));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		Document document = this.collection.find(Filters.eq("_id", id.toString())).first();
		if (document == null) {
			return Optional.empty();
		}
		T object = this.fromDocument.apply(document);
		this.cachedData.put(id, object);
		try {
			return Optional.of(ProxyMongoObject.createProxy(object, this.fieldUpdater));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Iterable<T> findAll() {
		return StreamSupport.stream(this.collection.find().spliterator(), false).map(this.fromDocument).toList();
	}

	@Override
	public Iterable<T> findAllById(Iterable<ID> ids) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public <S extends T> boolean save(S entity) {
		var id = getId.apply(entity).toString();
		Bson filter = Filters.eq("_id", id);

		Document document = toDocument.apply(entity);
		document.append("_id", id);

		var result = this.collection.updateOne(filter, new Document("$set", document), UPSERT_OPTIONS);
		return result.wasAcknowledged();
	}

	@Override
	public <S extends T> boolean saveAll(Iterable<S> entities) {
		entities.forEach(this::save);
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void serverStart(Plugin plugin) {

	}

	@Override
	public void serverStop(Plugin plugin) {
		saveAll(this.cachedData.values());
	}

}
