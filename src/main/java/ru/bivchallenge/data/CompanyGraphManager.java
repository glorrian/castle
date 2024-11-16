package ru.bivchallenge.data;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import ru.bivchallenge.dto.*;

import java.util.*;

/**
 * Manages a graph representation of a company's structure, including relationships
 * between the head company, legal entities, and natural entities.
 */
public class CompanyGraphManager {
    private final Graph<String, DefaultEdge> graph;
    private final Company headCompany;
    private final Map<Long, NaturalEntity> naturalEntityMap;
    private final Map<Long, LegalEntity> legalEntityMap;
    private final Map<Long, LegalEntity> legalEntityRegistry;

    public CompanyGraphManager(Company headCompany, Map<Long, LegalEntity> legalEntityRegistry) {
        this.headCompany = headCompany;
        this.legalEntityRegistry = legalEntityRegistry;

        this.naturalEntityMap = new HashMap<>();
        this.legalEntityMap = new HashMap<>();
        this.graph = new Multigraph<>(DefaultEdge.class);

        graph.addVertex(vertexId("H", headCompany.id()));
    }

    public Graph<String, DefaultEdge> getGraph() {
        return graph;
    }

    public Company getHeadCompany() {
        return headCompany;
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

        graph.addEdge(naturalVertex, companyVertex);
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

        graph.addEdge(legalVertex, parentVertex);
    }

    /**
     * Calculates and returns the beneficiaries (natural entities with more than 25% ownership).
     *
     * @return a set of beneficiaries
     */
    public BenefeciarRegistry getBeneficiaries() {
        BenefeciarRegistry beneficiaries = new BenefeciarRegistry(headCompany);

        for (Map.Entry<Long, NaturalEntity> entry : naturalEntityMap.entrySet()) {
            String naturalVertex = vertexId("N", entry.getKey());
            double totalOwnership = calculateTotalOwnership(
                    naturalVertex,
                    vertexId("H", headCompany.id()),
                    1.0,
                    new HashSet<>()
            );

            if (totalOwnership > 0.25) {
                beneficiaries.getBeneficiaries().add(
                        new Benefeciar(entry.getValue(), totalOwnership)
                );
            }
        }

        return beneficiaries;
    }

    private double calculateTotalOwnership(String source, String target, double cumulativePercentage, Set<String> visited) {
        if (source.equals(target)) {
            return cumulativePercentage;
        }

        if (!visited.add(source)) {
            return 0.0;
        }

        double totalOwnership = 0.0;

        for (DefaultEdge edge : graph.outgoingEdgesOf(source)) {
            String connectedVertex = getConnectedVertex(source, edge);
            double ownershipPercentage = getOwnershipPercentage(source, connectedVertex);
            totalOwnership += calculateTotalOwnership(
                    connectedVertex, target, cumulativePercentage * ownershipPercentage, visited
            );
        }

        visited.remove(source);
        return totalOwnership;
    }

    private double getOwnershipPercentage(String source, String target) {
        long entityId = Long.parseLong(source.split(":")[1]);

        if (source.startsWith("N:")) {
            NaturalEntity naturalEntity = naturalEntityMap.get(entityId);
            return naturalEntity != null ? naturalEntity.getSharePercent() : 0.0;
        } else if (source.startsWith("L:")) {
            LegalEntity legalEntity = legalEntityMap.get(entityId);
            return legalEntity != null ? legalEntity.getSharePercent() : 0.0;
        }

        return 0.0;
    }

    private String getCompanyVertex(long companyId) {
        return companyId == headCompany.id() ? vertexId("H", headCompany.id()) : vertexId("L", companyId);
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

    private String getConnectedVertex(String source, DefaultEdge edge) {
        return graph.getEdgeTarget(edge).equals(source) ? graph.getEdgeSource(edge) : graph.getEdgeTarget(edge);
    }
}
