package fr.omny.flow.tasks;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import fr.omny.flow.config.Config;
import fr.omny.odi.Component;

@Component
public class Dispatcher {

	private ScheduledExecutorService executor;

	Dispatcher() {
	}

	public Dispatcher(@Config("distributed.thread_config.thread_pool_size") int threadPoolSize) {
		LoggingThreadFactory threadFactory = new LoggingThreadFactory();
		this.executor = Executors.newScheduledThreadPool(4, threadFactory);
	}

	/**
	 * @param runnable
	 */
	public void submit(Runnable runnable) {
		this.executor.submit(runnable);
	}

	/**
	 * @param runnable
	 */
	public void submit(Runnable runnable, long delay, TimeUnit timeUnit) {
		this.executor.schedule(runnable, delay, timeUnit);
	}

	/**
	 * 
	 * @param runnable
	 * @param delay
	 * @param period
	 * @param timeUnit
	 */
	public void submitFixedRate(Runnable runnable, long delay, long period, TimeUnit timeUnit) {
		this.executor.scheduleAtFixedRate(runnable, delay, period, timeUnit);
	}

	/**
	 * 
	 * @param runnable
	 * @param delay
	 * @param period
	 * @param timeUnit
	 */
	public void submitFixedDelay(Runnable runnable, long delay, long period, TimeUnit timeUnit) {
		this.executor.scheduleWithFixedDelay(runnable, delay, period, timeUnit);
	}

	/**
	 * @param <T>
	 * @param supplier
	 * @return
	 */
	public <T> CompletableFuture<T> submit(Supplier<T> supplier) {
		return CompletableFuture.supplyAsync(supplier, this.executor);
	}

}
