package com.augustnagro.vertx.repo.tests.pg;

import com.augustnagro.vertx.repo.Deserializer;
import com.augustnagro.vertx.repo.Projection;

@Projection
public class WindsurfTypeCd {
  private final String cd, descr;

  public WindsurfTypeCd(String cd) {
    this(cd, null);
  }

  @Deserializer
  public WindsurfTypeCd(String cd, String descr) {
    this.cd = cd;
    this.descr = descr;
  }

  public String cd() {
    return cd;
  }

  public String descr() {
    return descr;
  }
}
