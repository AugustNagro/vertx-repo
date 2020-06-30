package com.augustnagro.vertx.repo;

import io.vertx.sqlclient.Tuple;

/**
 * A Spec is the result of type-safe SQL builder, and may be passed
 * to {@link ImmutableRepo#findAll(Spec)}. The functionality mirrors
 * Spring Data's Specification classes.
 * <br>
 * All Specs are executed as {@link io.vertx.sqlclient.PreparedQuery}s,
 * since it's expected that most Specs will have parameters.
 */
public interface Spec<E> {

  /**
   * The generated SQL, beginning from the WHERE clause. Parameters
   * must be formatted in the Vertx style and starting with one, like '$1'.
   * The parameters must match up with the {@link #tuple()} ordering.
   */
  String sql();

  /**
   * Tuple of this Spec's parameters.
   */
  Tuple tuple();
}
