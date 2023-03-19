package no.ntnu.it3708.Project_2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Individual.
 */
public class Individual {
    private HashMap<Integer, ArrayList<Integer>> bitstring; // represented as matrix [nurse_id][patients]
    private ArrayList<Individual> parents;
    private double fitness;

    /**
     * Instantiates a new Individual.
     *
     * @param bitstring the bitstring
     */
    public Individual(HashMap<Integer, ArrayList<Integer>> bitstring) {
        this.bitstring = bitstring;
        this.parents = new ArrayList<>();
        this.fitness = 0f;
    }

    public Individual(HashMap<Integer, ArrayList<Integer>> bitstring, double fitness) {
        this.bitstring = bitstring;
        this.parents = new ArrayList<>();
        this.fitness = fitness;
    }

    /**
     * Instantiates a new Individual.
     *
     * @param bitstring the bitstring
     * @param parents   the parents
     * @param fitness   the fitness
     */
    public Individual(HashMap<Integer, ArrayList<Integer>> bitstring, ArrayList<Individual> parents, double fitness) {
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

    public Individual deepCopy() {
        HashMap<Integer, ArrayList<Integer>> nBitstring = new HashMap<>();
        for (int nurse_idx = 0; nurse_idx < this.bitstring.size(); nurse_idx++) {
            ArrayList<Integer> patients = new ArrayList<>();
            for (Integer patient_idx : this.bitstring.get(nurse_idx)) {
                patients.add(patient_idx);
            }
            nBitstring.put(nurse_idx, patients);
        }
        return new Individual(nBitstring, this.fitness);
    }

    public Individual createChild(Individual parent1, Individual parent2) {
        Individual newIndividual = this.deepCopy();
        newIndividual.setFitness(0);
        newIndividual.getParents().add(parent1);
        newIndividual.getParents().add(parent2);
        return newIndividual;
    }

    @Override
    public String toString() {
        return "Individual{" +
                "bitstring=" + bitstring.values() +
                "\nfitness=" + fitness +
                '}';
    }
}
