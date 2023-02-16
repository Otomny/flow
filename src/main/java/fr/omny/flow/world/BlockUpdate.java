package fr.omny.flow.world;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.omny.flow.utils.tuple.Tuple;
import fr.omny.flow.utils.tuple.Tuple2;
import fr.omny.flow.utils.tuple.Tuple3;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 
 */
@Getter
@AllArgsConstructor
public class BlockUpdate {

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
	private String newBlockData;
	private Material newType;

	/**
	 * @param newBlockData the newBlockData to set
	 */
	public void setNewBlockData(String newBlockData) {
		this.newBlockData = newBlockData;
	}

	/**
	 * @param newType the newType to set
	 */
	public void setNewType(Material newType) {
		this.newType = newType;
	}

}
