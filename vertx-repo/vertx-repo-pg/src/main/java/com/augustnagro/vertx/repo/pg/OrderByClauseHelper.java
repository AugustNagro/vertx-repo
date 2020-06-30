package com.augustnagro.vertx.repo.pg;

class OrderByClauseHelper implements Sort, Sort.Ascending, Sort.Descending {
  
  final String sql;
  final Expression sortExpression;
  final Predicate seekPredicate;
  final boolean ascending;
  final boolean defaultNullHandling;
  
  OrderByClauseHelper(Expression sortExpression, boolean ascending) {
    sql = sortExpression.sql() + (ascending ? " ASC" : " DESC");
    this.sortExpression = sortExpression;
    seekPredicate = null;
    this.ascending = ascending;
    defaultNullHandling = true;
  }

  OrderByClauseHelper(String sql, Expression sortExpression, Predicate seekPredicate,
                      boolean ascending, boolean defaultNullHandling) {
    this.sql = sql;
    this.sortExpression = sortExpression;
    this.seekPredicate = seekPredicate;
    this.ascending = ascending;
    this.defaultNullHandling = defaultNullHandling;
  }

  @Override
  public String sql() {
    return sql;
  }

  @Override
  public Object[] params() {
    return sortExpression.params();
  }

  @Override
  public int paramCount() {
    return sortExpression.paramCount();
  }

  @Override
  public Predicate seekPredicate() {
    return seekPredicate;
  }

  @Override
  public Ascending nullsFirst() {
    if (!defaultNullHandling) return this;
    String newSql = sql + " NULLS FIRST";
    return new OrderByClauseHelper(newSql, sortExpression, seekPredicate, ascending, false);
  }

  @Override
  public Descending nullsLast() {
    if (!defaultNullHandling) return this;
    String newSql = sql + " NULLS LAST";
    return new OrderByClauseHelper(newSql, sortExpression, seekPredicate, ascending, false);
  }

  @Override
  public Sort seekGreaterThan(Object value) {
    Predicate newSeekPredicate = sortExpression.greaterThan(value);
    return new OrderByClauseHelper(sql, sortExpression, newSeekPredicate, ascending, defaultNullHandling);
  }

  @Override
  public Sort seekLessThan(Object value) {
    Predicate newSeekPredicate = sortExpression.lessThan(value);
    return new OrderByClauseHelper(sql, sortExpression, newSeekPredicate, ascending, defaultNullHandling);
  }
}
