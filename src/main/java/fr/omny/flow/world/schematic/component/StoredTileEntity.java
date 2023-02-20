package fr.omny.flow.world.schematic.component;

import org.bukkit.block.BlockState;

import lombok.Getter;

@Getter
public class StoredTileEntity {
	
	public static StoredTileEntity fromTile(BlockState blockState) {
		throw new UnsupportedOperationException("fromEntity not implemented");
	}

	private StoredLocation location;

	private StoredTileEntity() {
	}

}
