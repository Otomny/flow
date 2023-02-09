package fr.omny.flow.utils.mongodb;


import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.mongodb.MongoClientSettings;

import fr.omny.flow.utils.mongodb.codecs.ItemStackCodec;
import fr.omny.flow.utils.mongodb.codecs.LocationCodec;
import fr.omny.flow.utils.mongodb.codecs.WorldCodec;
import fr.omny.odi.Component;

@Component
public class FlowCodec {

	private List<Codec<?>> codecs = new ArrayList<>();

	public FlowCodec(){
		// Initializing
		this.codecs.addAll(List.of(
			new LocationCodec(),
			new WorldCodec(),
			new ItemStackCodec()
		));
	}

	public void registerCodec(Codec<?> codec) {
		this.codecs.add(codec);
	}

	/**
	 * 
	 * @return
	 */
	public CodecRegistry getCodecRegistries() {
		return CodecRegistries.fromRegistries(CodecRegistries.fromCodecs(this.codecs),
				MongoClientSettings.getDefaultCodecRegistry());
	}

}
