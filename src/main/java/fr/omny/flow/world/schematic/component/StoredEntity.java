package fr.omny.flow.world.schematic.component;

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

	private StoredLocation location;

	private StoredEntity() {
	}

}
