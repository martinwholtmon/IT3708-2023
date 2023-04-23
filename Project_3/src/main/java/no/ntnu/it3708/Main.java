/**
 * This class initiates the GA on a set of images
 */
package no.ntnu.it3708;

import no.ntnu.it3708.GA.GA;
import no.ntnu.it3708.Utils.ImageHandler;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) {
        // Set image set to work on
        Integer image_id = 86016;

        // Import the images to segment
        ImageHandler imageHandler = new ImageHandler();
        try {
            // Load image
            Color[][] image = imageHandler.loadImage(image_id);

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
