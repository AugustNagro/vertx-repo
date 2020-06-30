package com.augustnagro.vertx.repo.pg;

/**
 * Sort qualifier that may be used in {@link SpecBuilder#orderBy(Sort)}.
 * <br>
 * <a href="https://blog.jooq.org/2016/08/10/why-most-programmers-get-pagination-wrong/">Seek Pagination</a>
 * is simply better than offset-based pagination; Sort provides {@link #seekGreaterThan(Object)}
 * and {@link #seekLessThan(Object)} for this purpose. {@link SpecBuilder#offset(int)} is available
 * for offset-based pagination.
 * @param <E> Entity type
 * @param <T> Column type
 */
public interface Sort<E, T> extends SqlBuilder {

  /**
   * Seek predicate to be used in the where clause. May be null.
   */
  Predicate<E> seekPredicate();

  /**
   * Returns a Sort with {@link #seekPredicate()} set
   */
  Sort<E, T> seekGreaterThan(T value);

  /**
   * Returns a Sort with {@link #seekPredicate()} set
   */
  Sort<E, T> seekLessThan(T value);

  /**
   * Ascending variant of Sort. By default, nulls appear last.
   * @param <E> Entity type
   * @param <T> Column type
   */
  interface Ascending<E, T> extends Sort<E, T> {

    /**
     * Make nulls appear first
     */
    Ascending<E, T> nullsFirst();
  }

  /**
   * Descending variant of Sort. By default, nulls appear first.
   * @param <E> Entity type
   * @param <T> Column type
   */
  interface Descending<E, T> extends Sort<E, T> {

    /**
     * Make nulls appear last
     */
    Descending<E, T> nullsLast();
  }
}
