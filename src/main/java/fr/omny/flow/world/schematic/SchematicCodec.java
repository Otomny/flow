package fr.omny.flow.world.schematic;

import java.util.HashMap;
import java.util.Map;

import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import fr.omny.flow.world.schematic.codecs.SchematicV1;
import fr.omny.flow.world.schematic.codecs.SchematicVersion;

public class SchematicCodec implements Codec<Schematic> {

	private Map<Integer, SchematicVersion> schematicLoaders;

	public SchematicCodec() {
		this.schematicLoaders = new HashMap<>();
		register(new SchematicV1());
	}

	public void register(SchematicVersion v) {
		this.schematicLoaders.put(v.getVersion(), v);
	}

	public SchematicVersion get(int version) {
		return this.schematicLoaders.get(version);
	}

	public SchematicVersion getPrefered() {
		return null;
	}

	@Override
	public void encode(BsonWriter writer, Schematic value, EncoderContext encoderContext) {
		var schematicSerializer = getPrefered();
		writer.writeStartDocument();
		writer.writeInt32("version", schematicSerializer.getVersion());
		writer.writeBinaryData("data", new BsonBinary(schematicSerializer.save(value)));
		writer.writeEndDocument();
	}

	@Override
	public Schematic decode(BsonReader reader, DecoderContext decoderContext) {
		reader.readStartDocument();
		int version = reader.readInt32("version");
		var schematicDeserializer = get(version);
		Schematic schematic = schematicDeserializer.load(reader.readBinaryData("data").getData());
		reader.readEndDocument();
		return schematic;
	}

	@Override
	public Class<Schematic> getEncoderClass() {
		return Schematic.class;
	}

}
