package ru.bivchallenge.data

import ru.bivchallenge.dto.Benefeciar
import ru.bivchallenge.dto.Company
import ru.bivchallenge.dto.LegalEntity
import ru.bivchallenge.dto.NaturalEntity
import spock.lang.Specification

class CompanyGraphManagerSpec extends Specification {

    Company headCompany = new Company(1L, "321", "123", "Head Company")

    def "Graph should initialize with the head company vertex"() {
        given:
        def legalEntityRegistry = [:] as Map<Long, LegalEntity>

        when:
        def manager = new CompanyGraphManager(headCompany, legalEntityRegistry)

        then:
        manager.getGraph().vertexSet().contains("H:1")
    }

    def "Adding a natural entity should add a vertex and connect to its company"() {
        given:
        def legalEntityRegistry = [:] as Map<Long, LegalEntity>
        def naturalEntity = new NaturalEntity(101L, 1L, "124", "Doe", "John", "")

        when:
        def manager = new CompanyGraphManager(headCompany, legalEntityRegistry)
        manager.addEntity(naturalEntity)

        then:
        manager.getGraph().vertexSet().contains("N:101")
        manager.getGraph().containsEdge("N:101", "H:1")
    }

    def "Adding a legal entity should add a vertex and connect to its parent"() {
        given:
        def legalEntity = new LegalEntity(201L, 1L, "321", "123", "Legal Entity")
        def legalEntityRegistry = [201L: legalEntity]

        when:
        def manager = new CompanyGraphManager(headCompany, legalEntityRegistry)
        manager.addEntity(legalEntity)

        then:
        manager.getGraph().vertexSet().contains("L:201")
        manager.getGraph().containsEdge("L:201", "H:1")
    }

    def "Adding multiple entities should create the correct graph structure"() {
        given:
        def parentEntity = new LegalEntity(201L, 1L, "321", "123", "Parent Entity")
        def childEntity = new LegalEntity(202L, 201L, "322", "124", "Child Entity")
        def legalEntityRegistry = [201L: parentEntity, 202L: childEntity]
        /*
           Expected graph structure:

           H:1 (Head Company)
             |
             L:201 (Parent Entity)
               |
               L:202 (Child Entity)
       */

        when:
        def manager = new CompanyGraphManager(headCompany, legalEntityRegistry)
        manager.addEntity(childEntity)

        then:
        manager.getGraph().vertexSet().containsAll(["L:201", "L:202", "H:1"])
        manager.getGraph().containsEdge("L:202", "L:201")
        manager.getGraph().containsEdge("L:201", "H:1")
    }

    def "getBeneficiaries should return natural entities with ownership > 25%"() {
        given:
        def legalEntityRegistry = [:] as Map<Long, LegalEntity>
        def naturalEntity1 = new NaturalEntity(101L, 1L, "123", "Doe", "John", "")
        def naturalEntity2 = new NaturalEntity(102L, 1L, "124", "Doe", "Jane", "")
        naturalEntity1.sharePercent = 0.8
        naturalEntity2.sharePercent = 0.2
        def benefeciar = new Benefeciar(naturalEntity1, 0.8)

        when:
        def manager = new CompanyGraphManager(headCompany, legalEntityRegistry)
        manager.addEntity(naturalEntity1)
        manager.addEntity(naturalEntity2)

        and:
        def beneficiaries = manager.getBeneficiaries().getBeneficiaries()

        then:
        beneficiaries.size() == 1
        beneficiaries.contains(benefeciar)
    }

    def "getBeneficiaries should handle entities connected via multiple levels"() {
        given:
        def legalEntity1 = new LegalEntity(201L, 1L, "321", "123", "Legal Entity 1")
        def legalEntity2 = new LegalEntity(202L, 201L, "322", "124", "Legal Entity 2")
        def naturalEntity = new NaturalEntity(101L, 202L, "124", "Doe", "John", "")
        legalEntity1.sharePercent = 0.8
        legalEntity2.sharePercent = 0.6
        naturalEntity.sharePercent = 0.7
        def legalEntityRegistry = [201L: legalEntity1, 202L: legalEntity2]
        /*
            Expected graph structure:

            H:1 (Head Company)
              |
              L:201 (Legal Entity 1, 50%)
                |
                L:202 (Legal Entity 2, 40%)
                  |
                  N:101 (John Doe, 70%)
        */

        when:
        def manager = new CompanyGraphManager(headCompany, legalEntityRegistry)
        manager.addEntity(naturalEntity)

        and:
        def beneficiaries = manager.getBeneficiaries().getBeneficiaries()

        then:
        beneficiaries.size() == 1
        beneficiaries.toArray()[0].naturalEntity == naturalEntity
        beneficiaries.toArray()[0].percent == 0.8 * 0.6 * 0.7
    }
}
