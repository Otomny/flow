package fr.omny.flow.world.providers;

import fr.omny.flow.world.BlockUpdate;
import fr.omny.flow.world.pasting.BlockBatch;

/**
 * 
 */
public interface BlockApplyProvider {

	/**
	 * Called when a block is updated
	 * @param blockUpdate
	 */
	void blockPaste(BlockUpdate blockUpdate);

	/**
	 * Called when a batch of block as ended pasting
	 * @param batch
	 */
	void endBlockPaste(BlockBatch batch);

}
