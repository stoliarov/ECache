package ru.ecache.service;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class EEntitiesCacheStore {

    /**
     * JPA-сущности, учитываемые при обработке кэша. Key - полное имя класа. Value - Class.
     */
    private final Map<String, Class<?>> entities;

    public EEntitiesCacheStore() {
        entities = new HashMap<>();
    }

    public void addEntity(Class<?> entity) {

        if (entity == null) {
            return;
        }

        String entityClassName = entity.getName();

        if (StringUtils.isBlank(entityClassName)) {
            return;
        }

        entities.put(entityClassName, entity);
    }

    public boolean isCacheableEntity(Class<?> entity) {

        if (entity == null) {
            return false;
        }

        String entityClassName = entity.getName();

        if (StringUtils.isBlank(entityClassName)) {
            return false;
        }

        return entities.containsKey(entityClassName);
    }
}
