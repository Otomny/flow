package fr.omny.flow.world.serializer;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.block.BlockState;

public interface BlockStateSerializer<T> {
	
	public static final Set<BlockStateSerializer<?>> BLOCK_STATE_SERIALIZERS = new HashSet<>();

	/**
	 * 
	 * @param state
	 * @return
	 */
	boolean filter(BlockState state);

	/**
	 * 
	 * @param blockState
	 * @return
	 */
	String store(T blockState);

	/**
	 * 
	 * @param data
	 * @return
	 */
	T load(String data);

}