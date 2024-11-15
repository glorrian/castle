package ru.bivchallenge.processor;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;
import ru.bivchallenge.dto.Company;

import java.util.HashMap;
import java.util.Map;


public class GraphInitializerProcessor implements MapProcessor<Company, Graph<String, DefaultWeightedEdge>>{
    private Multigraph<String, DefaultWeightedEdge> multiGraph;

    @Override
    public Map<Long, Graph<String, DefaultWeightedEdge>> apply(Map<Long, Company> longCompanyMap) {
        Map<Long, Graph<String, DefaultWeightedEdge>> multgr = new HashMap<>();

        for (Map.Entry<Long, Company> entry : longCompanyMap.entrySet()) {
            long companyId = entry.getKey();
            Company company = entry.getValue();

            // Создаем мультиграф для текущей компании
            Multigraph<String, DefaultWeightedEdge> companyGraph = new Multigraph<>(DefaultWeightedEdge.class);

            // (Примерная) Реализация графа
            if (company != null) {
                companyGraph.addVertex("Vertex1");
                companyGraph.addVertex("Vertex2");
                DefaultWeightedEdge edge = companyGraph.addEdge("Vertex1", "Vertex2");
                companyGraph.setEdgeWeight(edge, 10.0);
            }


            multgr.put(companyId, companyGraph);
        }

        return multgr;
    }

}

