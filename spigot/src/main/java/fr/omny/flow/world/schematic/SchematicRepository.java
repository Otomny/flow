package fr.omny.flow.world.schematic;

import fr.omny.flow.api.data.MongoRepository;
import fr.omny.flow.api.data.Repository;

@Repository
public interface SchematicRepository extends MongoRepository<Schematic, String> {

}
