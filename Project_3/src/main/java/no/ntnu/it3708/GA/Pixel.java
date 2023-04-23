/**
 * Represent a pixel in the image.
 */
package no.ntnu.it3708.GA;

import java.awt.*;
import java.util.HashMap;

public class Pixel {
    private final int id;
    private int x;
    private int y;
    private Color color;
    private HashMap<Integer, Pixel> neighbors;  // 1-8 representing the neighbors: East-West-North-South east-west

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
}
