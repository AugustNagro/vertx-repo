package com.augustnagro.vertx.repo.tests.pg;

import com.augustnagro.vertx.repo.tests.pg.repos.CarRepo;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class ImmutableRepoTests {

  private PgPool pool;
  private CarRepo carRepo;

  @BeforeEach
  void setup() throws URISyntaxException, ExecutionException, InterruptedException, IOException {
    PgConnectOptions pgConnectOptions = new PgConnectOptions()
        .setUser(System.getProperty("user.name"))
        .setDatabase("test");
    pool = PgPool.pool(Vertx.vertx(), pgConnectOptions, new PoolOptions().setMaxSize(1));
    carRepo = new CarRepo(pool);

    String testSql = Files.readString(Path.of(getClass().getResource("/car.sql").toURI()));
    // since there's no injectable VertxTestContext, need to wait until this future completes..
    pool.query(testSql).execute().toCompletionStage().toCompletableFuture().get();
  }

  @Test
  void count(VertxTestContext ctx) {
    carRepo.count().onComplete(ctx.succeeding(count -> ctx.verify(() -> {
      assertEquals(3, count);
      ctx.completeNow();
    })));
  }
}
