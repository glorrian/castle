package ru.bivchallenge.dto;

import java.util.Objects;

/**
 * The {@code Benefeciar} record represents a beneficiary with a reference to a {@link NaturalEntity}
 * and an ownership percentage. This record is designed to encapsulate information about a beneficiary
 * in a lightweight and immutable manner.
 * <p>
 * It also implements the {@link RecordSeparable} interface, allowing the data to be separated into
 * a string array for serialization or tabular representation.
 *
 * @param naturalEntity the {@link NaturalEntity} associated with this beneficiary
 * @param percent       the ownership percentage of the beneficiary
 *
 * @see NaturalEntity
 * @see RecordSeparable
 */
public record Benefeciar(NaturalEntity naturalEntity, double percent) implements RecordSeparable {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Benefeciar that)) return false;
        return Objects.equals(naturalEntity, that.naturalEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(naturalEntity);
    }

    @Override
    public String[] separate() {
        return new String[] {"", String.valueOf(naturalEntity.getInn()), naturalEntity.getLastName() + " "  + naturalEntity.getFirstName() + " " + naturalEntity.getSecondName(), String.valueOf(percent)};
    }
}
