package ru.bivchallenge.processor;
import ru.bivchallenge.dto.Company;
import ru.bivchallenge.dto.CompanyGraph;

import java.util.Map;
import java.util.stream.Collectors;


public class GraphInitializerProcessor implements MapProcessor<Company, CompanyGraph>{
    @Override
    public Map<Long, CompanyGraph> apply(Map<Long, Company> companyMap) {
        return companyMap.entrySet().stream().map(entry -> {
            CompanyGraph companyGraph = new CompanyGraph(entry.getValue());
            return Map.entry(entry.getKey(), companyGraph);
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
