package no.ntnu.it3708.Project_2;

import java.util.ArrayList;

/**
 * The Sga.
 */
public class SGA {
    private final ObjectiveFunction objectiveFunction;
    private final Boolean maximize;
    private final Integer pop_size;
    private final Integer individual_size;
    private final Integer max_generations;
    private final Float crossover_rate;
    private final Float mutation_rate;

    private ArrayList<Population> generations;

    /**
     * Instantiates a new Sga.
     *
     * @param objectiveFunction the objective function
     * @param maximize          the maximize
     * @param pop_size          the pop size
     * @param individual_size   the individual size
     * @param max_generations   the max generations
     * @param crossover_rate    the crossover rate
     * @param mutation_rate     the mutation rate
     */
    public SGA(
            ObjectiveFunction objectiveFunction,
            Boolean maximize,
            Integer pop_size,
            Integer individual_size,
            Integer max_generations,
            Float crossover_rate,
            Float mutation_rate) {
        this.objectiveFunction = objectiveFunction;
        this.maximize = maximize;
        this.pop_size = pop_size;
        this.individual_size = individual_size;
        this.max_generations = max_generations;
        this.crossover_rate = crossover_rate;
        this.mutation_rate = mutation_rate;
        this.generations = new ArrayList<>();
    }
}
