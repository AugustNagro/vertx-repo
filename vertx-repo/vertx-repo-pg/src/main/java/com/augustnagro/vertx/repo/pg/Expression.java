package com.augustnagro.vertx.repo.pg;

/**
 * An Expression over an Entity's column
 * @param <E> Entity Type
 * @param <T> Column Type
 */
public interface Expression<E, T> extends SqlBuilder {

  /**
   * Placeholder used for the param number (ie, '$2'), since the exact
   * number will not be known until calling {@link SpecBuilder#build()}.
   */
  String PARAM_PLACEHOLDER = "$!";

  /**
   * Build an expression for a column name, whose {@link SqlBuilder#params()} is an
   * empty array, and {@link SqlBuilder#sql()} is the Entity's column name.
   * @param columnName Entity's column name
   * @param <E> Entity
   * @param <T> Column type
   */
  static <E, T> Expression<E, T> of(String columnName) {
    return new WhereClauseHelper(columnName);
  }

  /**
   * Predicate where this < rhs.
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> lessThan(Expression<E, T> rhs);

  /**
   * Predicate where this < rhs.
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> lessThan(T rhs);

  /**
   * Predicate where this > rhs.
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> greaterThan(Expression<E, T> rhs);

  /**
   * Predicate where this > rhs.
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> greaterThan(T rhs);

  /**
   * Predicate where this <= rhs.
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> lessThanOrEq(Expression<E, T> rhs);

  /**
   * Predicate where this <= rhs
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> lessThanOrEq(T rhs);

  /**
   * Predicate where this >= rhs
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> greaterThanOrEq(Expression<E, T> rhs);

  /**
   * Predicate where this >= rhs
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> greaterThanOrEq(T rhs);

  /**
   * Predicate where this = rhs
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> eq(Expression<E, T> rhs);

  /**
   * Predicate where this = rhs
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> eq(T rhs);

  /**
   * Predicate where this != rhs
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> notEq(Expression<E, T> rhs);

  /**
   * Predicate where this != rhs
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> notEq(T rhs);

  /**
   * Create a Between clause where (this BETWEEN x AND ...)
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Between<E, T> between(Expression<E, T> x);

  /**
   * Create a Between clause where (this BETWEEN x AND ...)
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Between<E, T> between(T x);

  /**
   * Create a Between clause where (this BETWEEN symmetric x AND ...)
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Between<E, T> betweenSymmetric(Expression<E, T> x);

  /**
   * Create a Between clause where (this BETWEEN symmetric x AND ...)
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Between<E, T> betweenSymmetric(T x);

  /**
   * Create a Between clause where (this NOT BETWEEN SYMMETRIC x AND ...)
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Between<E, T> notBetweenSymmetric(Expression<E, T> x);

  /**
   * Create a Between clause where (this NOT BETWEEN SYMMETRIC x AND ...)
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Between<E, T> notBetweenSymmetric(Object x);

  /**
   * Predicate where this IS DISTINCT FROM rhs
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> isDistinctFrom(Expression<E, T> rhs);

  /**
   * Predicate where this IS DISTINCT FROM rhs
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> isDistinctFrom(Object rhs);

  /**
   * Predicate where this IS NOT DISTINCT FROM rhs
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> isNotDistinctFrom(Expression<E, T> rhs);

  /**
   * Predicate where this IS NOT DISTINCT FROM rhs
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> isNotDistinctFrom(Object rhs);

  /**
   * Predicate where this IS NULL
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> isNull();

  /**
   * Predicate where this IS NOT NULL
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> isNotNull();

  /**
   * Make this a Ascending sort.
   */
  Sort.Ascending<E, T> asc();

  /**
   * Make this a Descending sort.
   */
  Sort.Descending<E, T> desc();

}
