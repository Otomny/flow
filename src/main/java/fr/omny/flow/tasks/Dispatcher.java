package fr.omny.flow.tasks;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import fr.omny.odi.Component;

@Component
/**
 * 
 */
public class Dispatcher {

	private ExecutorService executor;

	public Dispatcher() {
		LoggingThreadFactory threadFactory = new LoggingThreadFactory();
		this.executor = Executors.newScheduledThreadPool(4, threadFactory);
	}

	/**
	 * 
	 * @param runnable
	 */
	public void submit(Runnable runnable){
		this.executor.submit(runnable);
	}

	/**
	 * 
	 * @param <T>
	 * @param supplier
	 * @return
	 */
	public <T> CompletableFuture<T> submit(Supplier<T> supplier){
		return CompletableFuture.supplyAsync(supplier, this.executor);
	}

}
