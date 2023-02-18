package fr.omny.flow.aop.cache;


import java.lang.reflect.Method;

import fr.omny.flow.utils.Cache;
import fr.omny.flow.utils.FlowCache;
import fr.omny.odi.caching.Caching;
import fr.omny.odi.caching.CachingImpl;
import lombok.NonNull;

public class FlowCacheImplementation implements CachingImpl {

	private Cache<Integer, Object> cache;

	@Override
	public void applySettings(Class<?> forClass, Method forMethod, Caching cacheSettings) {
		long ttl = cacheSettings.ttl();
		long maxIdle = cacheSettings.maxIdleTime();
		long size = cacheSettings.size();
		this.cache = new FlowCache<>((int) size, ttl, maxIdle);
	}

	@Override
	public boolean contains(int key) {
		return this.cache.get(key).isPresent();
	}

	@Override
	public @NonNull Object get(int key) {
		return this.cache.get(key).get();
	}

	@Override
	public void put(int key, @NonNull Object result) {
		this.cache.set(key, result);
	}

}
