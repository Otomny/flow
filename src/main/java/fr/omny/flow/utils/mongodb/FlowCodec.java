package fr.omny.flow.utils.mongodb;


import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClientSettings;

import fr.omny.flow.utils.mongodb.codecs.ItemStackCodec;
import fr.omny.flow.utils.mongodb.codecs.LocationCodec;
import fr.omny.flow.utils.mongodb.codecs.WorldCodec;
import fr.omny.flow.world.Area;
import fr.omny.odi.Component;

@Component
public class FlowCodec {

	private List<Codec<?>> codecs = new ArrayList<>();
	private List<CodecProvider> codecProviders = new ArrayList<>();

	public FlowCodec() {
		// Initializing
		this.codecs.addAll(List.of(new LocationCodec(), new WorldCodec(), new ItemStackCodec()));
		registerCodecProvider(PojoCodecProvider.builder().register(Area.class).build());
	}

	public void registerCodec(Codec<?> codec) {
		this.codecs.add(codec);
	}

	public void registerCodecProvider(CodecProvider provider) {
		this.codecProviders.add(provider);
	}

	/**
	 * @return
	 */
	public CodecRegistry getCodecRegistries() {
		if (this.codecProviders.isEmpty()) {
			return CodecRegistries.fromRegistries(CodecRegistries.fromCodecs(this.codecs),
					MongoClientSettings.getDefaultCodecRegistry());
		} else {
			return CodecRegistries.fromRegistries(CodecRegistries.fromCodecs(this.codecs),
					CodecRegistries.fromProviders(this.codecProviders), MongoClientSettings.getDefaultCodecRegistry());
		}
	}

}
