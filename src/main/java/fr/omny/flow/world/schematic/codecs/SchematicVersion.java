package fr.omny.flow.world.schematic.codecs;

import fr.omny.flow.world.schematic.Schematic;

public interface SchematicVersion {
	
	/**
	 * 
	 * @return
	 */
	int getVersion();

	/**
	 * 
	 * @param data
	 * @return
	 */
	Schematic load(byte[] data);

	/**
	 * 
	 * @param data
	 * @return
	 */
	byte[] save(Schematic data);

}
