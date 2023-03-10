package fr.omny.flow.api.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/**
 * Annotation to indicate that a method should run on production env
 */
public @interface RunOnProd {
	
}
