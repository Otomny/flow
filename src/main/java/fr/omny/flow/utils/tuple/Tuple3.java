package fr.omny.flow.utils.tuple;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Tuple3<U, V, W> {
	
	private U first;
	private V second;
	private W third;

	public U getX(){
		return this.first;
	}

	public V getY(){
		return this.second;
	}

	public W getZ(){
		return this.third;
	}


}
