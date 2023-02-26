package fr.omny.flow.world;

import java.util.function.Function;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import fr.omny.flow.api.utils.tuple.Tuple;
import fr.omny.flow.api.utils.tuple.Tuple2;
import fr.omny.flow.api.utils.tuple.Tuple3;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 
 */
@Getter
@AllArgsConstructor
public class BlockUpdate {

	public static Function<BlockUpdate, BlockUpdate> setType(Material type) {
		return blockUpdate -> {
			blockUpdate.setNewType(type);
			return blockUpdate;
		};
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	public static Predicate<BlockUpdate> filter(Material type) {
		return blockUpdate -> blockUpdate.getType() == type;
	}

	/**
	 * 
	 * @return
	 */
	public static Predicate<BlockUpdate> excludeAir() {
		return blockUpdate -> blockUpdate.getType() != Material.AIR;
	}

	/**
	 * 
	 * @param block
	 * @param newMaterial
	 * @return
	 */
	public static BlockUpdate create(Block block) {
		String world = block.getWorld().getName();
		var blockPosition = Tuple.of(block.getX(), block.getY(), block.getZ());
		var chunkPosition = Tuple.of(block.getChunk().getX(), block.getChunk().getZ());
		String newBlockData = block.getBlockData().getAsString();
		return new BlockUpdate(world, blockPosition, chunkPosition, newBlockData, block.getType());
	}

	/**
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param blockAt
	 * @return
	 */
	public static BlockUpdate create(String world, int x, int y, int z, BlockData blockAt) {
		var blockPosition = Tuple.of(x, y, z);
		var chunkPosition = Tuple.of(x >> 4, z >> 4);
		String newBlockData = blockAt.getAsString();
		return new BlockUpdate(world, blockPosition, chunkPosition, newBlockData, blockAt.getMaterial());
	}

	/**
	 * 
	 * @param block
	 * @param newMaterial
	 * @return
	 */
	public static BlockUpdate create(Block block, Material newMaterial) {
		String world = block.getWorld().getName();
		var blockPosition = Tuple.of(block.getX(), block.getY(), block.getZ());
		var chunkPosition = Tuple.of(block.getChunk().getX(), block.getChunk().getZ());
		String newBlockData = Bukkit.createBlockData(newMaterial).toString();
		return new BlockUpdate(world, blockPosition, chunkPosition, newBlockData, newMaterial);
	}

	private String world;
	private Tuple3<Integer, Integer, Integer> blockPosition;
	private Tuple2<Integer, Integer> chunkPosition;
	private String blockData;
	private Material type;

	/**
	 * @param newBlockData the newBlockData to set
	 */
	public void setNewBlockData(String newBlockData) {
		this.blockData = newBlockData;
	}

	/**
	 * @param newType the newType to set
	 */
	public void setNewType(Material newType) {
		this.type = newType;
		this.blockData = "minecraft:" + newType.toString().toLowerCase();
	}

}
