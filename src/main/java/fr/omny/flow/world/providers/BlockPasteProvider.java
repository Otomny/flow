package fr.omny.flow.world.providers;


import org.bukkit.Bukkit;
import org.bukkit.World;

import fr.omny.flow.world.BlockUpdate;
import fr.omny.flow.world.pasting.BlockBatch;
import fr.omny.odi.Component;

/**
 * Dumbest and slowest implementation of all item GG!
 */
@Component
public class BlockPasteProvider implements BlockApplyProvider {

	@Override
	public void blockPaste(BlockUpdate blockUpdate) {
		World world = Bukkit.getWorld(blockUpdate.getWorld());
		var blockPosition = blockUpdate.getBlockPosition();
		world.setBlockData(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ(),
				Bukkit.createBlockData(blockUpdate.getNewBlockData()));
	}

	@Override
	public void endBlockPaste(BlockBatch batch) {
		// Nothing
	}

}
