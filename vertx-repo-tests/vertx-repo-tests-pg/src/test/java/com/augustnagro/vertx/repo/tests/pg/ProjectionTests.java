package com.augustnagro.vertx.repo.tests.pg;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import org.junit.jupiter.api.Assertions;
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
class ProjectionTests {

  private PgPool pool;

  @BeforeEach
  void setup() throws URISyntaxException, ExecutionException, InterruptedException, IOException {
    PgConnectOptions pgConnectOptions = new PgConnectOptions()
        .setUser(System.getProperty("user.name"))
        .setDatabase("test");
    pool = PgPool.pool(Vertx.vertx(), pgConnectOptions, new PoolOptions().setMaxSize(1));

    String testSql = Files.readString(Path.of(getClass().getResource("/windsurf_type_cd.sql").toURI()));
    // since there's no injectable VertxTestContext, need to wait until this future completes..
    pool.query(testSql).execute().toCompletionStage().toCompletableFuture().get();
  }

  @Test
  void testBuild(VertxTestContext ctx) {
    pool.preparedQuery("SELECT * FROM windsurf_type_cd ORDER BY cd")
        .execute()
        .map(WindsurfTypeCdProjection::build)
        .onComplete(ctx.succeeding(windsurfTypes -> ctx.verify(() -> {
          assertEquals(5, windsurfTypes.size());
          assertEquals("COURSE", windsurfTypes.get(0).cd());
          ctx.completeNow();
        })));
  }
}
