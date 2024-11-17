package ru.bivchallenge.executor;

import jakarta.inject.Inject;
import ru.bivchallenge.data.BenefeciarRegistry;
import ru.bivchallenge.data.CompanyGraphManager;
import ru.bivchallenge.dto.*;
import ru.bivchallenge.persistence.DataDispatcher;
import ru.bivchallenge.persistence.DataProvider;
import ru.bivchallenge.processor.GraphInitializerProcessor;
import ru.bivchallenge.processor.GraphRepairProcessor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * The {@code ProcessDataExecutor} class orchestrates concurrent processing of entity data into graph structures
 * and dispatches beneficiary information after computation.
 *
 * <p><b>Workflow:</b></p>
 * <ul>
 *     <li>Fetches data for companies, legal entities, and natural entities concurrently.</li>
 *     <li>Processes these entities into {@link CompanyGraphManager} graphs using parallel streams.</li>
 *     <li>Repairs graphs and extracts beneficiary data concurrently.</li>
 *     <li>Dispatches the beneficiary data using {@link DataDispatcher}.</li>
 * </ul>
 *
 * @see Executor
 */
public class ProcessDataExecutor implements Executor {
    private final DataProvider<LegalEntity> legalEntityDataProvider;
    private final DataProvider<NaturalEntity> naturalEntityDataProvider;
    private final DataProvider<Company> companyDataProvider;
    private final DataDispatcher<BenefeciarRegistry> benefeciarSetDataDispatcher;

    @Inject
    public ProcessDataExecutor(
            DataProvider<LegalEntity> legalEntityDataProvider,
            DataProvider<NaturalEntity> naturalEntityDataProvider,
            DataProvider<Company> companyDataProvider,
            DataDispatcher<BenefeciarRegistry> benefeciarSetDataDispatcher
    ) {
        this.legalEntityDataProvider = legalEntityDataProvider;
        this.naturalEntityDataProvider = naturalEntityDataProvider;
        this.companyDataProvider = companyDataProvider;
        this.benefeciarSetDataDispatcher = benefeciarSetDataDispatcher;
    }

    @Override
    public void execute() throws ExecutionException, InterruptedException {
        ForkJoinPool customThreadPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        try {
            // Step 1: Fetch data
            long startFetchTime = System.currentTimeMillis();
            CompletableFuture<Map<Long, Company>> companyDataFuture = CompletableFuture.supplyAsync(companyDataProvider::get, customThreadPool);
            CompletableFuture<Map<Long, LegalEntity>> legalEntityDataFuture = CompletableFuture.supplyAsync(legalEntityDataProvider::get, customThreadPool);
            CompletableFuture<Map<Long, NaturalEntity>> naturalEntityDataFuture = CompletableFuture.supplyAsync(naturalEntityDataProvider::get, customThreadPool);

            Map<Long, Company> companyMap = companyDataFuture.get();
            Map<Long, LegalEntity> legalEntityMap = legalEntityDataFuture.get();
            Map<Long, NaturalEntity> naturalEntityMap = naturalEntityDataFuture.get();
            System.out.println("Step 1: Data fetched in " + (System.currentTimeMillis() - startFetchTime) + " ms");

            // Step 2: Initialize graphs
            long startGraphInitTime = System.currentTimeMillis();
            GraphInitializerProcessor graphInitializerProcessor = new GraphInitializerProcessor(legalEntityMap);
            Map<Long, CompanyGraphManager> companyGraphMap = graphInitializerProcessor.apply(companyMap);
            System.out.println("Step 2: Graphs initialized in " + (System.currentTimeMillis() - startGraphInitTime) + " ms");

            // Step 3: Add legal entities to the graphs
            long startLegalEntityTime = System.currentTimeMillis();
            CompletableFuture<Void> processLegalEntities = CompletableFuture.runAsync(() -> legalEntityMap.values()
                    .parallelStream()
                    .forEach(legalEntity -> {
                        CompanyGraphManager manager = companyGraphMap.get(legalEntity.getCompanyId());
                        if (manager != null) {
                            manager.addEntity(legalEntity);
                        }
                    }), customThreadPool);
            processLegalEntities.join();
            System.out.println("Step 3: Legal entities processed in " + (System.currentTimeMillis() - startLegalEntityTime) + " ms");

            // Step 4: Add natural entities to the graphs
            long startNaturalEntityTime = System.currentTimeMillis();
            CompletableFuture<Void> processNaturalEntities = CompletableFuture.runAsync(() -> naturalEntityMap.values()
                    .parallelStream()
                    .forEach(naturalEntity -> {
                        CompanyGraphManager manager = companyGraphMap.get(naturalEntity.getCompanyId());
                        if (manager != null) {
                            manager.addEntity(naturalEntity);
                        }
                    }), customThreadPool);
            processNaturalEntities.join();
            System.out.println("Step 4: Natural entities processed in " + (System.currentTimeMillis() - startNaturalEntityTime) + " ms");

            // Step 5: Repair graphs and extract beneficiaries
            long startGraphRepairTime = System.currentTimeMillis();
            GraphRepairProcessor graphRepairProcessor = new GraphRepairProcessor();
            Set<BenefeciarRegistry> benefeciarRegistry = ConcurrentHashMap.newKeySet();
            companyGraphMap.values().parallelStream().forEach(manager -> {
                graphRepairProcessor.apply(manager);
                BenefeciarRegistry beneficiaries = manager.getBeneficiaries();
                if (beneficiaries != null && !beneficiaries.getBeneficiaries().isEmpty()) {
                    benefeciarRegistry.add(beneficiaries);
                }
            });
            System.out.println("Step 5: Graphs repaired and beneficiaries extracted in " + (System.currentTimeMillis() - startGraphRepairTime) + " ms");

            // Step 6: Dispatch beneficiary data
            long startDispatchTime = System.currentTimeMillis();
            benefeciarSetDataDispatcher.dispatch(benefeciarRegistry);
            System.out.println("Step 6: Data dispatched in " + (System.currentTimeMillis() - startDispatchTime) + " ms");

        } finally {
            customThreadPool.shutdown();
        }
    }
}
