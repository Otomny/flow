package fr.omny.flow.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;

import fr.omny.flow.utils.NumberUtils;
import fr.omny.flow.utils.tuple.Tuple;
import fr.omny.flow.utils.tuple.Tuple2;
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

	public Area(Location start, Location end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * Find all the chunk snapshots in that area
	 * 
	 * @return
	 */
	public Map<Tuple2<Integer, Integer>, ChunkSnapshot> getChunksSnapshots() {
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

		Map<Tuple2<Integer, Integer>, ChunkSnapshot> snapshots = new HashMap<>();

		for (int cx = minChunkX; cx <= maxChunkX; cx++) {
			for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
				var snapshot = this.start.getWorld().getChunkAt(cx, cz)
						.getChunkSnapshot();
				snapshots.put(Tuple.of(cx, cz), snapshot);
			}
		}
		return snapshots;
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
