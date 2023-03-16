package no.ntnu.it3708.Project_2;

import java.util.ArrayList;

/**
 * The Population.
 */
public class Population {
    private ArrayList<Individual> feasible_individuals;
    private ArrayList<Individual> infeasible_individuals;
    private final Population old_pop;
    private final Integer generation_nr;

    /**
     * Instantiates a new Population.
     */
    public Population() {
        this.feasible_individuals = new ArrayList<>();
        this.infeasible_individuals = new ArrayList<>();
        this.old_pop = null;
        this.generation_nr = 0;
    }

    /**
     * Instantiates a new Population.
     *
     * @param feasible_individuals   the feasible individuals
     * @param infeasible_individuals the infeasible individuals
     * @param old_pop                the old pop
     * @param generation_nr          the generation nr
     */
    public Population(ArrayList<Individual> feasible_individuals, ArrayList<Individual> infeasible_individuals, Population old_pop, Integer generation_nr) {
        this.feasible_individuals = feasible_individuals;
        this.infeasible_individuals = infeasible_individuals;
        this.old_pop = old_pop;
        this.generation_nr = generation_nr;
    }

    /**
     * Gets feasible individuals.
     *
     * @return the feasible individuals
     */
    public ArrayList<Individual> getFeasible_individuals() {
        return feasible_individuals;
    }

    /**
     * Gets infeasible individuals.
     *
     * @return the infeasible individuals
     */
    public ArrayList<Individual> getInfeasible_individuals() {
        return infeasible_individuals;
    }

    /**
     * Gets generation nr.
     *
     * @return the generation nr
     */
    public Integer getGeneration_nr() {
        return generation_nr;
    }

    @Override
    public String toString() {
        return "Generation " + generation_nr + " avg. fitness: " + calc_avg_fitness() + "\n" +
                "Best solution: " + get_best_solution().toString();
    }

    /**
     * Calculate the average fitness of a population
     * @return the average fitness
     */
    private double calc_avg_fitness() {
        double total_fitness = 0f;
        for (Individual i : feasible_individuals) total_fitness += i.getFitness();
        return total_fitness / feasible_individuals.size();
    }

    private Individual get_best_solution() {
        Individual best_individual = null;
        double best_fitness = Double.MAX_VALUE;

        for (Individual individual : this.getFeasible_individuals()) {
            if (individual.getFitness() < best_fitness) {
                best_individual = individual;
                best_fitness = individual.getFitness();
            }
        }
        return best_individual;
    }
}
