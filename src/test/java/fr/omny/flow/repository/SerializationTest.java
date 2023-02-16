package fr.omny.flow.repository;



import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.junit.jupiter.api.Test;

import fr.omny.flow.utils.mongodb.FlowCodec;
import lombok.Getter;
import lombok.Setter;

public class SerializationTest {

	@Test
	public void test_serializeField_WithCodecs() {
		DummyObject data = new DummyObject();
		BsonDocument container = new Document().toBsonDocument();
		FlowCodec codecs = new FlowCodec();

		var writer = new BsonDocumentWriter(container);

		Codec<Integer> codec = (Codec<Integer>) codecs.getCodecRegistries().get(Integer.class);
		writer.writeStartDocument();
		writer.writeName("data");
		codec.encode(writer, data.getData(), EncoderContext.builder().isEncodingCollectibleDocument(false).build());
		writer.writeEndDocument();

		var json = container.toJson();
		assertEquals("{\"data\": 0}", json);
	}

	@Test
	public void test_serializeField_WithCodecs_NestedData() {
		DummyObject data = new DummyObject();
		BsonDocument container = new Document().toBsonDocument();
		FlowCodec codecs = new FlowCodec();
		codecs.registerCodecProvider(PojoCodecProvider.builder().register(DummySubObject.class).build());
		var codec = codecs.getCodecRegistries().get(DummySubObject.class);

		var writer = new BsonDocumentWriter(container);

		writer.writeStartDocument();
		writer.writeName("subObject");
		codec.encode(writer, data.getSubObject(), EncoderContext.builder().isEncodingCollectibleDocument(false).build());
		writer.writeEndDocument();

		var json = container.toJson();
		assertEquals("{\"subObject\": {\"a\": 1, \"b\": 2.0, \"c\": \"word\", \"d\": [\"Hello\", \"Beautiful\", \"World\"]}}", json);
	}

	@Getter
	@Setter
	public static class DummyObject {

		int data = 0;
		DummySubObject subObject = new DummySubObject();

	}

	@Getter
	@Setter
	public static class DummySubObject {

		@BsonProperty()
		Long a = 1L;
		@BsonProperty()
		Double b = 2.0;
		@BsonProperty()
		String c = "word";
		@BsonProperty()
		List<String> d = List.of("Hello", "Beautiful", "World");

	}

}
