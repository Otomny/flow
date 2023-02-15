package fr.omny.flow.tasks;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configure the runnable to be executed as a component
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RunnableConfig {

	/**
	 * The name of the runnable in case of logging
	 * 
	 * @return
	 */
	String value() default "default";

	/**
	 * Delay in tick until the first call must be made
	 * 
	 * @return Amount of ticks before the first run
	 */
	long delay() default 0L;

	/**
	 * Period between two call
	 * 
	 * @return Amount of ticks between two run
	 */
	long period() default 20L;

	/**
	 * If it should run in asynchronous thread
	 * 
	 * @return True if async,false otherwise
	 */
	boolean async() default false;

	/**
	 * <p>
	 * The type of scheduler use to run the runnable default is Bukkit
	 * </p>
	 * <p>
	 * <strong>BE CAREFUL</strong> while using FLOW scheduler type, it's always running in async
	 * </p>
	 * 
	 * @return Type of scheduling
	 */
	SchedulerType type() default SchedulerType.BUKKIT;

}
