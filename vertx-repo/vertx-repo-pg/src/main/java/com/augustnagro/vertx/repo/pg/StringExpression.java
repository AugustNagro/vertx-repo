package com.augustnagro.vertx.repo.pg;

public interface StringExpression<E> extends Expression<E, String> {

  static <E> StringExpression<E> of(String columnName) {
    return new WhereClauseHelper(columnName);
  }

  StringExpression<E> concat(StringExpression<E> rhs);

  StringExpression<E> concat(Object rhs);

  Predicate<E> like(String pattern);

  Predicate<E> like(String pattern, char escapeCharacter);

  Predicate<E> notLike(String pattern);

  Predicate<E> notLike(String pattern, char escapeCharacter);

}
