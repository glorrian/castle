package ru.bivchallenge.dto;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;

import java.util.HashMap;
import java.util.Map;

public class CompanyGraph {
    private final Graph<String, DefaultWeightedEdge> graph;
    private final Company headCompany;
    private final Map<Long, NaturalEntity> naturalEntityMap;
    private final Map<Long, LegalEntity> legalEntityMap;

    public CompanyGraph(Company headCompany) {
        this.headCompany = headCompany;

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
        if (naturalEntityMap.containsKey(naturalEntity.id())) {
            graph.addVertex("N:" + naturalEntity.id());
            naturalEntityMap.put(naturalEntity.id(), naturalEntity);
        }

        if (!graph.containsEdge(
                "N:" + naturalEntity.id(),
                "L:" + naturalEntity.getCompanyId())
        ) {
            graph.addEdge("N:" + naturalEntity.id(),
                    "L:" + naturalEntity.getCompanyId(),
                    new DefaultWeightedEdge());
        }
    }

    public void addLegalEntity(LegalEntity legalEntity) {
        if (legalEntityMap.containsKey(legalEntity.id())) {
            graph.addVertex("N:" + legalEntity.id());
            legalEntityMap.put(legalEntity.id(), legalEntity);
        }

        if (!graph.containsEdge(
                "L:" + legalEntity.id(),
                "L:" + legalEntity.getCompanyId())
        ) {
            graph.addEdge("L:" + legalEntity.id(),
                    "L:" + legalEntity.getCompanyId(),
                    new DefaultWeightedEdge());
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
