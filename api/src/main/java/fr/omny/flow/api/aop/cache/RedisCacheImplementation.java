package fr.omny.flow.api.aop.cache;


import java.lang.reflect.Method;

import org.objectweb.asm.Type;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;

import fr.omny.odi.Autowired;
import fr.omny.odi.caching.Caching;
import fr.omny.odi.caching.CachingImpl;
import lombok.NonNull;

public class RedisCacheImplementation implements CachingImpl {

	@Autowired
	private RedissonClient redissonClient;

	private RLocalCachedMap<Integer, Object> cache;

	@Override
	public boolean contains(int key) {
		return cache.containsKey(key);
	}

	@Override
	public @NonNull Object get(int key) {
		return cache.get(key);
	}

	@Override
	public void put(int key, @NonNull Object result) {
		cache.put(key, result);
	}

	@Override
	public void applySettings(Class<?> forClass, Method forMethod, Caching cacheSettings) {
		String methodDescriptor = Type.getMethodDescriptor(forMethod);
		String cacheName = forClass.getSimpleName() + "#" + methodDescriptor;

		LocalCachedMapOptions<Integer, Object> options = LocalCachedMapOptions.defaults();
		options.cacheSize(cacheSettings.size()).timeToLive(cacheSettings.ttl()).maxIdle(cacheSettings.maxIdleTime());

		RLocalCachedMap<Integer, Object> map = redissonClient.getLocalCachedMap(cacheName, options);
		this.cache = map;
	}

}
