package fr.omny.flow.utils.mongodb.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldCodec implements Codec<World> {

	@Override
	public void encode(BsonWriter writer, World world, EncoderContext encoderContext) {
			writer.writeString(world.getName());
	}

	@Override
	public World decode(BsonReader reader, DecoderContext decoderContext) {
			return Bukkit.getWorld(reader.readString());
	}

	@Override
	public Class<World> getEncoderClass() {
			return World.class;
	}
}
