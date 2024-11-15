package ru.bivchallenge.executor;

import jakarta.inject.Inject;
import ru.bivchallenge.dto.Company;
import ru.bivchallenge.dto.LegalEntity;
import ru.bivchallenge.dto.NaturalEntity;
import ru.bivchallenge.persistence.DataProvider;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProcessDataExecutor implements Executor {
    private final DataProvider<LegalEntity> legalEntityDataProvider;
    private final DataProvider<NaturalEntity> naturalEntityDataProvider;
    private final DataProvider<Company> companyDataProvider;

    @Inject
    public ProcessDataExecutor(DataProvider<LegalEntity> legalEntityDataProvider, DataProvider<NaturalEntity> naturalEntityDataProvider, DataProvider<Company> companyDataProvider) {
        this.legalEntityDataProvider = legalEntityDataProvider;
        this.naturalEntityDataProvider = naturalEntityDataProvider;
        this.companyDataProvider = companyDataProvider;
    }

    @Override
    public void execute() throws ExecutionException, InterruptedException {
        try (ExecutorService executorService = Executors.newFixedThreadPool(3)) {
            long _start = System.currentTimeMillis();
            Future<Map<Long, Company>> companyDataFuture = executorService.submit(() -> companyDataProvider.get());

            Future<Map<Long, LegalEntity>> legalEntityDataFuture = executorService.submit(legalEntityDataProvider::get);

            Future<Map<Long, NaturalEntity>> naturalEntityDataFuture = executorService.submit(naturalEntityDataProvider::get);

            Map<Long, Company> companyMap = companyDataFuture.get();
            Map<Long, LegalEntity> legalEntityMap = legalEntityDataFuture.get();

            Map<Long, NaturalEntity> naturalEntityMap = naturalEntityDataFuture.get();
            System.out.println("All data loaded in " + (System.currentTimeMillis() - _start) + " ms");
            System.out.println("Company data size: " + naturalEntityMap.size());
        }
    }
}
