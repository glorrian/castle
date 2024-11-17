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
        /*
            Expected graph structure:

            H:1 (Head Company)
             |
             N:101 (John Doe, 80%) - Ownership > 25%
             |
             N:102 (Jane Doe, 20%) - Ownership <= 25%

            Expected beneficiaries:
            - John Doe (80%)
        */

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

    def "getBeneficiaries should handle entities connected via multiple levels2"() {
        given:
        def legalEntity1 = new LegalEntity(201L, 1L, "321", "123", "Legal Entity 1")
        def legalEntity2 = new LegalEntity(202L, 1L, "322", "124", "Legal Entity 2")
        def legalEntity3 = new LegalEntity(203L, 202L, "322", "125", "Legal Entity 3")
        def naturalEntity = new NaturalEntity(101L, 1L, "124", "Doe", "John", "")
        def naturalEntity1 = new NaturalEntity(101L, 203L, "124", "Doe", "John", "")
        def naturalEntity2 = new NaturalEntity(102L, 201L, "125", "Don", "Jod", "")
        def naturalEntity21 = new NaturalEntity(102L, 202L, "125", "Don", "Jod", "")
        def naturalEntity3 = new NaturalEntity(103L, 201L, "126", "Dok", "Jok", "")

        legalEntity1.sharePercent = 0.3
        legalEntity2.sharePercent = 0.5
        legalEntity3.sharePercent = 0.5
        naturalEntity.sharePercent = 0.2
        naturalEntity1.sharePercent = 1
        naturalEntity2.sharePercent = 0.5
        naturalEntity21.sharePercent = 0.5
        naturalEntity3.sharePercent = 0.5
        def legalEntityRegistry = [201L: legalEntity1, 202L: legalEntity2, 203L: legalEntity3]
        /*
            Expected graph structure:

        /    |       \
      (30%)   (20%)   (50%)
      /               \
   L:201    N:101       L:202
    /   \              |
(50%)  (50%)       (50%) \
  |      |             \   \
N:102  N:103         L:203  N:102
                   (50%) |
                        |
                      N:101
                   (100% from L:203)
        */

        when:
        def manager = new CompanyGraphManager(headCompany, legalEntityRegistry)
        manager.addEntity(legalEntity1)
        manager.addEntity(legalEntity2)
        manager.addEntity(legalEntity3)

        manager.addEntity(naturalEntity)
        manager.addEntity(naturalEntity1)
        manager.addEntity(naturalEntity2)
        manager.addEntity(naturalEntity21)
        manager.addEntity(naturalEntity3)

        and:
        def beneficiaries = manager.getBeneficiaries().getBeneficiaries()

        then:
        beneficiaries.size() == 2
        beneficiaries.toArray()[0].naturalEntity == naturalEntity
        beneficiaries.toArray()[0].percent == 0.2+0.5*0.5*1
        beneficiaries.toArray()[1].naturalEntity == naturalEntity2
        beneficiaries.toArray()[1].percent == 0.3*0.5+0.5*0.5
    }

    /**
     * <img src="../../../../resources/image/graph.jpg" alt="Graph Diagram" style="width:400px;height:auto;">
     */
    def "getBeneficiaries should handle entities connected via multiple levels HARD"() {
        given:
        def l1 = new LegalEntity(2L, 1L, "L1", "111", "Legal Entity 1")
        def l2 = new LegalEntity(3L, 1L, "L2", "112", "Legal Entity 2")
        def l3 = new LegalEntity(4L, 2L, "L3", "113", "Legal Entity 3")
        def l4 = new LegalEntity(5L, 4L, "L4", "114", "Legal Entity 4")
        def l5 = new LegalEntity(6L, 4L, "L5", "115", "Legal Entity 5")
        def l6 = new LegalEntity(7L, 5L, "L6", "116", "Legal Entity 6")
        def l7 = new LegalEntity(8L, 6L, "L7", "117", "Legal Entity 7")
        def l8 = new LegalEntity(9L, 7L, "L8", "118", "Legal Entity 8")
        def l9_90 = new LegalEntity(10L, 8L, "L9", "119", "Legal Entity 9")
        def l9_50 = new LegalEntity(10L, 7L, "L9", "120", "Legal Entity 9")

        def f1_50 = new NaturalEntity(1L, 9L, "F1", "121", "F1", "")
        def f2_50 = new NaturalEntity(2L, 9L, "F2", "122", "F2", "")
        def f2_100 = new NaturalEntity(2L, 10L, "F2", "123", "F2", "")
        def f3_10 = new NaturalEntity(3L, 8L, "F3", "124", "F3", "")
        def f3_100 = new NaturalEntity(3L, 3L, "F3", "125", "F3", "")
        def f3_30 = new NaturalEntity(3L, 6L, "F3", "125", "F3", "")
        and:
        l1.sharePercent = 0.8
        l2.sharePercent = 0.2
        l3.sharePercent = 1.0
        l4.sharePercent = 0.2
        l5.sharePercent = 0.8
        l6.sharePercent = 1.0
        l7.sharePercent = 0.7
        l8.sharePercent = 0.5
        l9_90.sharePercent = 0.9
        l9_50.sharePercent = 0.5
        f1_50.sharePercent = 0.5
        f2_50.sharePercent = 0.5
        f2_100.sharePercent = 1.0
        f3_10.sharePercent = 0.1
        f3_100.sharePercent = 1.0
        f3_30.sharePercent = 0.3

        def legalEntityRegistry = [2L: l1, 3L: l2, 4L: l3, 5L: l4, 6L: l5, 7L: l6, 8L: l7, 9L: l8, 10L: l9_90]

        when:
        def manager = new CompanyGraphManager(headCompany, legalEntityRegistry)
        manager.addEntity(l1)
        manager.addEntity(l2)
        manager.addEntity(l3)
        manager.addEntity(l4)
        manager.addEntity(l5)
        manager.addEntity(l6)
        manager.addEntity(l7)
        manager.addEntity(l8)
        manager.addEntity(l9_90)
        manager.addEntity(l9_50)
        manager.addEntity(f1_50)
        manager.addEntity(f2_50)
        manager.addEntity(f2_100)
        manager.addEntity(f3_10)
        manager.addEntity(f3_100)
        manager.addEntity(f3_30)

        and:
        def beneficiaries = manager.getBeneficiaries().getBeneficiaries()

        then:
        beneficiaries.size() == 2
        beneficiaries.toArray()[0].naturalEntity == f2_100
        beneficiaries.toArray()[0].percent == 0.5232
        beneficiaries.toArray()[1].naturalEntity == f3_100
        beneficiaries.toArray()[1].percent == 0.4368
    }


}
