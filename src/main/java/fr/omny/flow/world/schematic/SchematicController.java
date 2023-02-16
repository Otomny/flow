package fr.omny.flow.world.schematic;


import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class SchematicController implements Codec<Schematic> {

	@Override
	public void encode(BsonWriter writer, Schematic value, EncoderContext encoderContext) {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<Schematic> getEncoderClass() {
		return Schematic.class;
	}

	@Override
	public Schematic decode(BsonReader reader, DecoderContext decoderContext) {
		// TODO Auto-generated method stub
		return null;
	}

}
