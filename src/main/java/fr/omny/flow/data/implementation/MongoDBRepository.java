package fr.omny.flow.data.implementation;


import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Function;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import fr.omny.flow.data.MongoRepository;
import fr.omny.flow.utils.StrUtils;
import fr.omny.flow.utils.mongodb.MongoSerializer;
import fr.omny.odi.Autowired;

public class MongoDBRepository<T, ID> implements MongoRepository<T, ID> {

	private Class<?> dataClass;
	private Class<?> idClass;
	private MongoDatabase db;
	private MongoCollection<Document> collection;

	// Mapping function
	private Function<T, ID> getId;
	private Function<T, Document> toDocument;
	private Function<Document, T> fromDocument;

	public MongoDBRepository(Class<?> dataClass, Class<?> idClass, Function<T, ID> mappingFunction,
			@Autowired MongoClient client) {
		this.dataClass = dataClass;
		this.idClass = idClass;
		this.db = client.getDatabase("flow");
		this.collection = db.getCollection(StrUtils.toSnakeCase(dataClass.getSimpleName()));
		this.getId = mappingFunction;

		this.toDocument = MongoSerializer::transform;
		
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
		throw new UnsupportedOperationException("Exists is not implemented");
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
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public <S extends T> boolean saveAll(Iterable<S> entities) {
		throw new UnsupportedOperationException("not implemented");
	}

}
