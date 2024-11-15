package ru.bivchallenge.persistence;

import org.jgrapht.Graph;

import java.util.Map;

public class GraphDataContainer<V, E> implements DataContainer<Map<Long, Graph<V, E>>>{
    private Map<Long, Graph<V, E>> data;

    @Override
    public Map<Long, Graph<V, E>> get() {
        return data;
    }

    @Override
    public void set(Map<Long, Graph<V, E>> data) {
        this.data = data;
    }
}
