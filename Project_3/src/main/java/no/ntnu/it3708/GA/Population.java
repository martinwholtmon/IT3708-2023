/**
 * This class represent a population in the GA
 */
package no.ntnu.it3708.GA;

import java.util.ArrayList;

/**
 * The type Population.
 */
public class Population {
    private final Integer generationNr;
    private ArrayList<Individual> individuals;
    private ArrayList<Individual> paretoIndividuals;

    public Population() {
        this.individuals = initialPopulation();
        this.paretoIndividuals = new ArrayList<>();
        this.generationNr = 0;
    }

    /**
     * Gets generation nr.
     *
     * @return the generation nr
     */
    Integer getGenerationNr() {
        return generationNr;
    }

    private ArrayList<Individual> initialPopulation() {
        ArrayList<Individual> population = new ArrayList<>();

        // Generate individuals
        for (int i = 0; i < GA.POP_SIZE; i++) {
            Individual individual = new Individual();
            population.add(individual);
        }
        return population;
    }

    /**
     * NSGA-II loop: Creates the next generation
     *
     * @return new generation
     */
    Population nextGeneration() {
        return null;
    }
}
