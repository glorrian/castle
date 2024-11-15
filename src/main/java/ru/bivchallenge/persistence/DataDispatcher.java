package ru.bivchallenge.persistence;

import java.util.Set;

/**
 * Interface for dispatching or distributing a set of data of type {@link T}.
 * <p>
 * This interface defines the method to send or distribute a set of data items, where
 * {@link T} represents the type of data to be dispatched.
 *
 * @param <T> the type of data to be dispatched or distributed
 */
public interface DataDispatcher<T> {

    /**
     * Dispatches or distributes a set of data of type {@link T}.
     *
     * @param set a {@link Set} containing objects of type {@link T} to be dispatched or distributed.
     *            Each object in the set represents an individual data item to be processed.
     */
    void dispatch(Set<T> set);
}
