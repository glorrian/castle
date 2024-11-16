package ru.bivchallenge.processor;
import ru.bivchallenge.dto.Company;
import ru.bivchallenge.data.CompanyGraphManager;
import ru.bivchallenge.dto.LegalEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code GraphInitializerProcessor} class is responsible for initializing a map of {@link CompanyGraphManager}
 * objects from a map of {@link Company} objects. It implements the {@link MapProcessor} interface, enabling
 * transformation of keyed data from one type to another.
 *
 * <p>The main purpose of this processor is to create a {@link CompanyGraphManager} for each {@link Company}
 * and store it in a map using the same keys as the input map.</p>
 *
 * @see MapProcessor
 * @see Company
 * @see CompanyGraphManager
 */
public class GraphInitializerProcessor implements MapProcessor<Company, CompanyGraphManager>{
    private final Map<Long, LegalEntity> legalEntityRegistry;

    public GraphInitializerProcessor(Map<Long, LegalEntity> legalEntityRegistry) {
        this.legalEntityRegistry = legalEntityRegistry;
    }

    @Override
    public Map<Long, CompanyGraphManager> apply(Map<Long, Company> companyMap) {
        Map<Long, CompanyGraphManager> companyGraphMap = new HashMap<>(companyMap.size(), 1.0f);
        for (Map.Entry<Long, Company> entry : companyMap.entrySet()) {
            CompanyGraphManager companyGraphManager = new CompanyGraphManager(entry.getValue(), legalEntityRegistry);
            companyGraphMap.put(entry.getKey(), companyGraphManager);
        }
        return companyGraphMap;
    }
}
