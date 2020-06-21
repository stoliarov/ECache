package ru.ecache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ECache {

    /**
     * Имена таблиц, используемых при формировании ответа для данного запроса.
     */
    String[] tables() default {};


    /**
     * JPA-сущности, используемые при формировании ответа для данного запроса.
     */
    Class[] entities() default {};
}
