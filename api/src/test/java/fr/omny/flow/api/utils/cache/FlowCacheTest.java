package fr.omny.flow.api.utils.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import fr.omny.flow.api.aop.cache.FlowCacheImplementation;
import fr.omny.odi.Component;
import fr.omny.odi.Injector;
import fr.omny.odi.caching.Caching;

public class FlowCacheTest {

	@Test
	public void addData_To_Cache() {
		FlowCache<String, String> flowCache = new FlowCache<>(3, 2000, 2000);
		flowCache.set("1", "test1");
		flowCache.set("2", "test2");
		flowCache.set("3", "test3");
		assertEquals("test1", flowCache.get("1").get());
		assertEquals("test2", flowCache.get("2").get());
		assertEquals("test3", flowCache.get("3").get());
	}

	@Test
	public void addData_To_Cache_Test_Eviction() {
		FlowCache<String, String> flowCache = new FlowCache<>(3, 2000, 2000);
		flowCache.set("1", "test1");
		flowCache.set("2", "test2");
		flowCache.set("3", "test3");
		flowCache.set("4", "test4");
		assertFalse(flowCache.get("1").isPresent());
	}

	@Test
	public void testMultiThread_Access_Atomic() throws InterruptedException {
		final int size = 50;
		final ExecutorService executorService = Executors.newFixedThreadPool(4);
		FlowCache<Integer, String> cache = new FlowCache<>(size, 2000, 2000);
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

	@Test
	public void test_Caching_Annotation() {
		Injector.startTest();
		Injector.addService(Service.class);
		Service service = Injector.getService(Service.class);
		long start = System.currentTimeMillis();
		service.findForThat(0, "meta", Long.class);
		long end = System.currentTimeMillis();
		assertEquals(500L, end - start, 50L);
		start = System.currentTimeMillis();
		service.findForThat(0, "meta", Long.class);
		end = System.currentTimeMillis();
		assertEquals(0L, end - start, 50L);

		Injector.stopTest();
	}

	@Test
	public void test_Caching_Loop() {
		Injector.startTest();
		Injector.addService(Service.class);
		Service service = Injector.getService(Service.class);
		long start = System.currentTimeMillis();
		service.findForThat(0, "meta", Long.class);
		long end = System.currentTimeMillis();
		assertEquals(500L, end - start, 50L);
		for (int i = 0; i < 2000; i++) {
			start = System.currentTimeMillis();
			service.findForThat(0, "meta", Long.class);
			end = System.currentTimeMillis();
			assertEquals(0L, end - start, 50L);
		}

		Injector.stopTest();
	}

	@Component
	public static class Service {

		@Caching(implementation = FlowCacheImplementation.class, ttl = 500L, size = 50, maxIdleTime = 300L)
		public List<String> findForThat(int id, String metadata, Class<?> dumbClass) {
			try {
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return List.of("longComputation");
		}

	}

}
