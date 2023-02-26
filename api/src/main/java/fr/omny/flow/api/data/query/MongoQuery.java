package fr.omny.flow.api.data.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MongoQuery {

	/**
	 * 
	 * @return
	 */
	String value();

	/**
	 * 
	 * @return
	 */
	String fields();

}
