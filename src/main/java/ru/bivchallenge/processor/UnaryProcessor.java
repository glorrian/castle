package ru.bivchallenge.processor;

/**
 * The {@code UnaryProcessor} interface represents a specialized {@link Processor}
 * that takes an input of type {@code T} and produces an output of the same type {@code T}.
 * <p>
 * This interface simplifies the definition of processors where the input and output types are identical,
 * allowing for transformations, modifications, or validations of a single type.
 *
 * @param <T> the type of the input and output processed by this processor
 *
 * @see Processor
 */
public interface UnaryProcessor<T> extends Processor<T, T> {
}
