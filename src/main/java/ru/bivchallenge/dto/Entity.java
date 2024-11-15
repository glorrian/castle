package ru.bivchallenge.dto;


/**
 * Represents a base entity with an identifier.
 * Any class implementing this interface should provide an implementation for the ID retrieval.
 */
public interface Entity {

    /**
     * Gets the unique identifier of the entity.
     *
     * @return the ID of the entity as a long value
     */
    long id();
}
