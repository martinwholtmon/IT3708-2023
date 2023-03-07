package no.ntnu.it3708.Project_2;

/**
 * The Objective function.
 * Used for calculating the fitness and checking constraints.
 */
public class ObjectiveFunction {
    private final DataHandler data;

    /**
     * Instantiates a new Objective function.
     *
     * @param data the data
     */
    public ObjectiveFunction(DataHandler data) {
        this.data = data;
    }

    /**
     * Checks that the individual do not violate the constraints.
     *
     * @param individual the individual
     * @return the boolean
     */
    public boolean check_constraints(Individual individual) {
        return false;
    }

    /**
     * Calculates the fitness of an individual.
     *
     * @param individual the individual
     * @return the fitness
     */
    public float calculate_fitness(Individual individual) {
        return 0f;
    }
}
