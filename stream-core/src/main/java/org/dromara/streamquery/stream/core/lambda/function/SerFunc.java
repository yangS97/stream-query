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
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 可序列化的Function
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @author VampireAchao Cizai_
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface SerFunc<T, R> extends Function<T, R>, Serializable {

  /**
   * Applies this function to the given argument.
   *
   * @param t the function argument
   * @return the function result
   * @throws java.lang.Exception if any.
   */
  @SuppressWarnings("all")
  R applying(T t) throws Throwable;

  /** Applies this function to the given argument. */
  @Override
  default R apply(T t) {
    try {
      return applying(t);
    } catch (Throwable e) {
      throw new LambdaInvokeException(e);
    }
  }

  /**
   * Returns a function that always returns its input argument.
   *
   * @param <T> the type of the input and output objects to the function
   * @return a function that always returns its input argument
   */
  static <T> SerFunc<T, T> identity() {
    return t -> t;
  }

  /**
   * cast identity
   *
   * @param <T> param type
   * @param <R> result type
   * @return identity after casting
   */
  @SuppressWarnings("unchecked")
  static <T, R> Function<T, R> cast() {
    return t -> (R) t;
  }

  /**
   * entryFunc
   *
   * @param biFunc biFunc
   * @param <K> key
   * @param <V> value
   * @param <R> result
   * @return Function
   * @apiNote eg
   *     <pre>{@code
   * Steam.of(map).map(SerFunc.entryFunc((key, value) -> key + value))
   * }</pre>
   */
  static <K, V, R> Function<Map.Entry<K, V>, R> entryFunc(BiFunction<K, V, R> biFunc) {
    return entry -> biFunc.apply(entry.getKey(), entry.getValue());
  }
}
