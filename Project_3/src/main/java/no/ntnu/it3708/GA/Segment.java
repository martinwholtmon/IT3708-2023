/**
 * This is the genotype (chromosome) represent a segment of pixels in the image.
 * A segment is a collection of similar pixels in the image.
 * This is where we are calculating the objectives
 */
package no.ntnu.it3708.GA;


import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Segment {
    private HashMap<Integer, Pixel> pixels;
    private List<Integer> edgePixels; // Pixels aroud the segment (to draw line)
    private double edgeValue;
    private double connectivity;
    private double deviation;
    private Color centroid;

    Segment() {
        this.pixels = new HashMap<>();
        this.edgePixels = new ArrayList<>();
        // this.edgeValue = 0d;
        // this.connectivity = 0d;
        // this.deviation = 0d;
    }

    /**
     * Calculate all the objectives
     */
    void calculateObjectiveFunctions() {
        findCentroid();
        calculateEdgeValue();
        calculateConnectivity();
        calculateDeviation();
    }

    /**
     * Will find the centroid in the segment (average color)
     */
    private void findCentroid() {
        int r = 0;
        int g = 0;
        int b = 0;

        // Calc average
        for (Pixel pixel : this.pixels.values()) {
            Color pixeColor = pixel.getColor();
            r += pixeColor.getRed();
            g += pixeColor.getGreen();
            b += pixeColor.getBlue();
        }

        int size = this.pixels.size();
        this.centroid = new Color(r / size, g / size, b / size);
    }
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

    /**
     * Gets pixels.
     *
     * @return HashMap<Integer, Pixel>  return the pixels
     */
    public HashMap<Integer, Pixel> getPixels() {
        return pixels;
    }


    /**
     * Add a pixel to the list
     *
     * @param pixelId id of pixel
     * @param pixel   the pixel
     */
    public void addPixels(int pixelId, Pixel pixel) {
        this.pixels.put(pixelId, pixel);
    }

    /**
     * Gets edge value.
     *
     * @return double return the edgeValue
     */
    public double getEdgeValue() {
        return edgeValue;
    }

    /**
     * Gets connectivity.
     *
     * @return double return the connectivity
     */
    public double getConnectivity() {
        return connectivity;
    }

    /**
     * Gets deviation.
     *
     * @return double return the deviation
     */
    public double getDeviation() {
        return deviation;
    }

    @Override
    public String toString() {
        return "Segment{" +
                "pixels=" + pixels.keySet() +
                ", edgePixels=" + edgePixels +
                ", edgeValue=" + edgeValue +
                ", connectivity=" + connectivity +
                ", deviation=" + deviation +
                ", centroid=" + centroid +
                '}';
    }
}
