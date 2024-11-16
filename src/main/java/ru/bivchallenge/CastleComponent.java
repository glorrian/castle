package ru.bivchallenge;

import dagger.Component;
import ru.bivchallenge.executor.ProcessDataExecutor;
import ru.bivchallenge.module.BindingModule;
import ru.bivchallenge.module.LocalDataModule;

import javax.inject.Singleton;

/**
 * The {@code CastleComponent} interface is a Dagger component that provides dependency injection
 * for the application. It connects the {@link LocalDataModule} with the objects that require
 * its dependencies.
 * <p>
 * This component is responsible for creating and providing an instance of {@link ProcessDataExecutor}.
 * It is annotated with {@link Singleton} to ensure that the dependencies provided by this component
 * are singletons within the application scope.
 *
 * @see Component
 * @see ProcessDataExecutor
 * @see LocalDataModule
 */
@Singleton
@Component(modules = {LocalDataModule.class, BindingModule.class})
public interface CastleComponent {
    ProcessDataExecutor createProcessDataExecutor();
}
