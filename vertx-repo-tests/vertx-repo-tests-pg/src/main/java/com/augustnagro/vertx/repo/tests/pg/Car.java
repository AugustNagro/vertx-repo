package com.augustnagro.vertx.repo.tests.pg;

import com.augustnagro.vertx.repo.Id;
import com.augustnagro.vertx.repo.ImmutableEntity;

@ImmutableEntity
public record Car(@Id Long id, String model, Integer topSpeed) {

}
