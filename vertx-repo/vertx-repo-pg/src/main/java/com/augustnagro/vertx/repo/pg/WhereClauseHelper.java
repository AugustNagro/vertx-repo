package com.augustnagro.vertx.repo.pg;

import java.util.Arrays;
import java.util.StringJoiner;

class WhereClauseHelper implements SqlBuilder, Expression, NumberExpression, StringExpression, Predicate, Between {

  static final String LESS_THAN = " < ";
  static final String GREATER_THAN = " > ";
  static final String LESS_THAN_OR_EQ = " <= ";
  static final String GREATER_THAN_OR_EQ = " >= ";
  static final String EQ = " = ";
  static final String NOT_EQ = " <> ";
  static final String BETWEEN = " BETWEEN ";
  static final String BETWEEN_SYMMETRIC = " BETWEEN SYMMETRIC ";
  static final String NOT_BETWEEN_SYMMETRIC = " NOT BETWEEN SYMMETRIC ";
  static final String AND = " AND ";
  static final String IS_DISTINCT_FROM = " IS DISTINCT FROM ";
  static final String IS_NOT_DISTINCT_FROM = " IS NOT DISTINCT FROM ";
  static final String IS_NULL = " IS NULL";
  static final String IS_NOT_NULL = " IS NOT NULL";
  static final String IS_TRUE = " IS TRUE";
  static final String IS_NOT_TRUE = " IS NOT TRUE";
  static final String IS_FALSE = " IS FALSE";
  static final String IS_NOT_FALSE = " IS NOT FALSE";
  static final String IS_UNKNOWN = " IS UNKNOWN";
  static final String IS_NOT_UNKNOWN = " IS NOT UNKNOWN";
  static final String PLUS = " + ";
  static final String MINUS = " - ";
  static final String TIMES = " * ";
  static final String DIVIDED_BY = " / ";
  static final String MOD = " % ";
  static final String POW = " ^ ";
  static final String LIKE = " LIKE ";
  static final String NOT_LIKE = " NOT LIKE ";
  // functions
  static final String ABS = "ABS";
  static final String CEIL = "CEIL";
  static final String FLOOR = "FLOOR";
  static final String LN = "LN";
  static final String LOG = "LOG";
  static final String ROUND = "ROUND";
  static final String SIGN = "SIGN";
  static final String SQRT = "SQRT";
  static final String TRUNC = "TRUNC";
  static final String CHAR_LENGTH = "CHAR_LENGTH";
  static final String LOWER = "LOWER";
  static final String UPPER = "UPPER";
  static final String TRIM = "TRIM";
  static final String CONCAT = "CONCAT";
  static final String CONCAT_WS = "CONCAT_WS";
  static final String LENGTH = "LENGTH";
  static final String STARTS_WITH = "STARTS_WITH";
  static final String CURRENT_DATE = "CURRENT_DATE";
  static final String CURRENT_TIME = "CURRENT_TIME";
  static final String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";
  static final String EXTRACT = "EXTRACT";
  static final String DATE_TRUNC = "DATE_TRUNC";
  static final String NOW = "NOW";
  static final String COALESCE = "COALESCE";

  final String sql;
  final Object[] params;

  WhereClauseHelper(String columnOrNoArgFunction) {
    sql = columnOrNoArgFunction;
    params = new Object[0];
  }

  WhereClauseHelper(String sql, Object[] params) {
    this.sql = sql;
    this.params = params;
  }

  @Override
  public String sql() {
    return sql;
  }

  @Override
  public Object[] params() {
    return params;
  }

  @Override
  public int paramCount() {
    return params.length;
  }

  @Override
  public Predicate lessThan(Expression rhs) {
    return binaryInfixExp(LESS_THAN, this, rhs);
  }

  @Override
  public Predicate lessThan(Object rhs) {
    return binaryInfixExp(LESS_THAN, this, rhs);
  }

  @Override
  public Predicate greaterThan(Expression rhs) {
    return binaryInfixExp(GREATER_THAN, this, rhs);
  }

  @Override
  public Predicate greaterThan(Object rhs) {
    return binaryInfixExp(GREATER_THAN, this, rhs);
  }

  @Override
  public Predicate lessThanOrEq(Expression rhs) {
    return binaryInfixExp(LESS_THAN_OR_EQ, this, rhs);
  }

  @Override
  public Predicate lessThanOrEq(Object rhs) {
    return binaryInfixExp(LESS_THAN_OR_EQ, this, rhs);
  }

  @Override
  public Predicate greaterThanOrEq(Expression rhs) {
    return binaryInfixExp(GREATER_THAN_OR_EQ, this, rhs);
  }

  @Override
  public Predicate greaterThanOrEq(Object rhs) {
    return binaryInfixExp(GREATER_THAN_OR_EQ, this, rhs);
  }

  @Override
  public Predicate eq(Expression rhs) {
    return binaryInfixExp(EQ, this, rhs);
  }

  @Override
  public Predicate eq(Object rhs) {
    return binaryInfixExp(EQ, this, rhs);
  }

  @Override
  public Predicate notEq(Expression rhs) {
    return binaryInfixExp(NOT_EQ, this, rhs);
  }

  @Override
  public Predicate notEq(Object rhs) {
    return binaryInfixExp(NOT_EQ, this, rhs);
  }

  @Override
  public Between between(Expression x) {
    return betweenPart1(BETWEEN, this, x);
  }

  @Override
  public Between between(Object x) {
    return betweenPart1(BETWEEN, this, x);
  }

  @Override
  public Between betweenSymmetric(Expression x) {
    return betweenPart1(BETWEEN_SYMMETRIC, this, x);
  }

  @Override
  public Between betweenSymmetric(Object x) {
    return betweenPart1(BETWEEN_SYMMETRIC, this, x);
  }

  @Override
  public Between notBetweenSymmetric(Expression x) {
    return betweenPart1(NOT_BETWEEN_SYMMETRIC, this, x);
  }

  @Override
  public Between notBetweenSymmetric(Object x) {
    return betweenPart1(NOT_BETWEEN_SYMMETRIC, this, x);
  }

  @Override
  public Predicate and(Expression y) {
    String newSql = sql + AND + y.sql() + ")";
    return new WhereClauseHelper(newSql, mergeParams(params, y.params()));
  }

  @Override
  public Predicate and(Object y) {
    String newSql = sql + AND + PARAM_PLACEHOLDER + ")";
    return new WhereClauseHelper(newSql, mergeParams(params, y));
  }

  @Override
  public Predicate isDistinctFrom(Expression rhs) {
    return binaryInfixExp(IS_DISTINCT_FROM, this, rhs);
  }

  @Override
  public Predicate isDistinctFrom(Object rhs) {
    return binaryInfixExp(IS_DISTINCT_FROM, this, rhs);
  }

  @Override
  public Predicate isNotDistinctFrom(Expression rhs) {
    return binaryInfixExp(IS_NOT_DISTINCT_FROM, this, rhs);
  }

  @Override
  public Predicate isNotDistinctFrom(Object rhs) {
    return binaryInfixExp(IS_NOT_DISTINCT_FROM, this, rhs);
  }

  @Override
  public Predicate isNull() {
    return rightInfix(IS_NULL, this);
  }

  @Override
  public Predicate isNotNull() {
    return rightInfix(IS_NOT_NULL, this);
  }

  @Override
  public Predicate isTrue() {
    return rightInfix(IS_TRUE, this);
  }

  @Override
  public Predicate isNotTrue() {
    return rightInfix(IS_NOT_TRUE, this);
  }

  @Override
  public Predicate isFalse() {
    return rightInfix(IS_FALSE, this);
  }

  @Override
  public Predicate isNotFalse() {
    return rightInfix(IS_NOT_FALSE, this);
  }

  @Override
  public Predicate isUnknown() {
    return rightInfix(IS_UNKNOWN, this);
  }

  @Override
  public Predicate isNotUnknown() {
    return rightInfix(IS_NOT_UNKNOWN, this);
  }

  @Override
  public NumberExpression plus(NumberExpression rhs) {
    return binaryInfixExp(PLUS, this, rhs);
  }

  @Override
  public NumberExpression plus(Number rhs) {
    return binaryInfixExp(PLUS, this, rhs);
  }

  @Override
  public NumberExpression minus(NumberExpression rhs) {
    return binaryInfixExp(MINUS, this, rhs);
  }

  @Override
  public NumberExpression minus(Number rhs) {
    return binaryInfixExp(MINUS, this, rhs);
  }

  @Override
  public NumberExpression times(NumberExpression rhs) {
    return binaryInfixExp(TIMES, this, rhs);
  }

  @Override
  public NumberExpression times(Number rhs) {
    return binaryInfixExp(TIMES, this, rhs);
  }

  @Override
  public NumberExpression dividedBy(NumberExpression rhs) {
    return binaryInfixExp(DIVIDED_BY, this, rhs);
  }

  @Override
  public NumberExpression dividedBy(Number rhs) {
    return binaryInfixExp(DIVIDED_BY, this, rhs);
  }

  @Override
  public NumberExpression mod(NumberExpression rhs) {
    return binaryInfixExp(MOD, this, rhs);
  }

  @Override
  public NumberExpression mod(Number rhs) {
    return binaryInfixExp(MOD, this, rhs);
  }

  @Override
  public NumberExpression pow(NumberExpression rhs) {
    return binaryInfixExp(POW, this, rhs);
  }

  @Override
  public NumberExpression pow(Number rhs) {
    return binaryInfixExp(POW, this, rhs);
  }

  @Override
  public StringExpression concat(StringExpression rhs) {
    return function(CONCAT, this, rhs);
  }

  @Override
  public StringExpression concat(Object rhs) {
    return function(CONCAT, this, rhs);
  }

  @Override
  public Predicate like(String pattern) {
    return likePredicate(LIKE, this, pattern, null);
  }

  @Override
  public Predicate like(String pattern, char escapeCharacter) {
    return likePredicate(LIKE, this, pattern, escapeCharacter);
  }

  @Override
  public Predicate notLike(String pattern) {
    return likePredicate(NOT_LIKE, this, pattern, null);
  }

  @Override
  public Predicate notLike(String pattern, char escapeCharacter) {
    return likePredicate(NOT_LIKE, this, pattern, escapeCharacter);
  }

  @Override
  public Sort.Ascending asc() {
    return new OrderByClauseHelper(this, true);
  }

  @Override
  public Sort.Descending desc() {
    return new OrderByClauseHelper(this, false);
  }

  static WhereClauseHelper likePredicate(String function, SqlBuilder string, String pattern, Character escapeCharacter) {
    String sql = "(" + string.sql() + function + PARAM_PLACEHOLDER;
    if (escapeCharacter != null) {
      sql += " ESCAPE '" + escapeCharacter + "'";
    }
    sql += ")";
    return new WhereClauseHelper(sql, mergeParams(string.params(), pattern));
  }

  static WhereClauseHelper binaryInfixExp(String function, SqlBuilder lhs, SqlBuilder rhs) {
    String sql = "(" + lhs.sql() + function + rhs.sql() + ")";
    return new WhereClauseHelper(sql, mergeParams(lhs.params(), rhs.params()));
  }

  static WhereClauseHelper binaryInfixExp(String function, SqlBuilder lhs, Object rhs) {
    String sql = "(" + lhs.sql() + function + PARAM_PLACEHOLDER + ")";
    return new WhereClauseHelper(sql, mergeParams(lhs.params(), rhs));
  }

  static WhereClauseHelper betweenPart1(String function, SqlBuilder lhs, SqlBuilder x) {
    String sql = "(" + lhs.sql() + function + x.sql();
    return new WhereClauseHelper(sql, mergeParams(lhs.params(), x.params()));
  }

  static WhereClauseHelper betweenPart1(String function, SqlBuilder lhs, Object x) {
    String sql = "(" + lhs.sql() + function + PARAM_PLACEHOLDER;
    return new WhereClauseHelper(sql, mergeParams(lhs.params(), x));
  }

  static WhereClauseHelper rightInfix(String function, SqlBuilder lhs) {
    String sql = "(" + lhs.sql() + function + ")";
    return new WhereClauseHelper(sql, lhs.params());
  }

  static WhereClauseHelper function(String function, SqlBuilder x) {
    String sql = function + "(" + x.sql() + ")";
    return new WhereClauseHelper(sql, x.params());
  }

  static WhereClauseHelper function(String function, Object x) {
    String sql = function + "(" + PARAM_PLACEHOLDER + ")";
    return new WhereClauseHelper(sql, new Object[] { x });
  }

  static WhereClauseHelper function(String function, SqlBuilder x, SqlBuilder y) {
    String sql = function + "(" + x.sql() + ", " + y.sql() + ")";
    return new WhereClauseHelper(sql, mergeParams(x.params(), y.params()));
  }

  static WhereClauseHelper function(String function, SqlBuilder x, Object y) {
    String sql = function + "(" + x.sql() + ", " + PARAM_PLACEHOLDER + ")";
    return new WhereClauseHelper(sql, mergeParams(x.params(), y));
  }

  static WhereClauseHelper function(String function, SqlBuilder x, SqlBuilder y, Object z) {
    String sql = function + "(" + x.sql() + ", " + y.sql() + ", " + PARAM_PLACEHOLDER + ")";
    Object[] params = Arrays.copyOf(x.params(), x.paramCount() + y.paramCount() + 1);
    Object[] yParams = y.params();
    System.arraycopy(yParams, 0, params, x.paramCount(), yParams.length);
    params[params.length - 1] = z;
    return new WhereClauseHelper(sql, params);
  }

  static WhereClauseHelper varArgsFunction(String function, String firstArg, Object... args) {
    StringJoiner sql = new StringJoiner(", ", function + "(", ")");
    if (firstArg != null) sql.add(firstArg);
    int paramSize = 0;
    for (Object arg : args) {
      if (arg instanceof SqlBuilder) {
        SqlBuilder sqlBuilder = (SqlBuilder) arg;
        sql.add(sqlBuilder.sql());
        paramSize += sqlBuilder.paramCount();
      } else {
        sql.add(PARAM_PLACEHOLDER);
        ++paramSize;
      }
    }

    Object[] params = new Object[paramSize];
    int insertIndex = 0;
    for (Object arg : args) {
      if (arg instanceof SqlBuilder) {
        SqlBuilder sqlBuilder = (SqlBuilder) arg;
        Object[] builderParams = sqlBuilder.params();
        System.arraycopy(builderParams, 0, params, insertIndex, builderParams.length);
        insertIndex += builderParams.length;
      } else {
        params[insertIndex] = arg;
        ++insertIndex;
      }
    }

    return new WhereClauseHelper(sql.toString(), params);
  }

  static Object[] mergeParams(Object[] lhs, Object rhs) {
    Object[] res = Arrays.copyOf(lhs, lhs.length + 1);
    res[res.length - 1] = rhs;
    return res;
  }

  static Object[] mergeParams(Object lhs, Object[] rhs) {
    Object[] res = new Object[rhs.length + 1];
    System.arraycopy(rhs, 0, res, 1, rhs.length);
    res[0] = lhs;
    return res;
  }

  static Object[] mergeParams(Object[] lhs, Object[] rhs) {
    Object[] res = Arrays.copyOf(lhs, lhs.length + rhs.length);
    System.arraycopy(rhs, 0, res, lhs.length, rhs.length);
    return res;
  }

  static Object[] mergeParams(Object[] lhs, Object[] mid, Object[] rhs) {
    Object[] res = Arrays.copyOf(lhs, lhs.length + mid.length + rhs.length);
    System.arraycopy(mid, 0, res, lhs.length, mid.length);
    System.arraycopy(rhs, 0, res, lhs.length + mid.length, rhs.length);
    return res;
  }

}
