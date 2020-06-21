package ru.ecache.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ECacheName {

    LOCAL_CACHE("LocalCache"),

    REDIS_CACHE("RedisCache");

    private final String name;
}
