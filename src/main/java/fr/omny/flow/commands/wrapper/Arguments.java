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
	 * @param obj
	 */
	public void put(int index, Object obj){
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
	 * @return
	 */
	public int count(){
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

}
