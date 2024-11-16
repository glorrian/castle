package ru.bivchallenge.data;

import ru.bivchallenge.dto.Benefeciar;
import ru.bivchallenge.dto.Company;

import java.util.HashSet;

/**
 * A registry for storing beneficiaries associated with a company.
 * Maintains a set of beneficiaries and provides access to the associated company.
 */
public class BenefeciarRegistry {
    private final Company company;
    private final HashSet<Benefeciar> beneficiaries;

    public BenefeciarRegistry(Company company) {
        this.company = company;
        this.beneficiaries = new HashSet<>();
    }

    public Company getCompany() {
        return company;
    }

    public HashSet<Benefeciar> getBeneficiaries() {
        return beneficiaries;
    }
}