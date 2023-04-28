package no.ntnu.it3708;

/**
 * The type Parameters.
 */
public final class Parameters {
    /**
     * The constants for the image handler
     */
    public static final String IMAGE_BASE_FOLDER = "/training_images/";
    public static final String IMAGE_NAME = "Test image.jpg";
    
    /**
     * The constants for the GA
     */
    public static final Integer POP_SIZE = 1;

    public static final Integer MAX_GENERATIONS = 100;
    public static final Float CROSSOVER_RATE = 0.6f;
    public static final Float MUTATION_RATE = 0.1f;

    /**
     * The constants for segmentations
     */
    public static final int MIN_SEGMENTS = 3;

    public static final int MAX_SEGMENTS = 15;
}
