package ru.bivchallenge;


import ru.bivchallenge.executor.ProcessDataExecutor;
import ru.bivchallenge.module.LocalDataModule;

import java.util.concurrent.ExecutionException;

public class CastleApplication {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        LocalDataModule localDataModule = new LocalDataModule(args);
        CastleComponent castleComponent = DaggerCastleComponent.builder()
                .localDataModule(localDataModule)
                .build();

        ProcessDataExecutor processDataExecutor = castleComponent.createProcessDataExecutor();
        var start = System.currentTimeMillis();
        processDataExecutor.execute();
        var end = System.currentTimeMillis();
        System.out.println("Executon time: " + (end - start) + " ms");
    }
}