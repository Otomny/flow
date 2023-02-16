package fr.omny.flow.world;


import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.omny.flow.config.Config;
import fr.omny.flow.tasks.RunnableConfig;
import fr.omny.flow.world.pasting.BlockBatch;
import fr.omny.flow.world.providers.BlockApplyProvider;
import fr.omny.odi.Autowired;

@RunnableConfig(delay = 60L, period = 1L)
public class BlockPasteRunnable implements Runnable {

	private Queue<BlockBatch> blockBatchs = new ConcurrentLinkedQueue<>();
	@Config("world.pasting.blocks_per_tick")
	private int blockPerTicks;
	@Autowired
	private BlockApplyProvider provider;

	public BlockPasteRunnable() {}

	/**
	 * Update a block type
	 * 
	 * @param block The block
	 * @param newType The desired type
	 */
	public void update(Block block, Material newType) {
		add(BlockUpdate.create(block, newType));
	}

	/**
	 * Add a block update to the queue
	 * 
	 * @param blockUpdate
	 */
	public void add(BlockUpdate blockUpdate) {
		add(List.of(blockUpdate));
	}

	/**
	 * add a list of block updates to the queue
	 * 
	 * @param blockUpdates
	 */
	public void add(List<BlockUpdate> blockUpdates) {
		add(() -> {
		}, blockUpdates);
	}

	/**
	 * Add a batch to the queue
	 * <p>
	 * The list is copied in case it's immutable
	 * </p>
	 * 
	 * @param onEnd        Runnable called when the batch is fully pasted
	 * @param blockUpdates The list
	 */
	public void add(Runnable onEnd, List<BlockUpdate> blockUpdates) {
		add(new BlockBatch(onEnd, new ArrayList<>(blockUpdates)));
	}

	/**
	 * Add a batch to the queue
	 * 
	 * @param blockBatch BlockBatch
	 */
	public void add(BlockBatch blockBatch) {
		this.blockBatchs.add(blockBatch);
	}

	@Override
	public void run() {
		if (blockBatchs.isEmpty())
			return;
		int currentLooped = 0;
		BlockBatch blockBatch = this.blockBatchs.peek();
		var blockUpdates = blockBatch.getBlockUpdate();
		var iterator = blockUpdates.iterator();
		while (iterator.hasNext()) {
			var blockUpdate = iterator.next();
			provider.blockPaste(blockUpdate);
			iterator.remove();
			currentLooped++;
			if (currentLooped > this.blockPerTicks) {
				return;
			}
		}
		provider.endBlockPaste(blockBatch);
		this.blockBatchs.remove();
	}

}
