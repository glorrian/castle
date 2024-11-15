package ru.bivchallenge.provider;

import java.util.Map;
import ru.bivchallenge.dto.Entity;

/**
 * Provides data retrieval functionality for entities of type T.
 * This interface defines a contract for fetching entities by their unique identifiers.
 *
 * @param <T> the type of entity that this data provider handles,
 *            which must implement the {@link Entity} interface
 */
public interface DataProvider<T extends Entity> {

    /**
     * Retrieves a map of entities, where each entity is associated with its unique identifier.
     *
     * @return a {@link Map} where the keys are entity IDs (as {@code Long}) and
     *         the values are entities of type {@code T}
     */
    Map<Long, T> get();
}