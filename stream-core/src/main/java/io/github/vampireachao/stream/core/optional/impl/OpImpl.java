package io.github.vampireachao.stream.core.optional.impl;

import io.github.vampireachao.stream.core.lambda.function.SerCons;
import io.github.vampireachao.stream.core.lambda.function.SerFunc;
import io.github.vampireachao.stream.core.lambda.function.SerPred;
import io.github.vampireachao.stream.core.optional.CollOp;
import io.github.vampireachao.stream.core.optional.Op;
import io.github.vampireachao.stream.core.optional.StrOp;
import io.github.vampireachao.stream.core.optional.ThrowOp;
import io.github.vampireachao.stream.core.reflect.ReflectHelper;
import io.github.vampireachao.stream.core.stream.Steam;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * OpImpl
 *
 * @author VampireAchao
 * @since 2022/9/4
 */
public class OpImpl<T> implements Op<T> {

    protected final T value;

    public OpImpl(T value) {
        this.value = value;
    }

    @Override
    public boolean isPresent() {
        return Objects.nonNull(this.value);
    }

    @Override
    public boolean isEmpty() {
        return Objects.isNull(this.value);
    }

    @Override
    public boolean isEqual(Object value) {
        return Objects.equals(this.value, value);
    }

    @Override
    public boolean is(Predicate<T> predicate) {
        return filter(predicate).isPresent();
    }

    @Override
    public T get() {
        return this.value;
    }

    @Override
    public T orElse(T other) {
        return isPresent() ? this.value : other;
    }

    @Override
    public T orElseGet(Supplier<T> other) {
        return isPresent() ? this.value : other.get();
    }

    @Override
    public T orElseRun(Runnable other) {
        if (isEmpty()) {
            other.run();
        }
        return this.value;
    }

    @Override
    public T orElseThrow() {
        return orElseThrow(NoSuchElementException::new);
    }

    @Override
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (isPresent()) {
            return this.value;
        }
        throw exceptionSupplier.get();
    }

    @Override
    public Optional<T> toOptional() {
        return Optional.ofNullable(this.value);
    }

    @Override
    public Op<T> flatOptional(Function<? super T, Optional<T>> mapper) {
        if (isEmpty()) {
            return Op.empty();
        }
        return Op.ofOptional(mapper.apply(this.value));
    }

    @Override
    public Op<T> orOptional(Optional<? extends T> other) {
        return Op.of(other.orElse(null));
    }

    @Override
    public Op<T> typeOf(SerCons<? super T> action) {
        if (isNotInstance(this.value, action)) {
            return Op.empty();
        }
        action.accept(this.value);
        return this;
    }

    @Override
    public <U> Op<U> typeOfMap(SerFunc<? super T, ? extends U> mapper) {
        if (isNotInstance(this.value, mapper)) {
            return Op.empty();
        }
        return Op.of(mapper.apply(this.value));
    }

    @Override
    public Op<T> typeOfFilter(SerPred<? super T> condition) {
        if (isNotInstance(this.value, condition) || condition.negate().test(this.value)) {
            return Op.empty();
        }
        return this;
    }

    @Override
    public Op<T> typeOf(Type type, SerCons<? super T> action) {
        if (!ReflectHelper.isInstance(this.value, type)) {
            return Op.empty();
        }
        action.accept(this.value);
        return this;
    }

    @Override
    public <U> Op<U> typeOfMap(Type type, SerFunc<? super T, ? extends U> mapper) {
        if (!ReflectHelper.isInstance(this.value, type)) {
            return Op.empty();
        }
        return Op.of(mapper.apply(this.value));
    }

    @Override
    public Op<T> typeOfFilter(Type type, SerPred<? super T> SerPred) {
        if (!ReflectHelper.isInstance(this.value, type)) {
            return Op.empty();
        }
        return this;
    }

    @Override
    public <U> Op<U> map(Function<? super T, ? extends U> mapper) {
        return isPresent() ? Op.of(mapper.apply(this.value)) : Op.empty();
    }

    @Override
    public <U> ThrowOp<U> mapToThrow(SerFunc<? super T, ? extends U> mapper) {
        return isPresent() ? ThrowOp.of(() -> mapper.apply(this.value)) : ThrowOp.empty();
    }

    @Override
    public <U> CollOp<U> mapToColl(SerFunc<? super T, ? extends Collection<U>> mapper) {
        return isPresent() ? CollOp.of(mapper.apply(this.value)) : CollOp.empty();
    }

    @Override
    public StrOp mapToStr(SerFunc<? super T, ? extends CharSequence> mapper) {
        return isPresent() ? StrOp.of(mapper.apply(this.value)) : StrOp.empty();
    }

    @Override
    public <U> Op<U> flatMap(Function<? super T, ? extends Op<? extends U>> mapper) {
        return isPresent() ? Op.of(mapper.apply(this.value).get()) : Op.empty();
    }

    @Override
    public <U> Optional<U> flatMapToOptional(Function<? super T, ? extends Optional<U>> mapper) {
        return isPresent() ? mapper.apply(this.value) : Optional.empty();
    }

    @Override
    public Op<T> filter(Predicate<? super T> predicate) {
        return isPresent() && predicate.test(this.value) ? this : Op.empty();
    }

    @Override
    public <R> Op<T> filterEqual(R value) {
        return filter(v -> Objects.equals(this.value, value));
    }

    @Override
    public Op<T> ifPresent(Consumer<? super T> action) {
        if (isPresent()) {
            action.accept(this.value);
        }
        return this;
    }

    @Override
    public Op<T> or(Supplier<Op<T>> other) {
        return isPresent() ? this : other.get();
    }

    @Override
    public Steam<T> steam() {
        return Steam.of(this.value);
    }
}
