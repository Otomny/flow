package fr.omny.flow.world.schematic.codecs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Material;

import fr.omny.flow.api.utils.IOUtils;
import fr.omny.flow.world.schematic.Schematic;
import fr.omny.flow.world.schematic.component.StoredChest;
import fr.omny.flow.world.schematic.component.StoredLocation;

public class SchematicV1 implements SchematicVersion {

	public static final int RLE = 1;
	public static final int MATRIX = 2;

	@Override
	public int getVersion() {
		return 1;
	}

	@Override
	public Schematic load(byte[] data) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
			DataInputStream dataInputStream = new DataInputStream(new GZIPInputStream(inputStream));

			int width = IOUtils.readVarInt(dataInputStream);
			int height = IOUtils.readVarInt(dataInputStream);
			int length = IOUtils.readVarInt(dataInputStream);

			int materialCount = IOUtils.readVarInt(dataInputStream);
			List<String> blockDatas = new ArrayList<>();

			for (int i = 0; i < materialCount; i++) {
				blockDatas.add(dataInputStream.readUTF());
			}

			String[] matrix = new String[width * height * length];
			Arrays.fill(matrix, "minecraft:air");

			String toAssign = null;
			int skipTo = -1;
			int currentMode = -1;

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					for (int z = 0; z < length; z++) {
						int index = y * length * width + x * length + z;
						if (toAssign != null) {
							if (index < skipTo) {
								matrix[index] = toAssign;
								continue;
							} else {
								toAssign = null;
							}
						}
						int rawDataValue = IOUtils.readVarInt(dataInputStream);
						if (isMode(rawDataValue, blockDatas)) {
							if (mode(rawDataValue, blockDatas) == RLE) {
								// RLE
								int materialIndex = IOUtils.readVarInt(dataInputStream);
								int count = IOUtils.readVarInt(dataInputStream);
								skipTo = index + count;
								toAssign = blockDatas.get(materialIndex);
								matrix[index] = toAssign;
								currentMode = RLE;
							} else {
								int materialIndex = IOUtils.readVarInt(dataInputStream);
								matrix[index] = blockDatas.get(materialIndex);
								currentMode = MATRIX;
							}
						} else {
							if (currentMode == MATRIX) {
								matrix[index] = blockDatas.get(rawDataValue);
							} else if (currentMode == RLE) {
								int materialIndex = rawDataValue;
								int count = IOUtils.readVarInt(dataInputStream);
								skipTo = index + count;
								toAssign = blockDatas.get(materialIndex);
								matrix[index] = toAssign;
							}
						}

					}
				}
			}

			StoredLocation offset = StoredLocation.fromIO(dataInputStream);

			// chests contents
			int chestCount = IOUtils.readVarInt(dataInputStream);
			List<StoredChest> chests = new ArrayList<>();
			for (int i = 0; i < chestCount; i++) {
				StoredChest chest = StoredChest.fromIO(dataInputStream);
				chests.add(chest);
			}

			dataInputStream.close();
			Schematic schematic = new Schematic();
			schematic.setDimensions(width, height, length);
			schematic.setBlocks(matrix);
			schematic.setOffset(offset);
			schematic.setChests(chests);

			return schematic;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] save(Schematic data) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(32);
			DataOutputStream dataOutputStream = new DataOutputStream(new GZIPOutputStream(byteArrayOutputStream));
			// Dimensions
			IOUtils.writeVarInt(dataOutputStream, data.getWidth());
			IOUtils.writeVarInt(dataOutputStream, data.getHeight());
			IOUtils.writeVarInt(dataOutputStream, data.getLength());

			// blocks palette
			var uniqueBlockData = Stream.of(data.getBlocks()).distinct().toList();
			// Block count inside the palette
			IOUtils.writeVarInt(dataOutputStream, uniqueBlockData.size());

			// we write all the materials with UTF
			uniqueBlockData.forEach(material -> {
				try {
					dataOutputStream.writeUTF(material);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			// we generate the markers
			var markers = getMarkers(data, 3);
			int previousMode = -1;

			int skipTo = -1;
			for (int y = 0; y < data.getHeight(); y++) {
				for (int x = 0; x < data.getWidth(); x++) {
					for (int z = 0; z < data.getLength(); z++) {
						int index = y * data.getLength() * data.getWidth() + x * data.getLength() + z;
						int materialIndex = uniqueBlockData.indexOf(data.getBlocks()[index]);
						if (index < skipTo)
							continue;
						if (markers.containsKey(index)) {
							// index is marked as a differents mode (RLE/MATRIX)
							// we need to check
							MarkerData markerData = markers.get(index);
							if (markerData.mode == MATRIX) {
								if (previousMode != MATRIX) {
									IOUtils.writeVarInt(dataOutputStream, uniqueBlockData.size() + MATRIX);
									previousMode = MATRIX;
								}
								IOUtils.writeVarInt(dataOutputStream, materialIndex);
							} else if (markerData.mode == RLE) {
								if (previousMode != RLE) {
									IOUtils.writeVarInt(dataOutputStream, uniqueBlockData.size() + RLE);
									previousMode = RLE;
								}
								IOUtils.writeVarInt(dataOutputStream, materialIndex);
								IOUtils.writeVarInt(dataOutputStream, markerData.count);
								skipTo = index + markerData.count;
							}
						} else {
							IOUtils.writeVarInt(dataOutputStream, materialIndex);
						}
					}
				}
			}

			data.getOffset().storeIO(dataOutputStream);

			int chestCount = data.getChests().size();
			IOUtils.writeVarInt(dataOutputStream, chestCount);
			for (StoredChest chest : data.getChests()) {
				chest.storeIO(dataOutputStream);
			}

			dataOutputStream.close();

			byte[] dataArray = byteArrayOutputStream.toByteArray();
			return dataArray;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new byte[] {};
	}

	protected <E> int mode(int result, List<E> materials) {
		return result - materials.size();
	}

	protected <E> boolean isMode(int result, List<E> materials) {
		return mode(result, materials) == MATRIX || mode(result, materials) == RLE;
	}

	private Map<Integer, MarkerData> getMarkers(Schematic schematic, int maxInARow) {
		Map<Integer, MarkerData> markers = new HashMap<>();

		int height = schematic.getHeight();
		int width = schematic.getWidth();
		int length = schematic.getLength();

		String previousMaterial = null;
		int previousCount = 0;
		int previousIndex = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				for (int z = 0; z < length; z++) {
					int index = y * length * width + x * length + z;
					String material = schematic.getBlocks()[index];

					if (previousMaterial == null || !previousMaterial.equalsIgnoreCase(material)) {
						if (previousCount > maxInARow) {
							markers.put(previousIndex, new MarkerData(RLE, previousCount, previousMaterial));

							markers.put(index, new MarkerData(MATRIX));
						} else if (previousMaterial == null) {
							markers.put(index, new MarkerData(MATRIX));
						}
						previousMaterial = material;
						previousCount = 1;
						previousIndex = index;
					} else {
						previousCount++;
						if (index == (height * width * length) - 1) {
							if (previousCount > maxInARow) {
								markers.put(previousIndex, new MarkerData(RLE, previousCount, previousMaterial));
							}
						}
					}
				}
			}
		}

		return markers;
	}

	public record MarkerData(int mode, int count, String material) {
		public MarkerData(int mode, int count) {
			this(mode, count, "minecraft:stone");
		}

		public MarkerData(int mode) {
			this(mode, 0);
		}

		public Material materialRaw() {
			return Material.valueOf(this.material.split("\\:")[1].toUpperCase());
		}
	}

}
