package ru.bivchallenge.dto;

import java.util.Objects;

public record Benefeciar(NaturalEntity naturalEntity, long percent) implements RecordSeparable {
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
        return new String[] {"\t", String.valueOf(naturalEntity.id()), naturalEntity.getLastName(), naturalEntity.getFirstName(), naturalEntity.getSecondName(), String.valueOf(percent)};
    }
}
