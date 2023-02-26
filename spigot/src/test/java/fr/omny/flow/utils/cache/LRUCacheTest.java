package fr.omny.flow.utils.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import fr.omny.flow.api.utils.LRUCache;

public class LRUCacheTest {

	@Test
	public void addData_To_Cache() {
		LRUCache<String, String> lruCache = new LRUCache<>(3);
		lruCache.set("1", "test1");
		lruCache.set("2", "test2");
		lruCache.set("3", "test3");
		assertEquals("test1", lruCache.get("1").get());
		assertEquals("test2", lruCache.get("2").get());
		assertEquals("test3", lruCache.get("3").get());
	}

	@Test
	public void addData_To_Cache_Test_Eviction() {
		LRUCache<String, String> lruCache = new LRUCache<>(3);
		lruCache.set("1", "test1");
		lruCache.set("2", "test2");
		lruCache.set("3", "test3");
		lruCache.set("4", "test4");
		assertFalse(lruCache.get("1").isPresent());
	}

}
