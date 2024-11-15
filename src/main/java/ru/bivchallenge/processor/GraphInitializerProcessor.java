package ru.bivchallenge.processor;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;
import ru.bivchallenge.dto.Company;

import java.util.Map;


public class GraphInitializerProcessor implements MapProcessor<Company, Graph<String, DefaultWeightedEdge>>{
    private Multigraph<String, DefaultWeightedEdge> multiGraph;


    @Override
    public Map<Long, Graph<String, DefaultWeightedEdge>> apply(Map<Long, Company> longCompanyMap) {
        return Map.of();
    }
}

