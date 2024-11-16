package ru.bivchallenge.dto;

import com.sun.source.doctree.EntityTree;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;

import java.util.ArrayList;
import java.util.List;

public class CompanyGraph {
    private final Graph<Entity, DefaultWeightedEdge> graph;
    private final Company headCompany;
    private final List<Entity> entities;


    public CompanyGraph(Company headCompany) {
        this.headCompany = headCompany;
        this.entities = new ArrayList<>();

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
        if (!entities.stream().anyMatch(e -> e.id() == naturalEntity.id())) {
            entities.add(naturalEntity);
            graph.addVertex(naturalEntity);

            if (naturalEntity.getCompanyId() == headCompany.id()) {
                graph.addEdge(naturalEntity, headCompany, new DefaultWeightedEdge());
            } else {
                boolean companyExists = entities.stream().anyMatch(e -> e instanceof Company && e.id() == naturalEntity.getCompanyId());

                if (companyExists) {
                    graph.addEdge(naturalEntity, entities.stream()
                            .filter(e -> e instanceof Company && e.id() == naturalEntity.getCompanyId())
                            .findFirst()
                            .get(), new DefaultWeightedEdge());
                }
            }
        }
    }

    public void addLegalEntity(LegalEntity legalEntity) {
       // implementation is needed
        if (!entities.stream().anyMatch(e -> e.id() == legalEntity.id())) {
            entities.add(legalEntity);
            graph.addVertex(legalEntity);

            if (legalEntity.getCompanyId() == headCompany.id()) {
                graph.addEdge(legalEntity, headCompany, new DefaultWeightedEdge());
            } else {
                boolean companyExists = entities.stream().anyMatch(e -> e instanceof Company && e.id() == legalEntity.getCompanyId());

                if (companyExists) {
                    graph.addEdge(legalEntity, entities.stream()
                            .filter(e -> e instanceof Company && e.id() == legalEntity.getCompanyId())
                            .findFirst()
                            .get(), new DefaultWeightedEdge());
                }
            }
        }
    }

    public BenefeciarSet getBeneficiaries() {
       // implementation is needed
        return null;
    }
}
