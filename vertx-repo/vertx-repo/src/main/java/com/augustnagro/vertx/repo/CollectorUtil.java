package com.augustnagro.vertx.repo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * {@link java.util.stream.Collector} utility methods
 */
public class CollectorUtil {

  /**
   * Like {@link Collectors#toList()}, but with an initial capacity.
   * @param <E> Element type
   * @param expectedSize size of initial collection
   * @return List Collector
   */
  public static <E> Collector<E, ?, List<E>> toList(int expectedSize) {
    return Collector.of(
        () -> new ArrayList<>(expectedSize),
        List::add,
        (left, right) -> { left.addAll(right); return left; }
    );
  }
}
