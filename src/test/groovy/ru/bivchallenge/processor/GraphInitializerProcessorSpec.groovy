package ru.bivchallenge.processor

import ru.bivchallenge.data.CompanyGraphManager
import ru.bivchallenge.dto.Company
import ru.bivchallenge.dto.LegalEntity
import spock.lang.Specification

class GraphInitializerProcessorSpec extends Specification {

    def "should initialize CompanyGraphManager for each company"() {
        given:
        Map<Long, LegalEntity> legalEntityRegistry = [
                1L: new LegalEntity(1L, 1L, "123456789", "987654321", "Company A"),
                2L: new LegalEntity(2L, 2L, "223344556", "1122334455", "Company B")
        ]

        Map<Long, Company> companyMap = [
                1L: new Company(1L, "12345", "67890", "Company A"),
                2L: new Company(2L, "54321", "09876", "Company B")
        ]

        def processor = new GraphInitializerProcessor(legalEntityRegistry)

        when:
        def result = processor.apply(companyMap)

        then:
        result.size() == 2
        result[1L] instanceof CompanyGraphManager
        result[2L] instanceof CompanyGraphManager
    }

    def "should handle empty company map gracefully"() {
        given:
        Map<Long, LegalEntity> legalEntityRegistry = [:] // Empty registry
        Map<Long, Company> companyMap = [:] // Empty map

        def processor = new GraphInitializerProcessor(legalEntityRegistry)

        when:
        def result = processor.apply(companyMap)

        then:
        result.isEmpty()
    }
}
