/**
 * This class initiates the GA on a set of images
 */
package no.ntnu.it3708;

import no.ntnu.it3708.GA.GA;
import no.ntnu.it3708.Utils.ImageHandler;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * The type Main.
 */
public class Main {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        // Import the images to segment
        try {
            // Load image
            Color[][] image = ImageHandler.loadImage();

            // Start the GA
            GA moga = new GA(image);
            moga.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
