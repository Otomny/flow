package fr.omny.flow.world.serializer;

import org.bukkit.block.BlockState;
import org.bukkit.inventory.BlockInventoryHolder;

public class ContainerSerializer implements BlockStateSerializer<BlockInventoryHolder>{

	@Override
	public boolean filter(BlockState state) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'filter'");
	}

	@Override
	public String store(BlockInventoryHolder blockState) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'store'");
	}

	@Override
	public BlockInventoryHolder load(String data) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'load'");
	}
	
}
