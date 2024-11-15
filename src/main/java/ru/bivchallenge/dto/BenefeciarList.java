package ru.bivchallenge.dto;

import java.util.List;
import java.util.Objects;

public record BenefeciarList(Company company, List<NaturalEntity> naturalEntityList) implements Entity {

    @Override
    public long id() {
        return company.id();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BenefeciarList that)) return false;
        return Objects.equals(company(), that.company()) && Objects.equals(naturalEntityList(), that.naturalEntityList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(company(), naturalEntityList());
    }
}
