package fr.omny.flow.commands.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 
 */
public class Arguments {

	private Map<Integer, Object> arguments = new HashMap<>();

	public Arguments() {
	}

	/**
	 * 
	 * @param index
	 * @param obj
	 */
	public void put(int index, Object obj) {
		this.arguments.put(index, obj);
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	public boolean isPresent(int index) {
		return arguments.containsKey(index);
	}

	/**
	 * 
	 * @param index
	 * @param klass
	 * @return
	 */
	public boolean isPresent(int index, Class<?> klass) {
		return isPresent(index) && arguments.get(index) != null && klass.isAssignableFrom(arguments.get(index).getClass());
	}

	/**
	 * Number of arguments passed
	 * 
	 * @return
	 */
	public int count() {
		return this.arguments.size();
	}

	/**
	 * 
	 * @param <T>
	 * @param index
	 * @param klass
	 * @return
	 */
	public <T> T get(int index, Class<? extends T> klass) {
		if (!isPresent(index, klass))
			return null;
		return klass.cast(arguments.get(index));
	}

	/**
	 * Get the argument at the given index or the default value if the argument is
	 * not present or is not of the given type
	 * 
	 * @param <T>          The type of the argument
	 * @param index        The index of the argument
	 * @param klass        The class of the argument
	 * @param defaultValue The default value to return if the argument is not
	 * @return The argument if present and of the given type or the default value
	 */
	public <T> T getOr(int index, Class<? extends T> klass, T defaultValue) {
		T value = get(index, klass);
		return value == null ? defaultValue : value;
	}

	/**
	 * Get the argument at the given index or the default value if the argument is
	 * not present or is not of the given type
	 * 
	 * @param <T>   The type of the argument
	 * @param index The index of the argument
	 * @param klass The class of the argument
	 * @return The argument if present and of the given type or an empty optional
	 */
	public <T> Optional<T> getOptional(int index, Class<? extends T> klass) {
		return Optional.ofNullable(get(index, klass));
	}

}
