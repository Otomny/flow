package fr.omny.flow.data.implementation;


import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.redisson.api.RLiveObjectService;
import org.redisson.api.RedissonClient;

import fr.omny.flow.data.RedisRepository;
import fr.omny.odi.Autowired;

public final class RedissonRepository<T, ID> implements RedisRepository<T, ID> {

	private Class<?> dataClass;
	private Class<?> idClass;
	private RLiveObjectService redisService;
	private Function<T, ID> mappingFunction;

	public RedissonRepository(Class<?> dataClass, Class<?> idClass, Function<T, ID> mappingFunction,
			@Autowired RedissonClient client) {
		this.dataClass = dataClass;
		this.idClass = idClass;
		this.redisService = client.getLiveObjectService();
		this.mappingFunction = mappingFunction;

	}

	@Override
	public long count() {
		return this.redisService.count(dataClass, null);
	}

	@Override
	public void delete(T entity) {
		deleteById(mappingFunction.apply(entity));
	}

	@Override
	public void deleteAll() {
		throw new UnsupportedOperationException("Delete all is not implemented");
	}

	@Override
	public void deleteAll(Iterable<? extends T> entities) {
		entities.forEach(this::delete);
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
		return this.redisService.get(dataClass, id) != null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Optional<T> findById(ID id) {
		return (Optional<T>) Optional.ofNullable(this.redisService.get(dataClass, id));
	}

	@Override
	public Iterable<T> findAll() {
		throw new UnsupportedOperationException("Find all is not implemented");
	}

	@Override
	public Iterable<T> findAllById(Iterable<ID> ids) {
		throw new UnsupportedOperationException("Find all by id is not implemented");
	}

	@Override
	public <S extends T> boolean save(S entity) {
		this.redisService.persist(entity);
		return true;
	}

	@Override
	public <S extends T> boolean saveAll(Iterable<S> entities) {
		throw new UnsupportedOperationException("Save all is not implemented");
	}

	@Override
	public <S extends T> CompletableFuture<Boolean> saveAsync(S entity) {
		throw new UnsupportedOperationException("Save async is not implemented");
	}

}
