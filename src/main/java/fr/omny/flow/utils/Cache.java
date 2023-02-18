package fr.omny.flow.utils;

import java.util.Optional;

public interface Cache<K, V> {

	/**
	 * Set a value inside the cache
	 * @param key
	 * @param value
	 * @return
	 */
	boolean set(K key, V value);

	/**
	 * Get an optional value of the cache
	 * @param key the key to the value
	 * @return An optional
	 */
	Optional<V> get(K key);

	/**
	 * 
	 * @return The size of the current cache
	 */
	int size();

	/**
	 * Check if cache is empty
	 * @return True if cache is empty, false otherwise
	 */
	boolean isEmpty();

	/**
	 * Clear the cache
	 */
	void clear();
}
