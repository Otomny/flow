package fr.omny.flow.world.schematic;

import java.util.UUID;

import fr.omny.flow.data.Id;
import fr.omny.flow.data.Val;
import fr.omny.flow.utils.tuple.Tuple;
import fr.omny.flow.utils.tuple.Tuple3;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Schematic {
	
	@Id
	private UUID id;

	@Val
	@Setter
	private String name;

	@Val
	@Setter
	private Tuple3<Integer, Integer, Integer> dimensions;
	
	

	public int getWidth() {
		return this.dimensions.getX();
	}

	public int getHeight() {
		return this.dimensions.getY();
	}

	public int getLength() {
		return this.dimensions.getY();
	}

	public void setDimensions(int width, int height, int length) {
		setDimensions(Tuple.of(width, height, length));
	}

}
