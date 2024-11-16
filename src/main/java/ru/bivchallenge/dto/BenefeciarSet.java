package ru.bivchallenge.dto;

import java.util.HashSet;
import java.util.Objects;

public class BenefeciarSet {
    private final Company company;
    private final HashSet<Benefeciar> beneficiaries;

    public BenefeciarSet(Company company) {
        this.company = company;
        this.beneficiaries = new HashSet<>();
    }

    public Company getCompany() {
        return company;
    }

    public HashSet<Benefeciar> getBeneficiaries() {
        return beneficiaries;
    }

    public void addBenefeciar(Benefeciar benefeciar) {
        beneficiaries.add(benefeciar);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BenefeciarSet that)) return false;
        return Objects.equals(getCompany(), that.getCompany()) && Objects.equals(beneficiaries, that.beneficiaries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCompany(), getClass());
    }
}
