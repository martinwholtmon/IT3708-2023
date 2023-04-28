/**
 * This class is used to import images and prepare them for the GA
 */
package no.ntnu.it3708.Utils;

import no.ntnu.it3708.Parameters;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * The type Image handler.
 */
public final class ImageHandler {

    private ImageHandler() {
    }

    /**
     * Load an image from the resource folder given an image_id.
     * It will locate the correct folder, and then load the Test image.jpg
     *
     * @param image_id id of folder/image
     * @return image as a 2d array
     * @throws IOException        the io exception
     * @throws URISyntaxException the uri syntax exception
     */
    public static Color[][] loadImage(int image_id) throws IOException, URISyntaxException {
        // Read image file
        String path = Parameters.IMAGE_BASE_FOLDER + image_id + "/" + Parameters.IMAGE_NAME;
        String filename = ImageHandler.class.getResource(path).toURI().getPath();
        File imageFile = new File(filename);
        BufferedImage image = ImageIO.read(imageFile);
        return buffImageTo2d(image);
    }

    /***
     * Will create a 2d array from a buffered image
     *
     * @param image buffered image
     * @return 2d array of image
     */
    private static Color[][] buffImageTo2d(BufferedImage image) {
        // Convert it to usable format: 2d-array
        int width = image.getWidth();
        int height = image.getHeight();
        Color[][] pixelArray = new Color[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = image.getRGB(x, y);
                pixelArray[x][y] = new Color(pixel);
            }
        }
        return pixelArray;
    }

    /**
     * Will calculate the monochrome luminance of the color as an intensity
     * according to NTSC formula: Y = 0.299*r + 0.587*g + 0.114*b
     *
     * @param color The color to convert
     * @return the monochrome luminance [0.0, 255.0]
     */
    public static double intensity(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        if (r == g && r == b)
            return r;
        return 0.299 * r + 0.587 * g + 0.114 * b;
    }

    /**
     * Calculates the euclidean color distance
     *
     * @param c1 color 1
     * @param c2 color 2
     * @return the distance
     */
    public static double colorDistance(Color c1, Color c2) {
        return Math.sqrt(Math.pow((double) c1.getRed() - c2.getRed(), 2) +
                Math.pow((double) c1.getGreen() - c2.getGreen(), 2) +
                Math.pow((double) c1.getBlue() - c2.getBlue(), 2));
    }
}
