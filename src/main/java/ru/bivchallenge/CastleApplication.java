package ru.bivchallenge;


import ru.bivchallenge.executor.ProcessDataExecutor;

import java.util.concurrent.ExecutionException;

public class CastleApplication {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CastleComponent castleComponent = DaggerCastleComponent.create();
        ProcessDataExecutor processDataExecutor = castleComponent.createProcessDataExecutor();
        processDataExecutor.execute();
    }
}