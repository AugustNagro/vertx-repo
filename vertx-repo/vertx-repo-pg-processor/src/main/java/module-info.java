import com.augustnagro.vertx.repo.pg.processor.PgProcessor;

import javax.annotation.processing.Processor;

module vertx.repo.pg.processor {
  requires java.compiler;
  requires com.augustnagro.vertx.repo;

  provides Processor with PgProcessor;
}