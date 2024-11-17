package ru.bivchallenge.processor;

import org.jgrapht.Graph;
import ru.bivchallenge.data.CompanyGraphManager;
import ru.bivchallenge.dto.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GraphRepairProcessor implements UnaryProcessor<CompanyGraphManager> {

    @Override
    public CompanyGraphManager apply(CompanyGraphManager companyGraphManager) {
        // Start repairing the graph from the root vertex (head company)
        Company company = companyGraphManager.getHeadCompany();
        String rootVertex = createVertexId(company.id());
        repairVertex(companyGraphManager, rootVertex);
        return companyGraphManager;
    }

    private void repairVertex(CompanyGraphManager companyGraphManager, String vertex) {
        Graph<String, WeightedEdge> graph = companyGraphManager.getGraph();
        Set<WeightedEdge> incomingEdges = graph.incomingEdgesOf(vertex);

        // If there are no incoming edges, there is nothing to repair
        if (incomingEdges.isEmpty()) return;

        // If only one incoming edge exists, assign a weight of 1 if it's missing
        if (incomingEdges.size() == 1) {
            WeightedEdge edge = incomingEdges.iterator().next();
            if (edge.getWeight() == 0) {
                graph.setEdgeWeight(edge, 1.0);
            }
            return;
        }

        // Handle cases with multiple incoming edges
        restoreOrValidateWeights(companyGraphManager, vertex, incomingEdges);

        // Recursively repair child vertices
        for (WeightedEdge edge : incomingEdges) {
            repairVertex(companyGraphManager, graph.getEdgeSource(edge));
        }
    }

    private void restoreOrValidateWeights(CompanyGraphManager companyGraphManager, String vertex, Set<WeightedEdge> edges) {
        Graph<String, WeightedEdge> graph = companyGraphManager.getGraph();
        Map<String, BrokenEntity> brokenEdges = new HashMap<>();
        double totalWeight = 0.0;
        double totalShare = 0.0;
        boolean hasMissingShares = false;

        // Process each incoming edge
        for (WeightedEdge edge : edges) {
            String sourceVertex = graph.getEdgeSource(edge);
            OwnerEntity ownerEntity = getOwnerEntity(companyGraphManager, sourceVertex);

            // Track missing edges
            if (edge.getWeight() == 0) {
                brokenEdges.put(sourceVertex, new BrokenEntity(ownerEntity, edge));
            } else {
                totalWeight += edge.getWeight();
            }

            // Sum the owner shares
            if (ownerEntity.getShare() == 0) {
                hasMissingShares = true;
            } else {
                totalShare += ownerEntity.getShare();
            }
        }

        // Repair edges based on known shares
        if (!brokenEdges.isEmpty()) {
            if (!hasMissingShares) {
                repairUsingShares(graph, brokenEdges, totalShare);
            } else if (brokenEdges.size() == 1) {
                repairSingleEdge(graph, brokenEdges, totalWeight);
            }
        }
    }

    private void repairUsingShares(Graph<String, WeightedEdge> graph, Map<String, BrokenEntity> brokenEdges, double totalShare) {
        for (BrokenEntity brokenEntity : brokenEdges.values()) {
            double share = brokenEntity.ownerEntity.getShare();
            double newWeight = share / totalShare;
            brokenEntity.weightedEdge.setWeight(newWeight);
        }
    }

    private void repairSingleEdge(Graph<String, WeightedEdge> graph, Map<String, BrokenEntity> brokenEdges, double totalWeight) {
        // Assign the remaining weight to the single broken edge
        BrokenEntity brokenEntity = brokenEdges.values().iterator().next();
        double newWeight = 1.0 - totalWeight;
        brokenEntity.weightedEdge.setWeight(newWeight);
    }

    private OwnerEntity getOwnerEntity(CompanyGraphManager companyGraphManager, String vertexId) {
        // Determine the type of vertex and fetch the corresponding entity
        if (vertexId.startsWith("L:")) {
            return companyGraphManager.getLegalEntity(parseVertexId(vertexId));
        } else if (vertexId.startsWith("N:")) {
            return companyGraphManager.getNaturalEntity(parseVertexId(vertexId));
        } else {
            throw new IllegalArgumentException("Unknown vertex type: " + vertexId);
        }
    }

    private String createVertexId(long id) {
        return "H" + ":" + id;
    }

    private long parseVertexId(String vertexId) {
        return Long.parseLong(vertexId.split(":")[1]);
    }

    // Helper class to store information about broken edges
    record BrokenEntity(OwnerEntity ownerEntity, WeightedEdge weightedEdge) {}
}
