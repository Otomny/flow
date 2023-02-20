package fr.omny.flow.world.schematic.component;

import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoredLocation {

	@BsonProperty
	private double x;

	@BsonProperty
	private double y;

	@BsonProperty
	private double z;

}
