/**
 * This is the genotype (chromosome) represent a segment of pixels in the image.
 * A segment is a collection of similar pixels in the image.
 * This is where we are calculating the objectives
 */
package no.ntnu.it3708.GA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Segment {
    private HashMap<Integer, Pixel> pixels;
    private List<Integer> edgePixels; // Pixels aroud the segment (to draw line)
    private double edgeValue;
    private double connectivity;
    private double deviation;

    Segment() {
        this.pixels = new HashMap<>();
        this.edgePixels = new ArrayList<>();
        // this.edgeValue = 0d;
        // this.connectivity = 0d;
        // this.deviation = 0d;
    }

    void calculateObjectiveFunctions() {
        calculateEdgeValue();
        calculateConnectivity();
        calculateDeviation();
    }

    /**
     * Calculate the edge value
     */
    private void calculateEdgeValue() {
    }

    /**
     * Calculate the connectivity
     */
    private void calculateConnectivity() {
    }

    /**
     * Calculate the deviation
     *
     * @return the deviation
     */
    private void calculateDeviation() {
    }

}
