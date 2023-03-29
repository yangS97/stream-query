package org.dromara.streamquery.stream.core.lambda.function;

import org.dromara.streamquery.stream.core.lambda.LambdaInvokeException;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * SerSerBiCons
 *
 * @author VampireAchao Cizai_

 * @since 2022/6/8
 */
@FunctionalInterface
public interface SerBiCons<T, U> extends BiConsumer<T, U>, Serializable {
    /**
     * multi
     *
     * @param consumers lambda
     * @param <T>       type
     * @param <U>       a U class
     * @return lambda
     */
    @SafeVarargs
    static <T, U> SerBiCons<T, U> multi(SerBiCons<T, U>... consumers) {
        return Stream.of(consumers).reduce(SerBiCons::andThen).orElseGet(() -> (o, q) -> {
        });
    }

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @throws java.lang.Exception if any.
     */
    @SuppressWarnings("all")
    void accepting(T t, U u) throws Throwable;

    /**
     *
     * Performs this operation on the given arguments.
     */
    @Override
    default void accept(T t, U u) {
        try {
            accepting(t, u);
        } catch (Throwable e) {
            throw new LambdaInvokeException(e);
        }
    }

    /**
     * Returns a composed {@code SerBiCons} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code SerBiCons} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws java.lang.NullPointerException if {@code after} is null
     */
    default SerBiCons<T, U> andThen(SerBiCons<? super T, ? super U> after) {
        Objects.requireNonNull(after);
        return (l, r) -> {
            accepting(l, r);
            after.accepting(l, r);
        };
    }

    /**
     * nothing
     *
     * @param <T> a T class
     * @param <U> a U class
     * @return nothing
     */
    static <T, U> SerBiCons<T, U> nothing() {
        return (l, r) -> {
        };
    }
}