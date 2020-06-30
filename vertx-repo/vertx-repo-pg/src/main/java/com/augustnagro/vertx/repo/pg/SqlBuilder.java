package com.augustnagro.vertx.repo.pg;

/**
 * Base interface used for building {@link com.augustnagro.vertx.repo.Spec}s.
 * Implementations of this interface should be immutable.
 */
public interface SqlBuilder {

  /**
   * This builder's SQL
   */
  String sql();

  /**
   * This builder's parameters. The array returned may or may not be a copy, and thus
   * MUST NOT be modified.
   */
  Object[] params();

  /**
   * Shortcut for getting the number of parameters.
   */
  int paramCount();
}
