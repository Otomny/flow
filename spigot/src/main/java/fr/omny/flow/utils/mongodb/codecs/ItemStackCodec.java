package fr.omny.flow.utils.mongodb.codecs;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class ItemStackCodec implements Codec<ItemStack> {

	@Override
	public void encode(BsonWriter writer, ItemStack arg, EncoderContext ctx) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
			dataOutput.writeObject(arg);
			writer.writeBinaryData(new BsonBinary(outputStream.toByteArray()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ItemStack decode(BsonReader reader, DecoderContext ctx) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(reader.readBinaryData().getData());
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(bis);
			var item = (ItemStack) dataInput.readObject();
			dataInput.close();
			return item;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Class<ItemStack> getEncoderClass() {
		return ItemStack.class;
	}

}
