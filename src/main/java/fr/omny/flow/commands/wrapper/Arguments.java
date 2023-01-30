package fr.omny.flow.commands.wrapper;


import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class Arguments {

	private Map<Integer, Object> arguments = new HashMap<>();

	public Arguments() {}

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

}
