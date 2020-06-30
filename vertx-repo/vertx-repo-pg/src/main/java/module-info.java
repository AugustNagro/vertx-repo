module com.augustnagro.vertx.repo.pg {
  requires transitive com.augustnagro.vertx.repo;
  requires transitive io.vertx.client.sql.pg;

  exports com.augustnagro.vertx.repo.pg;
}