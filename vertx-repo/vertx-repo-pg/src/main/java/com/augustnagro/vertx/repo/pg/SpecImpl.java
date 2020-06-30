package com.augustnagro.vertx.repo.pg;

import com.augustnagro.vertx.repo.Spec;
import io.vertx.sqlclient.Tuple;

class SpecImpl<E> implements Spec<E> {
  private final String sql;
  private final Tuple tuple;

  SpecImpl(String sql, Object[] parameters) {
    this.sql = sql;
    tuple = Tuple.wrap(parameters);
  }

  @Override
  public String sql() {
    return sql;
  }

  @Override
  public Tuple tuple() {
    return tuple;
  }
}
