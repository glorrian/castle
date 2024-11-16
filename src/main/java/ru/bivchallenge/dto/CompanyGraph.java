package ru.bivchallenge.dto;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code CompanyGraph} class represents a graph structure that models relationships between
 * a company (head company) and associated entities such as {@link NaturalEntity} and {@link LegalEntity}.
 * <p>
 * It uses a {@link Multigraph} from the JGraphT library to manage the graph representation, where:
 * <ul>
 *     <li>Vertices are instances of {@link Entity} (e.g., {@link Company}, {@link NaturalEntity}, {@link LegalEntity}).</li>
 *     <li>Edges are instances of {@link DefaultWeightedEdge} to represent connections between entities.</li>
 * </ul>
 *
 * <p>The graph is initialized with the head company as its root vertex. Other entities, such as
 * {@link NaturalEntity} and {@link LegalEntity}, can be added as vertices and connected to the head company.
 *
 * @see org.jgrapht.Graph
 * @see org.jgrapht.graph.Multigraph
 */
public class CompanyGraph {
    private final Graph<String, DefaultWeightedEdge> graph;
    private final Company headCompany;
    private final Map<Long, NaturalEntity> naturalEntityMap;
    private final Map<Long, LegalEntity> legalEntityMap;
    private final Map<Long, LegalEntity> legalEntityRegistry;

    public CompanyGraph(Company headCompany, Map<Long, LegalEntity> legalEntityRegistry) {
        this.headCompany = headCompany;
        this.legalEntityRegistry = legalEntityRegistry;

        naturalEntityMap = new HashMap<>();
        legalEntityMap = new HashMap<>();
        graph = new Multigraph<>(DefaultWeightedEdge.class);
        graph.addVertex("H:" + headCompany.id());
    }

    public Graph<String, DefaultWeightedEdge> getGraph() {
        return graph;
    }

    public Company getHeadCompany() {
        return headCompany;
    }

    public void addNaturalEntity(NaturalEntity naturalEntity) {
        if (!naturalEntityMap.containsKey(naturalEntity.id())) {
            graph.addVertex("N:" + naturalEntity.id());
            naturalEntityMap.put(naturalEntity.id(), naturalEntity);
        }

        if (!graph.containsEdge("N:" + naturalEntity.id(), "L:" + naturalEntity.getCompanyId())) {
            if (naturalEntity.getCompanyId() == headCompany.id()) {
                graph.addEdge("N:" + naturalEntity.id(), "H:" + headCompany.id(), new DefaultWeightedEdge());
                return;
            }
            graph.addEdge("N:" + naturalEntity.id(), "L:" + naturalEntity.getCompanyId(), new DefaultWeightedEdge());

            if (!graph.containsVertex("L:" + naturalEntity.getCompanyId()) && legalEntityRegistry.containsKey(naturalEntity.getCompanyId())) {
                graph.addVertex("L:" + naturalEntity.getCompanyId());
                addLegalEntity(legalEntityRegistry.get(naturalEntity.getCompanyId()));
            }
            graph.addEdge("N:" + naturalEntity.id(), "L:" + naturalEntity.getCompanyId(), new DefaultWeightedEdge());
        }
    }

    public void addLegalEntity(LegalEntity legalEntity) {
        if (!legalEntityMap.containsKey(legalEntity.id())) {
            graph.addVertex("L:" + legalEntity.id());
            legalEntityMap.put(legalEntity.id(), legalEntity);
        }

        if (!graph.containsEdge("L:" + legalEntity.id(), "L:" + legalEntity.getCompanyId())) {
            if (legalEntity.getCompanyId() == headCompany.id()) {
                graph.addEdge("L:" + legalEntity.id(), "H:" + headCompany.id(), new DefaultWeightedEdge());
                return;
            }
            if (!graph.containsVertex("L:" + legalEntity.getCompanyId()) && legalEntityRegistry.containsKey(legalEntity.getCompanyId())) {
                graph.addVertex("L:" + legalEntity.getCompanyId());
                addLegalEntity(legalEntityRegistry.get(legalEntity.getCompanyId()));
            }
            graph.addEdge("L:" + legalEntity.id(), "L:" + legalEntity.getCompanyId(), new DefaultWeightedEdge());
        }
    }

    public BenefeciarSet getBeneficiaries() {
        BenefeciarSet beneficiaries = new BenefeciarSet(headCompany);

        for (Map.Entry<Long, NaturalEntity> entry : naturalEntityMap.entrySet()) {
            String naturalEntityVertex = "N:" + entry.getKey();
            double totalOwnership = calculateTotalOwnership(naturalEntityVertex, "H:" + headCompany.id(), 1.0);
            if (totalOwnership > 25.0) {
                beneficiaries.addBenefeciar(new Benefeciar(entry.getValue(), (long) totalOwnership));
            }
        }

        return beneficiaries;
    }

    private double calculateTotalOwnership(String source, String target, double multiplier) {
        if (source.equals(target)) {
            return multiplier;
        }

        double ownership = 0.0;

        for (DefaultWeightedEdge edge : graph.outgoingEdgesOf(source)) {
            String connectedVertex = graph.getEdgeTarget(edge);
            if (connectedVertex.equals(source)) {
                connectedVertex = graph.getEdgeSource(edge);
            }

            double edgeWeight = graph.getEdgeWeight(edge);
            ownership += calculateTotalOwnership(connectedVertex, target, multiplier * edgeWeight / 100);
        }

        return ownership;
    }
}
