package fr.omny.flow.translation;

import fr.omny.flow.api.data.RedisRepository;
import fr.omny.flow.api.data.Repository;

@Repository
public interface TranslationRepository extends RedisRepository<Translation, String>{
	
}
