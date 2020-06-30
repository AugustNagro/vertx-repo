package com.augustnagro.vertx.repo.tests.pg;

import com.augustnagro.vertx.repo.tests.pg.repos.TestPersonRepo;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

public class Main extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    PgConnectOptions pgConnectOptions = new PgConnectOptions()
        .setHost("localhost")
        .setPort(5432)
        .setUser("august")
        .setDatabase("test");
    PoolOptions poolOptions = new PoolOptions();
    PgPool pool = PgPool.pool(vertx, pgConnectOptions, poolOptions);

    HttpServerOptions options = new HttpServerOptions()
        .setPort(8080)
        .setHost("localhost");
    vertx.createHttpServer(options).requestHandler(req -> {
      HttpServerResponse response = req.response();
      pool.query("select count(*) from test_person").execute()
          .map(rs -> rs.toString())
          .onSuccess(count -> response.end(count.toString()))
          .onFailure(t -> {
            t.printStackTrace(System.err);
            response.end("errro");
          });
    }).listen(ar -> {
      if (ar.succeeded()) System.out.println("started");
      else System.out.println("failed");
    });

  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Main());
  }
}
