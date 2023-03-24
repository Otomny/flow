package fr.omny.flow.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.omny.flow.api.tasks.Dispatcher;
import fr.omny.odi.Injector;

public class DispatcherTest {

	@BeforeEach
	public void setup() {
		Injector.startTest();
	}

	@AfterEach
	public void tearDown() {
		Injector.stopTest();
	}

	@Test
	public void test_Dispatcher_PoolSize_Autowiring() throws InterruptedException, ExecutionException {
		Injector.addService(Integer.class, Dispatcher.THREAD_POOL_SIZE, 8);
		Injector.addService(Dispatcher.class);
		Dispatcher dispatcher = Injector.getService(Dispatcher.class);
		assertTrue(dispatcher != null);
		assertEquals(8, dispatcher.getThreadPoolSize());
	}

	@Test
	public void test_Dispatcher_PoolSize_Default() throws InterruptedException, ExecutionException {
		Injector.addService(Dispatcher.class);
		Dispatcher dispatcher = Injector.getService(Dispatcher.class);
		assertTrue(dispatcher != null);
		assertEquals(2, dispatcher.getThreadPoolSize());
	}

	@Test
	public void test_Dispatcher_Submit_Runnable() throws InterruptedException, ExecutionException {
		Injector.addService(Dispatcher.class);
		Dispatcher dispatcher = Injector.getService(Dispatcher.class);
		assertTrue(dispatcher != null);

		AtomicBoolean atomicBoolean = new AtomicBoolean(false);

		dispatcher.submit(() -> {
			atomicBoolean.set(true);
		});
		Thread.sleep(100);

		assertTrue(atomicBoolean.get());
	}

	@Test
	public void test_Dispatcher_Submit_Callable() throws InterruptedException, ExecutionException {
		Injector.addService(Dispatcher.class);
		Dispatcher dispatcher = Injector.getService(Dispatcher.class);
		assertTrue(dispatcher != null);

		var result = dispatcher.submit(() -> {
			try {
				Thread.sleep(100);
				return true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return false;
		});

		assertTrue(result.get());
	}

}
