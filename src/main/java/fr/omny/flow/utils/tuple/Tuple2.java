package fr.omny.flow.utils.tuple;


import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Tuple2<U, V> implements Map.Entry<U, V> {

	private U first;
	private V second;

	@Override
	public U getKey() {
		return this.first;
	}

	@Override
	public V getValue() {

		return this.second;
	}

	@Override
	public V setValue(V val) {
		return this.second = val;
	}

}
