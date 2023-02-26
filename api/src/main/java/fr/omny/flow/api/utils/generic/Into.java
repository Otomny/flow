package fr.omny.flow.api.utils.generic;

/**
 * Something that can be translated to a T object
 */
public interface Into<T> {

	/**
	 * 
	 * @return The required element type
	 */
	T transform();

}
