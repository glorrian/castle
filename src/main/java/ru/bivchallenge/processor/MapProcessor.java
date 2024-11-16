package ru.bivchallenge.processor;

import java.util.Map;


/**
 * The {@code MapProcessor} interface is a specialized {@link Processor} that operates on {@link Map} instances.
 * It processes a map with keys of type {@link Long} and values of type {@code T}, producing a new map
 * with the same keys and transformed values of type {@code R}.
 * <p>
 * This interface is designed for scenarios where batch processing of keyed data is required, providing a
 * convenient abstraction for transforming entire maps.
 *
 * @param <T> the type of the input values in the input map
 * @param <R> the type of the output values in the resulting map
 *
 * @see Processor
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface MapProcessor<T, R> extends Processor<Map<Long, T>, Map<Long, R>>{
}
