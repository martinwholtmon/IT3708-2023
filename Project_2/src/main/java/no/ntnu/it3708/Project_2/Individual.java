package no.ntnu.it3708.Project_2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Individual.
 */
public class Individual {
    private HashMap<Integer, ArrayList<Integer>> bitstring;    // represented as matrix [nurse_id][patients]
    private ArrayList<Individual> parents;
    private double fitness;


    /**
     * Instantiates a new Individual.
     *
     * @param nurse_id  the id of the nurse
     * @param bitstring the bitstring
     */
    public Individual(HashMap<Integer, ArrayList<Integer>> bitstring) {
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
    public Individual(HashMap<Integer, ArrayList<Integer>> bitstring, ArrayList<Individual> parents, Float fitness) {
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
    public double getFitness() {
        return fitness;
    }

    /**
     * Get bitstring int [ ].
     *
     * @return the int [ ]
     */
    public HashMap<Integer, ArrayList<Integer>> getBitstring() {
        return bitstring;
    }

    /**
     * Sets fitness.
     *
     * @param fitness the fitness
     */
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public String toString() {
        return "Individual{" +
                "bitstring=" + bitstring +
                ",\nfitness=" + fitness +
                '}';
    }
}
