package fr.omny.flow.data.implementation;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.plugin.Plugin;
import org.redisson.api.RLiveObjectService;
import org.redisson.api.RedissonClient;

import fr.omny.flow.attributes.ServerInfo;
import fr.omny.flow.data.RedisRepository;
import fr.omny.odi.Autowired;

public class RedissonCachedRepository<T, ID> implements RedisRepository<T, ID>, ServerInfo {

	private Map<ID, T> cachedData = new HashMap<>();
	private Class<?> dataClass;
	private Class<?> idClass;
	private RLiveObjectService redisService;

	public RedissonCachedRepository(Class<?> dataClass, Class<?> idClass, @Autowired RedissonClient client) {
		this.redisService = client.getLiveObjectService();
		this.dataClass = dataClass;
		this.idClass = idClass;
	}

	@Override
	public long count() {
		return this.redisService.count(dataClass, null);
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
		return this.cachedData.containsKey(id) || this.redisService.get(dataClass, id) != null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Optional<T> findById(ID id) {
		if (this.cachedData.containsKey(id)) {
			return Optional.of(this.cachedData.get(id));
		}
		var data = (T) this.redisService.get(dataClass, id);
		if (data == null)
			return Optional.empty();
		this.cachedData.put(id, data);
		return Optional.of(data);
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
