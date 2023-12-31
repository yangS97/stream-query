/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dromara.streamquery.stream.core.lambda.function;

import org.dromara.streamquery.stream.core.lambda.LambdaInvokeException;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.BinaryOperator;

/**
 * SerBiUnOp
 *
 * @param <T> the type of the input to the operation
 * @author VampireAchao Cizai_
 * @since 2022/6/8
 */
@FunctionalInterface
public interface SerBiOp<T> extends BinaryOperator<T>, Serializable {

  /**
   * Applies this function to the given arguments.
   *
   * @param t the first function argument
   * @param u the second function argument
   * @return the function result
   * @throws java.lang.Exception if any.
   */
  @SuppressWarnings("all")
  T applying(T t, T u) throws Throwable;

  /** Applies this function to the given arguments. */
  @Override
  default T apply(T t, T u) {
    try {
      return this.applying(t, u);
    } catch (Throwable e) {
      throw new LambdaInvokeException(e);
    }
  }

  /**
   * Returns a {@link SerBiOp} which returns the lesser of two elements according to the specified
   * {@code Comparator}.
   */
  static <T> SerBiOp<T> minBy(Comparator<? super T> comparator) {
    Objects.requireNonNull(comparator);
    return (a, b) -> comparator.compare(a, b) <= 0 ? a : b;
  }

  /**
   * Returns a {@link SerBiOp} which returns the greater of two elements according to the specified
   * {@code Comparator}.
   */
  static <T> SerBiOp<T> maxBy(Comparator<? super T> comparator) {
    Objects.requireNonNull(comparator);
    return (a, b) -> comparator.compare(a, b) >= 0 ? a : b;
  }

  /**
   * just before
   *
   * @param <T> type
   * @return before
   */
  static <T> SerBiOp<T> justBefore() {
    return (l, r) -> l;
  }

  /**
   * just after
   *
   * @param <T> type
   * @return after
   */
  static <T> SerBiOp<T> justAfter() {
    return (l, r) -> r;
  }
}
