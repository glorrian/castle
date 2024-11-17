package ru.bivchallenge.dto;

/**
 * The {@code Owner} interface represents an entity that has ownership or an association
 * with a company. It provides methods to retrieve ownership details, including the share
 * value and share percentage.
 *
 * <p>Implementing classes are expected to define how they represent ownership, such as
 * being a legal entity, natural entity, or another type of owner.</p>
 *
 * <p><b>Methods:</b></p>
 * <ul>
 *     <li>{@link #getShare()} - Retrieves the ownership share value of the entity.</li>
 *     <li>{@link #getSharePercent()} - Retrieves the ownership share as a percentage of the total.</li>
 * </ul>
 */
public interface Owner {

    /**
     * Retrieves the ownership share value of the entity.
     *
     * @return the ownership share value, typically in absolute terms
     */
    double getShare();

    /**
     * Retrieves the ownership share as a percentage of the total ownership.
     *
     * @return the ownership share percentage
     */
    double getSharePercent();
}
