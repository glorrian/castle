package ru.bivchallenge.processor


import ru.bivchallenge.data.CompanyGraphManager
import ru.bivchallenge.dto.Company
import ru.bivchallenge.dto.LegalEntity
import spock.lang.Specification

class GraphRepairProcessorSpec extends Specification {

    def "should repair single missing weight"() {
        given:
        def company = new Company(1L, "321", "123", "Head Company")
        def manager = createGraphWithSingleMissingWeight(company)

        when:
        def processor = new GraphRepairProcessor()
        processor.apply(manager)

        then:
        def graph = manager.getGraph()
        graph.getEdge("L:2", "H:1").getWeight() == 0.3
        graph.getEdge("L:3", "H:1").getWeight() == 0.7
    }

    def "should repair multiple missing weights proportionally"() {
        given:
        def company = new Company(1L, "321", "123", "Head Company")
        def manager = createGraphWithProportionalWeights(company)

        when:
        def processor = new GraphRepairProcessor()
        processor.apply(manager)

        then:
        def graph = manager.getGraph()
        def edgeL2 = graph.getEdge("L:2", "H:1")
        def edgeL3 = graph.getEdge("L:3", "H:1")
        edgeL2.getWeight() == 0.3
        edgeL3.getWeight() == 0.7
    }

    def "should repair complex graph with missing weights"() {
        given:
        def company = new Company(1L, "321", "123", "Head Company")
        def manager = createComplexGraphWithMissingWeights(company)

        when:
        def processor = new GraphRepairProcessor()
        processor.apply(manager)

        then:
        def graph = manager.getGraph()
        def edgeL2 = graph.getEdge("L:2", "H:1")
        def edgeL3 = graph.getEdge("L:3", "H:1")
        def edgeL4 = graph.getEdge("L:4", "L:3")
        def edgeL5 = graph.getEdge("L:5", "L:3")
        edgeL2.getWeight() == 0.5
        edgeL3.getWeight() == 0.5
        edgeL4.getWeight() == 0.5
        edgeL5.getWeight() == 0.5
    }

    private static CompanyGraphManager createGraphWithSingleMissingWeight(Company company) {
        def registry = [:] as Map<Long, LegalEntity>
        def manager = new CompanyGraphManager(company, registry)

        def L2 = new LegalEntity(2L, 1L, "L2", "123", "Legal Entity 2")
        L2.share = 30
        def L3 = new LegalEntity(3L, 1L, "L3", "124", "Legal Entity 3")
        L3.share = 70
        L3.sharePercent = 0.7
        registry.put(2L, L2)
        registry.put(3L, L3)
        manager.addEntity(L2)
        manager.addEntity(L3)

        return manager
    }

    private static CompanyGraphManager createGraphWithProportionalWeights(Company company) {
        def registry = [:] as Map<Long, LegalEntity>
        def manager = new CompanyGraphManager(company, registry)

        def L2 = new LegalEntity(2L, 1L, "L2", "123", "Legal Entity 2")
        L2.share = 30
        def L3 = new LegalEntity(3L, 1L, "L3", "124", "Legal Entity 3")
        L3.share = 70
        registry.put(2L, L2)
        registry.put(3L, L3)
        manager.addEntity(L2)
        manager.addEntity(L3)

        return manager
    }

    private static CompanyGraphManager createComplexGraphWithMissingWeights(Company company) {
        def registry = [:] as Map<Long, LegalEntity>
        def manager = new CompanyGraphManager(company, registry)

        def L2 = new LegalEntity(2L, 1L, "L2", "123", "Legal Entity 2")
        L2.sharePercent = 0.5
        def L3 = new LegalEntity(3L, 1L, "L3", "124", "Legal Entity 3")
        def L4 = new LegalEntity(4L, 3L, "L4", "125", "Legal Entity 4")
        def L5 = new LegalEntity(5L, 3L, "L5", "126", "Legal Entity 5")
        L4.share = 50
        L5.share = 50
        registry.put(2L, L2)
        registry.put(3L, L3)
        registry.put(4L, L4)
        registry.put(5L, L5)
        manager.addEntity(L2)
        manager.addEntity(L3)
        manager.addEntity(L4)
        manager.addEntity(L5)

        return manager
    }
}
