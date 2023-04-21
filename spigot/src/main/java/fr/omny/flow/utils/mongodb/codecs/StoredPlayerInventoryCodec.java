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

import fr.omny.flow.player.StoredPlayerInventory;

public class StoredPlayerInventoryCodec implements Codec<StoredPlayerInventory> {

	@Override
	public void encode(BsonWriter writer, StoredPlayerInventory value, EncoderContext encoderContext) {
		writer.writeStartDocument();
		writeItemStackArray(writer, "inventory", value.getInventoryContent());
		writeItemStackArray(writer, "armor", value.getArmorContent());
		writeItemStackArray(writer, "enderchest", value.getEnderChest());
		writer.writeEndDocument();
	}

	public void writeItemStackArray(BsonWriter writer, String name, ItemStack[] array) {
		writer.writeName(name);
		writer.writeStartDocument();
		if (array == null) {
			writer.writeInt32("size", -1);
			writer.writeEndDocument();
			return;
		}
		writer.writeInt32("size", array.length);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
			for (int i = 0; i < array.length; i++) {
				dataOutput.writeObject(array[i]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] itemStackBinary = outputStream.toByteArray();
		BsonBinary bsonBinary = new BsonBinary(itemStackBinary);
		writer.writeBinaryData("content", bsonBinary);
		writer.writeEndDocument();
	}

	@Override
	public Class<StoredPlayerInventory> getEncoderClass() {
		return StoredPlayerInventory.class;
	}

	@Override
	public StoredPlayerInventory decode(BsonReader reader, DecoderContext decoderContext) {
		var inventory = new StoredPlayerInventory();
		reader.readStartDocument();
		reader.readName("inventory");
		inventory.setInventoryContent(readItemStackArray(reader));
		reader.readName("armor");
		inventory.setArmorContent(readItemStackArray(reader));
		reader.readName("enderchest");
		inventory.setEnderChest(readItemStackArray(reader));
		reader.readEndDocument();
		return inventory;
	}

	public ItemStack[] readItemStackArray(BsonReader reader) {
		reader.readStartDocument();
		int size = reader.readInt32("size");
		if (size < 0) {
			reader.readEndDocument();
			return null;
		}
		try {
			ItemStack[] allocated = new ItemStack[size];
			ByteArrayInputStream bis = new ByteArrayInputStream(reader.readBinaryData("content").getData());
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(bis);
			for (int i = 0; i < size; i++) {
				var item = (ItemStack) dataInput.readObject();
				allocated[i] = item;
			}
			dataInput.close();
			reader.readEndDocument();
			return allocated;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
