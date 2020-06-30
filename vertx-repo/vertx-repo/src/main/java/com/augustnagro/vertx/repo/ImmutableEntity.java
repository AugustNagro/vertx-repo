package com.augustnagro.vertx.repo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Generates an abstract {@link ImmutableRepo} for this type, with all read-only CRUD operations.
 */
@Documented
@Target(ElementType.TYPE)
public @interface ImmutableEntity {
}
