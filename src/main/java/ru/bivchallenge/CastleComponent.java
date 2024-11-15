package ru.bivchallenge;

import dagger.Component;
import ru.bivchallenge.executor.ProcessDataExecutor;
import ru.bivchallenge.module.LocalDataModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = LocalDataModule.class)
public interface CastleComponent {
    ProcessDataExecutor createProcessDataExecutor();
}
