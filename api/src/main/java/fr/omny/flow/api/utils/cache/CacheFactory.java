package fr.omny.flow.api.utils.cache;

import fr.omny.flow.api.utils.cache.concurrent.ConcurrentCache;
import fr.omny.flow.api.utils.cache.lru.LRUCache;

/**
 * Created by marcal.perapoch on 02/03/2017.
 */
public interface CacheFactory {

    static <K,V> Cache<K, V> createConcurrentLRUCache(int maxElements) {
        return new ConcurrentCache<>(createLRUCache(maxElements));
    }

    static <K,V> Cache<K, V> createLRUCache(int maxElements) {
        return new LRUCache<>(maxElements);
    }
}
