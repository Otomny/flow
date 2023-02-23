package fr.omny.flow.world.schematic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import fr.omny.flow.data.Id;
import fr.omny.flow.data.Val;
import fr.omny.flow.utils.tuple.Tuple;
import fr.omny.flow.utils.tuple.Tuple3;
import fr.omny.flow.world.BlockPasteRunnable;
import fr.omny.flow.world.BlockUpdate;
import fr.omny.flow.world.schematic.component.StoredLocation;
import fr.omny.odi.Injector;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Schematic {

	@Id
	@Setter
	private UUID id = UUID.randomUUID();

	@Val
	@Setter
	private String name = "none";

	@Val
	@Setter
	private Tuple3<Integer, Integer, Integer> dimensions = Tuple.of(0, 0, 0);

	@Val
	@Setter
	private StoredLocation offset;

	@Val
	@Setter
	private String[] blocks;

	public int getWidth() {
		return this.dimensions.getX();
	}

	public int getHeight() {
		return this.dimensions.getY();
	}

	public int getLength() {
		return this.dimensions.getZ();
	}

	public void setDimensions(int width, int height, int length) {
		setDimensions(Tuple.of(width, height, length));
	}

	public double getOffSetX() {
		return offset.getX();
	}

	public double getOffSetY() {
		return offset.getY();
	}

	public double getOffSetZ() {
		return offset.getZ();
	}

	public void paste(Location location) {
		paste(() -> {
		}, location);
	}

	public void paste(Runnable onEnd, Location location) {
		paste(onEnd, location, false);
	}

	public void paste(Runnable onEnd, Location location, boolean skipAir) {
		List<BlockUpdate> blockUpdate = new ArrayList<>();

		var dx = location.getBlockX();
		var dy = location.getBlockY();
		var dz = location.getBlockZ();

		var ox = (int) offset.getX();
		var oy = (int) offset.getY();
		var oz = (int) offset.getZ();

		var height = getHeight();
		var length = getLength();
		var width = getWidth();
		var world = location.getWorld().getName();

		if (width * height * length != getBlocks().length) {
			throw new IllegalStateException(
					"Wrong size (expected " + getBlocks().length + ", got " + (width * height * length) + " [" + width + "*"
							+ height + "*" + length + "])");
		}

		for (int x = 0; x < width; x++) {
			for (int z = 0; z < length; z++) {
				for (int y = 0; y < height; y++) {
					int index = y * width * length + x * length + z;
					String blockAt = blocks[index];
					BlockData blockData = Bukkit.createBlockData(blockAt);
					if (blockData.getMaterial() == Material.AIR && skipAir)
						continue;
					blockUpdate.add(BlockUpdate.create(world, x + dx - ox, y + dy - oy, z + dz - oz, blockData));
				}
			}
		}
		Injector.getService(BlockPasteRunnable.class)
				.add(onEnd, blockUpdate);
	}

}
