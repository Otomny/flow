package fr.omny.flow.world.schematic.codecs;

import fr.omny.flow.world.schematic.Schematic;

public class SchematicV1 implements SchematicVersion{

	@Override
	public int getVersion() {
		return 1;
	}

	@Override
	public Schematic load(byte[] data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] save(Schematic data) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
