/**
 * This class initiates the GA on a set of images
 */
package no.ntnu.it3708;

import no.ntnu.it3708.Utils.ImageHandler;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) {
        // Set image set to work on
        Integer image_id = 86016;

        // Import the images to segment
        ImageHandler imageHandler = new ImageHandler();

        try {
            int[][] image = imageHandler.loadImage(image_id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
