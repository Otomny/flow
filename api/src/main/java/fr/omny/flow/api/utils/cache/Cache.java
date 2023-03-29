package fr.omny.flow.api.utils.cache;


import java.util.Optional;

/**
 * Created by marcalperapochamado on 18/02/17.
 */
public interface Cache<K, V> {

    Optional<V> get(K key);

    void put(K key, V value);

    boolean containsKey(K key);

    int size();

		void clear();

}
