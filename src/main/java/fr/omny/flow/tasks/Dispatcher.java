package fr.omny.flow.tasks;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
	public <T> Future<T> submit(Callable<T> supplier){
		return this.executor.submit(supplier);
	}

}
