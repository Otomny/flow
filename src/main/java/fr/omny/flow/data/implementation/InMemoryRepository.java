package fr.omny.flow.data.implementation;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import fr.omny.flow.data.JavaRepository;
import fr.omny.flow.utils.Objects;

/**
 * 
 */
public class InMemoryRepository<T, ID> implements JavaRepository<T, ID> {

	private Map<ID, T> data = new ConcurrentHashMap<>();
	private Function<T, ID> mappingFunction;

	public InMemoryRepository(Function<T, ID> mappingFunction) {
		this.mappingFunction = mappingFunction;
	}

	@Override
	public long count() {
		return this.data.size();
	}

	@Override
	public void delete(T entity) {
		this.data.remove(mappingFunction.apply(entity));
	}

	@Override
	public void deleteAll() {
		this.data.clear();
	}

	@Override
	public void deleteAll(Iterable<? extends T> entities) {
		entities.forEach(this::delete);
	}

	@Override
	public void deleteById(ID id) {
		this.data.remove(id);
	}

	@Override
	public void deleteAllById(Iterable<? extends ID> ids) {
		ids.forEach(this::deleteById);
	}

	@Override
	public boolean existsById(ID id) {
		return this.data.containsKey(id);
	}

	@Override
	public Optional<T> findById(ID id) {
		return Optional.ofNullable(this.data.get(id));
	}

	@Override
	public Iterable<T> findAll() {
		return this.data.values();
	}

	@Override
	public Iterable<T> findAllById(Iterable<ID> ids) {
		return StreamSupport.stream(ids.spliterator(), false).map(id -> findById(id).orElse(null)).filter(Objects::notNull)
				.toList();
	}

	@Override
	public <S extends T> boolean save(S entity) {
		throw new UnsupportedOperationException("Save is not implemented");
	}

	@Override
	public <S extends T> boolean saveAll(Iterable<S> entities) {
		throw new UnsupportedOperationException("Save all is not implemented");
	}

}
