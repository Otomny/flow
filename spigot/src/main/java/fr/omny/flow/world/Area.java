package fr.omny.flow.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.BlockInventoryHolder;

import fr.omny.flow.api.data.Val;
import fr.omny.flow.api.tasks.Dispatcher;
import fr.omny.flow.api.utils.NumberUtils;
import fr.omny.flow.api.utils.tuple.Tuple;
import fr.omny.flow.api.utils.tuple.Tuple2;
import fr.omny.flow.world.schematic.Schematic;
import fr.omny.flow.world.schematic.component.StoredChest;
import fr.omny.flow.world.schematic.component.StoredLocation;
import fr.omny.guis.OClass;
import fr.omny.guis.OField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@OClass
public class Area {

	/**
	 * 
	 */
	public static AreaBuilder center(Location center) {
		return new AreaBuilder(center, center);
	}

	@BsonProperty
	@Val
	@OField
	private Location start;
	@BsonProperty
	@Val
	@OField
	private Location end;

	public Area() {
		this.start = null;
		this.end = null;
	}

	/**
	 *
	 * @param start
	 * @param end
	 */
	public Area(Location start, Location end) {
		if (start.getWorld() != end.getWorld())
			throw new IllegalArgumentException(
					"Start and End are not in the same world");
		this.start = start;
		this.end = end;
	}

	/**
	 * Find all the chunk in that area
	 *
	 * @return
	 */
	@BsonIgnore
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
	@BsonIgnore
	public Map<Tuple2<Integer, Integer>, ChunkSnapshot> getChunksSnapshots() {
		return getChunks()
				.entrySet()
				.stream()
				.map(e -> Map.entry(e.getKey(), e.getValue().getChunkSnapshot()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@BsonIgnore
	public boolean contains(Location location) {

		var minX = Math.min(this.start.getBlockX(), this.end.getBlockX());
		var minY = Math.min(this.start.getBlockY(), this.end.getBlockY());
		var minZ = Math.min(this.start.getBlockZ(), this.end.getBlockZ());

		var maxX = Math.max(this.start.getBlockX(), this.end.getBlockX());
		var maxY = Math.max(this.start.getBlockY(), this.end.getBlockY());
		var maxZ = Math.max(this.start.getBlockZ(), this.end.getBlockZ());

		return location.getWorld().getName().equals(this.start.getWorld().getName()) && location.getX() >= minX
				&& location.getY() >= minY && location.getZ() >= minZ
				&& location.getX() <= maxX && location.getY() <= maxY
				&& location.getZ() <= maxZ;
	}

	@BsonIgnore
	public Schematic createSchematic(Location offset) {
		Objects.requireNonNull(this.start, "Error: Area, this.start is null");
		Objects.requireNonNull(this.end, "Error: Area, this.end is null");
		var minX = Math.min(this.start.getBlockX(), this.end.getBlockX());
		var minY = Math.min(this.start.getBlockY(), this.end.getBlockY());
		var minZ = Math.min(this.start.getBlockZ(), this.end.getBlockZ());

		var maxX = Math.max(this.start.getBlockX(), this.end.getBlockX());
		var maxY = Math.max(this.start.getBlockY(), this.end.getBlockY());
		var maxZ = Math.max(this.start.getBlockZ(), this.end.getBlockZ());

		Location realOffset = offset.clone().subtract(
				new Location(start.getWorld(), minX, minY, minZ));

		int width = Math.abs(maxX - minX) + 1;
		int height = Math.abs(maxY - minY) + 1;
		int length = Math.abs(maxZ - minZ) + 1;

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
					var chunkSnapshot = snapshots.get(Tuple.of(x >> 4, z >> 4));

					String blockAt = chunkSnapshot
							.getBlockData(NumberUtils.mod(x, 16), y,
									NumberUtils.mod(z, 16))
							.getAsString();

					blocks[index] = blockAt;

					var chunk = chunks.get(Tuple.of(x >> 4, z >> 4));
					BlockState state = chunk.getBlock(NumberUtils.mod(x, 16), y, NumberUtils.mod(z, 16))
							.getState();
					if (state instanceof Chest chest) {
						schematic.getChests().add(
								StoredChest.fromTile(chest, xIndex, yIndex, zIndex));
					} else if (state instanceof BlockInventoryHolder holder) {
						schematic.getChests()
								.add(StoredChest.fromTile(holder, index, yIndex, zIndex));
					}
				}
			}
		}

		schematic.setBlocks(blocks);
		schematic.setOffset(StoredLocation.fromWorld(realOffset));

		return schematic;
	}

	@BsonIgnore
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
						var chunkSnapshot = snapshots.get(Tuple.of(x >> 4, z >> 4));

						var blockAt = chunkSnapshot.getBlockData(NumberUtils.mod(x, 16), y,
								NumberUtils.mod(z, 16));
						blockUpdates.add(BlockUpdate.create(world, x, y, z, blockAt));
					}
				}
			}
			return blockUpdates;
		});
	}

	@BsonIgnore
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
					var chunkSnapshot = snapshots.get(Tuple.of(x >> 4, z >> 4));
					// Le Block à la position X, Y , Z

					var blockAt = chunkSnapshot.getBlockData(NumberUtils.mod(x, 16), y,
							NumberUtils.mod(z, 16));
					blockUpdates.add(BlockUpdate.create(world, x, y, z, blockAt));
				}
			}
		}
		return blockUpdates;
	}
}
