package ru.bivchallenge.dto;

/**
 * The {@code Owner} interface represents an entity that owns or is associated with a company.
 * It provides a method to retrieve the company ID of the associated company.
 *
 * <p>Implementing classes are expected to define how they are associated with a company,
 * for example, as a legal entity, natural entity, or other ownership type.</p>
 */
public interface Owner {
    double getShare();

    double getSharePercent();
}
