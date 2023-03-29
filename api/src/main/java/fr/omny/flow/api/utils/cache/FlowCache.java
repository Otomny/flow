package fr.omny.flow.api.utils.cache;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import fr.omny.flow.api.utils.tuple.Tuple;
import fr.omny.flow.api.utils.tuple.Tuple2;
import lombok.Getter;

@Getter
public class FlowCache<K, V> {

	/**
	 * Cache the timeCode First long = First insertion Second long = Last update
	 */
	private Cache<K, V> cache;
	private Map<K, Tuple2<Long, Long>> timeCodeMap = new ConcurrentHashMap<>();

	private final long ttl;
	private final long maxIddle;

	public FlowCache(int maxSize, long ttl, long maxIddle) {
		this(maxSize, ttl, maxIddle, 1000);
	}

	public FlowCache(int maxSize, long ttl, long maxIddle, long checkUpTime) {
		this.cache = CacheFactory.createConcurrentLRUCache(maxSize);
		this.ttl = ttl;
		this.maxIddle = maxIddle;
		Thread autoEvictionThread = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(checkUpTime);
					this.timeCodeMap.entrySet().stream()
							.filter(e -> e.getValue().getFirst() + ttl < System.currentTimeMillis()
									|| e.getValue().getSecond() + maxIddle < System.currentTimeMillis())
							.map(Map.Entry::getKey).forEach(timeCodeMap::remove);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		autoEvictionThread.setDaemon(true);
		autoEvictionThread.start();
	}

	public Optional<V> get(K key) {
		var result = this.cache.get(key);
		result.ifPresent(v -> {
			if (!timeCodeMap.containsKey(key)) {
				timeCodeMap.put(key, Tuple.of(System.currentTimeMillis(), System.currentTimeMillis()));
			} else {
				timeCodeMap.get(key).setValue(System.currentTimeMillis());
			}
		});
		return result;
	}

	public void set(K key, V value) {
		this.cache.put(key, value);
		timeCodeMap.put(key, Tuple.of(System.currentTimeMillis(), System.currentTimeMillis()));
	}

	public void clear() {
		cache.clear();
		this.timeCodeMap.clear();
	}

	public int size() {
		return cache.size();
	}

}
