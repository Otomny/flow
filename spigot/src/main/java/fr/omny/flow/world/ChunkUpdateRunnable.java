package fr.omny.flow.world;

import fr.omny.flow.api.config.Config;
import fr.omny.flow.tasks.RunnableConfig;
import fr.omny.flow.world.providers.BlockApplyProvider;
import fr.omny.odi.Autowired;

@RunnableConfig(delay = 60L, period = 1L)
public class ChunkUpdateRunnable implements Runnable{

	@Config("world.pasting.chunk_update_per_tick")
	private int chunksUpdatePerTick;
	@Autowired
	private BlockApplyProvider provider;

	@Override
	public void run() {
		
	}
	
}
