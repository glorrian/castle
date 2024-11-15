package ru.bivchallenge.processor;

import java.util.Map;

@FunctionalInterface
public interface MapProcessor<T, R> extends Processor<Map<Long, T>, Map<Long, R>>{
}
