package ru.bivchallenge.dto;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;

import java.util.List;

public class CompanyGraph {
    private final Graph<Entity, DefaultWeightedEdge> graph;
    private final Company headCompany;
    private final List<NaturalEntity> naturalEntities;

    public CompanyGraph(Company headCompany, List<NaturalEntity> naturalEntities) {
        this.headCompany = headCompany;
        this.naturalEntities = naturalEntities;

        graph = new Multigraph<>(DefaultWeightedEdge.class);
        graph.addVertex(headCompany);
    }

    public Graph<Entity, DefaultWeightedEdge> getGraph() {
        return graph;
    }

    public Company getHeadCompany() {
        return headCompany;
    }

    // logic for handle entity's relations
}
