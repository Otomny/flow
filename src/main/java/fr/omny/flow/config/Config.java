package fr.omny.flow.config;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
		ElementType.FIELD, ElementType.PARAMETER })
public @interface Config {

	String value();

}
