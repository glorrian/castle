package ru.bivchallenge.dto;

/**
 * The {@code OwnerEntity} interface represents an entity that both has a unique identifier
 * and is associated with a company. It extends the {@link Entity} and {@link Owner} interfaces,
 * combining their functionalities.
 *
 * <p>Classes implementing this interface must provide implementations for:
 * <ul>
 *   <li>{@code id()} from {@link Entity} - to return the unique identifier of the entity.</li>
 *   <li>{@code getCompanyId()} from {@link Owner} - to return the associated company ID.</li>
 * </ul>
 * </p>
 *
 * @see Entity
 * @see Owner
 */
public interface OwnerEntity extends Entity, Owner {
    /**
     * Returns the ID of the company associated with this owner.
     *
     * @return the ID of the associated company
     */
    long getCompanyId();
}
