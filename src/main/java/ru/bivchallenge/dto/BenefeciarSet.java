package ru.bivchallenge.dto;

import java.util.HashSet;

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
}