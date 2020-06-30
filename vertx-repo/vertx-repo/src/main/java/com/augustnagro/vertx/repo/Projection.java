package com.augustnagro.vertx.repo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Creates a utility Projection class on the next
 * compile for the annotated type. This class,
 * located in target/generated-sources/annotations
 * will be given the same package name as the annotated
 * type, and provide static methods to build instances
 * from a vertx RowSet.
 * <br>
 * The annotated type must have either one public
 * constructor, or one constructor annotated with
 * {@link Deserializer}.
 */
@Documented
@Target(ElementType.TYPE)
public @interface Projection {
}
