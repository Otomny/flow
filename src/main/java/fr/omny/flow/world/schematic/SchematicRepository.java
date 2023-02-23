package fr.omny.flow.world.schematic;

import java.util.UUID;

import fr.omny.flow.data.MongoRepository;
import fr.omny.flow.data.Repository;

@Repository
public interface SchematicRepository extends MongoRepository<Schematic, UUID> {

}
