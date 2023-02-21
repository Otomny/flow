package fr.omny.flow.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;

import fr.omny.flow.tasks.Dispatcher;
import fr.omny.flow.utils.NumberUtils;
import fr.omny.flow.utils.tuple.Tuple;
import fr.omny.flow.utils.tuple.Tuple2;
import fr.omny.flow.world.schematic.Schematic;
import fr.omny.guis.OClass;
import fr.omny.guis.OField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@OClass
public class Area {

	@OField
	private Location start;
	@OField
	private Location end;

	/**
	 * 
	 * @param start
	 * @param end
	 */
	public Area(Location start, Location end) {
		if (start.getWorld() != end.getWorld())
			throw new IllegalArgumentException("Start and End are not in the same world");
		this.start = start;
		this.end = end;
	}

	/**
	 * Find all the chunk in that area
	 * 
	 * @return
	 */
	public Map<Tuple2<Integer, Integer>, Chunk> getChunks() {
		Objects.requireNonNull(this.start, "Error: Area, this.start is null");
		Objects.requireNonNull(this.end, "Error: Area, this.end is null");

		var minX = Math.min(this.start.getBlockX(), this.end.getBlockX());
		var minZ = Math.min(this.start.getBlockZ(), this.end.getBlockZ());
		var maxX = Math.max(this.start.getBlockX(), this.end.getBlockX());
		var maxZ = Math.max(this.start.getBlockZ(), this.end.getBlockZ());

		// load chunk snapshots
		var minChunkX = minX >> 4;
		var minChunkZ = minZ >> 4;
		var maxChunkX = maxX >> 4;
		var maxChunkZ = maxZ >> 4;

		Map<Tuple2<Integer, Integer>, Chunk> chunks = new HashMap<>();

		for (int cx = minChunkX; cx <= maxChunkX; cx++) {
			for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {

				var chunk = this.start.getWorld().getChunkAt(cx, cz);
				chunks.put(Tuple.of(cx, cz), chunk);
			}
		}
		return chunks;
	}

	/**
	 * Find all the chunk snapshots in that area
	 * 
	 * @return
	 */
	public Map<Tuple2<Integer, Integer>, ChunkSnapshot> getChunksSnapshots() {
		return getChunks().entrySet()
				.stream()
				.map(e -> Map.entry(e.getKey(), e.getValue().getChunkSnapshot()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public Schematic createSchematic() {
		Objects.requireNonNull(this.start, "Error: Area, this.start is null");
		Objects.requireNonNull(this.end, "Error: Area, this.end is null");
		var minX = Math.min(this.start.getBlockX(), this.end.getBlockX());
		var minZ = Math.min(this.start.getBlockZ(), this.end.getBlockZ());
		var minY = Math.min(this.start.getBlockY(), this.end.getBlockY());
		var maxX = Math.max(this.start.getBlockX(), this.end.getBlockX());
		var maxZ = Math.max(this.start.getBlockZ(), this.end.getBlockZ());
		var maxY = Math.max(this.start.getBlockY(), this.end.getBlockY());
		// World world = this.start.getWorld();

		int width = maxX - minX;
		int height = maxY - minY;
		int length = maxZ - minZ;

		Schematic schematic = new Schematic();
		schematic.setDimensions(width, height, length);
		String[] blocks = new String[width * height * length];

		var chunks = getChunks();
		var snapshots = chunks.entrySet()
				.stream()
				.map(e -> Map.entry(e.getKey(), e.getValue().getChunkSnapshot()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		// load blocks
		for (int y = maxY; y >= minY; y--) {
			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					int yIndex = y - minY;
					int xIndex = x - minX;
					int zIndex = z - minZ;
					int index = yIndex * width * length + xIndex * length + zIndex;
					var chunkSnapshot = snapshots
							.get(Tuple.of(x >> 4, z >> 4));

					String blockAt = chunkSnapshot.getBlockData(
							NumberUtils.mod(x, 16), y, NumberUtils.mod(z, 16)).getAsString();

					blocks[index] = blockAt;
				}
			}
		}

		// load tile entities
		// Set<Block> treated = new HashSet<>();
		// for (Chunk chunk : chunks.values()) {
		// for (BlockState tile : chunk.getTileEntities()) {
		// Block block = tile.getBlock();
		// if(treated.contains(block))
		// continue;
		// treated.add(block);
		// throw new UnsupportedOperationException("Serializing tile entity is not
		// implemented");
		// }
		// }

		schematic.setBlocks(blocks);

		return schematic;
	}

	public CompletableFuture<List<BlockUpdate>> getBlockUpdate(Dispatcher dispatcher) {
		Objects.requireNonNull(this.start, "Error: Area, this.start is null");
		Objects.requireNonNull(this.end, "Error: Area, this.end is null");

		var minX = Math.min(this.start.getBlockX(), this.end.getBlockX());
		var minZ = Math.min(this.start.getBlockZ(), this.end.getBlockZ());
		var minY = Math.min(this.start.getBlockY(), this.end.getBlockY());
		var maxX = Math.max(this.start.getBlockX(), this.end.getBlockX());
		var maxZ = Math.max(this.start.getBlockZ(), this.end.getBlockZ());
		var maxY = Math.max(this.start.getBlockY(), this.end.getBlockY());
		var world = this.start.getWorld().getName();
		var snapshots = getChunksSnapshots();

		return dispatcher.submit(() -> {
			List<BlockUpdate> blockUpdates = new ArrayList<>();

			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					for (int y = maxY; y >= minY; y--) {
						var chunkSnapshot = snapshots
								.get(Tuple.of(x >> 4, z >> 4));

						var blockAt = chunkSnapshot.getBlockData(
								NumberUtils.mod(x, 16), y, NumberUtils.mod(z, 16));
						blockUpdates.add(BlockUpdate.create(world, x, y, z, blockAt));
					}
				}
			}
			return blockUpdates;
		});
	}

	public List<BlockUpdate> getBlockUpdate() {
		Objects.requireNonNull(this.start, "Error: Area, this.start is null");
		Objects.requireNonNull(this.end, "Error: Area, this.end is null");

		var minX = Math.min(this.start.getBlockX(), this.end.getBlockX());
		var minZ = Math.min(this.start.getBlockZ(), this.end.getBlockZ());
		var minY = Math.min(this.start.getBlockY(), this.end.getBlockY());
		var maxX = Math.max(this.start.getBlockX(), this.end.getBlockX());
		var maxZ = Math.max(this.start.getBlockZ(), this.end.getBlockZ());
		var maxY = Math.max(this.start.getBlockY(), this.end.getBlockY());

		var world = this.start.getWorld().getName();

		var snapshots = getChunksSnapshots();
		List<BlockUpdate> blockUpdates = new ArrayList<>();

		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				for (int y = maxY; y >= minY; y--) {
					var chunkSnapshot = snapshots
							.get(Tuple.of(x >> 4, z >> 4));
					// Le Block à la position X, Y , Z

					var blockAt = chunkSnapshot.getBlockData(
							NumberUtils.mod(x, 16), y, NumberUtils.mod(z, 16));
					blockUpdates.add(BlockUpdate.create(world, x, y, z, blockAt));
				}
			}
		}
		return blockUpdates;
	}

}
