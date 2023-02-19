package fr.omny.flow.utils.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import fr.omny.flow.utils.AtomicLRUCache;
import fr.omny.flow.utils.Cache;

public class AtomicLRUCacheTest {

	@Test
	public void runMultiThreadTask_WhenPutDataInConcurrentToCache_ThenNoDataLost() throws Exception {
		final int size = 50;
		final ExecutorService executorService = Executors.newFixedThreadPool(5);
		Cache<Integer, String> cache = new AtomicLRUCache<>(size);
		CountDownLatch countDownLatch = new CountDownLatch(size);
		try {
			IntStream.range(0, size).<Runnable>mapToObj(key -> () -> {
				cache.set(key, "value" + key);
				countDownLatch.countDown();
			}).forEach(executorService::submit);
			countDownLatch.await();
		} finally {
			executorService.shutdown();
		}
		assertEquals(cache.size(), size);
		IntStream.range(0, size).forEach(i -> assertEquals("value" + i, cache.get(i).get()));
	}

}
