package ru.bivchallenge.processor;

import java.util.function.Function;


/**
 * The {@code Processor} interface is a functional interface that extends {@link java.util.function.Function}.
 * It represents an operation that accepts a single input argument of type {@code T} and produces a result of type {@code R}.
 * <p>
 * Being a functional interface, {@code Processor} can be used as the assignment target for lambda expressions
 * or method references. This interface serves as a more domain-specific alias for {@link Function}.
 *
 * @param <T> the type of the input to the processor
 * @param <R> the type of the result of the processor
 *
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface Processor<T, R> extends Function<T, R> {
}
