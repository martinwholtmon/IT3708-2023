/**
 * This class represent a population in the GA
 */
package no.ntnu.it3708.GA;

import no.ntnu.it3708.Parameters;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Population.
 */
public class Population {
    private final Integer generationNr;
    private ArrayList<Individual> individuals;
    private List<List<Individual>> paretoFronts;

    /**
     * Instantiates a new Population.
     */
    public Population() {
        this.individuals = initialPopulation();
        this.paretoFronts = new ArrayList<>();
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

        // Non-dominated sorting + crowding distance
        this.paretoFronts = nonDominatedSorting(population);
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

    /**
     * Performs non-dominated sorting and updates the individuals rank.
     * It will rank the individuals based on how much it dominates the others.
     *
     * @param individuals
     */
    private List<List<Individual>> nonDominatedSorting(ArrayList<Individual> individuals) {
        List<List<Individual>> paretoFronts = new ArrayList<>();

        // Iterate over the individuals and rank them
        AtomicInteger rank = new AtomicInteger(1);
        ArrayList<Individual> remainingIndividuals = new ArrayList<>(individuals);
        while (!remainingIndividuals.isEmpty()) {
            List<Individual> currDominatingIndividuals = findDominatingIndividuals(remainingIndividuals);
            currDominatingIndividuals.forEach(i -> i.setRank(rank.get()));
            remainingIndividuals.removeAll(currDominatingIndividuals);
            rank.incrementAndGet();
            paretoFronts.add(currDominatingIndividuals);
        }
        return paretoFronts;
    }

    /**
     * This method will find all the individuals that are dominating.
     *
     * @param individuals individuals to compare
     * @return list of dominating individuals
     */
    private List<Individual> findDominatingIndividuals(ArrayList<Individual> individuals) {
        List<Individual> dominatingIndividuals = new ArrayList<>();

        // Iterate over the individuals
        for (Individual individual : individuals) {
            boolean isDominated = false;

            // use iterators so we can modify the list while iterating
            for (Iterator<Individual> it = dominatingIndividuals.iterator(); it.hasNext(); ) {
                Individual dominatingInd = it.next(); // get a dominating Individual to compare

                // if same, go next
                if (individual == dominatingInd) {
                    continue;
                }

                // if current individual got dominated, break
                if (dominatingInd.dominates(individual)) {
                    isDominated = true;
                    break;
                }

                // Current individual dominates
                // old individual no longer dominates, remove
                if (individual.dominates(dominatingInd)) {
                    it.remove();
                }
            }

            // if individual was not dominated, add to list
            if (!isDominated) {
                dominatingIndividuals.add(individual);
            }
        }
        return dominatingIndividuals;
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
