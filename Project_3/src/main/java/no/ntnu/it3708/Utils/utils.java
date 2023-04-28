package no.ntnu.it3708.Utils;

import java.util.Random;

/**
 * The type Utils.
 */
public final class utils {
    private static Random RANDOM = new Random();

    private utils() {

    }

    /**
     * Return a random int from 0 to upper
     *
     * @param upper limit
     * @return the int
     */
    public static int randomInt(int upper) {
        if (upper == 0)
            return 0;
        return RANDOM.nextInt(upper);
    }

    /**
     * Return random int between upper and lower
     *
     * @param lower limit
     * @param upper limit
     * @return the int
     */
    public static int randomInt(int lower, int upper) {
        return lower + RANDOM.nextInt(upper - lower + 1);
    }

    /**
     * Generate a random double between 0-1 (including)
     *
     * @return random double [0,1]
     */
    public static double randomDouble() {
        return RANDOM.nextDouble();

    }

    /**
     * Generate random double between the boundaries
     *
     * @param lower limit
     * @param upper limit
     * @return double
     */
    public static double randomDouble(int lower, int upper) {
        return lower + (upper - lower) * RANDOM.nextDouble();
    }
}
