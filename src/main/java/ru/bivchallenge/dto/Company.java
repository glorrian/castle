package ru.bivchallenge.dto;

import java.util.Objects;

/**
 * Represents a company entity with its identification attributes.
 * Implements the {@link Entity} interface.
 *
 * @param id       the unique identifier of the company
 * @param ogrn     the OGRN (Primary State Registration Number) of the company
 * @param inn      the INN (Taxpayer Identification Number) of the company
 * @param fullName the full name of the company
 */
public record Company(long id, String ogrn, String inn, String fullName) implements Entity, RecordSeparable {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company company)) return false;
        return id() == company.id();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }

    @Override
    public String[] separate() {
        return new String[]{String.valueOf(id), ogrn, inn, fullName};
    }
}
