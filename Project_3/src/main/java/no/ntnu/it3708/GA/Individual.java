/**
 * This class represent an individual in the GA.
 */
package no.ntnu.it3708.GA;

import java.util.List;

public class Individual {
    private List<Segment> segments;
    private double fitness;
    private double edgeValue;
    private double connectivity;
    private double deviation;

    /**
     * Generate an individual
     */
    public Individual() {
        this.segments = generateInitialSegments();
        calculateObjectiveFunctions();
        calculateFitness();
    }

    /**
     * Given a list of segments, generate individual
     *
     * @param segments set of pixels as segments
     */
    public Individual(List<Segment> segments) {
        this.segments = segments;
        calculateObjectiveFunctions();
        calculateFitness();
    }

    /**
     * Generate the initial segments for a new/initial individual
     *
     * @return list of segments
     */
    private List<Segment> generateInitialSegments() {
        return null;
    }

    /**
     * Calculate the objective functions
     */
    private void calculateObjectiveFunctions() {
        return;
    }

    /**
     * Calculate the fitness value given the objective values
     */
    private void calculateFitness() {
    }
}
