package com.augustnagro.vertx.repo.tests.pg;

import com.augustnagro.vertx.repo.Entity;
import com.augustnagro.vertx.repo.Id;

import java.time.OffsetDateTime;

@Entity
public record TestPerson(String firstName, String lastName, @Id Long id, Boolean isAdmin, OffsetDateTime created) {
}