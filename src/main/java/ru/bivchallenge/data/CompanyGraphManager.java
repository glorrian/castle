package ru.bivchallenge.data;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DirectedMultigraph;
import ru.bivchallenge.dto.*;

import java.util.*;

/**
 * Manages a graph representation of a company's structure, including relationships
 * between the head company, legal entities, and natural entities.
 */
public class CompanyGraphManager {
    private final Graph<String, WeightedEdge> graph;
    private final Company headCompany;
    private final Map<Long, NaturalEntity> naturalEntityMap;
    private final Map<Long, LegalEntity> legalEntityMap;
    private final Map<Long, LegalEntity> legalEntityRegistry;

    /**
     * Constructs a new CompanyGraphManager with the given head company and legal entity registry.
     *
     * @param headCompany the head company
     * @param legalEntityRegistry a map of legal entities by their IDs
     */
    public CompanyGraphManager(Company headCompany, Map<Long, LegalEntity> legalEntityRegistry) {
        this.headCompany = headCompany;
        this.legalEntityRegistry = legalEntityRegistry;

        this.naturalEntityMap = new HashMap<>();
        this.legalEntityMap = new HashMap<>();
        this.graph = new DirectedMultigraph<>(WeightedEdge.class);

        graph.addVertex(vertexId("H", headCompany.id()));
    }

    /**
     * Returns the graph representing the company's structure.
     *
     * @return the graph of the company structure
     */
    public Graph<String, WeightedEdge> getGraph() {
        return graph;
    }

    /**
     * Returns the head company.
     *
     * @return the head company
     */
    public Company getHeadCompany() {
        return headCompany;
    }

    public String getCompanyVertex(long companyId) {
        return companyId == headCompany.id() ? vertexId("H", headCompany.id()) : vertexId("L", companyId);
    }

    public String getNaturalVertex(long naturalId) {
        return vertexId("N", naturalId);
    }

    public LegalEntity getLegalEntity(long companyId) {
        return legalEntityRegistry.get(companyId);
    }

    public NaturalEntity getNaturalEntity(long naturalId) {
        return naturalEntityMap.get(naturalId);
    }

    /**
     * Adds a natural entity to the graph and connects it to its associated company.
     *
     * @param naturalEntity the natural entity to add
     */
    public void addEntity(NaturalEntity naturalEntity) {
        String naturalVertex = vertexId("N", naturalEntity.id());
        String companyVertex = getCompanyVertex(naturalEntity.getCompanyId());

        if (naturalEntityMap.putIfAbsent(naturalEntity.id(), naturalEntity) == null) {
            graph.addVertex(naturalVertex);
        }

        if (!graph.containsVertex(companyVertex)) {
            addLegalEntityIfRegistered(naturalEntity.getCompanyId());
        }

        WeightedEdge edge = new WeightedEdge(naturalEntity.getSharePercent());
        graph.addEdge(naturalVertex, companyVertex, edge);
    }

    /**
     * Adds a legal entity to the graph and connects it to its parent entity or the head company.
     *
     * @param legalEntity the legal entity to add
     */
    public void addEntity(LegalEntity legalEntity) {
        String legalVertex = vertexId("L", legalEntity.id());
        String parentVertex = getCompanyVertex(legalEntity.getCompanyId());

        if (legalEntityMap.putIfAbsent(legalEntity.id(), legalEntity) == null) {
            graph.addVertex(legalVertex);
        }

        if (!graph.containsVertex(parentVertex)) {
            addLegalEntityIfRegistered(legalEntity.getCompanyId());
        }

        WeightedEdge edge = new WeightedEdge(legalEntity.getSharePercent());
        graph.addEdge(legalVertex, parentVertex, edge);
    }

    /**
     * Calculates and returns the beneficiaries (natural entities with more than 25% ownership).
     *
     * @return a registry of beneficiaries
     */
    public BenefeciarRegistry getBeneficiaries() {
        BenefeciarRegistry beneficiaries = new BenefeciarRegistry(headCompany);

        for (Map.Entry<Long, NaturalEntity> entry : naturalEntityMap.entrySet()) {
            String naturalVertex = vertexId("N", entry.getKey());
            double totalOwnership = defineOwnershipPercentageForNaturalEntity(naturalVertex);

            if (totalOwnership > 0.25) {
                beneficiaries.getBeneficiaries().add(
                        new Benefeciar(entry.getValue(), totalOwnership)
                );
            }
        }

        return beneficiaries;
    }

    private double defineOwnershipPercentageForNaturalEntity(String naturalVertex) {
        AllDirectedPaths<String, WeightedEdge> allPaths = new AllDirectedPaths<>(graph);
        List<GraphPath<String, WeightedEdge>> paths = allPaths.getAllPaths(naturalVertex, vertexId("H", headCompany.id()), true, null);
        double totalOwnership = 0.0;
        for (GraphPath<String, WeightedEdge> path : paths) {
            totalOwnership += path.getEdgeList().stream().mapToDouble(WeightedEdge::getWeight).reduce(1, (a, b) -> a * b);
        }
        return totalOwnership;
    }

    private void addLegalEntityIfRegistered(long companyId) {
        LegalEntity legalEntity = legalEntityRegistry.get(companyId);
        if (legalEntity != null) {
            addEntity(legalEntity);
        }
    }

    private String vertexId(String prefix, long id) {
        return prefix + ":" + id;
    }
}
