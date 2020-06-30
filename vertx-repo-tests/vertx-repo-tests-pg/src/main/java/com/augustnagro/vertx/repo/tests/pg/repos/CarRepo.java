package com.augustnagro.vertx.repo.tests.pg.repos;

import com.augustnagro.vertx.repo.tests.pg.CarRepoBase;
import io.vertx.pgclient.PgPool;

public class CarRepo extends CarRepoBase {

  public CarRepo(PgPool sql) {
    super(sql);
  }
}
