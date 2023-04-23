/**
 * Represent a pixel in the image.
 */
package no.ntnu.it3708.GA;

import java.awt.*;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Pixel {
    private final int id;
    private int x;
    private int y;
    private Color color;
    private HashMap<Integer, Pixel> neighbors; // 1-8 representing the neighbors: East-West-North-South east-west

    public Pixel(int id, int x, int y, Color color) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.color = color;
        this.neighbors = new HashMap<>();

        // init hashmap for the eight neighbors
        for (int i = 1; i <= 8; i++) {
            this.neighbors.put(i, null);
        }
    }

    /**
     * Add a pixel as neighbor using the positions 1-8
     *
     * @param pos   position
     * @param pixel pixel
     */
    void addNeighboringPixel(int pos, Pixel pixel) {
        this.neighbors.put(pos, pixel);
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public HashMap<Integer, Pixel> getNeighbors() {
        return neighbors;
    }

    @Override
    public String toString() {
        return "Pixel{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", color=" + color +
                ", neighbors=" + neighbors.values().stream().map(Pixel::getId).collect(Collectors.toList()) +
                '}';
    }
}
