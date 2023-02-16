package fr.omny.flow.world.schematic;


import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import fr.omny.flow.data.Id;
import fr.omny.flow.data.Val;
import fr.omny.flow.utils.tuple.Tuple3;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Schematic {

	@BsonId
	@Id
	private UUID id;

	@BsonProperty
	@Val
	@Setter
	private String name;

	@BsonProperty
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

}
