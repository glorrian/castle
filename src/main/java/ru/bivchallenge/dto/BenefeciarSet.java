package ru.bivchallenge.dto;

import java.util.Objects;
import java.util.Set;

/**
 * The {@code BenefeciarSet} record represents a collection of beneficiaries associated with a specific {@link Company}.
 * It is designed to encapsulate the relationship between a company and its set of {@link Benefeciar} instances.
 * <p>
 * This record provides immutability and built-in implementations of {@link Object#equals(Object)}
 * and {@link Object#hashCode()} for consistent equality and hashing behavior.
 *
 * @param company           the {@link Company} associated with this beneficiary set
 * @param naturalEntitySet  the set of {@link Benefeciar} instances representing the beneficiaries of the company
 *
 * @see Company
 * @see Benefeciar
 */
public record BenefeciarSet(Company company, Set<Benefeciar> naturalEntitySet) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BenefeciarSet that)) return false;
        return Objects.equals(company(), that.company()) && Objects.equals(naturalEntitySet(), that.naturalEntitySet());
    }

    @Override
    public int hashCode() {
        return Objects.hash(company(), naturalEntitySet());
    }

    public boolean isEmpty() {
        return naturalEntitySet.isEmpty();
    }
}
