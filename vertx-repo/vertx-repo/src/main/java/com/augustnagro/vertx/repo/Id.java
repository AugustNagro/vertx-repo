package com.augustnagro.vertx.repo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * An {@link Entity}'s primary key. One and only one
 * of the target constructor's parameters must be
 * annotated with this.
 */
@Documented
@Target(ElementType.PARAMETER)
public @interface Id {
}
