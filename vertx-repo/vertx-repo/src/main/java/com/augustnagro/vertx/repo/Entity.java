package com.augustnagro.vertx.repo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicates that this type is an Entity, for
 * which vertx-repo will construct a {@link Repo}
 * implementation on the next compile.
 * <br>
 * <br>
 * An Entity type needs at least one public constructor.
 * If the entity has multiple constructors, then one
 * must be annotated with {@link Deserializer}. In either
 * case, the target constructor must also have one parameter
 * annotated with {@link Id}, which determines the entity's
 * primary key.
 * <br>
 * <br>
 * This generated class will be created in
 * target/generated-sources/annotations, with the same
 * package as its entity, and has convenience methods
 * for common crud operations. It is recommended that
 * you subclass these Repos in a repos/ package,
 * so that additional methods can be added for that
 * entity type. This also provides the benefit of
 * consolidating all of your sql queries into one place.
 */
@Documented
@Target(ElementType.TYPE)
public @interface Entity {
}
