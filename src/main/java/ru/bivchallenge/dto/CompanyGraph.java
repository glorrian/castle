package ru.bivchallenge.dto;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The {@code CompanyGraph} class represents a graph structure that models relationships between
 * a company (head company) and associated entities such as {@link NaturalEntity} and {@link LegalEntity}.
 */
public class CompanyGraph {
    private final Graph<String, DefaultEdge> graph;
    private final Company headCompany;
    private final Map<Long, NaturalEntity> naturalEntityMap;
    private final Map<Long, LegalEntity> legalEntityMap;
    private final Map<Long, LegalEntity> legalEntityRegistry;

    public CompanyGraph(Company headCompany, Map<Long, LegalEntity> legalEntityRegistry) {
        this.headCompany = headCompany;
        this.legalEntityRegistry = legalEntityRegistry;

        naturalEntityMap = new HashMap<>();
        legalEntityMap = new HashMap<>();
        graph = new Multigraph<>(DefaultEdge.class);
        graph.addVertex("H:" + headCompany.id());
    }

    public Graph<String, DefaultEdge> getGraph() {
        return graph;
    }

    public Company getHeadCompany() {
        return headCompany;
    }

    public void addNaturalEntity(NaturalEntity naturalEntity) {
        String naturalVertex = "N:" + naturalEntity.id();
        String companyVertex = naturalEntity.getCompanyId() == headCompany.id() ? "H:" + headCompany.id() : "L:" + naturalEntity.getCompanyId();

        if (!naturalEntityMap.containsKey(naturalEntity.id())) {
            graph.addVertex(naturalVertex);
            naturalEntityMap.put(naturalEntity.id(), naturalEntity);
        }

        if (!graph.containsVertex(companyVertex) && legalEntityRegistry.containsKey(naturalEntity.getCompanyId())) {
            addLegalEntity(legalEntityRegistry.get(naturalEntity.getCompanyId()));
        }

        if (!graph.containsEdge(naturalVertex, companyVertex)) {
            graph.addEdge(naturalVertex, companyVertex);
        }
    }

    public void addLegalEntity(LegalEntity legalEntity) {
        String legalVertex = "L:" + legalEntity.id();
        String parentVertex = legalEntity.getCompanyId() == headCompany.id() ? "H:" + headCompany.id() : "L:" + legalEntity.getCompanyId();

        if (!legalEntityMap.containsKey(legalEntity.id())) {
            graph.addVertex(legalVertex);
            legalEntityMap.put(legalEntity.id(), legalEntity);
        }

        if (!graph.containsVertex(parentVertex) && legalEntityRegistry.containsKey(legalEntity.getCompanyId())) {
            addLegalEntity(legalEntityRegistry.get(legalEntity.getCompanyId()));
        }

        if (!graph.containsEdge(legalVertex, parentVertex)) {
            graph.addEdge(legalVertex, parentVertex);
        }
    }

    public BenefeciarSet getBeneficiaries() {
        BenefeciarSet beneficiaries = new BenefeciarSet(headCompany);

        for (Map.Entry<Long, NaturalEntity> entry : naturalEntityMap.entrySet()) {
            String naturalEntityVertex = "N:" + entry.getKey();
            double totalOwnership = calculateTotalOwnership(naturalEntityVertex, "H:" + headCompany.id(), 1.0, new HashSet<>());
            if (totalOwnership > 0.25) {
                beneficiaries.getBeneficiaries().add(new Benefeciar(entry.getValue(), totalOwnership));
            }
        }

        return beneficiaries;
    }

    private double calculateTotalOwnership(String source, String target, double cumulativePercentage, Set<String> visited) {
        if (source.equals(target)) {
            return cumulativePercentage;
        }

        if (visited.contains(source)) {
            return 0.0;
        }

        visited.add(source);
        double totalOwnership = 0.0;

        for (DefaultEdge edge : graph.outgoingEdgesOf(source)) {
            String connectedVertex = graph.getEdgeTarget(edge).equals(source) ? graph.getEdgeSource(edge) : graph.getEdgeTarget(edge);

            double ownershipPercentage = getOwnershipPercentage(source, connectedVertex);
            totalOwnership += calculateTotalOwnership(connectedVertex, target, cumulativePercentage * ownershipPercentage, visited);
        }

        visited.remove(source);
        return totalOwnership;
    }

    private double getOwnershipPercentage(String source, String target) {
        if (source.startsWith("N:")) {
            NaturalEntity naturalEntity = naturalEntityMap.get(Long.parseLong(source.split(":")[1]));
            return naturalEntity != null ? naturalEntity.getSharePercent() : 0.0;
        } else if (source.startsWith("L:")) {
            LegalEntity legalEntity = legalEntityMap.get(Long.parseLong(source.split(":")[1]));
            return legalEntity != null ? legalEntity.getSharePercent() : 0.0;
        }
        return 0.0;
    }
}
