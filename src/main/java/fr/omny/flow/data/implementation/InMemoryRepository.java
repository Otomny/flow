package fr.omny.flow.data.implementation;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import fr.omny.flow.data.JavaRepository;
import fr.omny.flow.tasks.Dispatcher;
import fr.omny.flow.utils.Objects;
import fr.omny.odi.Autowired;

/**
 * 
 */
public class InMemoryRepository<T, ID> implements JavaRepository<T, ID> {

	private Map<ID, T> data = new ConcurrentHashMap<>();
	private Function<T, ID> mappingFunction;

	private Dispatcher dispatcher;

	public InMemoryRepository(Function<T, ID> mappingFunction, @Autowired Dispatcher dispatcher) {
		this.mappingFunction = mappingFunction;
		this.dispatcher = dispatcher;
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
	public CompletableFuture<Optional<T>> findByIdAsync(ID id) {
		return this.dispatcher.submit(() -> findById(id));
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
		var id = this.mappingFunction.apply(entity);
		return this.data.put(id, entity) != null;
	}

	@Override
	public <S extends T> boolean saveAll(Iterable<S> entities) {
		entities.forEach(this::save);
		return true;
	}

	@Override
	public <S extends T> CompletableFuture<Boolean> saveAsync(S entity) {
		return dispatcher.submit(() -> this.save(entity));
	}

}
