package ru.ecache.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import ru.ecache.model.ECacheName;
import ru.ecache.properties.ECacheProperties;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

@AllArgsConstructor
public class ECacheManager implements CacheManager {

    private final ECacheProperties eCacheProperties;

    @Override
    public <K, V> ECacheService<K, V> getCache(String cacheName) {

        if (ECacheName.LOCAL_CACHE.getName().equals(cacheName)) {

            return (ECacheService<K, V>) new LocalCacheStoreService();

        } else if (ECacheName.REDIS_CACHE.getName().equals(cacheName)) {

            RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
            JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
            redisTemplate.setConnectionFactory(jedisConnectionFactory);

            int intRedisPort;
            try {
                intRedisPort = Integer.parseInt(eCacheProperties.getRedisPort());
            } catch (Exception e) {
                return null;
            }

            String redisHost = eCacheProperties.getRedisHost();

            if (StringUtils.isEmpty(redisHost)) {
                return null;
            }

            jedisConnectionFactory.getStandaloneConfiguration().setHostName(redisHost);
            jedisConnectionFactory.getStandaloneConfiguration().setPort(intRedisPort);

            redisTemplate.setDefaultSerializer(new GenericToStringSerializer<>(Long.class));

            redisTemplate.afterPropertiesSet();

            return (ECacheService<K, V>) new RedisCacheStoreService(redisTemplate);
        }

        return null;
    }

    @Override
    public Iterable<String> getCacheNames() {
        return null;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType) {
        return null;
    }

    @Override
    public CachingProvider getCachingProvider() {
        return null;
    }

    @Override
    public URI getURI() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }

    @Override
    public Properties getProperties() {
        return null;
    }

    @Override
    public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(String cacheName, C configuration)
            throws IllegalArgumentException {
        return null;
    }

    @Override
    public void destroyCache(String cacheName) {

    }

    @Override
    public void enableManagement(String cacheName, boolean enabled) {

    }

    @Override
    public void enableStatistics(String cacheName, boolean enabled) {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        return null;
    }
}
