package com.augustnagro.vertx.repo.pg;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;

import static com.augustnagro.vertx.repo.pg.WhereClauseHelper.*;

public class Functions {

  /**
   * Absolute value function
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  public static <E> NumberExpression<E> abs(NumberExpression<E> x) {
    return function(ABS, x);
  }

  /**
   * Ceiling function
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  public static <E> NumberExpression<E> ceil(NumberExpression<E> x) {
    return function(CEIL, x);
  }

  /**
   * Floor function
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  public static <E> NumberExpression<E> floor(NumberExpression<E> x) {
    return function(FLOOR, x);
  }

  /**
   * Natural Log function
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  public static <E> NumberExpression<E> ln(NumberExpression<E> x) {
    return function(LN, x);
  }

  /**
   * Base 10 Log function
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  public static <E> NumberExpression<E> log(NumberExpression<E> x) {
    return function(LOG, x);
  }

  /**
   * Base 10 Log function
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  public static <E> NumberExpression<E> log10(NumberExpression<E> x) {
    return log(x);
  }

  /**
   * Round to Nearest Integer function
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  public static <E> NumberExpression<E> round(NumberExpression<E> x) {
    return function(ROUND, x);
  }

  /**
   * Sign function. Returns the sign of the argument (-1, 0, +1).
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  public static <E> NumberExpression<E> sign(NumberExpression<E> x) {
    return function(SIGN, x);
  }

  /**
   * Square Root function
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  public static <E> NumberExpression<E> sqrt(NumberExpression<E> x) {
    return function(SQRT, x);
  }

  /**
   * Truncate function. Truncates this number toward zero (ie, trunc(1.3) = 1).
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  public static <E> NumberExpression<E> trunc(NumberExpression<E> x) {
    return function(TRUNC, x);
  }

  /**
   * Truncate function. Truncates this number toward zero (ie, trunc(1.3) = 1).
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-math.html">https://www.postgresql.org/docs/current/functions-math.html</a>
   */
  public static <E> NumberExpression<E> trunc(NumberExpression<E> x, int y) {
    String sql = TRUNC + "(" + x.sql() + ", " + y + ")";
    return new WhereClauseHelper(sql, x.params());
  }

  /**
   * Character Length function. Returns the number of characters in a string.
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-string.html">https://www.postgresql.org/docs/current/functions-string.html</a>
   */
  public static <E> NumberExpression<E> charLength(StringExpression<E> x) {
    return function(CHAR_LENGTH, x);
  }

  /**
   * Character Length function. Returns the number of characters in a string.
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-string.html">https://www.postgresql.org/docs/current/functions-string.html</a>
   */
  public static <E> StringExpression<E> lower(StringExpression<E> x) {
    return function(LOWER, x);
  }

  /**
   * Uppercase function.
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-string.html">https://www.postgresql.org/docs/current/functions-string.html</a>
   */
  public static <E> StringExpression<E> upper(StringExpression<E> x) {
    return function(UPPER, x);
  }

  /**
   * Trim function. Removes leading and trailing space characters.
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-string.html">https://www.postgresql.org/docs/current/functions-string.html</a>
   */
  public static <E> StringExpression<E> trim(StringExpression<E> x) {
    return function(TRIM, x);
  }

  public enum TrimType {
    LEADING, TRAILING, BOTH
  }

  /**
   * Trim function. Parameter trimChars cannot be prepared and so is a SQL INJECTION VULNERABILITY;
   * it must be manually sanitized if coming from user input.
   *
   * @param trimType  Whether to remove from the leading, trailing, or both ends of the string
   * @param trimChars All characters in this String will be trimmed. Must be
   * @param toTrim    The String to trim
   * @param <E>       Entity
   * @return The trimmed String
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-string.html">https://www.postgresql.org/docs/current/functions-string.html</a>
   */
  public static <E> StringExpression<E> trim(TrimType trimType, String trimChars, StringExpression<E> toTrim) {
    String sql = TRIM + "(" + trimType + " '" + trimChars + "' FROM " + toTrim.sql() + ")";
    return new WhereClauseHelper(sql, toTrim.params());
  }

  /**
   * Concat function.
   *
   * @param args May contain direct parameters and Expressions; both are handled accordingly
   * @param <E>  Entity type
   * @return StringExpression representing the concatenation
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-string.html">https://www.postgresql.org/docs/current/functions-string.html</a>
   */
  public static <E> StringExpression<E> concat(Object... args) {
    return varArgsFunction(CONCAT, null, args);
  }

  /**
   * Concat-With-Separator Function.
   *
   * @param separator Separator to be used between arguments
   * @param args      May contain direct parameters and Expressions; both are handled accordingly
   * @param <E>       Entity type
   * @return StringExpression representing the concatenation
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-string.html">https://www.postgresql.org/docs/current/functions-string.html</a>
   */
  public static <E> StringExpression<E> concatWs(String separator, Object... args) {
    return varArgsFunction(CONCAT_WS, separator, args);
  }

  /**
   * String length function
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-string.html">https://www.postgresql.org/docs/current/functions-string.html</a>
   */
  public static <E> NumberExpression<E> length(StringExpression<E> x) {
    return function(LENGTH, x);
  }

  /**
   * String starts-with function
   *
   * @param string the string
   * @param prefix the prefix
   * @return true if string starts with prefix
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-string.html">https://www.postgresql.org/docs/current/functions-string.html</a>
   */
  public static <E> Predicate<E> startsWith(StringExpression<E> string, StringExpression<E> prefix) {
    return function(STARTS_WITH, string, prefix);
  }

  /**
   * String starts-with function
   *
   * @param string the string
   * @param prefix the prefix
   * @return true if string starts with prefix
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-string.html">https://www.postgresql.org/docs/current/functions-string.html</a>
   */
  public static <E> Predicate<E> startsWith(StringExpression<E> string, String prefix) {
    return function(STARTS_WITH, string, prefix);
  }

  /**
   * Current date function
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-datetime.html">https://www.postgresql.org/docs/current/functions-datetime.html</a>
   */
  public static <E> Expression<E, LocalDate> currentDate() {
    return new WhereClauseHelper(CURRENT_DATE);
  }

  /**
   * Current time function
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-datetime.html">https://www.postgresql.org/docs/current/functions-datetime.html</a>
   */
  public static <E> Expression<E, OffsetTime> currentTime() {
    return new WhereClauseHelper(CURRENT_TIME);
  }

  /**
   * Current timestamp function
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-datetime.html">https://www.postgresql.org/docs/current/functions-datetime.html</a>
   */
  public static <E> Expression<E, OffsetDateTime> currentTimestamp() {
    return new WhereClauseHelper(CURRENT_TIMESTAMP);
  }

  /**
   * Now function (equivalent to {@link #currentTimestamp()}).
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-datetime.html">https://www.postgresql.org/docs/current/functions-datetime.html</a>
   */
  public static <E> Expression<E, OffsetDateTime> now() {
    return new WhereClauseHelper(NOW + "()");
  }

  public enum ExtractField {
    CENTURY,
    DAY,
    DOW,
    DOY,
    EPOCH,
    HOUR,
    ISODOW,
    ISOYEAR,
    MICROSECONDS,
    MILLENNIUM,
    MILLISECONDS,
    MINUTE,
    MONTH,
    QUARTER,
    SECOND,
    TIMEZONE,
    TIMEZONE_HOUR,
    TIMEZONE_MINUTE,
    WEEK,
    YEAR
  }

  /**
   * Extract function. Gets a NumberExpression from a temporal Expression.
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-datetime.html">https://www.postgresql.org/docs/current/functions-datetime.html</a>
   */
  public static <E> NumberExpression<E> extract(ExtractField field,
                                                Expression<E, ? extends TemporalAccessor> temporalExpression) {
    String sql = EXTRACT + "(" + field + " FROM " + temporalExpression.sql() + ")";
    return new WhereClauseHelper(sql, temporalExpression.params());
  }

  public enum TruncField {
    MICROSECONDS("microseconds"),
    MILLISECONDS("milliseconds"),
    SECOND("seconds"),
    MINUTE("minute"),
    HOUR("hour"),
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
    QUARTER("quarter"),
    YEAR("year"),
    DECADE("decade"),
    CENTURY("century"),
    MILLENNIUM("millennium");

    final String sql;

    TruncField(String sql) {
      this.sql = sql;
    }

    @Override
    public String toString() {
      return sql;
    }
  }

  /**
   * Date truncation function. Truncates a timestamptz or interval to specified precision
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-datetime.html">https://www.postgresql.org/docs/current/functions-datetime.html</a>
   */
  public static <E, T extends Temporal> Expression<E, T> dateTrunc(TruncField field,
                                                                   Expression<E, T> temporalExpression) {
    String sql = DATE_TRUNC + "('" + field + "', " + temporalExpression.sql() + ")";
    return new WhereClauseHelper(sql, temporalExpression.params());
  }

  /**
   * Date truncation function. Truncates a timestamptz or interval to specified precision
   *
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-datetime.html">https://www.postgresql.org/docs/current/functions-datetime.html</a>
   */
  public static <E, T extends Temporal> Expression<E, T> dateTrunc(TruncField field,
                                                                   Expression<E, T> temporalExpression,
                                                                   ZoneId zoneId) {
    String sql = DATE_TRUNC + "('" + field + "', " + temporalExpression.sql() + ", " + zoneId + ")";
    return new WhereClauseHelper(sql, temporalExpression.params());
  }

  /**
   * Coalesce function.
   *
   * @return the first non-null expression, otherwise null
   * @implNote We really need Expression to be a sealed interface... will do this when the JEP is out of preview.
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-COALESCE-NVL-IFNULL">https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-COALESCE-NVL-IFNULL</a>
   */
  @SafeVarargs
  public static <E, T, X extends Expression<E, T>> X coalesce(X... exprs) {
    return (X) varArgsFunction(COALESCE, null, exprs);
  }

  /**
   * Coalesce function.
   *
   * @return the first non-null expression, otherwise null
   * @implNote We really need Expression to be a sealed interface... will do this when the JEP is out of preview.
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-COALESCE-NVL-IFNULL">https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-COALESCE-NVL-IFNULL</a>
   */
  public static <E, T, X extends Expression<E, T>> X coalesce(X first, T last) {
    return (X) function(COALESCE, first, last);
  }

  /**
   * Coalesce function.
   *
   * @return the first non-null expression, otherwise null
   * @implNote We really need Expression to be a sealed interface... will do this when the JEP is out of preview.
   * @see
   * <a href="https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-COALESCE-NVL-IFNULL">https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-COALESCE-NVL-IFNULL</a>
   */
  public static <E, T, X extends Expression<E, T>> X coalesce(X first, X second, T third) {
    return (X) function(COALESCE, first, second, third);
  }

}
