package ru.ecache.service;

import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Хранит id последнего изменения для каждой таблицы. Ключ - имя таблицы, значение - id изменения.
 * Использует Redis в качестве хранилища.
 */
@NoArgsConstructor
public class RedisCacheStoreService implements ECacheService<String, Long> {

    private static final String CACHE_SERVICE_NAME = "RedisCacheStoreService";

    private static final int DELTA = 1;

    private RedisTemplate<String, Long> redisTemplate;

    private boolean isClosed;

    public RedisCacheStoreService(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.isClosed = false;
    }

    private Long getWithInit(String tableName) {

        Long changeId = redisTemplate.boundValueOps(tableName).get();

        if (changeId == null) {
            changeId = redisTemplate.boundValueOps(tableName).increment(DELTA);
        }

        return changeId;
    }

    @Override
    public Long get(String tableName) {
        return getWithInit(tableName);
    }

    @Override
    public Map<String, Long> getAll(Set<? extends String> tableNames) {

        Map<String, Long> result = new HashMap<>();

        for (String tableName : tableNames) {

            Long changeId = getWithInit(tableName);
            result.put(tableName, changeId);
        }

        return result;
    }

    @Override
    public boolean containsKey(String tableName) {
        return redisTemplate.boundValueOps(tableName).get() != null;
    }

    @Override
    public void loadAll(Set<? extends String> set, boolean b, CompletionListener completionListener) {
        throw new UnsupportedOperationException();
    }

    /**
     * Инкрементирует id изменения указанной таблицы.
     *
     * @param tableName имя таблицы
     * @param aLong     ignored
     */
    @Override
    public void put(String tableName, Long aLong) {
        redisTemplate.boundValueOps(tableName).increment(DELTA);
    }

    @Override
    public Long getAndPut(String s, Long aLong) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Long> map) {

    }

    @Override
    public boolean putIfAbsent(String s, Long aLong) {
        return false;
    }

    @Override
    public boolean remove(String s) {
        return false;
    }

    @Override
    public boolean remove(String s, Long aLong) {
        return false;
    }

    @Override
    public Long getAndRemove(String s) {
        return null;
    }

    @Override
    public boolean replace(String s, Long aLong, Long v1) {
        return false;
    }

    @Override
    public boolean replace(String s, Long aLong) {
        return false;
    }

    @Override
    public Long getAndReplace(String s, Long aLong) {
        return null;
    }

    @Override
    public void removeAll(Set<? extends String> set) {

    }

    @Override
    public void removeAll() {

    }

    @Override
    public void clear() {

    }

    @Override
    public <C extends Configuration<String, Long>> C getConfiguration(Class<C> aClass) {
        return null;
    }

    @Override
    public <T> T invoke(String s, EntryProcessor<String, Long, T> entryProcessor, Object... objects)
            throws EntryProcessorException {
        return null;
    }

    @Override
    public <T> Map<String, EntryProcessorResult<T>> invokeAll(Set<? extends String> set,
                                                              EntryProcessor<String, Long, T> entryProcessor,
                                                              Object... objects) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public CacheManager getCacheManager() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return null;
    }

    @Override
    public void registerCacheEntryListener(
            CacheEntryListenerConfiguration<String, Long> cacheEntryListenerConfiguration) {

    }

    @Override
    public void deregisterCacheEntryListener(
            CacheEntryListenerConfiguration<String, Long> cacheEntryListenerConfiguration) {

    }

    @Override
    public Iterator<Entry<String, Long>> iterator() {
        return null;
    }
}
