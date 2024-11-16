package ru.bivchallenge.dto;

import org.jgrapht.graph.DefaultEdge;

public class WeightedEdge extends DefaultEdge {
    private double weight;

    public WeightedEdge(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
