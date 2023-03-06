package fr.omny.flow.api.utils.generic;

import java.util.function.Consumer;

public final class Consumers {

	private Consumers() {
	}

	/**
	 * Return an empty consumer
	 * 
	 * @param <T> The type
	 * @return The consumer
	 */
	public static <T> Consumer<T> empty() {
		return (e) -> {
		};
	}

}
