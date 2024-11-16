package ru.bivchallenge.processor;
import ru.bivchallenge.dto.Company;
import ru.bivchallenge.dto.CompanyGraph;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code GraphInitializerProcessor} class is responsible for initializing a map of {@link CompanyGraph}
 * objects from a map of {@link Company} objects. It implements the {@link MapProcessor} interface, enabling
 * transformation of keyed data from one type to another.
 *
 * <p>The main purpose of this processor is to create a {@link CompanyGraph} for each {@link Company}
 * and store it in a map using the same keys as the input map.</p>
 *
 * @see MapProcessor
 * @see Company
 * @see CompanyGraph
 */
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
