/**
 * This class is used to import images and prepare them for the GA
 */
package no.ntnu.it3708.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class ImageHandler {
    private static final String IMAGE_BASE_FOLDER = "/training_images/";
    private static final String IMAGE_NAME = "Test image.jpg";

    public ImageHandler() {
    }

    public int[][] loadImage(int image_id) throws IOException, URISyntaxException {
        // Read image file
        String path = IMAGE_BASE_FOLDER + image_id + "/" + IMAGE_NAME;
        String filename = getClass().getResource(path).toURI().getPath();
        File imageFile = new File(filename);
        BufferedImage image = ImageIO.read(imageFile);

        // Convert it to usable format: 2d-array
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] pixelArray = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = image.getRGB(x, y);
                pixelArray[x][y] = pixel;
            }
        }

        return pixelArray;
    }
}
