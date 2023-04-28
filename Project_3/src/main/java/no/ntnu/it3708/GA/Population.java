/**
 * This class represent a population in the GA
 */
package no.ntnu.it3708.GA;

import no.ntnu.it3708.Parameters;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * The type Population.
 */
public class Population {
    private final Integer generationNr;
    private ArrayList<Individual> individuals;
    private ArrayList<Individual> paretoIndividuals;

    /**
     * Instantiates a new Population.
     */
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
        for (int i = 0; i < Parameters.POP_SIZE; i++) {
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


    @Override
    public String toString() {
        return "Population{" +
                "generationNr=" + generationNr +
                ", paretoIndividuals=" + paretoIndividuals.stream()
                .map(Individual::toString)
                .collect(Collectors.toList()) +
                '}';
    }
}
