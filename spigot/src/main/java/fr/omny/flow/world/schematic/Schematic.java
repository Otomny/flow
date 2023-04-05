package fr.omny.flow.world.schematic;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BlockInventoryHolder;

import fr.omny.flow.api.data.Id;
import fr.omny.flow.api.data.Val;
import fr.omny.flow.api.utils.tuple.Tuple;
import fr.omny.flow.api.utils.tuple.Tuple3;
import fr.omny.flow.plugins.FlowPlugin;
import fr.omny.flow.world.BlockPasteRunnable;
import fr.omny.flow.world.BlockUpdate;
import fr.omny.flow.world.schematic.component.StoredChest;
import fr.omny.flow.world.schematic.component.StoredLocation;
import fr.omny.guis.attributes.Itemable;
import fr.omny.guis.backend.GuiItemBuilder;
import fr.omny.odi.Injector;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@Getter
public class Schematic implements Itemable {

	@Id
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

	@Val
	@Setter
	private List<StoredChest> chests = new ArrayList<>();

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

		final int dx = location.getBlockX();
		final int dy = location.getBlockY();
		final int dz = location.getBlockZ();

		final int ox = (int) offset.getX();
		final int oy = (int) offset.getY();
		final int oz = (int) offset.getZ();

		var height = getHeight();
		var length = getLength();
		var width = getWidth();
		var world = location.getWorld().getName();

		if (width * height * length != getBlocks().length) {
			throw new IllegalStateException("Wrong size (expected " +
					getBlocks().length + ", got " +
					(width * height * length) + " [" + width +
					"*" + height + "*" + length + "])");
		}

		for (int x = 0; x < width; x++) {
			for (int z = 0; z < length; z++) {
				for (int y = 0; y < height; y++) {
					int index = y * width * length + x * length + z;
					String blockAt = blocks[index];
					BlockData blockData = Bukkit.createBlockData(blockAt);
					if (blockData.getMaterial() == Material.AIR && skipAir)
						continue;
					blockUpdate.add(BlockUpdate.create(world, x + dx - ox, y + dy - oy,
							z + dz - oz, blockData));
				}
			}
		}
		Injector.getService(BlockPasteRunnable.class).add(() -> {
			// paste chest content
			for (StoredChest storedChest : this.getChests()) {
				Location realLocation = storedChest.getLocation()
						.toLocation(location.getWorld())
						.add(dx, dy, dz)
						.subtract(ox, oy, oz);
				var blockState = realLocation.getBlock().getState();
				if (blockState instanceof Chest chest) {
					chest.getBlockInventory().setContents(storedChest.getContent());
				} else if (blockState instanceof BlockInventoryHolder inventoryHolder) {
					inventoryHolder.getInventory().setContents(storedChest.getContent());
				} else {
					Injector.getService(FlowPlugin.class)
							.getLogger()
							.log(Level.SEVERE, "Error loading chest inventory, block is not a chest or a container",
									new IllegalStateException(
											"Error, loaded block inventory was not found in world coord: [real " +
													realLocation + "] [stored " + storedChest.getLocation() + "]"));
				}
			}
			onEnd.run();
		}, blockUpdate);
	}

	@Override
	public GuiItemBuilder item(Player player) {
		return new GuiItemBuilder()
				.name(this.name)
				.icon(Material.STONE_PICKAXE)
				.description(Component.text("Dimension: [" + this.dimensions.getX() + ", " + this.dimensions.getY() + ", "
						+ this.dimensions.getZ() + " ]", NamedTextColor.YELLOW));
	}
}
