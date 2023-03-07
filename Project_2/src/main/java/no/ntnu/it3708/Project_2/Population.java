package no.ntnu.it3708.Project_2;

import java.util.ArrayList;

public class Population {
    private ArrayList<Individual> feasible_individuals;
    private ArrayList<Individual> infeasible_individuals;
    private final Population old_pop;
    private final Integer generation_nr;

    public Population() {
        this.feasible_individuals = new ArrayList<>();
        this.infeasible_individuals = new ArrayList<>();
        this.old_pop = null;
        this.generation_nr = 0;
    }

    public Population(ArrayList<Individual> feasible_individuals, ArrayList<Individual> infeasible_individuals, Population old_pop, Integer generation_nr) {
        this.feasible_individuals = feasible_individuals;
        this.infeasible_individuals = infeasible_individuals;
        this.old_pop = old_pop;
        this.generation_nr = generation_nr;
    }

    public ArrayList<Individual> getFeasible_individuals() {
        return feasible_individuals;
    }

    public ArrayList<Individual> getInfeasible_individuals() {
        return infeasible_individuals;
    }

    public Integer getGeneration_nr() {
        return generation_nr;
    }

    @Override
    public String toString() {
        return "Generation " + generation_nr + " avg. fitness: " + calc_avg_fitness();
    }

    private Float calc_avg_fitness() {
        Float total_fitness = 0f;
        for (Individual i : feasible_individuals) total_fitness += i.getFitness();
        return total_fitness / feasible_individuals.size();
    }
}
