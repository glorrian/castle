package ru.bivchallenge.dto;

import java.util.Objects;
import java.util.Set;

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
}
