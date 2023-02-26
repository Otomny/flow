package fr.omny.flow.world.schematic.component;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.bukkit.block.BlockState;

import lombok.Getter;

@Getter
public class StoredTileEntity {

	public static StoredTileEntity fromTile(BlockState blockState) {
		throw new UnsupportedOperationException("fromEntity not implemented");
	}

	public static StoredTileEntity fromIO(DataInputStream inputStream) {
		throw new UnsupportedOperationException("fromIO not implemented");

	}

	private StoredLocation location;

	private StoredTileEntity() {
	}

	public void storeIO(DataOutputStream outputStream) {
		throw new UnsupportedOperationException("storeIO not implemented");
	}

}
