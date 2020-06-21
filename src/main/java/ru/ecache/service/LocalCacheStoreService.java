package ru.ecache.service;

import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Хранит время последнего изменения для каждой таблицы. Ключ - имя таблицы, значение - время изменения.
 */
public class LocalCacheStoreService implements ECacheService<String, Long> {

    private static final String CACHE_SERVICE_NAME = "CacheStoreService";

    /**
     * Время модификации для каждой таблицы
     */
    private final ConcurrentMap<String, Long> tableNameToChangeTime;

    private boolean isClosed;

    public LocalCacheStoreService() {
        this.tableNameToChangeTime = new ConcurrentHashMap<>();
        this.isClosed = false;
    }

    private Long putIfNotPresent(String tableName, Long changeTime) {

        if (changeTime == null) {
            Date date = new Date();
            changeTime = date.getTime();
            this.put(tableName, changeTime);
        }

        return changeTime;
    }

    @Override
    public Long get(String tableName) {

        Long changeTime = tableNameToChangeTime.get(tableName);
        changeTime = putIfNotPresent(tableName, changeTime);

        return changeTime;
    }

    @Override
    public Map<String, Long> getAll(Set<? extends String> tableNames) {

        Map<String, Long> result = new HashMap<>();

        for (String tableName : tableNames) {

            Long changeTime = tableNameToChangeTime.get(tableName);
            changeTime = putIfNotPresent(tableName, changeTime);

            result.put(tableName, changeTime);
        }

        return result;
    }

    @Override
    public boolean containsKey(String tableName) {
        return tableNameToChangeTime.containsKey(tableName);
    }

    @Override
    public void loadAll(Set<? extends String> set, boolean b, CompletionListener completionListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(String tableName, Long changeTime) {
        tableNameToChangeTime.put(tableName, changeTime);
    }

    @Override
    public Long getAndPut(String tableName, Long changeTime) {

        Long previousChangeTime = tableNameToChangeTime.get(tableName);
        this.put(tableName, changeTime);

        return previousChangeTime;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Long> map) {
        tableNameToChangeTime.putAll(map);
    }

    @Override
    public boolean putIfAbsent(String tableName, Long changeTime) {
        Long previousValue = tableNameToChangeTime.putIfAbsent(tableName, changeTime);
        return previousValue == null;
    }

    @Override
    public boolean remove(String tableName) {
        Long previousValue = tableNameToChangeTime.remove(tableName);
        return previousValue != null;
    }

    @Override
    public boolean remove(String tableName, Long changeTime) {
        tableNameToChangeTime.remove(tableName, changeTime);
        if (tableNameToChangeTime.containsKey(tableName) && changeTime.equals(tableNameToChangeTime.get(tableName))) {
            tableNameToChangeTime.remove(tableName);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Long getAndRemove(String tableName) {

        if (tableNameToChangeTime.containsKey(tableName)) {
            Long previousValue = tableNameToChangeTime.get(tableName);
            this.remove(tableName);
            return previousValue;
        } else {
            return null;
        }
    }

    @Override
    public boolean replace(String tableName, Long previousChangeTime, Long newChangeTime) {
        return tableNameToChangeTime.replace(tableName, previousChangeTime, newChangeTime);
    }

    @Override
    public boolean replace(String tableName, Long changeTime) {
        Long previousValue = tableNameToChangeTime.replace(tableName, changeTime);
        return previousValue != null;
    }

    @Override
    public Long getAndReplace(String tableName, Long changeTime) {
        if (tableNameToChangeTime.containsKey(tableName)) {
            Long previousValue = tableNameToChangeTime.get(tableName);
            this.put(tableName, changeTime);
            return previousValue;
        } else {
            return null;
        }
    }

    @Override
    public void removeAll(Set<? extends String> tableNames) {
        for (String tableName : tableNames) {
            this.remove(tableName);
        }
    }

    @Override
    public void removeAll() {
        tableNameToChangeTime.clear();
    }

    @Override
    public void clear() {
        tableNameToChangeTime.clear();
    }

    @Override
    public <C extends Configuration<String, Long>> C getConfiguration(Class<C> aClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invoke(String s, EntryProcessor<String, Long, T> entryProcessor, Object... objects)
            throws EntryProcessorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Map<String, EntryProcessorResult<T>> invokeAll(
            Set<? extends String> set, EntryProcessor<String, Long, T> entryProcessor,
            Object... objects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return CACHE_SERVICE_NAME;
    }

    @Override
    public CacheManager getCacheManager() {
        return null;
    }

    @Override
    public void close() {

        this.isClosed = true;
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
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
