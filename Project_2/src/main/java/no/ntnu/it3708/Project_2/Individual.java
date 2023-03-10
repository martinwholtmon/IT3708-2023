package no.ntnu.it3708.Project_2;

import java.util.ArrayList;

/**
 * The Individual.
 */
public class Individual {
    private final int nurse_id;
    private final int[] bitstring;
    private ArrayList<Individual> parents;
    private Float fitness;


    /**
     * Instantiates a new Individual.
     *
     * @param nurse_id  the id of the nurse
     * @param bitstring the bitstring
     */
    public Individual(int nurse_id, int[] bitstring) {
        this.nurse_id = nurse_id;
        this.bitstring = bitstring;
        this.parents = new ArrayList<>();
        this.fitness = 0f;
    }

    /**
     * Instantiates a new Individual.
     *
     * @param nurse_id  the id of the nurse
     * @param bitstring the bitstring
     * @param parents   the parents
     * @param fitness   the fitness
     */
    public Individual(int nurse_id, int[] bitstring, ArrayList<Individual> parents, Float fitness) {
        this.nurse_id = nurse_id;
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
     * Gets nurse id.
     *
     * @return the nurse id
     */
    public int getNurse_id() {
        return nurse_id;
    }

    /**
     * Get bitstring int [ ].
     *
     * @return the int [ ]
     */
    public int[] getBitstring() {
        return bitstring;
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
