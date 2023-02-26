package fr.omny.flow.api.utils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import fr.omny.flow.api.utils.tuple.Tuple;
import fr.omny.flow.api.utils.tuple.Tuple2;
import lombok.Getter;

@Getter
public class FlowCache<K, V> extends AtomicLRUCache<K, V> {

	/**
	 * Cache the timeCode First long = First insertion Second long = Last update
	 */
	private Map<K, Tuple2<Long, Long>> timeCodeMap = new ConcurrentHashMap<>();

	private final long ttl;
	private final long maxIddle;

	public FlowCache(int maxSize, long ttl, long maxIddle) {
		this(maxSize, ttl, maxIddle, 1000);
	}

	public FlowCache(int maxSize, long ttl, long maxIddle, long checkUpTime) {
		super(maxSize);
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

	@Override
	public Optional<V> get(K key) {
		var result = super.get(key);
		result.ifPresent(v -> timeCodeMap.get(key).setValue(System.currentTimeMillis()));
		return result;
	}

	@Override
	public boolean set(K key, V value) {
		boolean result = super.set(key, value);
		if (result)
			timeCodeMap.put(key, Tuple.of(System.currentTimeMillis(), System.currentTimeMillis()));
		return result;
	}

	@Override
	public void clear() {
		super.clear();
		this.timeCodeMap.clear();
	}

}
