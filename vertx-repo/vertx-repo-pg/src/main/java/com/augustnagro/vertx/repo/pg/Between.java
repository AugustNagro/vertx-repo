package com.augustnagro.vertx.repo.pg;

/**
 * A partial BETWEEN clause, that can be completed to a Predicate with
 * {@link #and(Object)} or {@link #and(Expression)}.
 * @param <E> Entity
 * @param <T> Column
 */
public interface Between<E, T> extends SqlBuilder {

  /**
   * Complete this BETWEEN clause.
   * @param y right-hand side of the BETWEEN's AND clause
   */
  Predicate<E> and(Expression<E, T> y);

  /**
   * Complete this BETWEEN clause.
   * @param y right-hand side of the BETWEEN's AND clause
   */
  Predicate<E> and(T y);
}
