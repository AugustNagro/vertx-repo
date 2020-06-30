package com.augustnagro.vertx.repo;

import io.vertx.core.Future;
import io.vertx.sqlclient.SqlClient;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Helper for querying an {@link ImmutableEntity}.
 * @param <E> Entity
 * @param <ID> Entity's Primary Key, annotated with {@link Id}
 */
public interface ImmutableRepo<E, ID> {

  /**
   * Count of Entities.
   */
  Future<Long> count();

  /**
   * Count of Entities
   */
  Future<Long> count(SqlClient sql);

  /**
   * True if an Entity with id exists.
   */
  Future<Boolean> existsById(ID id);

  /**
   * True if an Entity with id exists.
   */
  Future<Boolean> existsById(SqlClient sql, ID id);

  /**
   * Select all Entities
   */
  Future<List<E>> findAll();

  /**
   * Select all Entities
   */
  Future<List<E>> findAll(SqlClient sql);

  /**
   * Select all Entities conforming the Spec
   */
  Future<List<E>> findAll(Spec<E> spec);

  /**
   * Select all Entities conforming the Spec
   */
  Future<List<E>> findAll(SqlClient sql, Spec<E> spec);

  /**
   * Select an Entity by Id.
   */
  Future<Optional<E>> findById(ID id);

  /**
   * Select an Entity by Id.
   */
  Future<Optional<E>> findById(SqlClient sql, ID id);

  /**
   * Select all Entities with Ids in the collection.
   * <br>
   * <br>
   * The ordering is not guaranteed, and null will not
   * be inserted for invalid Ids.
   */
  Future<List<E>> findAllById(Collection<ID> ids);

  /**
   * Select all Entities with Ids in the collection.
   * <br>
   * <br>
   * The ordering is not guaranteed, and null will not
   * be inserted for invalid Ids.
   */
  Future<List<E>> findAllById(SqlClient sql, Collection<ID> ids);

}
