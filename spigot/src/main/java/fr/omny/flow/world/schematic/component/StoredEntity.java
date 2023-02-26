package fr.omny.flow.world.schematic.component;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import lombok.Getter;

@Getter
public class StoredEntity {

	public static StoredEntity fromEntity(Entity entity) {
		if (entity instanceof Player)
			throw new IllegalArgumentException("Can't serialize player");
		throw new UnsupportedOperationException("fromEntity not implemented");
	}

	public static StoredEntity fromIO(DataInputStream inputStream) {
		var location = StoredLocation.fromIO(inputStream);
		throw new UnsupportedOperationException("fromIO not implemented");

	}

	private StoredLocation location;

	private StoredEntity() {
	}

	public void storeIO(DataOutputStream outputStream) {
		location.storeIO(outputStream);
		throw new UnsupportedOperationException("storeIO not implemented");
	}

}
