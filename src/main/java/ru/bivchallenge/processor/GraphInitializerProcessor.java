package ru.bivchallenge.processor;
import ru.bivchallenge.dto.Company;
import ru.bivchallenge.dto.CompanyGraph;

import java.util.HashMap;
import java.util.Map;


public class GraphInitializerProcessor implements MapProcessor<Company, CompanyGraph>{
    @Override
    public Map<Long, CompanyGraph> apply(Map<Long, Company> companyMap) {
        Map<Long, CompanyGraph> companyGraphMap = new HashMap<>(companyMap.size(), 1.0f);
        for (Map.Entry<Long, Company> entry : companyMap.entrySet()) {
            CompanyGraph companyGraph = new CompanyGraph(entry.getValue());
            companyGraphMap.put(entry.getKey(), companyGraph);
        }
        return companyGraphMap;
    }
}
