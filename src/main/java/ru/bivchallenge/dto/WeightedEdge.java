package ru.bivchallenge.dto;

import org.jgrapht.graph.DefaultEdge;

/**
 * The {@code WeightedEdge} class represents an edge in a graph with an associated weight.
 * It extends the {@link DefaultEdge} class from the JGraphT library, allowing it to store
 * additional data, such as the weight of the edge.
 *
 * <p>Weights are typically used to represent the strength, cost, or percentage associated
 * with the edge in the graph.</p>
 *
 * @see DefaultEdge
 */
public class WeightedEdge extends DefaultEdge {
    private double weight;

    /**
     * Constructs a {@code WeightedEdge} with the specified weight.
     *
     * @param weight the weight of the edge
     */
    public WeightedEdge(double weight) {
        this.weight = weight;
    }

    /**
     * Returns the weight of the edge.
     *
     * @return the weight of the edge
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the weight of the edge to the specified value.
     *
     * @param weight the new weight of the edge
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }
}
