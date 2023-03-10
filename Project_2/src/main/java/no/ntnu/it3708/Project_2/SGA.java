package no.ntnu.it3708.Project_2;

import java.util.ArrayList;
import java.util.Random;

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
     * @param maximize          if maximize
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

    public void simulate() {
        Population population = init_population();
        System.out.print(population);
        this.generations.add(population);

//        // Run the SGA loop
//        while (population.getGeneration_nr() < this.max_generations) {
//
//        }

    }

    private Population init_population() {
        Population population = new Population();
        int nurse_id = 1;


        while (true) {
            while (population.getFeasible_individuals().size() < pop_size) {
                // Create individual
                Individual individual = new Individual(nurse_id, generate_bitstring(individual_size));
                objectiveFunction.calculate_fitness(individual);

                // check constraints and add individual
                if (objectiveFunction.check_individual_constraints(individual)) {
                    population.getFeasible_individuals().add(individual);
                    nurse_id++;
                    System.out.println(nurse_id);
                }
            }

            if (objectiveFunction.check_population_constraints(population)) {
                return population;
            }

            // Try again :/
            population = new Population();
        }
    }

    /**
     * Generate a random bitstring
     * @param individual_size   size of the individual
     * @return  the bitstring
     */
    private int[] generate_bitstring(Integer individual_size) {
        Random rd = new Random();
        int min = 0;
        int max = 1;

        int[] bitstring = new int[individual_size];
        for (int i = 0; i < bitstring.length; i++) {
            bitstring[i] = rd.nextInt(max-min+1) + min;
        }
        return bitstring;
    }
}
