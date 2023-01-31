package fr.omny.flow.data.implementation;

import java.util.Optional;

import fr.omny.flow.data.RedisRepository;

public final class RedissonRepository<T, ID> implements RedisRepository<T, ID>{

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(T entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll(Iterable<? extends T> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteById(ID id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAllById(Iterable<? extends ID> ids) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean existsById(ID id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<T> findById(ID id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Iterable<T> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<T> findAllById(Iterable<ID> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends T> boolean save(S entity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <S extends T> boolean saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
