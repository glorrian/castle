package ru.bivchallenge.dto;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;

import java.util.ArrayList;
import java.util.List;

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
