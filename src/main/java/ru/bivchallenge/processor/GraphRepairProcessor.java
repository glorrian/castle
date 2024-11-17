package ru.bivchallenge.processor;

import org.jgrapht.Graph;
import ru.bivchallenge.data.CompanyGraphManager;
import ru.bivchallenge.dto.*;

import java.util.*;

/**
 * The {@code GraphRepairProcessor} class is responsible for repairing a graph by ensuring that
 * all weights on incoming edges are correctly calculated or validated.
 *
 * <p>This optimized version includes caching, reduced string operations, and iterative graph traversal.</p>
 */
public class GraphRepairProcessor implements UnaryProcessor<CompanyGraphManager> {

    @Override
    public CompanyGraphManager apply(CompanyGraphManager companyGraphManager) {
        // Start repairing the graph from the root vertex (head company)
        Company company = companyGraphManager.getHeadCompany();
        String rootVertex = createVertexId(company.id());
        repairVertex(companyGraphManager, rootVertex);
        return companyGraphManager;
    }

    private void repairVertex(CompanyGraphManager companyGraphManager, String startVertex) {
        Graph<String, WeightedEdge> graph = companyGraphManager.getGraph();
        Set<String> visited = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        stack.push(startVertex);

        while (!stack.isEmpty()) {
            String vertex = stack.pop();
            if (!visited.add(vertex)) continue;

            Set<WeightedEdge> incomingEdges = graph.incomingEdgesOf(vertex);
            if (incomingEdges.isEmpty()) continue;

            if (incomingEdges.size() == 1) {
                WeightedEdge edge = incomingEdges.iterator().next();
                if (edge.getWeight() == 0) {
                    edge.setWeight(1.0);
                }
            } else {
                // Cache owner entities for this vertex
                Map<String, OwnerEntity> ownerCache = new HashMap<>();
                for (WeightedEdge edge : incomingEdges) {
                    String sourceVertex = graph.getEdgeSource(edge);
                    ownerCache.computeIfAbsent(sourceVertex, v -> getOwnerEntity(companyGraphManager, v));
                }

                restoreOrValidateWeights(companyGraphManager, vertex, incomingEdges, ownerCache);
            }

            // Add source vertices of incoming edges to the stack
            for (WeightedEdge edge : incomingEdges) {
                stack.push(graph.getEdgeSource(edge));
            }
        }
    }

    private void restoreOrValidateWeights(CompanyGraphManager companyGraphManager, String vertex,
                                          Set<WeightedEdge> edges, Map<String, OwnerEntity> ownerCache) {
        Graph<String, WeightedEdge> graph = companyGraphManager.getGraph();
        Map<String, BrokenEntity> brokenEdges = new HashMap<>();
        double totalWeight = 0.0;
        double totalShare = 0.0;
        boolean hasMissingShares = false;

        for (WeightedEdge edge : edges) {
            String sourceVertex = graph.getEdgeSource(edge);
            OwnerEntity ownerEntity = ownerCache.get(sourceVertex);

            if (edge.getWeight() == 0) {
                brokenEdges.put(sourceVertex, new BrokenEntity(ownerEntity, edge));
                hasMissingShares |= (ownerEntity.getShare() == 0);
            } else {
                totalWeight += edge.getWeight();
            }

            if (ownerEntity.getShare() > 0) {
                totalShare += ownerEntity.getShare();
            }
        }

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
        BrokenEntity brokenEntity = brokenEdges.values().iterator().next();
        double newWeight = 1.0 - totalWeight;
        brokenEntity.weightedEdge.setWeight(newWeight);
    }

    private OwnerEntity getOwnerEntity(CompanyGraphManager companyGraphManager, String vertexId) {
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
        int colonIndex = vertexId.indexOf(':');
        return Long.parseLong(vertexId.substring(colonIndex + 1));
    }

    // Helper class to store information about broken edges
    record BrokenEntity(OwnerEntity ownerEntity, WeightedEdge weightedEdge) {}
}
