package no.ntnu.it3708.Project_2;

import java.util.ArrayList;

/**
 * The Individual.
 */
public class Individual {
    private final ArrayList<Integer> bitstring;
    private ArrayList<Individual> parents;
    private Float fitness;


    /**
     * Instantiates a new Individual.
     *
     * @param bitstring the bitstring
     */
    public Individual(ArrayList<Integer> bitstring) {
        this.bitstring = bitstring;
        this.parents = new ArrayList<>();
        this.fitness = 0f;
    }

    /**
     * Instantiates a new Individual.
     *
     * @param bitstring the bitstring
     * @param parents   the parents
     * @param fitness   the fitness
     */
    public Individual(ArrayList<Integer> bitstring, ArrayList<Individual> parents, Float fitness) {
        this.bitstring = bitstring;
        this.parents = parents;
        this.fitness = fitness;
    }

    /**
     * Gets parents.
     *
     * @return the parents
     */
    public ArrayList<Individual> getParents() {
        return parents;
    }

    /**
     * Sets parents.
     *
     * @param parents the parents
     */
    public void setParents(ArrayList<Individual> parents) {
        this.parents = parents;
    }

    /**
     * Gets fitness.
     *
     * @return the fitness
     */
    public Float getFitness() {
        return fitness;
    }

    /**
     * Sets fitness.
     *
     * @param fitness the fitness
     */
    public void setFitness(Float fitness) {
        this.fitness = fitness;
    }

    @Override
    public String toString() {
        return "Individual{" +
                "bitstring=" + bitstring +
                ", fitness=" + fitness +
                '}';
    }
}
