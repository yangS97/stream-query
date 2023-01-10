package io.github.vampireachao.stream.core.collection;

import io.github.vampireachao.stream.core.stream.Steam;
import io.github.vampireachao.stream.core.variable.VariableHelper;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * <p>Maps class.</p>
 *
 * @author VampireAchao
 * @since 2022/10/21 16:35
 */
public class Maps {

    private Maps() {
        /* Do not new me! */
    }

    /**
     * <p>of.</p>
     *
     * @param initialCapacity a int
     * @param <K>             a K class
     * @param <V>             a V class
     * @return a {@link java.util.Map} object
     */
    public static <K, V> Map<K, V> of(int initialCapacity) {
        return new HashMap<>(initialCapacity);
    }

    /**
     * <p>of.</p>
     *
     * @param k   a K object
     * @param v   a V object
     * @param <K> a K class
     * @param <V> a V class
     * @return a {@link java.util.Map} object
     */
    public static <K, V> Map<K, V> of(K k, V v) {
        final Map<K, V> map = of(1);
        map.put(k, v);
        return map;
    }

    /**
     * <p>of.</p>
     *
     * @param k   a K object
     * @param v   a V object
     * @param k1  a K object
     * @param v1  a V object
     * @param <K> a K class
     * @param <V> a V class
     * @return a {@link java.util.Map} object
     */
    public static <K, V> Map<K, V> of(K k, V v, K k1, V v1) {
        final Map<K, V> map = of(1 << 1);
        map.put(k, v);
        map.put(k1, v1);
        return map;
    }

    /**
     * <p>of.</p>
     *
     * @param k   a K object
     * @param v   a V object
     * @param k1  a K object
     * @param v1  a V object
     * @param k2  a K object
     * @param v2  a V object
     * @param <K> a K class
     * @param <V> a V class
     * @return a {@link java.util.Map} object
     */
    public static <K, V> Map<K, V> of(K k, V v, K k1, V v1, K k2, V v2) {
        final Map<K, V> map = of(1 << 2);
        map.put(k, v);
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    /**
     * <p>entry.</p>
     *
     * @param key   a K object
     * @param value a V object
     * @param <K>   a K class
     * @param <V>   a V class
     * @return a {@link java.util.Map.Entry} object
     */
    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    /**
     * <p>oneToManyToOne.</p>
     *
     * @param middleMap     a {@link java.util.Map} object
     * @param attachMap     a {@link java.util.Map} object
     * @param unaryOperator a {@link java.util.function.UnaryOperator} object
     * @param <K>           a K class
     * @param <A>           a A class
     * @param <V>           a V class
     * @return a {@link io.github.vampireachao.stream.core.stream.Steam} object
     */
    @SafeVarargs
    public static <K, A, V> Steam<Map.Entry<K, List<V>>>
    oneToManyToOne(Map<K, List<A>> middleMap, Map<A, V> attachMap, UnaryOperator<Steam<V>>... unaryOperator) {
        return Steam.of(middleMap.entrySet()).map(e -> entry(e.getKey(),
                VariableHelper.<UnaryOperator<Steam<V>>>first(unaryOperator, UnaryOperator::identity)
                        .apply(Steam.of(e.getValue()).map(attachMap::get)).toList()));
    }

    /**
     * <p>oneToOneToOne.</p>
     *
     * @param middleMap     a {@link java.util.Map} object
     * @param attachMap     a {@link java.util.Map} object
     * @param unaryOperator a {@link java.util.function.UnaryOperator} object
     * @param <K>           a K class
     * @param <A>           a A class
     * @param <V>           a V class
     * @return a {@link io.github.vampireachao.stream.core.stream.Steam} object
     */
    @SafeVarargs
    public static <K, A, V> Steam<Map.Entry<K, V>>
    oneToOneToOne(Map<K, A> middleMap, Map<A, V> attachMap, UnaryOperator<V>... unaryOperator) {
        return Steam.of(middleMap.entrySet()).map(e -> entry(e.getKey(),
                VariableHelper.<UnaryOperator<V>>first(unaryOperator, UnaryOperator::identity)
                        .apply(attachMap.get(e.getValue()))));
    }

    /**
     * <p>oneToOneToMany.</p>
     *
     * @param middleMap     a {@link java.util.Map} object
     * @param attachMap     a {@link java.util.Map} object
     * @param unaryOperator a {@link java.util.function.UnaryOperator} object
     * @param <K>           a K class
     * @param <A>           a A class
     * @param <V>           a V class
     * @return a {@link io.github.vampireachao.stream.core.stream.Steam} object
     */
    @SafeVarargs
    public static <K, A, V> Steam<Map.Entry<K, List<V>>>
    oneToOneToMany(Map<K, A> middleMap, Map<A, List<V>> attachMap, UnaryOperator<Steam<V>>... unaryOperator) {
        return Steam.of(middleMap.entrySet()).map(e -> entry(e.getKey(),
                VariableHelper.<UnaryOperator<Steam<V>>>first(unaryOperator, UnaryOperator::identity)
                        .apply(Steam.of(e.getValue()).flat(attachMap::get)).toList()));
    }

    public static <K, V> Map<K, V> empty() {
        return Collections.emptyMap();
    }
}
