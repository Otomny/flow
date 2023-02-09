package fr.omny.flow.data.mongodb;


import java.util.UUID;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class StringUUIDCodec implements Codec<UUID> {

	@Override
	public void encode(BsonWriter writer, UUID data, EncoderContext ctx) {
		writer.writeString(data == null ? "null" : data.toString());
	}

	@Override
	public Class<UUID> getEncoderClass() {
		return UUID.class;
	}

	@Override
	public UUID decode(BsonReader reader, DecoderContext ctx) {
		String s = reader.readString();
		return s == "null" ? null : UUID.fromString(s);
	}

}
