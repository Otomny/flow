package fr.omny.flow.api.data.implementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.mongodb.MongoClientSettings;

import fr.omny.odi.Component;

@Component
public class FlowCodec {

	private List<Codec<?>> codecs = new ArrayList<>();
	private List<CodecProvider> codecProviders = new ArrayList<>();

	public FlowCodec() {
	}

	@Deprecated
	public void registerCodec(Codec<?> codec) {
		this.codecs.add(codec);
	}

	public void add(Codec<?> codec) {
		this.codecs.add(codec);
	}

	public void addAll(Collection<Codec<?>> codec) {
		this.codecs.addAll(codec);
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
