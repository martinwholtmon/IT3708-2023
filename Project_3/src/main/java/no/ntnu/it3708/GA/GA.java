/**
 * This class handles the genetic algorithm loop and all its functions
 */
package no.ntnu.it3708.GA;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class GA {
    static final Integer POP_SIZE = 30;
    static final Integer MAX_GENERATIONS = 100;
    static final Float CROSSOVER_RATE = 0.6f;
    static final Float MUTATION_RATE = 0.1f;
    static final Random RANDOM = new Random();
    static ArrayList<Pixel> pixels;
    private ArrayList<Population> generations;

    public GA(Color[][] image) {
        pixels = new ArrayList<>();
        this.generations = new ArrayList<>();
        generatePixels(image);
    }

    public void start() {
        Population population = new Population();
        this.generations.add(population);
        System.out.println(population);

        while (population.getGenerationNr() < MAX_GENERATIONS) {
            Population next_population = population.nextGeneration();
            this.generations.add(population);
            System.out.println(population);
        }
    }

    /**
     * Given input image (2d array), it will generate the pixels to operate on
     * during the image segmentation
     *
     * @param image input image as 2d array
     * @return pixel representation as a 2d array (essentially the "genes")
     */
    private void generatePixels(Color[][] image) {
        int imgHeight = image.length;
        int imgWidth = image[0].length;

        // Create pixels
        Pixel[][] createdPixels = new Pixel[imgHeight][imgWidth];
        int id = 0;
        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                Pixel pixel = new Pixel(id, x, y, image[y][x]);
                createdPixels[y][x] = pixel;

                // Add object list of pixels
                pixels.add(pixel);
                id++;
            }
        }

        // Add the nearest neighboring pixels
        addNearestNeighboringPixels(createdPixels, imgHeight, imgWidth);
    }

    /**
     * Update the pixels with the nearest neighboring pixels.
     * This could be refactored, but kept like this for readability.
     *
     * @param pixels    pixels to modify
     * @param imgHeight height of image
     * @param imgWidth  width of image
     */
    private void addNearestNeighboringPixels(Pixel[][] pixels, int imgHeight, int imgWidth) {
        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                // Get the pixel to operate on
                Pixel pixel = pixels[y][x];

                // 1 - east
                if (x + 1 < imgWidth) {
                    pixel.addNeighboringPixel(pixels[y][x + 1], Direction.EAST);
                }

                // 2 - west
                if (x - 1 >= 0) {
                    pixel.addNeighboringPixel(pixels[y][x - 1], Direction.WEST);
                }

                // 3 - north
                if (y - 1 >= 0) {
                    pixel.addNeighboringPixel(pixels[y - 1][x], Direction.NORTH);
                }

                // 4 - south
                if (y + 1 < imgHeight) {
                    pixel.addNeighboringPixel(pixels[y + 1][x], Direction.SOUTH);
                }

                // 5 - east-top
                if (x + 1 < imgWidth && y - 1 >= 0) {
                    pixel.addNeighboringPixel(pixels[y - 1][x + 1], Direction.EAST_NORTH);
                }

                // 6 - east-bottom
                if (x + 1 < imgWidth && y + 1 < imgHeight) {
                    pixel.addNeighboringPixel(pixels[y + 1][x + 1], Direction.EAST_SOUTH);
                }

                // 7 - west-top
                if (x - 1 >= 0 && y - 1 >= 0) {
                    pixel.addNeighboringPixel(pixels[y - 1][x - 1], Direction.EAST_NORTH);
                }

                // 8 - west-bottom
                if (x - 1 >= 0 && y + 1 < imgHeight) {
                    pixel.addNeighboringPixel(pixels[y + 1][x - 1], Direction.EAST_SOUTH);
                }
            }
        }
    }
}
