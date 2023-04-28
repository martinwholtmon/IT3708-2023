/**
 * Represent a pixel in the image.
 */
package no.ntnu.it3708.GA;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Pixel.
 */
public class Pixel {
    private final int id;
    private int x;
    private int y;
    private Color color;
    private List<Node> neighbors; // representing the neighbors: East-West-North-South east-west

    /**
     * Instantiates a new Pixel.
     *
     * @param id    the id
     * @param x     the x
     * @param y     the y
     * @param color the color
     */
    public Pixel(int id, int x, int y, Color color) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.color = color;
        this.neighbors = new ArrayList<>();
    }

    /**
     * Add a pixel neighbour as an edge
     *
     * @param pixel     neighboring pixel
     * @param direction direction
     */
    void addNeighboringPixel(Pixel pixel, Direction direction) {
        // Add edge
        this.neighbors.add(new Node(this, pixel, direction));
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets x.
     *
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * Gets y.
     *
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * Gets color.
     *
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets neighbors.
     *
     * @return the neighbors
     */
    public List<Node> getNeighbors() {
        return neighbors;
    }

    @Override
    public String toString() {
        return "Pixel{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", color=" + color +
                ", neighbors=" + neighbors.stream()
                .map(Node::getPixel)
                .map(Pixel::getId)
                .collect(Collectors.toList())
                +
                '}';
    }
}
