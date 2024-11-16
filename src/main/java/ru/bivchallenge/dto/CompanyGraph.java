package ru.bivchallenge.dto;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;

import java.util.ArrayList;
import java.util.List;

public class CompanyGraph {
    private final Graph<Entity, DefaultWeightedEdge> graph;
    private final Company headCompany;
    private final List<NaturalEntity> naturalEntities;

    public CompanyGraph(Company headCompany) {
        this.headCompany = headCompany;
        this.naturalEntities = new ArrayList<>();

        graph = new Multigraph<>(DefaultWeightedEdge.class);
        graph.addVertex(headCompany);
    }

    public Graph<Entity, DefaultWeightedEdge> getGraph() {
        return graph;
    }

    public Company getHeadCompany() {
        return headCompany;
    }

    public void addNaturalEntity(NaturalEntity naturalEntity) {
       // implementation is needed
    }

    public void addLegalEntity(LegalEntity legalEntity) {
       // implementation is needed
    }

    public BenefeciarSet getBeneficiaries() {
       // implementation is needed
        return null;
    }
}
