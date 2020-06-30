package com.augustnagro.vertx.repo.pg;

/**
 * A Boolean Expression
 * @param <E> Entity type
 */
public interface Predicate<E> extends Expression<E, Boolean> {

  /**
   * Build a Predicate for a Boolean column, whose {@link SqlBuilder#params()} is an
   * empty array, and {@link SqlBuilder#sql()} is the Entity's column name.
   * @param columnName Entity's column name
   * @param <E> Entity
   * @param <T> Column type
   */
  static <E> Predicate<E> of(String columnName) {
    return new WhereClauseHelper(columnName);
  }

  /**
   * Returns true if this is true
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> isTrue();

  /**
   * Returns true if this is false or unknown
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> isNotTrue();

  /**
   * Returns true if this is false
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> isFalse();

  /**
   * Returns true if this is true or unknown
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> isNotFalse();

  /**
   * Returns true if this is unknown
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> isUnknown();

  /**
   * Returns true if this is true or false
   * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html">https://www.postgresql.org/docs/current/functions-comparison.html</a>
   */
  Predicate<E> isNotUnknown();
}
