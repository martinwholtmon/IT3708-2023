package no.ntnu.it3708.Utils;

import java.util.Random;

public final class utils {
    private static Random RANDOM = new Random();

    private utils() {

    }

    /**
     * Return a random int from 0 to upper
     * 
     * @param upper upper limit
     */
    public static int randomInt(int upper) {
        if (upper == 0)
            return 0;
        return RANDOM.nextInt(upper);
    }

    /**
     * Return random int between upper and lower
     * 
     * @param upper upper limit
     * @param lower lower limit
     */
    public static int randomInt(int upper, int lower) {
        return lower + RANDOM.nextInt(upper - lower + 1);
    }

    public static double randomDouble() {
        return RANDOM.nextDouble();

    }

    public static double randomDouble(double upper, double lower) {
        return lower + (upper - lower) * RANDOM.nextDouble();
    }
}
