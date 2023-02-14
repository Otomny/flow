package fr.omny.flow.utils.tuple;

/**
 * Tuple class
 */
public final class Tuple {

	private Tuple() {}

	/**
	 * Build a 2 value tuple
	 * 
	 * @param <U>
	 * @param <V>
	 * @param first
	 * @param second
	 * @return
	 */
	public static <U, V> Tuple2<U, V> of(U first, V second) {
		return new Tuple2<U, V>(first, second);
	}

	/**
	 * Build a 3 value tuple
	 * 
	 * @param <U>
	 * @param <V>
	 * @param <W>
	 * @param first
	 * @param second
	 * @param third
	 * @return
	 */
	public static <U, V, W> Tuple3<U, V, W> of(U first, V second, W third) {
		return new Tuple3<U, V, W>(first, second, third);
	}

}
