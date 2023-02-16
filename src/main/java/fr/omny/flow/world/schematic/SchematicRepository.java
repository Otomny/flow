package fr.omny.flow.world.schematic;

import java.util.UUID;

import fr.omny.flow.data.MongoRepository;

public interface SchematicRepository extends MongoRepository<Schematic, UUID>{
	
}
