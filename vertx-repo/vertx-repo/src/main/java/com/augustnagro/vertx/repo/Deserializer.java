package com.augustnagro.vertx.repo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Determines the constructor to be used for building
 * this entity from ResultSets. This annotation is
 * required if there are more than one constructor,
 * and there may only be one @Deserializer annotation
 * on a given entity, otherwise the chosen constructor
 * is indeterminate.
 */
@Documented
@Target(ElementType.CONSTRUCTOR)
public @interface Deserializer {
}
