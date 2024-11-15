package ru.bivchallenge.processor;

import java.util.function.Function;

@FunctionalInterface
public interface Processor<T, R> extends Function<T, R> {
}
