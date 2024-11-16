package ru.bivchallenge.data

import ru.bivchallenge.dto.Benefeciar
import ru.bivchallenge.dto.Company
import ru.bivchallenge.dto.NaturalEntity
import spock.lang.Specification

class BenefeciarRegistrySpec extends Specification {

    def "should correctly initialize BenefeciarRegistry with a company"() {
        given:
        def company = new Company(1L, "1234567890123", "1234567890", "Test Company")

        when:
        def registry = new BenefeciarRegistry(company)

        then:
        registry.getCompany() == company
        registry.getBeneficiaries().isEmpty()
    }

    def "should allow adding beneficiaries to the registry"() {
        given:
        def company = new Company(1L, "1234567890123", "1234567890", "Test Company")
        def registry = new BenefeciarRegistry(company)

        and:
        def beneficiary = new Benefeciar(null, 0.5) // Replace null with a proper NaturalEntity or object if required

        when:
        registry.getBeneficiaries().add(beneficiary)

        then:
        registry.getBeneficiaries().size() == 1
        registry.getBeneficiaries().contains(beneficiary)
    }

    def "should allow checking if the registry contains a beneficiary"() {
        given:
        def company = new Company(1L, "1234567890123", "1234567890", "Test Company")
        def registry = new BenefeciarRegistry(company)
        def beneficiary = new Benefeciar(null, 0.3) // Replace null with a proper NaturalEntity or object if required

        when:
        registry.getBeneficiaries().add(beneficiary)

        then:
        registry.getBeneficiaries().contains(beneficiary)
    }

    def "should allow working with multiple beneficiaries"() {
        given:
        def company = new Company(1L, "1234567890123", "1234567890", "Test Company")
        def registry = new BenefeciarRegistry(company)

        and:
        def beneficiary1 = new Benefeciar(new NaturalEntity(1L, 1L, "123", "Doe", "John", ""), 0.4)
        def beneficiary2 = new Benefeciar(new NaturalEntity(2L, 1L, "123", "Doe", "John", ""), 0.6)

        when:
        registry.getBeneficiaries().add(beneficiary1)
        registry.getBeneficiaries().add(beneficiary2)

        then:
        registry.getBeneficiaries().size() == 2
        registry.getBeneficiaries().contains(beneficiary1)
        registry.getBeneficiaries().contains(beneficiary2)
    }

    def "should ensure that duplicate beneficiaries are not allowed"() {
        given:
        def company = new Company(1L, "1234567890123", "1234567890", "Test Company")
        def registry = new BenefeciarRegistry(company)
        def beneficiary = new Benefeciar(null, 0.4)

        when:
        registry.getBeneficiaries().add(beneficiary)
        registry.getBeneficiaries().add(beneficiary)

        then:
        registry.getBeneficiaries().size() == 1
    }
}
