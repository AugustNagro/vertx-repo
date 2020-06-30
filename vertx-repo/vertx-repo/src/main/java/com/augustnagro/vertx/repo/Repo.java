package com.augustnagro.vertx.repo;

import io.vertx.core.Future;
import io.vertx.sqlclient.SqlClient;

import java.util.Collection;
import java.util.List;

/**
 * Implementation of common CRUD operations for an {@link Entity}
 * @param <E> Entity
 * @param <ID> Entity's Primary Key, annotated with {@link Id}
 */
public interface Repo<E, ID> extends ImmutableRepo<E, ID> {

  /**
   * Delete this Entity
   */
  Future<Void> delete(E entity);

  /**
   * Delete this Entity
   */
  Future<Void> delete(SqlClient sql, E entity);

  /**
   * Delete all Entities. Be careful!
   */
  Future<Void> deleteAll();

  /**
   * Delete all Entities. Be careful!
   */
  Future<Void> deleteAll(SqlClient sql);

  /**
   * Delete all entities in the Collection
   */
  Future<Void> deleteAll(Collection<E> entities);

  /**
   * Delete all entities in the Collection
   */
  Future<Void> deleteAll(SqlClient sql, Collection<E> entities);

  /**
   * Delete all entities with an ID in the collection
   */
  Future<Void> deleteAllById(Collection<ID> ids);

  /**
   * Delete all entities with an ID in the collection
   */
  Future<Void> deleteAllById(SqlClient sql, Collection<ID> ids);

  /**
   * Save this entity. If the Entity's Id is null, it will be
   * inserted. Otherwise, it will be either updated or inserted
   * (upserted).
   */
  Future<E> save(E entity);

  /**
   * Save this entity. If the Entity's Id is null, it will be
   * inserted. Otherwise, it will be either updated or inserted
   * (upserted).
   */
  Future<E> save(SqlClient sql, E entity);

  /**
   * Save all Entities in the Collection. If an Entity's Id is null,
   * it will be inserted. Otherwise, it will be either updated or inserted
   * (upserted).
   */
  Future<List<E>> saveAll(Collection<E> entities);

  /**
   * Save all Entities in the Collection. If an Entity's Id is null,
   * it will be inserted. Otherwise, it will be either updated or inserted
   * (upserted).
   */
  Future<List<E>> saveAll(SqlClient sql, Collection<E> entities);

}
