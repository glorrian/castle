package ru.bivchallenge.data;

import ru.bivchallenge.dto.Benefeciar;
import ru.bivchallenge.dto.Company;

import java.util.HashSet;

/**
 * The {@code BenefeciarSet} class represents a collection of beneficiaries
 * (individuals or entities) associated with a specific company. This class is used
 * to store and manage the list of beneficiaries, along with their respective ownership information.
 *
 * <p><b>Fields:</b></p>
 * <ul>
 *     <li>{@code company} - The {@link Company} instance representing the associated company.</li>
 *     <li>{@code beneficiaries} - A {@link HashSet} containing the beneficiaries as {@link Benefeciar} objects.</li>
 * </ul>
 *
 * <p><b>Usage:</b></p>
 * <p>
 * This class allows adding and retrieving beneficiaries of a company, providing
 * a structured way to manage ownership relationships.
 * </p>
 *
 * @see Company
 * @see Benefeciar
 */
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
}