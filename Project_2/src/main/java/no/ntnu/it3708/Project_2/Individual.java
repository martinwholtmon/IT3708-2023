package no.ntnu.it3708.Project_2;

import java.util.ArrayList;

public class Individual {
    private final ArrayList<Integer> bitstring;
    private ArrayList<Individual> parents;
    private Float fitness;


    public Individual(ArrayList<Integer> bitstring) {
        this.bitstring = bitstring;
        this.parents = new ArrayList<>();
        this.fitness = 0f;
    }

    public Individual(ArrayList<Integer> bitstring, ArrayList<Individual> parents, Float fitness) {
        this.bitstring = bitstring;
        this.parents = parents;
        this.fitness = fitness;
    }

    public ArrayList<Individual> getParents() {
        return parents;
    }

    public void setParents(ArrayList<Individual> parents) {
        this.parents = parents;
    }

    public Float getFitness() {
        return fitness;
    }

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
