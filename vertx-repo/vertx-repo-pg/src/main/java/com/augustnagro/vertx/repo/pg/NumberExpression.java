package com.augustnagro.vertx.repo.pg;

/**
 * An {@link Expression} of type {@link Number}.
 * @param <E> Entity type
 */
public interface NumberExpression<E> extends Expression<E, Number> {

  /**
   * Build a NumberExpression for a column name, whose {@link SqlBuilder#params()} is an
   * empty array, and {@link SqlBuilder#sql()} is the Entity's column name.
   * @param columnName Entity's column name
   * @param <E> Entity
   * @param <T> Column type
   */
  static <E> NumberExpression<E> of(String columnName) {
    return new WhereClauseHelper(columnName);
  }

  /**
   * Add rhs to this
   * @see <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  NumberExpression<E> plus(NumberExpression<E> rhs);

  /**
   * Add rhs to this
   * @see <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  NumberExpression<E> plus(Number rhs);

  /**
   * Subtract rhs from this
   * @see <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  NumberExpression<E> minus(NumberExpression<E> rhs);

  /**
   * Subtract rhs from this
   * @see <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  NumberExpression<E> minus(Number rhs);

  /**
   * Multiply this by rhs
   * @see <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  NumberExpression<E> times(NumberExpression<E> rhs);

  /**
   * Multiply this by rhs
   * @see <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  NumberExpression<E> times(Number rhs);

  /**
   * Divide this by rhs
   * @see <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  NumberExpression<E> dividedBy(NumberExpression<E> rhs);

  /**
   * Divide this by rhs
   * @see <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  NumberExpression<E> dividedBy(Number rhs);

  /**
   * Returns this modulo rhs
   * @see <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  NumberExpression<E> mod(NumberExpression<E> rhs);

  /**
   * Returns this modulo rhs
   * @see <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  NumberExpression<E> mod(Number rhs);

  /**
   * Returns this to the power of rhs (ie, this ^ rhs)
   * @see <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  NumberExpression<E> pow(NumberExpression<E> rhs);

  /**
   * Returns this to the power of rhs (ie, this ^ rhs)
   * @see <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  NumberExpression<E> pow(Number rhs);

}
