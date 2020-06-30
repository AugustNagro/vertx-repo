package com.augustnagro.vertx.repo.pg;

import com.augustnagro.vertx.repo.Spec;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builder class for {@link Spec}. This class is not thread-safe.
 * @param <E> Entity type
 */
public class SpecBuilder<E> {
  private static final Pattern PARAM_PLACEHOLDER_PATTERN =
      Pattern.compile(Expression.PARAM_PLACEHOLDER, Pattern.LITERAL);

  private final ArrayList<Predicate<E>> predicates = new ArrayList<>();
  private final ArrayList<Sort<E, ?>> sorts = new ArrayList<>();
  private int totalParams = 0;
  private Integer limit = null;
  private Integer offset = null;

  /**
   * Add a new Predicate to the Spec
   * @return this
   */
  public SpecBuilder<E> where(Predicate<E> p) {
    predicates.add(p);
    totalParams += p.paramCount();
    return this;
  }

  /**
   * Add a new Sort to the Spec. If {@link Sort#seekPredicate()} is not null, it is added
   * to the builder as well.
   * @return this
   */
  public SpecBuilder<E> orderBy(Sort<E, ?> sort) {
    sorts.add(sort);
    totalParams += sort.paramCount();
    Predicate<E> seekPredicate = sort.seekPredicate();
    if (seekPredicate != null) where(seekPredicate);
    return this;
  }

  /**
   * Limit the number of results
   * @return this
   */
  public SpecBuilder<E> limit(int limit) {
    this.limit = limit;
    return this;
  }

  /**
   * Offset method. You should consider using <a href="https://blog.jooq.org/2016/08/10/why-most-programmers-get-pagination-wrong/">Seek Pagination</a>
   * instead.
   * @return this
   */
  public SpecBuilder<E> offset(int offset) {
    this.offset = offset;
    return this;
  }

  /**
   * Builds the Spec
   */
  public Spec<E> build() {
    Object[] allParams = new Object[totalParams];
    int insertPos = 0;

    StringJoiner whereClause = new StringJoiner(" AND ", "WHERE ", "").setEmptyValue("");
    for (Predicate<E> p : predicates) {
      whereClause.add(p.sql());
      Object[] params = p.params();
      System.arraycopy(params, 0, allParams, insertPos, params.length);
      insertPos += params.length;
    }

    StringJoiner orderByClause = new StringJoiner(", ", " ORDER BY ", "").setEmptyValue("");
    for (Sort<E, ?> sort : sorts) {
      orderByClause.add(sort.sql());
      Object[] sortParams = sort.params();
      System.arraycopy(sortParams, 0, allParams, insertPos, sortParams.length);
      insertPos += sortParams.length;
    }

    String sql = whereClause.toString() + orderByClause.toString();

    if (limit != null) {
      sql += " LIMIT " + limit;
    }

    if (offset != null) {
      sql += " OFFSET " + offset;
    }

    // replace parameter placeholders with proper values
    Matcher m = PARAM_PLACEHOLDER_PATTERN.matcher(sql);
    StringBuilder sb = new StringBuilder();
    int p = 1;
    while (m.find()) {
      m.appendReplacement(sb, "\\$" + p);
      ++p;
    }
    m.appendTail(sb);

    return new SpecImpl<>(sb.toString(), allParams);
  }
}
