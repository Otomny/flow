package fr.omny.flow.data.implementation;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.plugin.Plugin;
import org.redisson.api.RLiveObjectService;

import fr.omny.flow.attributes.ServerInfo;
import fr.omny.flow.data.RedisRepository;

public class RedissonCachedRepository<T, ID> implements RedisRepository<T, ID>, ServerInfo {

	private Map<ID, T> cachedData = new HashMap<>();
	private Class<?> dataClass;
	private Class<?> idClass;
	private RLiveObjectService redisService;

	public RedissonCachedRepository(Class<?> dataClass, Class<?> idClass, RLiveObjectService redisService){
		this.redisService = redisService;
		this.dataClass = dataClass;
		this.idClass = idClass;
	}

	@Override
	public long count() {
		throw new UnsupportedOperationException("count is not implemented");
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
		throw new UnsupportedOperationException("Exist by id is not implemented");
	}

	@Override
	public Optional<T> findById(ID id) {
		throw new UnsupportedOperationException("Find by id is not implemented");
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
		throw new UnsupportedOperationException("Save is not implemented");
	}

	@Override
	public <S extends T> boolean saveAll(Iterable<S> entities) {
		throw new UnsupportedOperationException("Save all is not implemented");
	}

	@Override
	public void serverStart(Plugin plugin) {
		throw new UnsupportedOperationException("Load from redis is not implemented");
	}

	@Override
	public void serverStop(Plugin plugin) {
		throw new UnsupportedOperationException("Save to redis is not implemented");
	}

}
