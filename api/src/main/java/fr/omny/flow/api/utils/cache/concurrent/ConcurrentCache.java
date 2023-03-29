package fr.omny.flow.api.utils.cache.concurrent;

import static fr.omny.flow.api.utils.cache.concurrent.Guard.guardedBy;

import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import fr.omny.flow.api.utils.cache.Cache;

/**
 * Created by marcalperapochamado on 19/02/17.
 */
public class ConcurrentCache<K, V> implements Cache<K, V> {

	private final Cache<K, V> cache;
	private final ReadWriteLock readWriteLock;

	public ConcurrentCache(Cache<K, V> cache) {
		this.cache = cache;
		this.readWriteLock = new ReentrantReadWriteLock();
	}

	@Override
	public Optional<V> get(K key) {
		return guardedBy(readWriteLock).executeWrite(() -> cache.get(key));
	}

	@Override
	public void put(K key, V value) {
		guardedBy(readWriteLock).executeWrite(() -> cache.put(key, value));
	}

	@Override
	public boolean containsKey(K key) {
		return guardedBy(readWriteLock).executeRead(() -> cache.containsKey(key));
	}

	@Override
	public int size() {
		return guardedBy(readWriteLock).executeRead(() -> cache.size());
	}

	@Override
	public void clear() {
		guardedBy(readWriteLock).executeWrite(() -> cache.clear());
	}

}
