package fr.omny.flow.translation;

import fr.omny.flow.data.RedisRepository;
import fr.omny.flow.data.Repository;

@Repository
public interface TranslationRepository extends RedisRepository<Translation, String>{
	
}
