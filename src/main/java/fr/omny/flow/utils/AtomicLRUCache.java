package fr.omny.flow.utils;


import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AtomicLRUCache<K, V> extends LRUCache<K, V> {

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public AtomicLRUCache(int maxSize) {
		super(maxSize);
	}

	@Override
	public boolean set(K key, V value) {
		this.lock.writeLock().lock();
		try {
			return super.set(key, value);
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	@Override
	public Optional<V> get(K key) {
		this.lock.readLock().lock();
		try {
			return super.get(key);
		} finally {
			this.lock.readLock().unlock();
		}
	}

}
