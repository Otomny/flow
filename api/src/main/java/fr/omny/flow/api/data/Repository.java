package fr.omny.flow.api.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Repository {

	/**
	 * The name of the repository, by default, the class name in snake case
	 * 
	 * @return
	 */
	String value() default "__class";

	/**
	 * If the implementation should cache data
	 * 
	 * @return
	 */
	boolean cache() default false;

	/**
	 * @deprecated Use {@link Repository#value()}
	 * @return the same as value()
	 */
	@Deprecated
	String name() default "__class";

}
