package ru.bivchallenge.executor;

import jakarta.inject.Inject;
import ru.bivchallenge.dto.*;
import ru.bivchallenge.persistence.DataDispatcher;
import ru.bivchallenge.persistence.DataProvider;
import ru.bivchallenge.processor.GraphInitializerProcessor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * The {@code ProcessDataExecutor} class is responsible for orchestrating the processing of various entity types
 * (e.g., {@link Company}, {@link LegalEntity}, {@link NaturalEntity}) into a graph-based structure and dispatching
 * the resulting beneficiary data.
 * <p>
 * It leverages an {@link ExecutorService} to perform concurrent data fetching from multiple {@link DataProvider}s and
 * processes the data using a {@link GraphInitializerProcessor}.
 * <p>
 * The final processed data is dispatched using a {@link DataDispatcher}.
 *
 * <p><b>Workflow:</b></p>
 * <ul>
 *     <li>Fetches data for companies, legal entities, and natural entities concurrently.</li>
 *     <li>Builds {@link CompanyGraph} objects by associating legal and natural entities with companies.</li>
 *     <li>Extracts beneficiary sets and dispatches them via the provided {@link DataDispatcher}.</li>
 * </ul>
 *
 * @see Executor
 * @see DataProvider
 * @see DataDispatcher
 * @see GraphInitializerProcessor
 */
public class ProcessDataExecutor implements Executor {
    private final DataProvider<LegalEntity> legalEntityDataProvider;
    private final DataProvider<NaturalEntity> naturalEntityDataProvider;
    private final DataProvider<Company> companyDataProvider;

    private final DataDispatcher<BenefeciarSet> benefeciarSetDataDispatcher;

    @Inject
    public ProcessDataExecutor(
            DataProvider<LegalEntity> legalEntityDataProvider,
            DataProvider<NaturalEntity> naturalEntityDataProvider,
            DataProvider<Company> companyDataProvider,
            DataDispatcher<BenefeciarSet> benefeciarSetDataDispatcher
    ) {
        this.legalEntityDataProvider = legalEntityDataProvider;
        this.naturalEntityDataProvider = naturalEntityDataProvider;
        this.companyDataProvider = companyDataProvider;
        this.benefeciarSetDataDispatcher = benefeciarSetDataDispatcher;
    }

    @Override
    public void execute() throws ExecutionException, InterruptedException {
        try (ExecutorService executorService = Executors.newFixedThreadPool(3)) {
            Future<Map<Long, Company>> companyDataFuture = executorService.submit(companyDataProvider::get);
            Future<Map<Long, LegalEntity>> legalEntityDataFuture = executorService.submit(legalEntityDataProvider::get);
            Future<Map<Long, NaturalEntity>> naturalEntityDataFuture = executorService.submit(naturalEntityDataProvider::get);

            Map<Long, Company> companyMap = companyDataFuture.get();
            Map<Long, LegalEntity> legalEntityMap = legalEntityDataFuture.get();
            GraphInitializerProcessor graphInitializerProcessor = new GraphInitializerProcessor(legalEntityMap);
            Map<Long, CompanyGraph> companyGraphMap = graphInitializerProcessor.apply(companyMap);
            legalEntityMap.forEach((key, legalEntity) -> {
                CompanyGraph companyGraph = companyGraphMap.get(legalEntity.getCompanyId());
                if (companyGraph != null) {
                    companyGraph.addLegalEntity(legalEntity);
                }
            });
            Map<Long, NaturalEntity> naturalEntityMap = naturalEntityDataFuture.get();
            naturalEntityMap.forEach((key, naturalEntity) -> {
                CompanyGraph companyGraph = companyGraphMap.get(naturalEntity.getCompanyId());
                if (companyGraph != null) {
                    companyGraph.addNaturalEntity(naturalEntity);
                }
            });
            Set<BenefeciarSet> benefeciarSet = new HashSet<>();
            companyGraphMap.forEach((key, companyGraph) -> {
                BenefeciarSet beneficiaries = companyGraph.getBeneficiaries();
                if (beneficiaries != null && !beneficiaries.getBeneficiaries().isEmpty()) {
                    benefeciarSet.add(beneficiaries);
                }
            });
            benefeciarSetDataDispatcher.dispatch(benefeciarSet);

        }
    }
}
