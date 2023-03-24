package fr.omny.flow.api.tasks;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import fr.omny.odi.Autowired;
import fr.omny.odi.Component;
import lombok.Getter;

@Component(proxy = false)
public class Dispatcher {

	public static final String THREAD_POOL_SIZE = "distributed.thread_config.thread_pool_size";

	@Getter
	private ScheduledExecutorService executor;

	@Getter
	private int threadPoolSize;

	public Dispatcher(@Autowired(THREAD_POOL_SIZE) Optional<Integer> threadPoolSize) {
		LoggingThreadFactory threadFactory = new LoggingThreadFactory();
		this.executor = Executors.newScheduledThreadPool(threadPoolSize.orElse(2),
				threadFactory);
		this.threadPoolSize = threadPoolSize.orElse(2);
	}

	// @Post
	// public void __init(@Autowired(THREAD_POOL_SIZE) Optional<Integer>
	// threadPoolSize) {
	// LoggingThreadFactory threadFactory = new LoggingThreadFactory();
	// this.executor = Executors.newScheduledThreadPool(threadPoolSize,
	// threadFactory);
	// this.threadPoolSize = threadPoolSize;
	// }

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
