package ru.bivchallenge.executor;

@FunctionalInterface
public interface Executor {
    void execute() throws Exception;
}
