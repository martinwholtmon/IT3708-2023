/**
 * This class represent the relationship between two pixels
 */
package no.ntnu.it3708.GA;

import no.ntnu.it3708.Utils.ImageHandler;

/**
 * The type Node.
 */
class Node implements Comparable<Node> {
    private Pixel pixel;
    private Pixel neighboringPixel;
    private double weight;
    private Direction direction;

    /**
     * Instantiates a new Node.
     *
     * @param pixel            the pixel
     * @param neighboringPixel the neighboring pixel
     * @param direction        the direction
     */
    public Node(Pixel pixel, Pixel neighboringPixel, Direction direction) {
        this.pixel = pixel;
        this.neighboringPixel = neighboringPixel;
        this.direction = direction;
        this.weight = ImageHandler.colorDistance(pixel.getColor(), neighboringPixel.getColor());
    }

    /**
     * Gets pixel.
     *
     * @return the pixel
     */
    public Pixel getPixel() {
        return pixel;
    }

    /**
     * Gets neighboring pixel.
     *
     * @return the neighboring pixel
     */
    public Pixel getNeighboringPixel() {
        return neighboringPixel;
    }

    /**
     * Gets weight.
     *
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Gets direction.
     *
     * @return Direction return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    @Override
    public int compareTo(Node other) {
        return Double.compare(weight, other.weight);
    }
}
