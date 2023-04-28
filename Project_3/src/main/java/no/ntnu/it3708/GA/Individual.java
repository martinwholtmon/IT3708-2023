/**
 * This class represent an individual in the GA.
 */
package no.ntnu.it3708.GA;

import no.ntnu.it3708.Parameters;
import no.ntnu.it3708.Utils.utils;

import java.util.*;

/**
 * The type Individual.
 */
public class Individual {
    private List<Segment> segments;
    private double fitness;
    private double edgeValue;
    private double connectivity;
    private double deviation;

    /**
     * Generate an initial individual
     */
    public Individual() {
        this.segments = new ArrayList<>();
        this.edgeValue = 0d;
        this.connectivity = 0d;
        this.deviation = 0d;
        generateInitialSegments();
        calculateObjectiveFunctions();
        calculateFitness();
    }

    /**
     * Given a list of segments, creates the individual
     *
     * @param segments set of pixels as segments
     */
    public Individual(List<Segment> segments) {
        this.segments = segments;
        calculateObjectiveFunctions();
        calculateFitness();
    }

    /**
     * Generate the initial segments for a new/initial individual.
     * From the paper "A Multi-objective Evolutionary Algorithm for Color Image
     * Segmentation", is recommended to construct the initial population using MST
     * (Minimum spanning tree).
     */
    private void generateInitialSegments() {
        Map<Integer, Segment> visitedPixels = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>();

        // Pick random segment size between the GA boundaries
        int numberOfSegments = utils.randomInt(Parameters.MIN_SEGMENTS, Parameters.MAX_SEGMENTS);

        // Initialize segments with random root pixel
        for (int i = 0; i < numberOfSegments; i++) {
            Segment segment = new Segment();
            segments.add(segment);

            // Pick a random "root" pixel that has not been visited yet
            Pixel rootPixel = GA.pixels.get(utils.randomInt(GA.pixels.size()));
            while (visitedPixels.containsKey(rootPixel.getId())) {
                rootPixel = GA.pixels.get(utils.randomInt(GA.pixels.size()));
            }
            visitedPixels.put(rootPixel.getId(), segment);
            segment.addPixels(rootPixel.getId(), rootPixel);
            pq.addAll(rootPixel.getNeighbors());
        }

        // add nodes
        while (!pq.isEmpty()) {
            Node edge = pq.poll();
            Pixel pixel = edge.getPixel();
            Pixel neighPixel = edge.getNeighboringPixel();

            if (!visitedPixels.containsKey(neighPixel.getId())) {
                Segment segment = visitedPixels.get(pixel.getId());
                visitedPixels.put(neighPixel.getId(), segment);
                segment.addPixels(neighPixel.getId(), neighPixel);

                // add neighbors to queue
                pq.addAll(neighPixel.getNeighbors());
            }
        }
    }

    /**
     * Calculate the objective functions.
     */
    private void calculateObjectiveFunctions() {
        for (Segment segment : segments) {
            segment.calculateObjectiveFunctions();
            this.connectivity += segment.getConnectivity();
            this.deviation += segment.getDeviation();
            this.edgeValue += segment.getEdgeValue();
        }
    }

    /**
     * Calculate the fitness value given the objective values
     */
    private void calculateFitness() {
    }
}
