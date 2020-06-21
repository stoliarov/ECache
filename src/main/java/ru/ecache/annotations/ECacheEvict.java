package ru.ecache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ECacheEvict {

    /**
     * Имена таблиц для которых должен быть сброшен кэш.
     */
    String[] tables() default {};

    /**
     * JPA-сущности, для которых должен быть сброшен кэш.
     */
    Class[] entities() default {};
}