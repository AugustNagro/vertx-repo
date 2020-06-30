package com.augustnagro.vertx.repo.tests.pg.repos;

import com.augustnagro.vertx.repo.tests.pg.TestPersonRepoBase;
import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

public class TestPersonRepo extends TestPersonRepoBase {

  public TestPersonRepo(PgPool sql) {
    super(sql);
  }

  public Future<Long> countAfterDeletingGeorge() {
    return sql.withTransaction(con -> con
        .preparedQuery("DELETE FROM test_person WHERE id = $1")
        .execute(Tuple.of(1))
        .flatMap(rs -> count(con)));
  }

}
