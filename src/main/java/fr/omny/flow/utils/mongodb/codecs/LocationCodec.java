package fr.omny.flow.utils.mongodb.codecs;


import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationCodec implements Codec<Location> {

	@Override
	public void encode(BsonWriter writer, Location location, EncoderContext encoderContext) {
		writer.writeStartDocument();
		writer.writeString("world", location.getWorld().getName());
		writer.writeDouble("x", location.getX());
		writer.writeDouble("y", location.getY());
		writer.writeDouble("z", location.getZ());
		writer.writeDouble("yaw", location.getYaw());
		writer.writeDouble("pitch", location.getPitch());
		writer.writeEndDocument();
	}

	@Override
	public Location decode(BsonReader reader, DecoderContext decoderContext) {
		reader.readStartDocument();
		World world = Bukkit.getWorld(reader.readString("world"));
		double x = reader.readDouble("x");
		double y = reader.readDouble("y");
		double z = reader.readDouble("z");
		double yaw = reader.readDouble("yaw");
		double pitch = reader.readDouble("pitch");
		reader.readEndDocument();
		return new Location(world, x, y, z, (float) yaw, (float) pitch);
	}

	@Override
	public Class<Location> getEncoderClass() {
		return Location.class;
	}
}